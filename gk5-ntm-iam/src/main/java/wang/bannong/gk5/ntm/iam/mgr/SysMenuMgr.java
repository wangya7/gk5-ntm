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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.PaginationResult;
import wang.bannong.gk5.ntm.iam.common.constant.IamConstant;
import wang.bannong.gk5.ntm.iam.common.domain.SysMenu;
import wang.bannong.gk5.ntm.iam.common.dto.SysMenuDto;
import wang.bannong.gk5.ntm.iam.common.vo.SysMenuVo;
import wang.bannong.gk5.ntm.iam.dao.SysMenuDao;

/**
 * 菜单管理
 * type=1 一级菜单
 * type=2 二级菜单
 * type=3 二级菜单下按钮
 */
@Slf4j
@Component
public class SysMenuMgr {

    @Autowired
    private SysMenuDao  masterSysMenuDao;
    @Autowired
    private SysMenuDao  slaveSysMenuDao;
    @Autowired
    private SysUserMgr  sysUserMgr;
    @Autowired
    private SysTopicMgr sysTopicMgr;

    public SysMenu queryById(Long id) throws Exception {
        return slaveSysMenuDao.selectById(id);
    }

    public List<SysMenu> queryByIds(Set<Long> ids) throws Exception {
        return slaveSysMenuDao.selectBatchIds(ids);
    }

    public Map<Long, SysMenu> queryMapByIds(Set<Long> ids) throws Exception {
        List<SysMenu> SysMenus = queryByIds(ids);
        if (CollectionUtils.isNotEmpty(SysMenus)) {
            return SysMenus.stream().collect(Collectors.toMap(SysMenu::getId, i -> i));
        }
        return Collections.EMPTY_MAP;
    }

    public Map<Long, String> queryNameMapByIds(Set<Long> ids) throws Exception {
        List<SysMenu> SysMenus = queryByIds(ids);
        if (CollectionUtils.isNotEmpty(SysMenus)) {
            return SysMenus.stream().collect(Collectors.toMap(SysMenu::getId, i -> i.getName()));
        }
        return Collections.EMPTY_MAP;
    }

    public List<SysMenu> allMenus() throws Exception {
        return masterSysMenuDao.allMenus();
    }


    //************* 接口操作

