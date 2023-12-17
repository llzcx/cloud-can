# 对象存储系统cloud-can

### 前言


该项目荣获第十四届服创大赛中部赛区三等奖。


### 项目介绍


cloud-can是一个分布式对象存储系统(object storage system)，数据存储采用了本地文件系统，对象的元数据存储采用了mysql和redis。



用户: 包括用户和子用户,实现了上级对下级资源分配和管理。

存储桶(bucket): 存储对象的桶,存储桶内支持文件夹。

存储桶策略(BucketPolicy): 可以使用该策略向存储桶及其中对象授予访问权限,bucketPolicy权限设置可以精确到一个具体的文件夹。

访问控制列表(ACL): 用于管理存储桶和对象的访问权限,包含了多种访问访问策略,如私有,公共读写,子用户读等。

归档: 用户可以将不需要访问的对象数据压缩存储,降低存储的成本。

对象数据压缩：视频或者图片可进行压缩后存储，占用更少的存储空间存储更多的数据。   

数据加密：实现了SM4加密，对收到的对象数据进行加密，再将得到的加密的数据持久化保存。

对象数据的上传与下载：可以通过简单上传和分片上传将文件上传到存储空间，以及下载存储空间的文件。

对象的校验与去重：使用SHA1算法进行全链路校验和去重。

存储桶和对象标签：通过Bucket和object的标签功能，进行分类管理。

支持对象数据的秒传、校验、去重和断电续传等功能。


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


#### 项目模块


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

实现方式 ：参考Amazon S3的ACL和存储桶策略,利用mysql+请求拦截器+反射实现BucketPolicy,对于子用户和其他用户上层桶的BucketAcl和下层对象的objectAcl,同时支持存储桶内文件夹,以及对象的AccessKey。（output.md提供了接口级别关系）

实现效果： 用户/企业可以灵活对子用户以及其他账号的权限进行设置,限制资源的访问。

#### 安全,放心的对象存储

痛点问题 ：对象在传输的途中可能发送改变同时在计算机和移动设备等信息系统中，可能存在各种安全威胁和非法访问等。如果敏感数据没有进行加密存储，则有可能被未经授权的人员或程序获取和窃取，进而导致数据泄露。

实现方式 ：使用SHA1完成全链路校验保障数据上传和下载时的一致性，为用户提供国密算法-SM4进行数据加密,系统在加密以后再将数据进行存储。

#### 支持横向扩容，提升系统总容量

痛点问题：单机存储应用无法满足对象存储海量数据的需求。

实现方式：可以同时部署多个卷，可以根据不同的负载均衡算法将对象分配到不同的卷中。

#### 支持异地部署，提供高可用性对象服务

痛点问题 ：当存储对象的节点发生网络分区和宕机时数据服务不可用

实现方式 ：基于sofa-jraft构建多主备的分布式存储应用,主库和备库之间通过网络传输日志信息保持分布式一致性，当主库发生故障时，可以快速切换到备库继续提供服务，从而实现了高可用。

实现效果 ：保持对象服务的高可用性,主节点下线时,备库依然可以在线提供服务,保证了clou-can数据服务的“在线性”

#### 异步归档

痛点问题 ：大量对象归档,导致常规业务的吞吐量受到较大影响。

实现方式 ：rocketmq的集群消费方式+一次拉取少量的归档消息进行异步归档,并提供给用户查询对象状态。

#### Restful API的对象访问，实现了对象的去重、秒传、小文件快传、大文件分片上传

实现方式 ：对象访问基本与亚马逊S3协议一致，采用扁平式存储桶设计但又设计了文件夹功能便于用户进行对象管理。






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


兼容WIndows和Linux



#### 存储服务：oss-data 说明


group参数为卷名，这里的卷可以类比WIndows的D盘E盘，可以为每个卷部署多个oss-data，同一个卷内所有节点数据通过raft协议保持一致性。（建议节点数为奇数）

cluster为卷内所有oss-data成员，所有成员通过网络进行数据同步。

oss-data为单个存储节点，多个oss-data可以组成一个卷。

jraft-data-path和position为本地存储目录，建议放在一个父目录下。

rpc-addr为rpc网络信息需要占用端口号与卷内其他节点进行通信，建议端口号为server.port + 10。

![image](https://github.com/llzcx/cloud-can/assets/111289933/166faaf9-a124-4a1c-a41e-3718f3b7e574)


![image](https://github.com/llzcx/cloud-can/assets/111289933/5686130a-64ef-4fbc-a503-37a9c193901e)


![image](https://github.com/llzcx/cloud-can/assets/111289933/a704ba6a-3c33-4553-9656-0902636f721a)



#### 用户服务：oss-service 说明


主要向外提供用户和权限服务，可以单节点部署访问，也可以部署集群+nginx来访问。

![image](https://github.com/llzcx/cloud-can/assets/111289933/142d419a-830c-4d1a-b891-085a4a782dfa)



#### 解析服务：oss-gateway 说明


解析用户的Rustful API请求，拦截和修改参数，从而适配卷协议需要的参数转发到真正的数据服务。

![image](https://github.com/llzcx/cloud-can/assets/111289933/c601e826-e4cb-4435-976b-edbc607b6168)



#### 归档服务：oss-cold 说明


用于将不常访问的数据压缩存储，减少存储成本。

![image](https://github.com/llzcx/cloud-can/assets/111289933/7b7eb04f-ea41-4b9d-912e-28a05438ce23)




