package wang.bannong.gk5.ntm.iam.common.dto;

import lombok.Data;

@Data
public class SysTopicDto extends SysBaseDto {
    private static final long serialVersionUID = -317433604223292281L;
    private String name;
    private String unique;
    private Long   menu2Id;
    private Long   menu3Id;
    private Byte   status;
}