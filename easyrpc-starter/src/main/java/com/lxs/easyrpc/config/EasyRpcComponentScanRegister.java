package com.lxs.easyrpc.config;

import com.lxs.easyrpc.proxy.Invoke;
import com.lxs.easyrpc.proxy.InvokeHandler;
import com.lxs.easyrpc.proxy.exporter.ExporterFacory;
import com.lxs.easyrpc.proxy.impl.ConsumerInvoke;
import com.lxs.easyrpc.proxy.impl.ProdInvoke;
import com.lxs.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

@Slf4j

public class EasyRpcComponentScanRegister implements InitializingBean {

    @Autowired
    private EasyRpcProperties easyRpcProperties;

    /**
     * 开启包扫描，需要绑定一个包路径
     */
    public void afterPropertiesSet() throws InstantiationException, IllegalAccessException {
        if (easyRpcProperties.getBasePackage() == null)//使用默认扫描路径
        {
            throw new RpcException("默认扫描路径需要配置");
        }

        if(SpringUtil.getBean(EasyRpcProperties.class).getPort()!=0) {
            conmponentScanProd();//配置提供者端口才会启动提供者服务。
        }
        componentScanConsumer();

    }

    private void conmponentScanProd() throws InstantiationException, IllegalAccessException {
        Reflections f = new Reflections(easyRpcProperties.getBasePackage());
        Set<Class<?>> set = f.getTypesAnnotatedWith(RpcProd.class);//获取所有的RpcProd注解
        if (CollectionUtils.isEmpty(set)) {
            return;
        }
        for (Class<?> c : set) {
            if (c.getInterfaces() == null || c.getInterfaces().length > 1) {
                log.error("实体类必须有一个接口且必须一个接口:{}", c);
                throw new RpcException("实体类必须有一个接口且必须一个接口");
            }
            Object ref = SpringUtil.getBean(c.getInterfaces()[0]);//根据接口获取实例
            if (ref == null) {
                ref = c.newInstance();
            }
           Object  v = c.getPackage();
            RpcProd annotation = c.getAnnotation(RpcProd.class);
            //将所有接口都暴露出去]
            Invoke<?> invoke = new ProdInvoke(c.getInterfaces()[0], ref, c.getInterfaces()[0].getMethods(), annotation.timeout());
            ExporterFacory.export(invoke);

        }
    }

    private void componentScanConsumer() {
        Reflections f = new Reflections(easyRpcProperties.getBasePackage(),new FieldAnnotationsScanner());
        Set<Field> fields = f.getFieldsAnnotatedWith(RpcConsumer.class);
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        for (Field field : fields) {
            RpcConsumer anoation = (RpcConsumer) field.getAnnotation(RpcConsumer.class);
            //对字段建立动态代理,通过invoke建立动态代理
            ConsumerInvoke<?> invoke = new ConsumerInvoke<>(field.getType());
            invoke.setTimeout(anoation.timeout());
            InvocationHandler ih=new InvokeHandler(invoke,anoation.retryTime());//代理实例的调用处理程序。

            Object targetInterface=
                         Proxy.newProxyInstance(invoke.getClass().getClassLoader(),new Class[]{invoke.getInterface()},ih);
            String  methodPattern = "set"+field.getName().substring(0, 1).toUpperCase()+ field.getName().substring(1);
          //  targetInterface.hashCode();
                try {
                Class c=  field.getDeclaringClass();
                Method method = field.getDeclaringClass().getMethod(methodPattern,field.getType());
                if(SpringUtil.getBean(c)==null)
                {
                    log.error("实体不存在，请使用spring创建:{}",c);
                    throw new RpcException("实体不存在"+c);
                }
                method.invoke(SpringUtil.getBean(c),targetInterface);
                } catch (Exception e) {
                    e.printStackTrace();
                }


        }
    }
}
