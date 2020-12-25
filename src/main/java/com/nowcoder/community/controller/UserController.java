package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.*;
import com.nowcoder.community.util.CommunityConstants;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/user")
public class UserController implements CommunityConstants {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        // 上传文件名称
        String fileName = CommunityUtil.generateUUID();
        // 设置响应信息 七牛云JDK 客户端直传
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // 生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    // 七牛云上传成功后，更新头像路径
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "文件名不能为空!");
        }

        String url = "http://" + headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUsers().getId(), url);

        return CommunityUtil.getJSONString(0);
    }

    // 废弃
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }

        // 生成随机的文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发送异常！", e);
        }

        //更新当前用户头像路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUsers();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    // 废弃
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 输出文件的格式后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            // 建立缓冲区，增加效率
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
            throw new RuntimeException("读取头像失败，服务器响应异常！", e);
        }
    }

    @RequestMapping(path = "/password", method = RequestMethod.POST)
    public String updatePassword(Model model, String oriPassword, String newPassword) {
        if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("newPasswordMsg", "新密码不能为空！");
            return "/site/setting";
        }
        User user = hostHolder.getUsers();
        oriPassword = CommunityUtil.md5(oriPassword + user.getSalt());
        if (!oriPassword.equals(user.getPassword())) {
            model.addAttribute("oriPasswordMsg", "原密码输入不正确！");
            return "/site/setting";
        }
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(), newPassword);
        return "redirect:/logout";
    }

    // 个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在！");
        }

        // 用户
        model.addAttribute("user", user);

        // 点赞数量
        int likeCount = likeService.findUserLikeCount(user.getId());
        model.addAttribute("likeCount", likeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUsers() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUsers().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }

    // 我的帖子
    @RequestMapping(path = "/my-post/{userId}", method = RequestMethod.GET)
    public String getMyPostPage(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在！");
        }
        // 用户
        model.addAttribute("user", user);
        // 帖子列表
        List<DiscussPost> postList = discussPostService.findDiscussPosts(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> mapList = new ArrayList<>();
        int postCount = postList.size();
        for (DiscussPost post : postList) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            map.put("likeCount", likeCount);
            mapList.add(map);
        }
        model.addAttribute("mapList", mapList);
        model.addAttribute("postCount", postCount);
        return "/site/my-post";
    }

    @RequestMapping(path = "/my-reply/{userId}", method = RequestMethod.GET)
    public String getMyReplyPage(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在！");
        }
        // 用户
        model.addAttribute("user", user);
        // 回复列表
        List<Comment> commentList = commentService.findCommentsByUserId(userId, page.getOffset(), page.getLimit());
        int commentCount = commentList.size();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Comment comment : commentList) {
            Map<String, Object> map = new HashMap<>();
            DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
            if (post == null) {
                commentService.updateCommentStatus(2, comment.getId());
                --commentCount;
                continue;
            }
            map.put("post", post);
            map.put("comment", comment);
            mapList.add(map);
        }
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("mapList", mapList);
        return "/site/my-reply";
    }
}
