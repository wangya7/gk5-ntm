package wang.bannong.gk5.ntm.iam.mgr;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.iam.common.constant.IamConstant;
import wang.bannong.gk5.ntm.iam.common.domain.SysMenu;
import wang.bannong.gk5.ntm.iam.common.domain.SysRoleMenu;
import wang.bannong.gk5.ntm.iam.common.dto.SysAuthDto;
import wang.bannong.gk5.ntm.iam.common.vo.SysMenuVo;
import wang.bannong.gk5.ntm.iam.dao.SysRoleMenuDao;
import wang.bannong.gk5.ntm.iam.dao.SysRoleTopicDao;

@Slf4j
@Component
public class SysAuthMgr {

    @Autowired
    private SysUserMgr      sysUserMgr;
    @Autowired
    private SysRoleMgr      sysRoleMgr;
    @Autowired
    private SysMenuMgr      sysMenuMgr;
    @Autowired
    private SysTopicMgr     sysTopicMgr;
    @Autowired
    private SysRoleMenuDao  masterSysRoleMenuDao;
    @Autowired
    private SysRoleMenuDao  slaveSysRoleMenuDao;
    @Autowired
    private SysRoleTopicDao masterSysRoleTopicDao;
    @Autowired
    private SysRoleTopicDao slaveSysRoleTopicDao;

    public List<SysRoleMenu> queryRoleMenuByRoleId(Long roleId) throws Exception {
        return slaveSysRoleMenuDao.selectByRoleId(roleId);
    }

    public List<SysRoleMenu> queryRoleMenuByRoleIds(List<Long> roleIds) throws Exception {
        return slaveSysRoleMenuDao.selectByRoleIds(roleIds);
    }

    /**
     * 获取一个管理员对应的菜单权限
     */
    public NtmResult queryMyAuthMenu(Long adminId) throws Exception {
        return authMenu(sysUserMgr.queryRoleIds(adminId));
    }

    /**
     * 获取一个角色对应的菜单权限
     */
    public NtmResult queryAuthMenu(Long roleId) throws Exception {
        return authMenu(Collections.singletonList(roleId));
    }

    private  NtmResult authMenu(List<Long> roleIds) throws Exception {
        Set<Long> set = queryRoleMenuByRoleIds(roleIds)
                .parallelStream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toSet());

        List<SysMenu> menus = sysMenuMgr.allMenus();
        List<SysMenuVo> _2 = menus.stream()
                                  .filter(i -> i.getType().equals(IamConstant.MENU_SECOND))
                                  .map(i -> SysMenuVo.of(i, set.contains(i.getId())))
                                  .collect(Collectors.toList());

        Map<String, List<SysMenuVo>> _2_3 = menus.stream()
                                                 .filter(i -> i.getType().equals(IamConstant.MENU_BUTTON))
                                                 .map(i -> SysMenuVo.of(i, set.contains(i.getId())))
                                                 .collect(Collectors.groupingBy(SysMenuVo::getPid));

        if (MapUtils.isNotEmpty(_2_3)) {
            for (SysMenuVo item : _2) {
                List<SysMenuVo> tmp = _2_3.get(item.getId());
                if (CollectionUtils.isNotEmpty(tmp)) {
                    item.setChildren(tmp.stream()
                                        .sorted(Comparator.comparingInt(SysMenuVo::getSort))
                                        .collect(Collectors.toList()));
                }
            }
        }

        List<SysMenuVo> _1 = menus.stream()
                                  .filter(i -> i.getType().equals(IamConstant.MENU_FIRST))
                                  .map(i -> SysMenuVo.of(i))
                                  .sorted(Comparator.comparingInt(SysMenuVo::getSort))
                                  .collect(Collectors.toList());

        Map<String, List<SysMenuVo>> _1_2 = _2.stream()
                                              .sorted(Comparator.comparingInt(SysMenuVo::getSort))
                                              .collect(Collectors.groupingBy(SysMenuVo::getPid));
        for (SysMenuVo item : _1) {
            item.setChildren(_1_2.get(item.getId()));
        }

        return NtmResult.success(_1);
    }

    @Transactional
    public NtmResult modifyAuthMenu(SysAuthDto dto) throws Exception {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, dto.getRoldId());
        masterSysRoleMenuDao.delete(wrapper);
        List<Long> menuIds = dto.getMenuIds();
        if (CollectionUtils.isNotEmpty(menuIds)) {
            List<SysRoleMenu> list = new ArrayList<>();
            for (Long menuId : menuIds) {
                SysRoleMenu item = new SysRoleMenu();
                item.setMenuId(menuId);
                item.setRoleId(dto.getRoldId());
                list.add(item);
            }
            masterSysRoleMenuDao.batchInsert(list);
        }
        return NtmResult.SUCCESS;
    }

}
