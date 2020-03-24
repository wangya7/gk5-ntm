package wang.bannong.gk5.ntm.common.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class NtmApiParamVo implements Serializable {
    private static final long serialVersionUID = -4018745555823897497L;

    private String  apiParamId;
    private String  pid;
    private String  apiId;
    private String  name;
    private byte    status;
    private byte    type;
    private Boolean isRequired;
    private String  errMsg;
    private String  desp;
    private String  example;
    private String  createTime;
    private String  modifyTime;
}
