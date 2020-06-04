package wang.bannong.gk5.ntm.standalone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * Created by wang.bannong on 2018/5/13 下午10:05
 */
@Data
@Component
@ConfigurationProperties(prefix = "ntm")
public class ApiYmlConfig {
    private Map<String, Map<String, String>> apiMap = new HashMap<>();
}
