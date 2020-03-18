package wang.bannong.gk5.ntm.common.model;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import wang.bannong.gk5.ntm.common.constant.HttpMethod;

@Builder
@Getter
@ToString
public class NtmRequest implements Serializable {
    private static final long serialVersionUID = 3732969215062658029L;
    /** Header */
    private final String     api;
    private       String     ia;
    private       String     oia;    // 平滑更新ia，需要保存旧值
    private final int        v;
    private final long       ts;
    private final String     lng;
    private final String     lat;
    private final Ttid       ttid;
    private final String     appid;

    /** Body */
    private final String              channel;
    private final String              sign;
    private final Map<String, String> data;

    private final NtmCookie  cookie;
    private final HttpMethod method;
    private final String     ip;


    @Data
    @AllArgsConstructor
    public static class Ttid implements Serializable {
        private static final long serialVersionUID = 1512997979222379878L;

        // 厂牌机型@平台@设备系统信息@大版本号.小版本号.bugfix版本
        // HUAWEI P20@Android@Android 7.0@1.0.0
        // Apple SE@IOS@ios11.0.0@1.0.0
        private String brand;
        private String platform;
        private String system;
        private String version;
    }

    public void setIa(String iaNew) {
        this.ia = iaNew;
    }
}
