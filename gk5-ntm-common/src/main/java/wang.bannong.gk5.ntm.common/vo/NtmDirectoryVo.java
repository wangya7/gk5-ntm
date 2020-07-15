package wang.bannong.gk5.ntm.common.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class NtmDirectoryVo implements Serializable {
    private static final long serialVersionUID = 5235641202667618838L;

    private String  id;
    private String  pid;
    private String  name;
    private Integer sort;
    private Boolean hasChildren;
}
