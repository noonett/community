# 网上社区系统

### 概述
基于Spring生态的博客社区系统项目，主体模块是帖子、用户、消息、统计等。
![网站架构图](https://github.com/noonett/PictureBed/blob/master/网站架构图.png?raw=true)

### 主要技术
前端：Thymeleaf、JQuery、CSS、Html
应用服务器：Spring，SpringMVC，SpringSecurity、Mybatis、Quartz、
消息队列：Kafka
缓存层：Caffeine、Redis
数据层：MySQL、Elasticsearch（搜索）

### 设计实现
1. 基于前缀树算法实现帖子、评论和消息的敏感词过滤。
2. 基于Redis和Caffeine实现帖子和评论等访问热点的多级缓存，基于Redis实现令牌桶算法对部分接口进行限流处理，实现布隆过滤器过滤用户访问不存在帖子ID和用户ID的请求以防止缓存穿透。
3. 基于SpringSecurity和Interceptor实现用户鉴权，CSRF攻击的防护。
4. 基于Kafka消息队列，实现响应与数据库写入、Elasticsearch更新的分离，达到快速响应。
5. 基于Quartz实现分布式定时任务，完成帖子热度刷新的任务。
