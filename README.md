@[TOC](基于netty实现类似dubbo的rpc中间件)

# easyRpc
类似dubbo，easyRpc的功能是面向接口的远程方法调用。可集群容错。设计上模仿dubbo，实际上跟dubbo比起来就是🌶️🌶️🌶️🌶️🐔。仅用于学习交流。

## 概念
1. 消息提供者（provider），服务提供者，将服务暴露并注册到注册中心。可集群
2. 注册中心（register），管理服务提供者提供的服务，具有负载均衡的功能（仅仅有随机策略），将服务暴露给消费者。
3. 消费者（consumer），通过注册中心找到服务提供者再进行调用。

## 如何使用
注册中心启动：

注意：注册中心一定要先于生产者启动，否则服务无法注册到注册中心。

注册中心:[jar包](https://pan.baidu.com/s/1k66XujW19-Z2x26ccV-X0A) 提取码: pyr8
下载后执行命令
```
 java -jar easyregister-1.0-SNAPSHOT.jar
```
默认占用端口号20888，心跳最大间隔时间为30（30无心跳服务将被注册中心下线）。如需指定
```
 java -jar easyregister-1.0-SNAPSHOT.jar 端口号 心跳最大间隔时间
```
也可以不下载jar包，直接将[源码](https://github.com/404008945/easyrpc)克隆到本地，运行main方法即可
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200513190831426.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NDM2MTcy,size_16,color_FFFFFF,t_70)
项目启动：
需要maven，springboot。
1. 由于easyRpc并不在maven公服上，第一步需要将 [源码](https://github.com/404008945/easyrpc)**down下来利用idea或者在项目根目录执行mvn install打jar包到本地仓库**。打包后直接在需要使用的项目中依赖即可
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020051319330048.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NDM2MTcy,size_16,color_FFFFFF,t_70)
```
  <dependency>
            <artifactId>easyrpc-starter</artifactId>
            <groupId>com.lxs</groupId>
            <version>1.0-SNAPSHOT</version>
  </dependency>
```
若需要使用，依赖easyRpc的starter模块，
启动easyRpc需要在springBoot启动类加上注解@EasyRpcEnable
![](https://img-blog.csdnimg.cn/20200513175516705.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NDM2MTcy,size_16,color_FFFFFF,t_70)
生产者配置：
```
#扫描包路径，用于服务提供者服务暴露，消费者引入服务
easyrpc.basePackage=com.rpctest.test
#注册中心端口
easyrpc.registerPort=20888  
#注册中心ip
easyrpc.registerIp=127.0.0.1

#服务提供者占用端口，如不配置，则不会提供服务
easyrpc.port=20222

#20秒发送一个心跳包通知注册中心自己在线，若不配置，默认15
easyrpc.heartBeatTime=20

```
在需要暴露服务的类加上@RpcProd注解。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200513180456133.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NDM2MTcy,size_16,color_FFFFFF,t_70)

消费者配置：
```
#扫描包路径，用于服务提供者服务暴露，消费者引入服务
easyrpc.basePackage=com.rpctest.test
#注册中心端口
easyrpc.registerPort=20888  
#注册中心ip
easyrpc.registerIp=127.0.0.1
```
服务提供模块需要配置，easyrpc.port用于提供服务给消费者，如果一个模块纯消费就不必配置
服务引入：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020051318135343.png)
如需引用服务，需要在字段上加上 @RpcConsumer(timeout = 10,retryTime = 3)，其中timeout为请求超时时间（不配置默认为5），retrytime为重试次数（默认为3）。此注解标识含义是，若10秒无返回则重试，重试3次均无返回则认为接口调用超时。
## 生产者消费者的使用demo
[demo地址](https://github.com/404008945/rpctest)
启动此demo需要按照前面的教程easyRpc打包到本地仓库，并且启动注册中心。
注意：demo中配置的注册中心ip和端口号需要与注册中心一致。
本demo中有两个provider和consumer，两个provider注册到一个注册中心集群，分别启动这三个模块就可以调用了
![在这里插入图片描述](https://img-blog.csdnimg.cn/202005131939466.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NDM2MTcy,size_16,color_FFFFFF,t_70)

访问 http://localhost:8866/index
消费者会调用服务提供者的服务并返回结果
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200513192008775.png)

## 性能测试
以下测试均在无业务处理的情况下，仅仅走通链路
单次调用耗时
![](https://img-blog.csdnimg.cn/20200513172226711.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NDM2MTcy,size_16,color_FFFFFF,t_70)
jmeter压力测试
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200513172516431.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NDM2MTcy,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200513172523718.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NDM2MTcy,size_16,color_FFFFFF,t_70)
由于注册中心，服务提供者，消费者均在一台电脑上，性能测试意义不大。

## 总结
easyRpc可以实现简单的分布式调用，异常抛出，生产者集群，使用netty异步可以做到高并发。但是实际生产用途不大，
仅仅用来练手与学习，主要使用了反射，代理和netty异步通信，源码已在文中给出链接，如有兴趣可一起交流。
