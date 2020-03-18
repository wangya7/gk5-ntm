package wang.bannong.gk5.ntm.common.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class NtmApiVo implements Serializable {
    private static final long serialVersionUID = 3366245839146063293L;

    private String  id;
    private String  unique;
    private String  name;
    private int     version;
    private byte    method;
    private String  appid;
    private String  innerInterface;
    private String  innerMethod;
    private Boolean isIa;
    private Boolean isAsync;
    private int     dailyFlowLimit;
    private String  result;
    private byte    status;
    private String  createTime;
    private String  modifyTime;

}
