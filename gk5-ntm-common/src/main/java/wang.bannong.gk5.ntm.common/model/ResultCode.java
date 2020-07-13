package wang.bannong.gk5.ntm.common.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ResultCode {
    success(0, "成功"),
    header_miss_arg(11, "header缺少参数%s"),
    body_miss_arg(12, "body缺少参数%s"),

    arg_format_illegal(13, "参数%s非法"),
    no_bean(13, "没有定义Bean【%s】"),

    appid_not_exist(21, "非法请求"),
    api_not_exist(22, "接口不存在"),
    api_forbid(23, "接口已被禁用"),
    ia_invalid(25, "重新登录"),
    request_too_frequently(26, "操作频繁，稍后再试"),
    sign_invalid(27, "签名无效"),
    app_too_low(29, "APP版本过低，需要更新"),
    request_timeout(31, "请求超时"),

    rpc_invoke_exception(41, "RPC调用异常"),

    api_param_missing(51, "参数【%s】缺失"),
    dto_param_missing(52, "Dto【%s】缺失成员变量【%s】"),

    biz_exception(99, "%s"),
    ;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    private int    code;
    private String msg;

    public static Map<Integer, ResultCode> map = new HashMap<>();

    static {
        for (ResultCode item : EnumSet.allOf(ResultCode.class)) {
            map.put(item.getCode(), item);
        }
    }
}
