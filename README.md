# 对象存储系统cloud-can

### 前言
该项目为前后端分离项目的后端部分，前端地址：传送门 。

### 项目介绍
cloud-can是一个SaaS模式的分布式对象存储系统(object storage system)，包括了客户端和后台管理端,基于java实现。
客户端主要包括以下内容:

用户模块: 包括用户和子用户,实现了上级对下级资源分配和管理

存储桶(bucket): 存储对象的桶,存储桶内支持文件夹

存储桶策略(BucketPolicy): 可以使用该策略向存储桶及其中对象授予访问权限,bucketPolicy权限设置可以精确到一个具体的文件夹

访问控制列表(ACL): 用于管理存储桶和对象的访问权限,包含了多种访问访问策略,如私有,公共读写,子用户读等

归档: 用户可以将不需要访问的对象数据压缩存储,降低存储的成本

对象数据压缩：视频或者图片可进行压缩后存储，占用更少的存储空间存储更多的数据。   

数据加密：实现了SM4加密，对收到的对象数据进行加密，再将得到的加密的数据持久化保存。

对象数据的上传与下载：可以通过简单上传和分片上传将文件上传到存储空间，以及下载存储空间的文件。

存储桶和对象标签：通过Bucket和object的标签功能，进行分类管理。

支持对象数据的秒传、校验、去重和断电续传等功能。

数据备份与复原：用户可以设置不同的访问权限和级别进行备份，保障了数据的访问安全。

### 技术选型
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

#### 项目模块布局
cloud-can -- 源码目录

├── common -- 公共模块,存放实体类工具类等

├── oss-clod-data -- 归档服务

├── oss-data -- 存储对象数据服务

├── oss-interface -- RPC接口

├── oss-gateway -- 对象下载路由服务

├── oss-service -- 处理核心业务服务


### 项目特色

#### 高效,安全的对象管理

用户需求 ：保证用户(个人/企业)的对象存储的安全性。

实现方式 ：参考Amazon S3的ACL和存储桶策略,利用mysql+请求拦截器+反射实现BucketPolicy,对于子用户和其他用户上层桶的BucketAcl和下层对象的objectAcl,同时支持存储桶内文件夹,以及对象的AccessKey。

实现效果： 用户/企业可以灵活对权限进行设置,限制资源的访问。

#### 安全,放心的对象存储

痛点问题 ：在计算机和移动设备等信息系统中，可能存在各种安全威胁和非法访问等。如果敏感数据没有进行加密存储，则有可能被未经授权的人员或程序获取和窃取，进而导致数据泄露。

用户需求 ：用户需要对敏感信息进行加密。

实现方式 ：为用户提供国密算法-SM4进行数据加密,系统在加密以后再将数据进行存储。

#### 支持横向扩容

痛点问题：单机存储应用无法满足对象存储海量数据的需求。

实现方式：采用分布式架构存储对象:  Nacos集群 + 数据服务集群部署。

实现效果：可扩容,数据服务可热部署,经过压测,增加节点以后可以大幅度提升系统的吞吐量。

#### 支持异地热备

痛点问题 ：当存储对象的节点发生网络分区和宕机时数据服务不可用

实现方式 ：基于sofa-jraft构建多主备的分布式存储应用,主库和备库之间通过网络传输日志信息保持分布式一致性，当主库发生故障时，可以快速切换到备库继续提供服务，从而实现了高可用。

实现效果 ：保持对象服务的高可用性,主节点下线时,备库依然可以在线提供服务,保证了云罐数据服务的“在线性”

#### 存储负载均衡

痛点问题 ：数据集群内数据分布不均衡;普通Hash算法不能适应热部署场景;节点性能配置可能不均衡

实现方式 ：提供一致性hash算法和加权轮询算法。

实现效果 ：当节点配置均匀时用户可以采用一致性hash算法,当节点硬件配置、网络带宽、处理能力等不均衡时则采用加权轮询算法.在虚拟节点数为1M的情况下几乎平均分配。

#### 异步归档

痛点问题 ：大量对象归档,导致常规业务的吞吐量受到较大影响。

实现方式 ：rocketmq的集群消费方式+一次拉取少量的归档消息进行异步归档,并提供给用户查询对象状态。

实现效果 ：削弱高并发场景下归档占用的服务资源,减轻磁盘IO和网络IO压力,从而提升对象存储核心业务的吞吐量。

#### 对象秒传

痛点问题 ：上传对象时对象去重,对象秒传等查询对象hash的需求频繁,常规磁盘存储无法满足需求

实现方式 ：redis是单线程,纯内存,将hash值的存储信息存储在redis,直接查询redis。

实现效果 ：经测试在1000个线程并发访问秒传接口时redis的ops是mysql的近2倍。






### 部署

|   前置环境   |
| ---- |
|   apache-maven-3.6.3   |
|   jdk1.8   |
|   msyql8   |
|   rocketmq-all-5.0.0-bin-release   |
|   nacos2.0.4   |
|   redis-x64-3.0.504   |
|   SOFAJRaft1.3.13   |


#### 说明:
oss-data的cluster为raft算法中每个节点配置,每个节点都存储了相同的数据。

group为该raft集群的名字,配置多个raft集群可以进行横向扩容



#### 举例:

操作系统: windows10

nacos: 192.168.50.236:8848

mysql: addr=101.35.43.156:3306 username=root password=xxx

redis: 192.168.50.236:6379

rocketmq: 192.168.50.236:9876

oss-gateway5555: 192.168.50.236:5555

一共配置了一个raft集群：192.168.50.236:8021,192.168.50.236:8022,192.168.50.236:8023

oss-data8021: 192.168.50.236:8021

oss-data8022: 192.168.50.236:8022

oss-data8023: 192.168.50.236:8023

oss-cold-data5700: 192.168.50.236:5700



#### oss-gateway:

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

--mysql.password=xxx



#### oss-data:

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



#### oss-service:

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

--mysql.password=xxx

--dubbo.protocol.host=192.168.50.236

--dubbo.protocol.port=8085

--rocketmq.addr=192.168.50.236:9876




#### oss-cold-data:

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

--mysql.password=xxx

--rocketmq.addr=192.168.50.236:9876

--cold_storage_name=cold-storage1

--position=D:\OSS\cold_01

