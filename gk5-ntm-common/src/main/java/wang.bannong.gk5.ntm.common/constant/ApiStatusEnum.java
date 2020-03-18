package wang.bannong.gk5.ntm.common.constant;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ApiStatusEnum {
    common((byte) 1, "正常"),
    forbid((byte) 3, "禁用"),
    delete((byte) 9, "删除"),
    ;

    private byte   code;
    private String value;

    ApiStatusEnum(byte code, String value) {
        this.code = code;
        this.value = value;
    }

    private static Map<Byte, ApiStatusEnum> map = new HashMap<>();

    static {
        for (ApiStatusEnum item : EnumSet.allOf(ApiStatusEnum.class)) {
            map.put(item.getCode(), item);
        }
    }

    public static ApiStatusEnum of(byte code) {
        return map.get(code);
    }
}
