package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.AssertTrue;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTests {

    private DiscussPost data;

    @Autowired
    private DiscussPostService discussPostService;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass");
    }

    @Before
    public void before() {
        System.out.println("before");

        // 初始化数据
        data = new DiscussPost();
        data.setUserId(111);
        data.setCreateTime(new Date());
        data.setContent("Test test");
        data.setTitle("test");
        discussPostService.addDiscussPost(data);
    }

    @After
    public void after() {
        System.out.println("after");

        // 删除数据
        discussPostService.updateStatus(data.getId(), 2);
    }

    @Test
    public void test1() {
        System.out.println("test1");
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    public void testFindById() {
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertNotNull(post);
        Assert.assertEquals(data.getId(), post.getId());
        Assert.assertEquals(data.getTitle(), post.getTitle());
        Assert.assertEquals(data.getContent(), post.getContent());
        Assert.assertEquals(data.toString(), post.toString());
    }

    @Test
    public void testUpdateScore() {
        int rows = discussPostService.updateScore(data.getId(), 2000);
        Assert.assertEquals(1, rows);
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        // 第三个参数表示精度
        Assert.assertEquals(2000, post.getScore(), 2);
    }
}
