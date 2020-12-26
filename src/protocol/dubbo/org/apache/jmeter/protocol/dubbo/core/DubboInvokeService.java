package org.apache.jmeter.protocol.dubbo.core;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;

import java.io.IOException;

/**
 * @author AntzUhl
 * @Date 2020/12/26 21:31
 * @Description
 */
public class DubboInvokeService {


    public static ApplicationConfig application = new ApplicationConfig("DubboSample");


    public static String callDubbo(String interfaceName, String ipAddr, String methodName,
                                 String []paramTypes, String []paramValues) {
        // 1.构建通用代理service
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        // 2.直连指定的dubbo服务
        reference.setUrl("dubbo://"+ipAddr+"/"+interfaceName);
        reference.setGeneric(true);
        reference.setTimeout(20000);
        // 3.指定dubbo服务接口
        reference.setInterface(interfaceName);
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);
        // 4.指定具体的调用方法，封装调用参数，这里也可以引入对应dubbo-api构建入参
        Object o = genericService.$invoke(methodName, paramTypes, paramValues);
        String result = "";
        if (o != null) {
            try {
                result = JSON.json(o);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
