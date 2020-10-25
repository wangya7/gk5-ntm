package wang.bannong.gk5.ntm.common.constant;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by bn. on 2019/7/4 3:16 PM
 */
@Getter
@ToString
public enum HttpMethod {

    GET((byte) 1),
    POST((byte) 2),
    PUT((byte) 3),
    PATCH((byte) 4),
    DELETE((byte) 5);

    HttpMethod(byte code) {
        this.code = code;
    }

    private byte code;

    public static Map<String, HttpMethod> map = new HashMap<>();

    static {
        for (HttpMethod item : EnumSet.allOf(HttpMethod.class)) {
            map.put(item.name(), item);
        }
    }


    public static HttpMethod of(byte code) {
        for (HttpMethod item : EnumSet.allOf(HttpMethod.class)) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

    public static HttpMethod ofName(String method) {
        for (HttpMethod item : EnumSet.allOf(HttpMethod.class)) {
            if (method.equalsIgnoreCase(item.name())) {
                return item;
            }
        }
        return null;
    }
}
