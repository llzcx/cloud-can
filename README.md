# 对象存储系统cloud-can

#### 前言
该项目为前后端分离项目的前端部分，前端地址：传送门 。

#### 项目介绍
cloud-can是一个SaaS模式的对象存储项目，包括了客户端和后台管理端,基于java实现。主要包括用户管理、存储桶(bucket)管理,资源权限管理(BucketPolicy和ACL),对象管理(元数据模块和文件模块)等功能。

#### 技术选型
|   技术   |   说明   |   官网   |
| ---- | ---- | ---- |
|   nacos   |   服务注册与发现   |   https://github.com/alibaba/nacos   |
|   gateway   |   网关路由   |   https://github.com/spring-cloud/spring-cloud-gateway   |
|   Dubbo   |   RPC   |   https://github.com/apache/dubbo   |
|   SOFAJRaft  |   RAFT算法实现   |   https://github.com/sofastack/sofa-jraft   |
|   RocketMQ  |   消息中间件   |   https://github.com/apache/rocketmq   |
|   redis  |   缓存   |   https://github.com/redis/redis   |
|   mysql  |   数据库   |   https://github.com/mysql   |

#### 技术架构图
<img width="437" alt="image" src="https://user-images.githubusercontent.com/111289933/235817656-0a106a58-84c2-4b62-a6b6-e97bee1d18d9.png">

#### 安装教程


#### 使用说明

oss-gateway5555:
--server.address=0.0.0.0
--server.port=5555
--nacos.addr=192.168.50.236:8848
--naocs.username=nacos
--nacos.password=nacos
--redis.ip=192.168.50.236
--redis.port=6379
--mysql.addr=101.35.43.156:3306
--mysql.username=root
--mysql.password=123abc456

oss-data8021:
--server.address=0.0.0.0
--server.port=8021
--group=group1
--cluster=192.168.50.236:8031,192.168.50.236:8032,192.168.50.236:8033
--jraft-data-path=D:\oss\01\jraft_data_path
--position=D:\oss\01\position
--rpc-addr=192.168.50.236:8031
--dubbo.protocol.port=20881
--dubbo.protocol.host=192.168.50.236
--nacos.addr=192.168.50.236:8848
--naocs.username=nacos
--nacos.password=nacos
--redis.ip=192.168.50.236
--redis.port=6379

oss-data8022:
--server.address=0.0.0.0
--server.port=8022
--group=group1
--cluster=192.168.50.236:8031,192.168.50.236:8032,192.168.50.236:8033
--jraft-data-path=D:\oss\02\jraft_data_path
--position=D:\oss\02\position
--rpc-addr=192.168.50.236:8032
--dubbo.protocol.host=192.168.50.236
--dubbo.protocol.port=20882
--nacos.addr=192.168.50.236:8848
--naocs.username=nacos
--nacos.password=nacos
--redis.ip=192.168.50.236
--redis.port=6379

oss-data8023:
--server.address=0.0.0.0
--server.port=8023
--group=group1
--cluster=192.168.50.236:8031,192.168.50.236:8032,192.168.50.236:8033
--jraft-data-path=D:\oss\03\jraft_data_path
--position=D:\oss\03\position
--rpc-addr=192.168.50.236:8033
--dubbo.protocol.host=192.168.50.236
--dubbo.protocol.port=20883
--nacos.addr=192.168.50.236:8848
--naocs.username=nacos
--nacos.password=nacos
--redis.ip=192.168.50.236
--redis.port=6379

oss-service8080:
--server.address=0.0.0.0
--server.port=8080
--nacos.addr=192.168.50.236:8848
--naocs.username=nacos
--nacos.password=nacos
--redis.ip=192.168.50.236
--redis.port=6379
--mysql.addr=101.35.43.156:3306
--mysql.username=root
--mysql.password=123abc456
--dubbo.protocol.host=192.168.50.236
--dubbo.protocol.port=8085
--rocketmq.addr=192.168.50.236:9876

oss-cold-data5700:
--server.address=0.0.0.0
--server.port=5700
--nacos.addr=192.168.50.236:8848
--naocs.username=nacos
--nacos.password=nacos
--redis.ip=192.168.50.236
--redis.port=6379
--mysql.addr=101.35.43.156:3306
--mysql.username=root
--mysql.password=123abc456
--rocketmq.addr=192.168.50.236:9876
--cold_storage_name=cold-storage1
--position=D:\OSS\cold_01




#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
