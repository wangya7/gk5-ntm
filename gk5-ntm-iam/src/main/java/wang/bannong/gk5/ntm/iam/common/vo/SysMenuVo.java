package wang.bannong.gk5.ntm.iam.common.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import wang.bannong.gk5.ntm.iam.common.domain.SysMenu;

@Data
public class SysMenuVo implements Serializable {
    private static final long serialVersionUID = 6753651961733214373L;

    private String          id;
    private String          pid;
    private String          name;
    private String          directory;
    private Byte            type;      // 1-一级菜单 2-二级菜单 3-菜单中的按钮
    private Integer         sort;
    private Boolean         visible = false;
    private Boolean         hasChildren;
    private List<SysMenuVo> children;


    public static SysMenuVo of(SysMenu menu) {
        SysMenuVo vo = new SysMenuVo();
        vo.setId(String.valueOf(menu.getId()));
        vo.setPid(String.valueOf(menu.getPid()));
        vo.setName(menu.getName());
        vo.setType(menu.getType());
        vo.setSort(menu.getSort());
        vo.setDirectory(menu.getDirectory());
        return vo;
    }

    public static SysMenuVo of(SysMenu menu, Boolean visible) {
        SysMenuVo vo = new SysMenuVo();
        vo.setId(String.valueOf(menu.getId()));
        vo.setPid(String.valueOf(menu.getPid()));
        vo.setName(menu.getName());
        vo.setType(menu.getType());
        vo.setSort(menu.getSort());
        vo.setVisible(visible);
        vo.setDirectory(menu.getDirectory());
        return vo;
    }
}
