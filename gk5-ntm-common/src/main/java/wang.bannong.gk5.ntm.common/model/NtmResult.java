package wang.bannong.gk5.ntm.common.model;

import java.io.Serializable;

/**
 * Created by bn. on 2019/7/4 3:03 PM
 */
public class NtmResult implements Serializable {
    private static final long serialVersionUID = 5568567823884100823L;

    private boolean success;
    private String  msg;
    private int     code;
    private Object  data = null;

    public static NtmResult SUCCESS = success();

    private static NtmResult success() {
        return of(ResultCode.success);
    }

    public static NtmResult of(ResultCode resultCode) {
        NtmResult result = new NtmResult();
        result.setSuccess(resultCode == ResultCode.success);
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        return result;
    }

    public static NtmResult of(ResultCode resultCode, String...msgs) {
        NtmResult result = new NtmResult();
        result.setSuccess(resultCode == ResultCode.success);
        result.setCode(resultCode.getCode());
        result.setMsg(String.format(resultCode.getMsg(), msgs));
        return result;
    }

    public static NtmResult fail(String error) {
        NtmResult result = of(ResultCode.biz_exception);
        result.setMsg(error);
        return result;
    }


    public static NtmResult success(Object data) {
        NtmResult result = of(ResultCode.success);
        result.setData(data);
        return result;
    }

    public static NtmResult success(String msg) {
        NtmResult result = of(ResultCode.success);
        result.setMsg(msg);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @SuppressWarnings("unchecked")
    public <T> T  getData() {
        return (T)data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NtmResult{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
