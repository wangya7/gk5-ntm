package wang.bannong.gk5.ntm.iam.service;

import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.iam.common.dto.SysAuthDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysMenuDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysOrgDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysRoleDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysTopicDto;
import wang.bannong.gk5.ntm.iam.common.dto.SysUserDto;

public interface IamService {

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

    /***** 菜单管理 *****/
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

    /***** 人员管理 *****/
    NtmResult queryUser(SysUserDto dto);
    NtmResult addUser(SysUserDto dto);
    NtmResult modifyUser(SysUserDto dto);
    NtmResult deleteUser(SysUserDto dto);
    NtmResult modifyPasswd(SysUserDto dto);
    NtmResult forgetPasswd(SysUserDto dto);

    /***** 权限管理 *****/
    NtmResult queryAuth(SysAuthDto dto);
    NtmResult modifyAuth(SysAuthDto dto);
}
