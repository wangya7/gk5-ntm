package wang.bannong.gk5.ntm.common.dto;

import lombok.Data;
import lombok.ToString;
import wang.bannong.gk5.ntm.common.model.BaseDto;

@Data
@ToString(callSuper = true)
public class NtmDirectoryDto extends BaseDto {
    private static final long serialVersionUID = -4217064416098724250L;

    private Long    id;
    private Long    pid;
    private String  name;
    private Integer sort;
}
