package wang.bannong.gk5.ntm.common.dto;

import java.util.Date;

import lombok.Data;
import wang.bannong.gk5.ntm.common.model.BaseDto;

@Data
public class ApiParamDto extends BaseDto {
    private static final long serialVersionUID = -1285545281939659754L;

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
    private Date    beginTime;
    private Date    endTime;
}
