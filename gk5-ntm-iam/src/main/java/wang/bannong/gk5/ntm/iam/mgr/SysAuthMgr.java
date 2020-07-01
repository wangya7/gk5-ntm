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
import java.util.HashSet;
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

@Slf4j
@Component
public class SysAuthMgr {

    @Autowired
    private SysUserMgr     sysUserMgr;
    @Autowired
    private SysMenuMgr     sysMenuMgr;
    @Autowired
    private SysRoleMenuDao masterSysRoleMenuDao;
    @Autowired
    private SysRoleMenuDao slaveSysRoleMenuDao;

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
        List<Long> roleIds = sysUserMgr.queryRoleIds(adminId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return NtmResult.fail("管理员信息错误：没有绑定系统角色");
        }
        Set<Long> set = queryRoleMenuByRoleIds(roleIds)
                .parallelStream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toSet());
        
        List<SysMenu> menus = sysMenuMgr.allMenus();
        List<SysMenuVo> _2 = menus.stream()
                                  .filter(i -> i.getType().equals(IamConstant.MENU_SECOND) && set.contains(i.getId()))
                                  .map(i -> SysMenuVo.of(i, true))
                                  .collect(Collectors.toList());

        Map<String, List<SysMenuVo>> _2_3 = menus.stream()
                                                 .filter(i -> i.getType().equals(IamConstant.MENU_BUTTON) && set.contains(i.getId()))
                                                 .map(i -> SysMenuVo.of(i, true))
                                                 .collect(Collectors.groupingBy(SysMenuVo::getPid));
        if (MapUtils.isNotEmpty(_2_3)) {
            for (SysMenuVo item : _2) {
                List<SysMenuVo> tmp = _2_3.get(item.getId());
                if (CollectionUtils.isNotEmpty(tmp)) {
                    item.setChildren(tmp.stream()
                                        .sorted(Comparator.comparingInt(SysMenuVo::getSort))
                                        .collect(Collectors.toList()));
                    item.setHasChildren(true);
                }
            }
        }
        // 收集所有的一级目录
        Set<Long> _2Pid = _2.stream().map(i -> Long.valueOf(i.getPid())).collect(Collectors.toSet());
        log.info("_2Pid={}", _2Pid);

        List<SysMenuVo> _1 = menus.stream()
                                  .filter(i -> i.getType().equals(IamConstant.MENU_FIRST) && _2Pid.contains(i.getId()))
                                  .map(i -> SysMenuVo.of(i))
                                  .sorted(Comparator.comparingInt(SysMenuVo::getSort))
                                  .collect(Collectors.toList());

        Map<String, List<SysMenuVo>> _1_2 = _2.stream()
                                              .sorted(Comparator.comparingInt(SysMenuVo::getSort))
                                              .collect(Collectors.groupingBy(SysMenuVo::getPid));
        for (SysMenuVo item : _1) {
            item.setChildren(_1_2.get(item.getId()));
            item.setHasChildren(true);
        }

        return NtmResult.success(_1);
    }

    /**
     * 获取一个角色对应的菜单权限
     */
    public NtmResult queryAuthMenu(Long roleId) throws Exception {
        Set<Long> set = queryRoleMenuByRoleIds(Collections.singletonList(roleId))
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

        // 一级菜单永远展示
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

        // 这里面要求 必须是二级、三级菜单按钮
        List<Long> menuIds = sysMenuMgr.queryByIds(new HashSet<>(dto.getMenuIds())).stream()
                                       .filter(i -> i.getType() > 1)
                                       .map(SysMenu::getId)
                                       .collect(Collectors.toList());

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
