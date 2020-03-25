package wang.bannong.gk5.ntm.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import wang.bannong.gk5.ntm.iam.common.domain.SysUserRole;

public interface SysUserRoleDao extends BaseMapper<SysUserRole> {
    int batchInsert(List<SysUserRole> list);
}
