package org.apache.jmeter.protocol.dubbo.sampler;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.protocol.dubbo.core.DubboInvokeService;
import org.apache.jmeter.protocol.dubbo.core.JsonFormatUtil;
import org.apache.jmeter.protocol.dubbo.gui.DubboSamplerGUI;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class DubboSampler extends AbstractSampler{

    private static final Logger logger = LoggerFactory.getLogger(DubboSamplerGUI.class);

    private static final long serialVersionUID = 240L;

    public static final String zookeeper = "zookeeper.text";
    public static final String application = "application.text";
    public static final String service = "service.text";
    public static final String method = "method.text";
    public static final String ip = "ip.text";
    public static final String paramTypes = "paramTypes.text";
    public static final String paramValues = "paramValues.value";

    private static AtomicInteger classCount = new AtomicInteger(0); // keep track of classes created


    private String getTitle() {
        return this.getName();
    }

    public String getZookeeper() {
        return getPropertyAsString(zookeeper);
    }

    public String getParamTypes() {
        return getPropertyAsString(paramTypes);
    }

    public String getParamValues() {
        return getPropertyAsString(paramValues);
    }

    public String getApplication() {
        return getPropertyAsString(application);
    }

    public String getService() {
        return getPropertyAsString(service);
    }

    public String getMethod() {
        return getPropertyAsString(method);
    }

    public String getIp() {
        return getPropertyAsString(ip);
    }


    public DubboSampler() {
        setName("Dubbo Sampler");
        classCount.incrementAndGet();
        trace("DubboSampler()");
    }

    private void trace(String s) {
        String tl = getTitle();
        String tn = Thread.currentThread().getName();
        String th = this.toString();
        logger.debug(tn + " (" + classCount.get() + ") " + tl + " " + s + " " + th);
    }

    @Override
    public SampleResult sample(Entry arg0) {
        trace("sample()");
        SampleResult res = new SampleResult();
        // 处理
        String application = getApplication();
        String serviceName = getService();
        String ip = getIp();
        String method = getMethod();
        String paramType = getParamTypes();
        String paramValue = getParamValues();
        String[] paramTypes = paramType.split(",");
        String[] paramValues = paramValue.split(",");
        String result = "";
        res.sampleStart();
        try {
            result = DubboInvokeService.callDubbo(serviceName, ip, method, paramTypes, paramValues);
        } catch (Exception e) {
            res.setSuccessful(false);
            res.setResponseCode("500");
            res.setResponseMessage(e.toString());
        }
        res.sampleEnd();
        res.setSamplerData(application+"-"+serviceName+"-"+method+"-"+ip+"-"+paramValue);
        if (StringUtils.isNotBlank(result)) {
            String jsonResult = JsonFormatUtil.formatJson(result);
            res.setDataType(SampleResult.TEXT);
            res.setResponseData(jsonResult);
            res.setResponseMessage("OK");
        } else if (result == null) {
            res.setResponseMessage("Result is Null");
        }
        res.setResponseCodeOK();
        return res;
    }

}