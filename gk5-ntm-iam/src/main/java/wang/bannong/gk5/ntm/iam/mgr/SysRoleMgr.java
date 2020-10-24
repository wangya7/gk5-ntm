package wang.bannong.gk5.ntm.iam.mgr;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.iam.common.domain.SysRole;
import wang.bannong.gk5.ntm.iam.common.domain.SysUserRole;
import wang.bannong.gk5.ntm.iam.common.dto.SysRoleDto;
import wang.bannong.gk5.ntm.iam.common.vo.SysRoleVo;
import wang.bannong.gk5.ntm.iam.dao.SysRoleDao;
import wang.bannong.gk5.util.DateUtils;
import wang.bannong.gk5.util.model.Pair;

@Slf4j
@Component
public class SysRoleMgr {

    @Autowired
    private SysRoleDao masterSysRoleDao;
    @Autowired
    private SysRoleDao slaveSysRoleDao;
    @Autowired
    private SysUserMgr sysUserMgr;

    public SysRole queryByName(String name) throws Exception {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getName, name);
        return slaveSysRoleDao.selectOne(wrapper);
    }

    public SysRole queryById(Long id) throws Exception {
        return slaveSysRoleDao.selectById(id);
    }

    public List<SysRole> queryByIds(Set<Long> ids) throws Exception {
        return slaveSysRoleDao.selectBatchIds(ids);
    }

    public Map<Long, SysRole> queryMapByIds(Set<Long> ids) throws Exception {
        List<SysRole> SysRoles = queryByIds(ids);
        if (CollectionUtils.isNotEmpty(SysRoles)) {
            return SysRoles.stream().collect(Collectors.toMap(SysRole::getId, i -> i));
        }
        return Collections.EMPTY_MAP;
    }


    public Map<Long, String> queryNameMapByIds(Set<Long> ids) throws Exception {
        List<SysRole> SysRoles = queryByIds(ids);
        if (CollectionUtils.isNotEmpty(SysRoles)) {
            return SysRoles.stream().collect(Collectors.toMap(SysRole::getId, i -> i.getName()));
        }
        return Collections.EMPTY_MAP;
    }

    public List<SysRole> allRole() throws Exception {
        return masterSysRoleDao.allRole();
    }

    //************* 接口操作

    public NtmResult querySysRole(SysRoleDto dto) throws Exception {
        List<SysRole> list = allRole();
        if (CollectionUtils.isNotEmpty(list)) {
            Map<Long, List<SysRole>> listMap = list.stream()
                                                   .collect(Collectors.groupingBy(SysRole::getPid));


            List<SysRole> sysOrgsFirst = list.stream()
                                             .filter(i -> i.getPid().compareTo(0L) == 0)
                                             .collect(Collectors.toList());

            List<SysRoleVo> vos = new ArrayList<>();
            for (SysRole record : sysOrgsFirst) {
                vos.add(buildLoop(record, listMap));
            }
            return NtmResult.success(vos);
        }

        return NtmResult.success(Collections.EMPTY_LIST);
    }

    private SysRoleVo buildLoop(SysRole record, Map<Long, List<SysRole>> listMap) {
        SysRoleVo vo = new SysRoleVo();
        vo.setId(String.valueOf(record.getId()));
        vo.setPid(String.valueOf(record.getPid()));
        vo.setName(record.getName());
        vo.setCreateTime(DateUtils.format(record.getCreateTime()));

        List<SysRole> children = listMap.get(record.getId());
        if (CollectionUtils.isNotEmpty(children)) {
            List<SysRoleVo> childrenVos = new ArrayList<>();
            for (SysRole sysOrgInner : children) {
                childrenVos.add(buildLoop(sysOrgInner, listMap));
            }
            vo.setChildren(childrenVos);
            vo.setHasChildren(true);
        } else {
            vo.setChildren(null);
            vo.setHasChildren(false);
        }
        return vo;
    }

    public NtmResult querySysRoleSet(SysRoleDto dto) throws Exception {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (dto.getName() != null) {
            wrapper.like(SysRole::getName, dto.getName());
        }
        List<SysRole> list = slaveSysRoleDao.selectList(wrapper);
        List<Pair<String, String>> pairs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysRole sysRole : list) {
                pairs.add(Pair.of(String.valueOf(sysRole.getId()), sysRole.getName()));
            }

        }
        return NtmResult.success(pairs);
    }

    @Transactional
    public NtmResult addSysRole(SysRoleDto dto) throws Exception {
        SysRole record = queryByName(dto.getName());
        if (record != null) {
            return NtmResult.fail("角色名称重复，重新操作");
        }
        record = new SysRole();
        record.setPid(dto.getPid());
        record.setName(dto.getName());
        record.setCreateTime(new Date());
        record.setModifyTime(record.getCreateTime());
        masterSysRoleDao.insert(record);
        return NtmResult.success(record);
    }


    /**
     * pid不允许修改
     */
    @Transactional
    public NtmResult modifySysRole(SysRoleDto dto) throws Exception {
        Long id = dto.getId();
        SysRole record = masterSysRoleDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("角色不存在");
        }

        SysRole SysRoles = queryByName(dto.getName());
        if (SysRoles != null && !SysRoles.getId().equals(id)) {
            return NtmResult.fail("角色名称重复，重新操作");
        }
        record.setName(dto.getName());
        record.setModifyTime(new Date());
        masterSysRoleDao.updateById(record);
        return NtmResult.success(record);
    }


    @Transactional
    public NtmResult deleteSysRole(SysRoleDto dto) throws Exception {
        Long id = dto.getId();
        SysRole record = masterSysRoleDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("角色不存在");
        }
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getPid, id);

        List<SysRole> SysRoles = masterSysRoleDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(SysRoles)) {
            return NtmResult.fail("角色还存在下属，先处理下级角色");
        }

        List<SysUserRole> sysUserRoles = sysUserMgr.querySysUserRoleByRoleId(id);
        if (CollectionUtils.isNotEmpty(sysUserRoles)) {
            return NtmResult.fail("角色还存在管理员，请先处理");
        }
        masterSysRoleDao.deleteById(id);
        return NtmResult.success(record);
    }

}
