package wang.bannong.gk5.ntm.rpc.rpc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Created by bn. on 2019/8/12 11:20 AM
 */
@Data
@Component
@ConfigurationProperties(prefix = "dubbo")
public class DubboConfigProperties {

    private String  appCfgName;
    private String  registryAddress;
    private int     timeout;

}
