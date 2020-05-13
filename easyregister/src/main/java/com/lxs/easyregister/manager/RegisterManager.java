package com.lxs.easyregister.manager;

import com.lxs.easyregister.provider.Provider;
import com.lxs.remote.RegisterResponseMessageContent;
import com.lxs.remote.RegisterResult;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 负责管理所有生产者服务
 */
@Slf4j
public class RegisterManager {

    private  Map<String,Provider> remotes =  new HashMap<>();//维护消息提供者集合

    private  Map<String, Map<String,Provider>> registerMap = new HashMap<>();//1以目标接口作为key,提供者ip，和提供者分别作为value 的key和value

    /**
     * 加锁保证原子操作实现线程安全
     */
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * 注册服务
     */
    public  void registe(Provider provider){
        lock.writeLock().lock();
        String host = provider.getIpAddr()+":"+provider.getPort();
        remotes.put(host,provider);//维护进
        try {
        Map<String,Provider>  providers = registerMap.get(provider.getTargetInterface());
     //此处需要上锁,
        if (providers == null) {
            providers = new HashMap<>();
        }

        providers.put(host,provider);//

        registerMap.put(provider.getTargetInterface(), providers);
        }catch (Exception e)
        {
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            lock.writeLock().unlock();
        }


    }


     public RegisterResponseMessageContent getProvider(String key){  //通过key获取服务提供者,这里因为读取了size，需要加锁，此方法执行则不允许添加或删除元素
        //此处来个随机，模拟负载均衡
         lock.readLock().lock();
         try {
             RegisterResponseMessageContent registerResponseMessageContent = new RegisterResponseMessageContent();
             Map<String, Provider> providerSet = registerMap.get(key);
             if(providerSet == null ){
                 registerResponseMessageContent.setResult(new RegisterResult(RegisterResult.FAIL,"服务不存在"));
                 return registerResponseMessageContent;
             }
             List<Provider> list = providerSet.keySet().stream().map(e->providerSet.get(e)).filter(e->e.isActive()).collect(Collectors.toList());
             if (list == null || list.size() == 0) {
                 registerResponseMessageContent.setResult(new RegisterResult(RegisterResult.FAIL,"服务不存在"));
                 return registerResponseMessageContent;
             }
             int val = (int) (Math.random() * list.size());
             //List<String> ips = providerSet.entrySet().stream().map(e -> e.getValue().getIpAddr()).collect(Collectors.toList());
             Provider provider = list.get(val);

             RegisterResult registerResult = new RegisterResult();
             registerResult.setMessageType(registerResult.SUCCESS);
             registerResult.setTargetInterface(provider.getTargetInterface());
             registerResult.setIpAddr(provider.getIpAddr());//注册中心获取不到服务提供者的端口号。需要配置
             registerResult.setPort(provider.getPort());
             registerResponseMessageContent.setResult(registerResult);
             return registerResponseMessageContent;

         } catch (Exception e) {
             log.error(e.getMessage());
             e.printStackTrace();
         }finally {
             lock.readLock().unlock();
         }
        return null;

    }
    public boolean containsUrl(String key){
        lock.readLock().lock();
        try {
            return registerMap.containsKey(key);
        }catch (Exception e)
        {
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            lock.readLock().unlock();
        }
        return false;
    }
    public boolean containsRemoete(String ip){
        lock.readLock().lock();
        try {
            return remotes.containsKey(ip);
        }catch (Exception e)
        {
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            lock.readLock().unlock();
        }
        return false;
    }

    public  void  remove(String ipAddr){
       lock.writeLock().lock();
       try {
           if(remotes!=null&&remotes.get(ipAddr)!=null) {
               remotes.get(ipAddr).setActive(false);
           }

       }catch (Exception e){
           log.error(e.getMessage());
           e.printStackTrace();
       }finally {
           log.info("{} removed",ipAddr);
           lock.writeLock().unlock();
       }
    }

    public void updateHeartBeat(String ip, ScheduledFuture scheduledFuture){   //更新心跳
        lock.writeLock().lock();
        try {
            if(remotes.get(ip)!=null)
            {
                remotes.get(ip).setScheduledFuture(scheduledFuture);
            }else{
                scheduledFuture.cancel(true);
            }
        }catch (Exception e) {
           log.error("心跳更新异常:{}",e);
        }finally {
            lock.writeLock().unlock();
        }
    }


}