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

import hz.qxbn.taoism.administrative.AdministrativeUtils;
import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.PaginationResult;
import wang.bannong.gk5.ntm.iam.common.domain.SysOrg;
import wang.bannong.gk5.ntm.iam.common.dto.SysOrgDto;
import wang.bannong.gk5.ntm.iam.common.vo.SysOrgVo;
import wang.bannong.gk5.ntm.iam.dao.SysOrgDao;
import wang.bannong.gk5.util.DateUtils;

@Slf4j
@Component
public class SysOrgMgr {

    @Autowired
    private SysOrgDao  masterSysOrgDao;
    @Autowired
    private SysOrgDao  slaveSysOrgDao;
    @Autowired
    private SysUserMgr sysUserMgr;

    public SysOrg queryById(Long id) throws Exception {
        return slaveSysOrgDao.selectById(id);
    }

    public List<SysOrg> queryByIds(Set<Long> ids) throws Exception {
        return slaveSysOrgDao.selectBatchIds(ids);
    }

    public Map<Long, SysOrg> queryMapByIds(Set<Long> ids) throws Exception {
        List<SysOrg> sysOrgs = queryByIds(ids);
        if (CollectionUtils.isNotEmpty(sysOrgs)) {
            return sysOrgs.stream().collect(Collectors.toMap(SysOrg::getId, i -> i));
        }
        return Collections.EMPTY_MAP;
    }

    public Map<Long, String> queryNameMapByIds(Set<Long> ids) throws Exception {
        List<SysOrg> sysOrgs = queryByIds(ids);
        if (CollectionUtils.isNotEmpty(sysOrgs)) {
            return sysOrgs.stream().collect(Collectors.toMap(SysOrg::getId, i -> i.getName()));
        }
        return Collections.EMPTY_MAP;
    }

    //************* 接口操作

    public NtmResult querySysOrg(SysOrgDto dto) throws Exception {
        int pageNum = 1, pageSize = 1 << 10;
        LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrg::getPid, dto.getPid());
        List<SysOrg> list = slaveSysOrgDao.selectList(wrapper);

        if (CollectionUtils.isNotEmpty(list)) {
            pageSize = list.size();
            LambdaQueryWrapper<SysOrg> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.in(SysOrg::getPid, list.stream().map(SysOrg::getId).collect(Collectors.toList()));
            Map<Long, List<SysOrg>> listMap = slaveSysOrgDao.selectList(wrapper)
                                                            .stream()
                                                            .collect(Collectors.groupingBy(SysOrg::getPid));
            List<SysOrgVo> vos = new ArrayList<>();
            for (SysOrg record : list) {
                SysOrgVo vo = new SysOrgVo();
                vo.setId(String.valueOf(record.getId()));
                vo.setPid(String.valueOf(record.getPid()));
                vo.setName(record.getName());
                vo.setNumber(record.getNumber());
                vo.setProvince(AdministrativeUtils.of(record.getProvince()));
                vo.setCity(AdministrativeUtils.of(record.getCity()));
                vo.setArea(AdministrativeUtils.of(record.getArea()));
                vo.setStreet(AdministrativeUtils.of(record.getStreet()));
                vo.setAddress(record.getAddress());
                vo.setType(record.getType());
                vo.setHasChildren(CollectionUtils.isNotEmpty(listMap.get(record.getId())));
                vo.setCreateTime(DateUtils.format(record.getCreateTime()));
                vos.add(vo);
            }
            return NtmResult.success(PaginationResult.of(pageNum, pageSize, pageNum, pageSize, vos));
        }

        return NtmResult.success(PaginationResult.empty(pageNum, pageSize));
    }

    @Transactional
    public NtmResult addSysOrg(SysOrgDto dto) throws Exception {
        LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrg::getPid, dto.getPid())
               .eq(SysOrg::getName, dto.getName());

        SysOrg record = masterSysOrgDao.selectOne(wrapper);
        if (record != null) {
            return NtmResult.fail("组织名称重复，重新操作");
        }
        record = new SysOrg();
        record.setPid(dto.getPid());
        record.setName(dto.getName());
        record.setNumber(dto.getNumber());
        record.setAddress(dto.getAddress());
        record.setStreet(dto.getStreet());
        record.setArea(dto.getArea());
        record.setCity(dto.getCity());
        record.setProvince(dto.getProvince());
        record.setType(dto.getType());
        record.setCreateTime(new Date());
        record.setModifyTime(record.getCreateTime());
        masterSysOrgDao.insert(record);
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult modifySysOrg(SysOrgDto dto) throws Exception {
        Long id = dto.getId();
        SysOrg record = masterSysOrgDao.selectById(id);
        LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrg::getPid, record.getPid())
               .eq(SysOrg::getName, dto.getName());

        SysOrg sysOrgs = masterSysOrgDao.selectOne(wrapper);
        if (sysOrgs != null && !sysOrgs.getId().equals(id)) {
            return NtmResult.fail("组织名称重复，重新操作");
        }
        record.setName(dto.getName());
        record.setNumber(dto.getNumber());
        record.setAddress(dto.getAddress());
        record.setStreet(dto.getStreet());
        record.setArea(dto.getArea());
        record.setCity(dto.getCity());
        record.setProvince(dto.getProvince());
        record.setType(dto.getType());
        record.setModifyTime(new Date());
        masterSysOrgDao.updateById(record);
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult deleteSysOrg(SysOrgDto dto) throws Exception {
        Long id = dto.getId();
        SysOrg record = masterSysOrgDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("组织不存在");
        }
        LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrg::getPid, id);

        List<SysOrg> sysOrgs = masterSysOrgDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(sysOrgs)) {
            return NtmResult.fail("组织还存在下属，先处理下级组织");
        }

        if (sysUserMgr.isExistStaff(id)) {
            return NtmResult.fail("组织还存在员工，先处理下级员工");
        }

        masterSysOrgDao.deleteById(id);
        return NtmResult.success(record);
    }

}