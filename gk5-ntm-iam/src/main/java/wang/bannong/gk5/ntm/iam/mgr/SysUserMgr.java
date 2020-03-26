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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.PaginationResult;
import wang.bannong.gk5.ntm.iam.common.constant.IamConstant;
import wang.bannong.gk5.ntm.iam.common.domain.SysRole;
import wang.bannong.gk5.ntm.iam.common.domain.SysUser;
import wang.bannong.gk5.ntm.iam.common.domain.SysUserRole;
import wang.bannong.gk5.ntm.iam.common.dto.SysUserDto;
import wang.bannong.gk5.ntm.iam.common.vo.SysUserVo;
import wang.bannong.gk5.ntm.iam.dao.SysUserDao;
import wang.bannong.gk5.ntm.iam.dao.SysUserRoleDao;
import wang.bannong.gk5.util.DateUtils;
import wang.bannong.gk5.util.MD5Util;
import wang.bannong.gk5.util.SnowFlakeGenerator;
import wang.bannong.gk5.util.domain.Pair;

@Slf4j
@Component
public class SysUserMgr {

    @Autowired
    private SysOrgMgr      sysOrgMgr;
    @Autowired
    private SysUserDao     masterSysUserDao;
    @Autowired
    private SysUserDao     slaveSysUserDao;
    @Autowired
    private SysUserRoleDao masterSysUserRoleDao;
    @Autowired
    private SysUserRoleDao slaveSysUserRoleDao;
    @Autowired
    private SysRoleMgr     sysRoleMgr;


    public SysUser queryById(Long id) throws Exception {
        return slaveSysUserDao.selectById(id);
    }

