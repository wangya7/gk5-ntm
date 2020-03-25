package wang.bannong.gk5.ntm.iam.common.dto;

import lombok.Data;

@Data
public class SysOrgDto extends SysBaseDto {
    private static final long serialVersionUID = -200846518034738067L;

    private Long    pid;
    private String  name;
    private String  number;
    private String  address;
    private Integer street;
    private Integer area;
    private Integer city;
    private Integer province;
    private Byte    type;
    private Byte    status;
}
