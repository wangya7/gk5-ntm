package wang.bannong.gk5.ntm.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import wang.bannong.gk5.ntm.iam.common.domain.SysRoleMenu;

public interface SysRoleMenuDao extends BaseMapper<SysRoleMenu> {
    int batchInsert(List<SysRoleMenu> list);

    List<SysRoleMenu> selectByRoleId(Long roleId);

    List<SysRoleMenu> selectByRoleIds(List<Long> roleIds);
}