    public SysUser queryByMobile(String mobile) throws Exception {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getMobile, mobile);
        return slaveSysUserDao.selectOne(wrapper);
    }

    public List<SysUser> queryByIds(List<Long> ids) throws Exception {
        return slaveSysUserDao.selectBatchIds(ids);
    }

    public Map<Long, SysUser> queryMapByIds(List<Long> ids) throws Exception {
        List<SysUser> sysOrgs = queryByIds(ids);
        if (CollectionUtils.isNotEmpty(sysOrgs)) {
            return sysOrgs.stream().collect(Collectors.toMap(SysUser::getId, i -> i));
        }
        return Collections.EMPTY_MAP;
    }

    public Map<Long, String> queryNameMapByIds(List<Long> ids) throws Exception {
        List<SysUser> sysOrgs = queryByIds(ids);
        if (CollectionUtils.isNotEmpty(sysOrgs)) {
            return sysOrgs.stream().collect(Collectors.toMap(SysUser::getId, i -> i.getName()));
        }
        return Collections.EMPTY_MAP;
    }


    /**
     * 组织下时候存在有效员工
     *
     * @param orgId 组织ID
     * @return F-不存在 T-存在
     */
    public boolean isExistStaff(Long orgId) throws Exception {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getOrgId, orgId);
        return CollectionUtils.isNotEmpty(slaveSysUserDao.selectList(wrapper));
    }


    //************* 接口操作

    public NtmResult login(SysUserDto dto) throws Exception {
        SysUser user = queryByMobile(dto.getMobile());
        if (user.getPassword().equals(MD5Util.md5LowerCase(dto.getMobile() + dto.getPassword()))) {
            return NtmResult.success(user);
        }
        return NtmResult.fail("手机号码密码不匹配");
    }

    public NtmResult querySysUser(SysUserDto dto) throws Exception {
        int pageNum = dto.getPageNum(), pageSize = dto.getPageSize();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (dto.getName() != null)
            wrapper.like(SysUser::getName, dto.getName());
        if (dto.getMobile() != null)
            wrapper.like(SysUser::getMobile, dto.getMobile());

        Page<SysUser> page = new Page<>(pageNum, pageSize);
        slaveSysUserDao.selectPage(page, wrapper);
        List<SysUser> list = page.getRecords();
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> creatorIds = new ArrayList<>();
            Set<Long> orgIds = new HashSet<>();
            Set<Long> ids = new HashSet<>();
            for (SysUser record : list) {
                ids.add(record.getId());
                Long creatorId = record.getCreatorId();
                if (creatorId != null && creatorId.compareTo(0L) > 0) {
                    creatorIds.add(record.getCreatorId());
                }
                orgIds.add(record.getOrgId());
            }
            Map<Long, String> userNameMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(creatorIds)) {
                userNameMap = queryNameMapByIds(creatorIds);
            }

            Map<Long, String> orgNameMap = sysOrgMgr.queryNameMapByIds(orgIds);

            LambdaQueryWrapper<SysUserRole> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.in(SysUserRole::getUserId, ids);
            List<SysUserRole> userRoleList = slaveSysUserRoleDao.selectList(wrapper1);
            Map<Long, List<SysUserRole>> roleMap = userRoleList
                    .stream().collect(Collectors.groupingBy(SysUserRole::getUserId));
            Map<Long, SysRole> sysRoleMap = sysRoleMgr.queryMapByIds(userRoleList
                    .stream().map(SysUserRole::getRoleId).collect(Collectors.toSet()));

            List<SysUserVo> vos = new ArrayList<>();
            for (SysUser record : list) {
                SysUserVo vo = SysUserVo.of(record);
                Long creatorId = record.getCreatorId();
                if (creatorId != null && creatorId.compareTo(0L) > 0) {
                    vo.setCreatorId(String.valueOf(creatorId));
                    vo.setCreatorName(userNameMap.get(creatorId));
                }
                vo.setOrgId(String.valueOf(record.getOrgId()));
                vo.setOrgName(orgNameMap.get(record.getOrgId()));
                List<SysUserRole> roles = roleMap.get(record.getId());
                if (CollectionUtils.isNotEmpty(roles)) {
                    List<Pair<String, String>> pairs = new ArrayList<>();
                    for (SysUserRole role : roles) {
                        Long roleId = role.getRoleId();
                        pairs.add(Pair.of(String.valueOf(roleId), sysRoleMap.get(roleId).getName()));
                    }
                    vo.setRoles(pairs);
                }

                vos.add(vo);
            }
            return NtmResult.success(PaginationResult.of(page, vos));
        }
        return NtmResult.success(PaginationResult.empty(pageNum, pageSize));
    }

    @Transactional
    public NtmResult addSysUser(SysUserDto dto) throws Exception {
        String mobile = dto.getMobile();
        SysUser record = queryByMobile(mobile);
        if (record != null) {
            return NtmResult.fail("管理员手机号码已存在，重新操作");
        }
        record = new SysUser();
        Long id = SnowFlakeGenerator.Factory.creategGnerator(11L, 11L).nextId();
        record.setId(id);
        record.setName(dto.getName());
        record.setMobile(mobile);
        record.setOrgId(record.getOrgId());
        record.setIdentity(record.getIdentity());
        record.setPassword(MD5Util.md5LowerCase(mobile + dto.getPassword()));
        record.setWorkNumber(record.getWorkNumber());
        record.setIcon(record.getIcon());
        record.setEmail(record.getEmail());

        record.setStatus(IamConstant.EFF);
        record.setLastLoginTime(null);
        record.setCreatorId(dto.getCreatorId());
        Date now = new Date();
        record.setCreateTime(now);
        record.setModifyTime(now);
        if (masterSysUserDao.insert(record) > 0) {
            List<SysUserRole> userRoleList = new ArrayList<>();
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole item = new SysUserRole();
                item.setRoleId(roleId);
                item.setUserId(id);
                userRoleList.add(item);
            }
            if (masterSysUserRoleDao.batchInsert(userRoleList) > 0) {
                return NtmResult.success(record);
            }
        }
        return NtmResult.fail(NtmConstant.EXP_MSG);
    }

    @Transactional
    public NtmResult modifySysUser(SysUserDto dto) throws Exception {
        Long id = dto.getId();
        SysUser record = masterSysUserDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("管理员不存在");
        }
        String mobile = dto.getMobile();
        SysUser SysUser = queryByMobile(mobile);
        if (SysUser != null && !SysUser.getId().equals(id)) {
            return NtmResult.fail("管理员手机号存在，重新操作");
        }

        record.setName(dto.getName());
        record.setMobile(mobile);
        record.setOrgId(record.getOrgId());
        record.setIdentity(record.getIdentity());
        record.setPassword(MD5Util.md5LowerCase(mobile + record.getPassword()));
        record.setWorkNumber(record.getWorkNumber());
        record.setIcon(record.getIcon());
        record.setEmail(record.getEmail());
        record.setStatus(dto.getStatus());
        record.setModifyTime(new Date());
        if (masterSysUserDao.updateById(record) > 0) {
            LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserRole::getUserId, id);
            if (masterSysUserRoleDao.delete(wrapper) > 0) {
                List<SysUserRole> userRoleList = new ArrayList<>();
                for (Long roleId : dto.getRoleIds()) {
                    SysUserRole item = new SysUserRole();
                    item.setRoleId(roleId);
                    item.setUserId(id);
                    userRoleList.add(item);
                }
                if (masterSysUserRoleDao.batchInsert(userRoleList) > 0) {
                    return NtmResult.success(record);
                }
            }
        }
        return NtmResult.fail(NtmConstant.EXP_MSG);
    }


    @Transactional
    public NtmResult deleteSysUser(SysUserDto dto) throws Exception {
        Long id = dto.getId();
        SysUser record = masterSysUserDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("管理员不存在");
        }
        if (masterSysUserDao.deleteById(id) > 0) {
            return NtmResult.success(record);
        }
        return NtmResult.fail(NtmConstant.EXP_MSG);
    }


    @Transactional
    public NtmResult modifyPasswd(SysUserDto dto) throws Exception {
        String passwd = dto.getPassword();
        String passwdOld = dto.getPasswordOld();
        if (passwd.equals(passwdOld)) {
            return NtmResult.fail("新密码与旧密码不能一致");
        }
        Long id = dto.getId();
        SysUser record = masterSysUserDao.selectById(id);
        String mobile = record.getMobile();
        if (!MD5Util.md5LowerCase(mobile + passwdOld).equals(record.getPassword())) {
            return NtmResult.fail("旧密码错误，确认后重新输入");
        }

        record.setPassword(MD5Util.md5LowerCase(mobile + passwd));
        record.setModifyTime(new Date());
        if (masterSysUserDao.updateById(record) > 0) {
            return NtmResult.SUCCESS;
        }
        return NtmResult.fail(NtmConstant.EXP_MSG);
    }

    @Transactional
    public NtmResult forgetPasswd(SysUserDto dto) throws Exception {
        String mobile = dto.getMobile();
        SysUser record = queryByMobile(mobile);
        if (record == null) {
            return NtmResult.fail("管理员不存在");
        }
        record.setPassword(MD5Util.md5LowerCase(mobile + dto.getPassword()));
        record.setModifyTime(new Date());
        if (masterSysUserDao.updateById(record) > 0) {
            return NtmResult.SUCCESS;
        }
        return NtmResult.fail(NtmConstant.EXP_MSG);
    }

    /**
     * 获取管理员的角色集合
     */
    public List<Long> queryRoleIds(Long userId) throws Exception {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        return slaveSysUserRoleDao.selectList(wrapper)
                                  .stream()
                                  .map(SysUserRole::getRoleId)
                                  .collect(Collectors.toList());
    }

}
