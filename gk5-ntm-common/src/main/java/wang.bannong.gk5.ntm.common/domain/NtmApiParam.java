package wang.bannong.gk5.ntm.common.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class NtmApiParam implements Serializable {
    private static final long serialVersionUID = -7755179769209111390L;

    private Long    id;
    private Long    pid;
    private Long    apiId;
    private String  name;
    private Byte    status;
    private Byte    type;
    private Boolean isRequired;
    private String  errMsg;
    private String  desp;
    private String  example;
    private Date    createTime;
    private Date    modifyTime;

}