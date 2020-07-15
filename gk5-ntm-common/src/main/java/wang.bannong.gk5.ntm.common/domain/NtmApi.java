package wang.bannong.gk5.ntm.common.domain;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class NtmApi implements Serializable {
    private static final long serialVersionUID = -7729924480298703376L;

    private Long    id;
    private Long    dictId;
    @TableField(value = "`unique`")
    private String  unique;
    private String  name;
    private Integer version;
    private Byte    method;
    private String  appid;
    private String  innerInterface;
    private String  innerMethod;
    private Boolean isIa;
    private Boolean isAsync;
    private Integer dailyFlowLimit;
    private String  result;
    private Byte    status;
    private Date    createTime;
    private Date    modifyTime;

}