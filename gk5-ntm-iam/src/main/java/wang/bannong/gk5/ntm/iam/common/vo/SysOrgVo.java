package wang.bannong.gk5.ntm.iam.common.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import wang.bannong.gk5.administrative.model.Administrative;

@Data
public class SysOrgVo implements Serializable {
    private static final long serialVersionUID = -5492320928889247474L;

    private String         id;
    private String         pid;
    private String         name;
    private String         number;
    private String         address;
    private Administrative street;
    private Administrative area;
    private Administrative city;
    private Administrative province;
    private Byte           type;
    private Byte           status;
    private Boolean        hasChildren;
    private List<SysOrgVo> children;
    private String         createTime;
}
