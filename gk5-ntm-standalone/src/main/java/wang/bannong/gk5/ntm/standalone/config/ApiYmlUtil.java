package wang.bannong.gk5.ntm.standalone.config;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;

import lombok.Getter;
import wang.bannong.gk5.ntm.common.constant.HttpMethod;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.util.Constant;

/**
 * Created by wang.bannong on 2018/5/14 下午7:01
 */
@Component
@EnableConfigurationProperties(ApiYmlConfig.class)
public class ApiYmlUtil {

    final static String ERROR_MSG = "api[%s] config error!";

    @Autowired
    private        ApiYmlConfig               apiYmlConfig;
    @Getter
    private static Map<String, NtmApi>        allApiInfo    = new HashMap<>();
    @Getter
    private static Set<String>                allApiInfoSet = new HashSet<>();
    @Getter
    private static Map<String, NtmApiChannel> apiChannelMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Map<String, Map<String, String>> map = apiYmlConfig.getApiMap();
        if (MapUtils.isNotEmpty(map)) {
            Set<Map.Entry<String, Map<String, String>>> outSets = map.entrySet();
            for (Map.Entry<String, Map<String, String>> outEntry : outSets) {
                Map<String, String> innner = outEntry.getValue();
                String channel = outEntry.getKey();
                NtmApiChannel gateApiChannel = NtmApiChannel.of(channel);
                if (MapUtils.isNotEmpty(innner)) {
                    Set<Map.Entry<String, String>> inSets = innner.entrySet();
                    for (Map.Entry<String, String> innerEntry : inSets) {
                        String apiKey = innerEntry.getKey();
                        String apiValue = innerEntry.getValue();
                        Objects.requireNonNull(apiKey, String.format(ERROR_MSG, apiKey));
                        Objects.requireNonNull(apiValue, String.format(ERROR_MSG, apiKey));

                        String[] arr1 = apiKey.split(Constant.UNDERLINE);
                        if (arr1.length != 2)
                            throw new RuntimeException(String.format(ERROR_MSG, apiKey));

                        String[] arr2 = apiValue.split(Constant.COMMA);
                        if (arr2.length != 3)
                            throw new RuntimeException(String.format(ERROR_MSG, apiKey));

                        NtmApi info = new NtmApi();
                        info.setId(0L);
                        String unique = arr1[0].trim();
                        info.setUnique(unique);
                        info.setName(unique);
                        info.setVersion(converApiVersion(arr1[1].trim()));
                        info.setAppid("");
                        info.setInnerInterface(arr2[0].trim());
                        info.setIsIa(Integer.parseInt(arr2[1]) > 0);
                        info.setIsAsync(false);

                        HttpMethod httpMethod = HttpMethod.ofName(arr2[2].trim());
                        if (httpMethod == null) {
                            throw new RuntimeException(String.format(ERROR_MSG, apiKey));
                        }
                        info.setMethod(httpMethod.getCode());

                        allApiInfo.put(apiKey, info);
                        allApiInfoSet.add(apiKey);
                        apiChannelMap.put(apiKey, gateApiChannel);
                    }
                }
            }
        }
    }

    public static NtmApi getApiInfo(final String apiName, final int version) {
        Objects.requireNonNull(apiName, "api name cannot be null");
        return allApiInfo.get(apiName + Constant.UNDERLINE + version);
    }

    public static NtmApiChannel getApiChannel(final String apiName, final int version) {
        return apiChannelMap.get(apiName + Constant.UNDERLINE + version);
    }


    public static boolean isContain(final String apiName, final int version) {
        return CollectionUtils.isNotEmpty(allApiInfoSet) && allApiInfoSet.contains(apiName + Constant.UNDERLINE + version);
    }


    public static int converApiVersion(String version) {
        int pos = -1;
        return (pos = version.indexOf(".")) < 0 ?
                NumberUtils.toInt(version) : NumberUtils.toInt(version.substring(0, pos));
    }

}
