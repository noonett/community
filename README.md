# 网上社区系统

### 概述
基于Spring生态的博客社区系统项目，主体模块是帖子、用户、消息、统计、搜索、分享等。

### 顶层设计
1. 帖子模块：发帖、删帖、帖子加精置顶、帖子评论、帖子和评论点赞、最热帖子查看
2. 用户模块：注册、登陆、个人主页、用户信息变更、密码找回、用户己发帖和评论、用户间关注、用户权限控制
3. 消息模块：用户间收发消息、系统发送站内信提醒
4. 统计模块：DAU、UV统计
5. 搜索模块：搜索关键词
6. 分享模块：生成长图分享帖子内容

### 主要技术
- 前端：Thymeleaf、JQuery、CSS、HTML
- 应用服务器：Spring，SpringMVC，SpringSecurity、Mybatis、Quartz、Caffeine
- 消息队列：Kafka
- 缓存层：Redis
- 数据层：MySQL、Elasticsearch（搜索）

### 设计实现
1. 基于前缀树算法实现帖子、评论和消息的敏感词过滤。
2. 基于Redis和Caffeine实现帖子和评论等访问热点的多级缓存。
3. 基于Redis实现令牌桶算法对部分接口进行限流处理。
4. 基于BitMap实现DAU统计，基于HyperlogLog实现UV统计
5. 实现布隆过滤器过滤用户访问不存在帖子ID和用户ID的请求以防止缓存穿透。
6. 基于SpringSecurity和Interceptor实现用户鉴权，CSRF攻击的防护。
7. 基于Elasticsearch实现关键词搜索。
8. 基于Kafka消息队列，实现响应与数据库写入、Elasticsearch更新的分离，达到快速响应。
9. 基于Quartz实现分布式定时任务，完成帖子热度刷新的任务。

### 分布式部署
![网站架构图](https://github.com/noonett/PictureBed/blob/master/网站架构图.png?raw=true)