    public NtmResult menuTree() throws Exception {
        List<SysMenu> menus = allMenus();
        log.info("menus={}", menus);
        List<SysMenuVo> _2 = menus.stream()
                                  .filter(i -> i.getType().equals(IamConstant.MENU_SECOND))
                                  .map(i -> SysMenuVo.of(i))
                                  .collect(Collectors.toList());

        Map<String, List<SysMenuVo>> _2_3 = menus.stream()
                                                 .filter(i -> i.getType().equals(IamConstant.MENU_BUTTON))
                                                 .map(i -> SysMenuVo.of(i))
                                                 .collect(Collectors.groupingBy(SysMenuVo::getPid));

        if (MapUtils.isNotEmpty(_2_3)) {
            for (SysMenuVo item : _2) {
                List<SysMenuVo> tmp = _2_3.get(item.getId());
                if (CollectionUtils.isNotEmpty(tmp)) {
                    item.setHasChildren(true);
                    item.setChildren(tmp.stream()
                                        .sorted(Comparator.comparingInt(SysMenuVo::getSort))
                                        .collect(Collectors.toList()));
                } else {
                    item.setHasChildren(false);
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
            List<SysMenuVo> tmp = _1_2.get(item.getId());
            if (CollectionUtils.isNotEmpty(tmp)) {
                item.setChildren(tmp);
                item.setHasChildren(true);
            } else {
                item.setHasChildren(false);
            }
        }

        return NtmResult.success(_1);
    }


    public NtmResult querySysMenu(SysMenuDto dto) throws Exception {
        int pageNum = 1, pageSize = 1 << 10;
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getPid, dto.getPid());
        List<SysMenu> list = slaveSysMenuDao.selectList(wrapper);

        if (CollectionUtils.isNotEmpty(list)) {
            pageSize = list.size();
            LambdaQueryWrapper<SysMenu> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.in(SysMenu::getPid, list.stream().map(SysMenu::getId).collect(Collectors.toList()));
            Map<Long, List<SysMenu>> listMap = slaveSysMenuDao.selectList(wrapper)
                                                              .stream()
                                                              .collect(Collectors.groupingBy(SysMenu::getPid));
            List<SysMenuVo> vos = new ArrayList<>();
            for (SysMenu record : list) {
                SysMenuVo vo = SysMenuVo.of(record);
                vo.setHasChildren(CollectionUtils.isNotEmpty(listMap.get(record.getId())));
                vos.add(vo);
            }
            return NtmResult.success(PaginationResult.of(pageNum, pageSize, pageNum, pageSize, vos));
        }

        return NtmResult.success(PaginationResult.empty(pageNum, pageSize));
    }

    @Transactional
    public NtmResult addSysMenu(SysMenuDto dto) throws Exception {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        Long pid = dto.getPid();
        wrapper.eq(SysMenu::getPid, pid)
               .eq(SysMenu::getName, dto.getName());

        SysMenu record = masterSysMenuDao.selectOne(wrapper);
        if (record != null) {
            return NtmResult.fail("菜单名称重复，重新操作");
        }

        String directory = dto.getDirectory();
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getPid, pid)
               .eq(SysMenu::getDirectory, directory);
        record = masterSysMenuDao.selectOne(wrapper);
        if (record != null) {
            return NtmResult.fail("菜单路径重复，重新操作");
        }

        Byte type = dto.getType();
        if (pid.compareTo(0L) > 0) {
            SysMenu sysMenu = queryById(pid);
            if (sysMenu.getType().equals(IamConstant.MENU_FIRST) && !type.equals(IamConstant.MENU_SECOND)) {
                return NtmResult.fail("目录只能添加菜单");
            }

            if (sysMenu.getType().equals(IamConstant.MENU_SECOND) && !type.equals(IamConstant.MENU_BUTTON)) {
                return NtmResult.fail("菜单只能按钮");
            }

            if (sysMenu.getType().equals(IamConstant.MENU_BUTTON)) {
                return NtmResult.fail("按钮禁止添加下级");
            }
        }

        record = new SysMenu();
        record.setPid(dto.getPid());
        record.setName(dto.getName());
        record.setDirectory(directory);
        record.setType(type);
        record.setSort(dto.getSort());
        record.setCreateTime(new Date());
        if (masterSysMenuDao.insert(record) > 0) {
            return NtmResult.success(record);
        }
        return NtmResult.fail(NtmConstant.EXP_MSG);
    }


    /**
     * pid，type这些不允许修改
     */
    @Transactional
    public NtmResult modifySysMenu(SysMenuDto dto) throws Exception {
        Long id = dto.getId();
        SysMenu record = masterSysMenuDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("菜单不存在");
        }
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getPid, record.getPid())
               .eq(SysMenu::getName, dto.getName());

        SysMenu sysMenus1 = masterSysMenuDao.selectOne(wrapper);
        if (sysMenus1 != null && !sysMenus1.getId().equals(id)) {
            return NtmResult.fail("菜单名称重复，重新操作");
        }

        String directory = dto.getDirectory();
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getPid, record.getPid())
               .eq(SysMenu::getDirectory, directory);
        SysMenu sysMenus2 = masterSysMenuDao.selectOne(wrapper);
        if (sysMenus2 != null && !sysMenus2.getId().equals(id)) {
            return NtmResult.fail("菜单路径重复，重新操作");
        }

        record.setSort(dto.getSort());
        record.setName(dto.getName());
        record.setDirectory(directory);
        if (masterSysMenuDao.updateById(record) > 0) {
            return NtmResult.success(record);
        }
        return NtmResult.fail(NtmConstant.EXP_MSG);
    }


    @Transactional
    public NtmResult deleteSysMenu(SysMenuDto dto) throws Exception {
        Long id = dto.getId();
        SysMenu record = masterSysMenuDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("菜单不存在");
        }
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getPid, id);

        List<SysMenu> SysMenus = masterSysMenuDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(SysMenus)) {
            return NtmResult.fail("菜单还存在下属，先处理下级菜单");
        }

        if (masterSysMenuDao.deleteById(id) > 0) {
            if (record.getType() > 1) {
                // 删除对应菜单的Topic
                if (sysTopicMgr.deleteByMenuId(record) > 0) {
                    return NtmResult.success(record);
                }
            } else {
                return NtmResult.success(record);
            }
        }
        return NtmResult.SUCCESS;
    }
}
