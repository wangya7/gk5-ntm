package wang.bannong.gk5.ntm.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import wang.bannong.gk5.ntm.iam.common.domain.SysRole;

public interface SysRoleDao extends BaseMapper<SysRole> {

    List<SysRole> allRole();

}
