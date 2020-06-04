package wang.bannong.gk5.ntm.rpc.rpc;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import wang.bannong.gk5.ntm.rpc.rpc.config.DubboConfigProperties;
import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.util.SpringBeanUtils;

/**
 * Created by bn. on 2019/10/17 7:48 PM
 */
@Slf4j
public final class ReferenceConfigBuilder {

    @Autowired
    private static DubboConfigProperties properties;

    private static ApplicationConfig applicationConfig;

    public static Map<String, ReferenceConfig<GenericService>>  referenceConfigSynchMap  = new HashMap<>();
    public static Map<String, ReferenceConfig<GenericService>>  referenceConfigAsynchMap = new HashMap<>();


    static {
        if (properties == null) {
            properties = SpringBeanUtils.getBean(DubboConfigProperties.class);
        }
        log.info("init rpc properties[{}]", properties);
        applicationConfig = new ApplicationConfig();
        applicationConfig.setName(properties.getAppCfgName());
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(properties.getRegistryAddress());
        applicationConfig.setRegistry(registryConfig);
    }


    public static ReferenceConfig<GenericService> getSynch(String serviceInterface) {
        ReferenceConfig<GenericService> referenceConfig = referenceConfigSynchMap.get(serviceInterface);
        if (referenceConfig == null) {
            referenceConfig = new ReferenceConfig<>();
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setGeneric(true);
            referenceConfig.setTimeout(properties.getTimeout());
            referenceConfig.setInterface(serviceInterface);
            referenceConfig.setAsync(false);
        }
        return referenceConfig;
    }

    public static ReferenceConfig<GenericService> getAsynch(String serviceInterface) {
        ReferenceConfig<GenericService> referenceConfig = referenceConfigAsynchMap.get(serviceInterface);
        if (referenceConfig == null) {
            referenceConfig = new ReferenceConfig<>();
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setGeneric(true);
            referenceConfig.setTimeout(properties.getTimeout());
            referenceConfig.setInterface(serviceInterface);
            referenceConfig.setAsync(true);
        }
        return referenceConfig;
    }

}
