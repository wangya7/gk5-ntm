package wang.bannong.gk5.ntm.iam.service;

import java.util.List;

import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.iam.common.domain.SysUser;
import wang.bannong.gk5.ntm.iam.common.dto.SysAuthDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysMenuDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysOrgDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysRoleDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysTopicDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysUserDto;

public interface IamClient {

    /**
     * 判断管理员是否可以访问该Topic
     *
     * @param sysUserId   管理员
     * @param topicUnique Topic标识
     * @return 结果
     */
    NtmResult accessTopic(Long sysUserId, String topicUnique);

    /***** 组织管理 *****/
    NtmResult queryOrg(SysOrgDto dto);
    NtmResult addOrg(SysOrgDto dto);
    NtmResult modifyOrg(SysOrgDto dto);
    NtmResult deleteOrg(SysOrgDto dto);
    NtmResult queryAllOrgSet(SysOrgDto dto);

    /***** 菜单管理 *****/
    NtmResult allMenuList(SysMenuDto dto);
    NtmResult queryMenu(SysMenuDto dto);
    NtmResult addMenu(SysMenuDto dto);
    NtmResult modifyMenu(SysMenuDto dto);
    NtmResult deleteMenu(SysMenuDto dto);

    /***** Topic管理 *****/
    NtmResult queryTopic(SysTopicDto dto);
    NtmResult addTopic(SysTopicDto dto);
    NtmResult modifyTopic(SysTopicDto dto);
    NtmResult deleteTopic(SysTopicDto dto);

    /***** 角色管理 *****/
    NtmResult queryRole(SysRoleDto dto);
    NtmResult addRole(SysRoleDto dto);
    NtmResult modifyRole(SysRoleDto dto);
    NtmResult deleteRole(SysRoleDto dto);
    NtmResult queryAllRoleSet(SysRoleDto dto);

    /***** 人员管理 *****/
    NtmResult login(SysUserDto dto);
    NtmResult queryUser(SysUserDto dto);
    NtmResult addUser(SysUserDto dto);
    NtmResult modifyUser(SysUserDto dto);
    NtmResult deleteUser(SysUserDto dto);
    NtmResult modifyPasswd(SysUserDto dto);
    NtmResult forgetPasswd(SysUserDto dto);

    List<SysUser> queryAdminByRoleName(String roleName);

    /***** 权限管理 *****/
    NtmResult queryMyAuth(long adminId);
    NtmResult queryAuth(Long roleId);
    NtmResult modifyAuth(SysAuthDto dto);
}
