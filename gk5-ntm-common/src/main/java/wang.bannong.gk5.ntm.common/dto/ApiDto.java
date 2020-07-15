package wang.bannong.gk5.ntm.common.dto;

import java.util.Date;

import lombok.Data;
import lombok.ToString;
import wang.bannong.gk5.ntm.common.model.BaseDto;

@Data
@ToString(callSuper = true)
public class ApiDto extends BaseDto {

    private static final long serialVersionUID = 6256707767503617544L;

    private Long    apiId;
    private Long    dictId;
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
    private Date    beginTime;
    private Date    endTime;

}
