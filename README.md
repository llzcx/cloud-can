# 对象存储系统

#### 介绍
服创后端仓库

#### 软件架构
软件架构说明


#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

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



#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
