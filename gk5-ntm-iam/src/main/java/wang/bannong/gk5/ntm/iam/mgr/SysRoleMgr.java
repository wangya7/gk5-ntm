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
import wang.bannong.gk5.ntm.common.model.PaginationResult;
import wang.bannong.gk5.ntm.iam.common.constant.IamConstant;
import wang.bannong.gk5.ntm.iam.common.domain.SysRole;
import wang.bannong.gk5.ntm.iam.common.dto.SysRoleDto;
import wang.bannong.gk5.ntm.iam.common.vo.SysRoleVo;
import wang.bannong.gk5.ntm.iam.dao.SysRoleDao;
import wang.bannong.gk5.util.DateUtils;
import wang.bannong.gk5.util.domain.Pair;

@Slf4j
@Component
public class SysRoleMgr {


    @Autowired
    private SysRoleDao masterSysRoleDao;
    @Autowired
    private SysRoleDao slaveSysRoleDao;
    @Autowired
    private SysUserMgr sysUserMgr;

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

    //************* 接口操作

    public NtmResult querySysRole(SysRoleDto dto) throws Exception {
        int pageNum = 1, pageSize = 1 << 10;
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getPid, dto.getPid())
               .eq(SysRole::getStatus, IamConstant.EFF);
        List<SysRole> list = slaveSysRoleDao.selectList(wrapper);

        if (CollectionUtils.isNotEmpty(list)) {
            pageSize = list.size();
            LambdaQueryWrapper<SysRole> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(SysRole::getStatus, IamConstant.EFF)
                    .in(SysRole::getPid, list.stream().map(SysRole::getId).collect(Collectors.toList()));
            Map<Long, List<SysRole>> listMap = slaveSysRoleDao.selectList(wrapper)
                                                              .stream()
                                                              .collect(Collectors.groupingBy(SysRole::getPid));
            List<SysRoleVo> vos = new ArrayList<>();
            for (SysRole record : list) {
                SysRoleVo vo = new SysRoleVo();
                vo.setId(String.valueOf(record.getId()));
                vo.setPid(String.valueOf(record.getPid()));
                vo.setName(record.getName());
                vo.setStatus(record.getStatus());
                vo.setHasChildren(CollectionUtils.isNotEmpty(listMap.get(record.getId())));
                vo.setCreateTime(DateUtils.format(record.getCreateTime()));
                vos.add(vo);
            }
            return NtmResult.success(PaginationResult.of(pageNum, pageSize, pageNum, pageSize, vos));
        }

        return NtmResult.success(PaginationResult.empty(pageNum, pageSize));
    }

    public NtmResult querySysRoleSet(SysRoleDto dto) throws Exception {
        int pageNum = 1, pageSize = 1 << 10;
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, IamConstant.EFF);
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
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getPid, dto.getPid())
               .eq(SysRole::getName, dto.getName());

        SysRole record = masterSysRoleDao.selectOne(wrapper);
        if (record != null) {
            return NtmResult.fail("角色名称重复，重新操作");
        }
        record = new SysRole();
        record.setPid(dto.getPid());
        record.setName(dto.getName());
        record.setStatus(IamConstant.EFF);
        record.setCreateTime(new Date());
        record.setModifyTime(record.getCreateTime());
        masterSysRoleDao.insert(record);
        return NtmResult.success(record);
    }


    /**
     * pid不允许修改
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @Transactional
    public NtmResult modifySysRole(SysRoleDto dto) throws Exception {
        Long id = dto.getId();
        SysRole record = masterSysRoleDao.selectById(id);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getPid, record.getPid())
               .eq(SysRole::getName, dto.getName());

        SysRole SysRoles = masterSysRoleDao.selectOne(wrapper);
        if (SysRoles != null && !SysRoles.getId().equals(id)) {
            return NtmResult.fail("角色名称重复，重新操作");
        }

        record.setName(dto.getName());
        record.setStatus(dto.getStatus());
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
        wrapper.eq(SysRole::getPid, id)
               .eq(SysRole::getStatus, IamConstant.EFF);

        List<SysRole> SysRoles = masterSysRoleDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(SysRoles)) {
            return NtmResult.fail("角色还存在下属，先处理下级角色");
        }

        record.setStatus(IamConstant.EXP);
        record.setModifyTime(new Date());
        masterSysRoleDao.updateById(record);
        return NtmResult.success(record);
    }

}
