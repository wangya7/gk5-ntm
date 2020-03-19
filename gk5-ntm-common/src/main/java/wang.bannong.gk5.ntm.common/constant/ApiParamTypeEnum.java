package wang.bannong.gk5.ntm.common.constant;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ApiParamTypeEnum {
    _Byte((byte) 2, "Byte"),
    _Integer((byte) 4, "Integer"),
    _Long((byte) 6, "Long"),
    _Boolean((byte) 7, "Boolean"),
    _Double((byte) 8, "Double"),
    _BigDecimal((byte) 9, "BigDecimal"),
    _String((byte) 11, "String"),
    _JSONObject((byte) 12, "Object"),
    _JSONArray((byte) 13, "Array"),

    ;
    private byte   code;
    private String value;


    private static Map<Byte, ApiParamTypeEnum> map = new HashMap<>();

    static {
        for (ApiParamTypeEnum item : EnumSet.allOf(ApiParamTypeEnum.class)) {
            map.put(item.getCode(), item);
        }
    }

    public static ApiParamTypeEnum of(byte code) {
        return map.get(code);
    }

    ApiParamTypeEnum(byte code, String value) {
        this.code = code;
        this.value = value;
    }

}
