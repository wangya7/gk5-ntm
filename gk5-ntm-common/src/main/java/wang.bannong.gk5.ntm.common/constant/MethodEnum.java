package wang.bannong.gk5.ntm.common.constant;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum MethodEnum {

    GET((byte) 1, "GET"), 
    POST((byte) 2, "POST"), 
    PUT((byte) 3, "PUT"), 
    PATCH((byte) 4,"PATCH"), 
    DELETE((byte) 5, "DELETE");

    private Byte   code;
    private String value;

    MethodEnum(Byte code, String value) {
        this.code = code;
        this.value = value;
    }

    public static final Map<Byte, MethodEnum> methodEnumMap = new HashMap<>();

    static {
        for (MethodEnum gen : EnumSet.allOf(MethodEnum.class)) {
            methodEnumMap.put(gen.getCode(), gen);
        }
    }
}
