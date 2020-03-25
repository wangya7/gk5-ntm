package wang.bannong.gk5.ntm.iam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.iam.common.dto.SysAuthDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysMenuDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysOrgDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysRoleDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysTopicDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysUserDto;
import wang.bannong.gk5.ntm.iam.mgr.SysAuthMgr;
import wang.bannong.gk5.ntm.iam.mgr.SysMenuMgr;
import wang.bannong.gk5.ntm.iam.mgr.SysOrgMgr;
import wang.bannong.gk5.ntm.iam.mgr.SysRoleMgr;
import wang.bannong.gk5.ntm.iam.mgr.SysTopicMgr;
import wang.bannong.gk5.ntm.iam.mgr.SysUserMgr;
import wang.bannong.gk5.ntm.iam.service.IamService;

@Slf4j
@Service("iamService")
public class IamServiceDefault implements IamService {

    @Autowired
    private SysOrgMgr   sysOrgMgr;
    @Autowired
    private SysMenuMgr  sysMenuMgr;
    @Autowired
    private SysTopicMgr sysTopicMgr;
    @Autowired
    private SysRoleMgr  sysRoleMgr;
    @Autowired
    private SysUserMgr  sysUserMgr;
    @Autowired
    private SysAuthMgr  sysAuthMgr;

    @Override
    public NtmResult accessTopic(Long sysUserId, String topicUnique) {
        return NtmResult.SUCCESS;
    }

    /***** 组织管理 *****/

    @Override
    public NtmResult queryOrg(SysOrgDto dto) {
        try {
            return sysOrgMgr.querySysOrg(dto);
        } catch (Exception e) {
            log.error("查询组织报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult addOrg(SysOrgDto dto) {
        try {
            return sysOrgMgr.addSysOrg(dto);
        } catch (Exception e) {
            log.error("新增组织报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult modifyOrg(SysOrgDto dto) {
        try {
            return sysOrgMgr.modifySysOrg(dto);
        } catch (Exception e) {
            log.error("修改组织报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult deleteOrg(SysOrgDto dto) {
        try {
            return sysOrgMgr.deleteSysOrg(dto);
        } catch (Exception e) {
            log.error("删除组织报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    /***** 菜单管理 *****/

    @Override
    public NtmResult queryMenu(SysMenuDto dto) {
        try {
            return sysMenuMgr.querySysMenu(dto);
        } catch (Exception e) {
            log.error("查询菜单报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult addMenu(SysMenuDto dto) {
        try {
            return sysMenuMgr.addSysMenu(dto);
        } catch (Exception e) {
            log.error("新增菜单报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult modifyMenu(SysMenuDto dto) {
        try {
            return sysMenuMgr.modifySysMenu(dto);
        } catch (Exception e) {
            log.error("修改菜单报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult deleteMenu(SysMenuDto dto) {
        try {
            return sysMenuMgr.deleteSysMenu(dto);
        } catch (Exception e) {
            log.error("删除菜单报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    /***** Topic管理 *****/

    @Override
    public NtmResult queryTopic(SysTopicDto dto) {
        try {
            return sysTopicMgr.querySysTopic(dto);
        } catch (Exception e) {
            log.error("查询topic报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult addTopic(SysTopicDto dto) {
        try {
            return sysTopicMgr.querySysTopic(dto);
        } catch (Exception e) {
            log.error("新增topic报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult modifyTopic(SysTopicDto dto) {
        try {
            return sysTopicMgr.querySysTopic(dto);
        } catch (Exception e) {
            log.error("修改topic报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult deleteTopic(SysTopicDto dto) {
        try {
            return sysTopicMgr.querySysTopic(dto);
        } catch (Exception e) {
            log.error("删除topic报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    /***** 角色管理 *****/

    @Override
    public NtmResult queryRole(SysRoleDto dto) {
        try {
            return sysRoleMgr.querySysRole(dto);
        } catch (Exception e) {
            log.error("查询角色报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult addRole(SysRoleDto dto) {
        try {
            return sysRoleMgr.addSysRole(dto);
        } catch (Exception e) {
            log.error("新增角色报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult modifyRole(SysRoleDto dto) {
        try {
            return sysRoleMgr.modifySysRole(dto);
        } catch (Exception e) {
            log.error("修改角色报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult deleteRole(SysRoleDto dto) {
        try {
            return sysRoleMgr.deleteSysRole(dto);
        } catch (Exception e) {
            log.error("删除角色报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    /***** 人员管理 *****/

    @Override
    public NtmResult queryUser(SysUserDto dto) {
        try {
            return sysUserMgr.querySysUser(dto);
        } catch (Exception e) {
            log.error("查询管理员报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult addUser(SysUserDto dto) {
        try {
            return sysUserMgr.addSysUser(dto);
        } catch (Exception e) {
            log.error("新增管理员报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult modifyUser(SysUserDto dto) {
        try {
            return sysUserMgr.modifySysUser(dto);
        } catch (Exception e) {
            log.error("修改管理员报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult deleteUser(SysUserDto dto) {
        try {
            return sysUserMgr.deleteSysUser(dto);
        } catch (Exception e) {
            log.error("删除管理员报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult modifyPasswd(SysUserDto dto) {
        return null;
    }

    @Override
    public NtmResult forgetPasswd(SysUserDto dto) {
        return null;
    }

    /***** 权限管理 *****/

    @Override
    public NtmResult queryAuth(SysAuthDto dto) {
        try {
            return sysAuthMgr.queryAuthMenu(dto);
        } catch (Exception e) {
            log.error("根据角色查询菜单权限报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public NtmResult modifyAuth(SysAuthDto dto) {
        try {
            return sysAuthMgr.modifyAuthMenu(dto);
        } catch (Exception e) {
            log.error("修改角色的菜单权限报错，dto={}，异常信息：", dto, e);
            throw new RuntimeException(e);
        }
    }
}
