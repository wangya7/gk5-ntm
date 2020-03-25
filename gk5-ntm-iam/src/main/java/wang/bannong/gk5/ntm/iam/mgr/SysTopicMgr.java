package wang.bannong.gk5.ntm.iam.mgr;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.PaginationResult;
import wang.bannong.gk5.ntm.iam.common.domain.SysMenu;
import wang.bannong.gk5.ntm.iam.common.domain.SysTopic;
import wang.bannong.gk5.ntm.iam.common.dto.SysTopicDto;
import wang.bannong.gk5.ntm.iam.common.vo.SysTopicVo;
import wang.bannong.gk5.ntm.iam.dao.SysTopicDao;
import wang.bannong.gk5.util.DateUtils;


/***
 * Topic对应一个单一的功能点，也可以说是单一的智能
 * 二级界面可以有多个Topic，但是一个按钮只能对应一个Topic
 */
@Slf4j
@Component
public class SysTopicMgr {

    @Autowired
    private SysTopicDao masterSysTopicDao;
    @Autowired
    private SysTopicDao slaveSysTopicDao;

    public SysTopic queryById(Long id) throws Exception {
        return slaveSysTopicDao.selectById(id);
    }

    public SysTopic queryByUnique(String unique) throws Exception {
        LambdaQueryWrapper<SysTopic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTopic::getUnique, unique);
        return slaveSysTopicDao.selectOne(wrapper);
    }

    public List<SysTopic> queryByIds(List<Long> ids) throws Exception {
        return slaveSysTopicDao.selectBatchIds(ids);
    }

    /**
     * 获取二级菜单下面共有多少个功能点
     */
    public List<SysTopic> queryListByMenu2(Long menu2Id) throws Exception {
        LambdaQueryWrapper<SysTopic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTopic::getMenu2Id, menu2Id);
        return slaveSysTopicDao.selectList(wrapper);
    }

    /**
     * 获取按钮绑定的功能点
     */
    public SysTopic queryByMenu3(Long menu2Id, Long menu3Id) throws Exception {
        LambdaQueryWrapper<SysTopic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTopic::getMenu2Id, menu2Id)
               .eq(SysTopic::getMenu3Id, menu3Id);

        return slaveSysTopicDao.selectOne(wrapper);
    }

    public Map<Long, SysTopic> queryMapByIds(List<Long> ids) throws Exception {
        List<SysTopic> sysOrgs = queryByIds(ids);
        if (CollectionUtils.isNotEmpty(sysOrgs)) {
            return sysOrgs.stream().collect(Collectors.toMap(SysTopic::getId, i -> i));
        }
        return Collections.EMPTY_MAP;
    }

    //************* 接口操作

    public NtmResult querySysTopic(SysTopicDto dto) throws Exception {
        int pageNum = dto.getPageNum(), pageSize = dto.getPageSize();
        LambdaQueryWrapper<SysTopic> wrapper = new LambdaQueryWrapper<>();
        if (dto.getName() != null)
            wrapper.like(SysTopic::getName, dto.getName());
        if (dto.getUnique() != null)
            wrapper.like(SysTopic::getUnique, dto.getUnique());
        if (dto.getMenu2Id() != null)
            wrapper.eq(SysTopic::getMenu2Id, dto.getMenu2Id());
        if (dto.getMenu3Id() != null)
            wrapper.eq(SysTopic::getMenu3Id, dto.getMenu3Id());
        Page<SysTopic> page = new Page<>(pageNum, pageSize);
        slaveSysTopicDao.selectPage(page, wrapper);
        List<SysTopic> list = page.getRecords();
        if (CollectionUtils.isNotEmpty(list)) {
            List<SysTopicVo> vos = new ArrayList<>();
            for (SysTopic record : list) {
                SysTopicVo vo = new SysTopicVo();
                vo.setId(String.valueOf(record.getId()));
                vo.setUnique(record.getUnique());
                vo.setName(record.getName());
                vo.setMenu2Id(String.valueOf(record.getMenu2Id()));
                vo.setMenu3Id(String.valueOf(record.getMenu3Id()));
                vo.setCreateTime(DateUtils.format(record.getCreateTime()));
                vos.add(vo);
            }
            return NtmResult.success(PaginationResult.of(page, vos));
        }
        return NtmResult.success(PaginationResult.empty(pageNum, pageSize));
    }

    @Transactional
    public NtmResult addSysTopic(SysTopicDto dto) throws Exception {
        SysTopic record = queryByUnique(dto.getUnique());
        if (record != null) {
            return NtmResult.fail("Topic名称重复，重新操作");
        }
        record = new SysTopic();
        record.setUnique(dto.getUnique());
        record.setName(dto.getName());
        record.setMenu2Id(dto.getMenu2Id());
        record.setMenu3Id(dto.getMenu3Id());
        record.setCreateTime(new Date());
        record.setModifyTime(record.getCreateTime());
        masterSysTopicDao.insert(record);
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult modifySysTopic(SysTopicDto dto) throws Exception {
        Long id = dto.getId();
        SysTopic record = masterSysTopicDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("Topic不存在");
        }
        SysTopic SysTopic = queryByUnique(dto.getUnique());
        if (SysTopic != null && !SysTopic.getId().equals(id)) {
            return NtmResult.fail("Topic名称重复，重新操作");
        }
        record.setName(dto.getName());
        record.setUnique(dto.getUnique());
        record.setMenu2Id(dto.getMenu2Id());
        record.setMenu3Id(dto.getMenu3Id());
        record.setModifyTime(new Date());
        masterSysTopicDao.updateById(record);
        return NtmResult.success(record);
    }


    @Transactional
    public NtmResult deleteSysTopic(SysTopicDto dto) throws Exception {
        Long id = dto.getId();
        SysTopic record = masterSysTopicDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("Topic不存在");
        }
        masterSysTopicDao.deleteById(id);
        return NtmResult.success(record);
    }

    /**
     * 删除菜单 一定要删除绑定的topic
     *
     * @param record 菜单
     */
    public int deleteByMenuId(SysMenu record) throws Exception {
        LambdaQueryWrapper<SysTopic> wrapper = new LambdaQueryWrapper<>();
        if (record.getType() == 2) {
            wrapper.eq(SysTopic::getMenu2Id, record.getId());
        } else {
            wrapper.eq(SysTopic::getMenu3Id, record.getId());
        }
        return masterSysTopicDao.delete(wrapper);
    }
}
