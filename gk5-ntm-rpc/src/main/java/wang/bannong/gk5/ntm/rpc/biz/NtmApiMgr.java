package wang.bannong.gk5.ntm.rpc.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.domain.NtmApiParam;
import wang.bannong.gk5.ntm.common.dto.ApiDto;
import wang.bannong.gk5.ntm.common.dto.ApiParamDto;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.PaginationResult;
import wang.bannong.gk5.ntm.common.vo.NtmApiDocVo;
import wang.bannong.gk5.ntm.common.vo.NtmApiParamVo;
import wang.bannong.gk5.ntm.common.vo.NtmApiVo;
import wang.bannong.gk5.ntm.rpc.dao.NtmApiDao;
import wang.bannong.gk5.ntm.rpc.dao.NtmApiParamDao;
import wang.bannong.gk5.util.DateUtils;
import wang.bannong.gk5.util.SnowFlakeGenerator;

@Slf4j
@Component
public class NtmApiMgr {

    @Autowired
    private NtmApiDao      masterNtmApiDao;
    @Autowired
    private NtmApiDao      slaveNtmApiDao;
    @Autowired
    private NtmApiParamDao masterNtmApiParamDao;
    @Autowired
    private NtmApiParamDao slaveNtmApiParamDao;
    @Autowired
    private NtmApiCache    ntmApiCache;

    public NtmApi queryById(Long id) {
        return masterNtmApiDao.selectById(id);
    }

    public List<NtmApi> queryByIds(List<Long> ids) {
        return masterNtmApiDao.selectBatchIds(ids);
    }

    public NtmApi queryByUnique(String unique) {
        QueryWrapper<NtmApi> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(NtmApi::getUnique, unique);
        return masterNtmApiDao.selectOne(wrapper);
    }

    /**
     * 网关调用，属于高频调用，缓存实现
     */
    public NtmApi queryByUniqueAndVersionAppid(String unique, int version, String appid) {
        return ntmApiCache.queryApi(unique, version, appid);
    }

    /**
     * 网关调用，属于高频调用，缓存实现
     */
    public List<NtmApiParam> queryParams(Long apiId) {
        return ntmApiCache.queryApiParams(apiId);
    }

    public NtmApiParam queryParamByAppidAndName(Long apiId, String name) {
        QueryWrapper<NtmApiParam> wrapper = new QueryWrapper<>();
        wrapper.lambda()
               .eq(NtmApiParam::getApiId, apiId)
               .eq(NtmApiParam::getName, name);
        return masterNtmApiParamDao.selectOne(wrapper);
    }

    public NtmApiParam queryParamById(Long id) {
        return masterNtmApiParamDao.selectById(id);
    }

    public List<NtmApiParam> queryParamByIds(List<Long> ids) {
        return masterNtmApiParamDao.selectBatchIds(ids);
    }

    //************************ 接口

    public NtmResult apiList(ApiDto dto) throws Exception {
        log.info("query apis, dto={}", dto);
        LambdaQueryWrapper<NtmApi> wrapper = new LambdaQueryWrapper<>();

        if (dto.getApiId() != null) {
            wrapper.eq(NtmApi::getId, dto.getApiId());
        }
        if (dto.getDictId() != null) {
            wrapper.eq(NtmApi::getDictId, dto.getDictId());
        }
        if (dto.getName() != null) {
            wrapper.like(NtmApi::getName, dto.getName());
        }
        if (dto.getUnique() != null) {
            wrapper.like(NtmApi::getUnique, dto.getUnique());
        }
        if (dto.getAppid() != null) {
            wrapper.eq(NtmApi::getAppid, dto.getAppid());
        }

        wrapper.orderByDesc(NtmApi::getModifyTime);

        Page<NtmApi> ntmApiPage = new Page<>(dto.getPageNum(), dto.getPageSize());
        slaveNtmApiDao.selectPage(ntmApiPage, wrapper);
        List<NtmApi> apiList = ntmApiPage.getRecords();
        if (CollectionUtils.isNotEmpty(apiList)) {
            List<NtmApiVo> vos = new ArrayList<>();
            for (NtmApi record : apiList) {
                NtmApiVo vo = new NtmApiVo();
                vo.setApiId(String.valueOf(record.getId()));
                vo.setDictId(String.valueOf(record.getDictId()));
                vo.setUnique(record.getUnique());
                vo.setName(record.getName());
                vo.setVersion(record.getVersion());
                vo.setMethod(record.getMethod());
                vo.setAppid(record.getAppid());
                vo.setInnerInterface(record.getInnerInterface());
                vo.setInnerMethod(record.getInnerMethod());
                vo.setIsIa(record.getIsIa());
                vo.setIsAsync(record.getIsAsync());
                vo.setDailyFlowLimit(record.getDailyFlowLimit());
                vo.setResult(record.getResult());
                vo.setStatus(record.getStatus());
                vo.setCreateTime(DateUtils.format(record.getCreateTime()));
                vo.setModifyTime(DateUtils.format(record.getModifyTime()));
                vos.add(vo);
            }
            return NtmResult.success(PaginationResult.of(ntmApiPage, vos));
        }
        return NtmResult.success(PaginationResult.empty(dto.getPageNum(), dto.getPageSize()));
    }

    @Transactional
    public NtmResult addApi(ApiDto dto) throws Exception {
        String appid = dto.getAppid();
        String unique = dto.getUnique();
        int version = dto.getVersion();
        NtmApi record = queryByUniqueAndVersionAppid(unique, version, appid);
        if (record != null) {
            return NtmResult.fail("接口已经存在");
        }
        record = new NtmApi();
        record.setId(SnowFlakeGenerator.Factory.creategGnerator(3L, 3L).nextId());
        record.setDictId(dto.getDictId());
        record.setUnique(unique);
        record.setName(dto.getName());
        record.setVersion(version);
        record.setMethod(dto.getMethod());
        record.setAppid(appid);
        record.setInnerInterface(dto.getInnerInterface());
        record.setInnerMethod(dto.getInnerMethod());
        record.setIsIa(dto.getIsIa());
        record.setIsAsync(dto.getIsAsync());
        record.setDailyFlowLimit(dto.getDailyFlowLimit());
        record.setResult(dto.getResult());
        record.setStatus(NtmConstant.EFF_STATUS);
        record.setCreateTime(new Date());
        record.setModifyTime(record.getCreateTime());
        masterNtmApiDao.insert(record);
        ntmApiCache.updateApiCache(record);
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult modifyApi(ApiDto dto) throws Exception {
        Long id = dto.getApiId();
        NtmApi record = masterNtmApiDao.selectById(id);
        if (record == null) {
            return NtmResult.fail("接口不存在");
        }

        // 禁用操作
        if (dto.getStatus() != null && dto.getStatus().equals(NtmConstant.EXP_STATUS)) {
            record.setStatus(dto.getStatus());
            record.setModifyTime(new Date());
            masterNtmApiDao.updateById(record);
            return NtmResult.success(record);
        }


        String appid;
        if (dto.getAppid() != null) {
            appid = dto.getAppid();
            record.setAppid(appid);
        } else {
            appid = record.getAppid();
        }

        String unique;
        if (dto.getUnique() != null) {
            unique = dto.getUnique();
            record.setUnique(unique);
        } else {
            unique = record.getUnique();
        }

        int version;
        if (dto.getVersion() != null) {
            version = dto.getVersion();
            record.setVersion(version);
        } else {
            version = record.getVersion();
        }

        NtmApi recordCheck = queryByUniqueAndVersionAppid(unique, version, appid);
        if (recordCheck != null && !recordCheck.getId().equals(id)) {
            return NtmResult.fail("接口已经存在");
        }

        record.setName(dto.getName());
        record.setMethod(dto.getMethod());
        record.setInnerInterface(dto.getInnerInterface());
        record.setInnerMethod(dto.getInnerMethod());
        record.setIsIa(dto.getIsIa());
        record.setIsAsync(dto.getIsAsync());
        record.setDailyFlowLimit(dto.getDailyFlowLimit());
        record.setResult(dto.getResult());

        record.setModifyTime(new Date());
        masterNtmApiDao.updateById(record);
        ntmApiCache.updateApiCache(record);
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult deleteApi(ApiDto dto) throws Exception {
        if (masterNtmApiDao.deleteById(dto.getApiId()) > 0) {
            LambdaQueryWrapper<NtmApiParam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(NtmApiParam::getApiId, dto.getApiId());
            masterNtmApiParamDao.delete(lambdaQueryWrapper);
        }
        return NtmResult.SUCCESS;
    }


    @Transactional
    public NtmResult updateApiReturn(ApiDto dto) throws Exception {
        NtmApi record = queryById(dto.getApiId());
        record.setResult(dto.getResult());
        masterNtmApiDao.updateById(record);
        ntmApiCache.updateApiCache(record);
        return NtmResult.SUCCESS;
    }

    public NtmResult apiParamList(ApiParamDto dto) throws Exception {
        log.info("query api-params,dto={}", dto);
        LambdaQueryWrapper<NtmApiParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NtmApiParam::getApiId, dto.getApiId())
               .orderByDesc(NtmApiParam::getModifyTime);

        Page<NtmApiParam> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        slaveNtmApiParamDao.selectPage(page, wrapper);
        List<NtmApiParam> apiParamList = page.getRecords();
        if (CollectionUtils.isNotEmpty(apiParamList)) {
            List<NtmApiParamVo> vos = new ArrayList<>();
            for (NtmApiParam record : apiParamList) {
                NtmApiParamVo vo = new NtmApiParamVo();
                vo.setApiParamId(String.valueOf(record.getId()));
                vo.setPid(String.valueOf(record.getPid()));
                vo.setApiId(String.valueOf(record.getApiId()));
                vo.setName(record.getName());
                vo.setStatus(record.getStatus());
                vo.setType(record.getType());
                vo.setIsRequired(record.getIsRequired());
                vo.setErrMsg(record.getErrMsg());
                vo.setDesp(record.getDesp());
                vo.setExample(record.getExample());
                vo.setCreateTime(DateUtils.format(record.getCreateTime()));
                vo.setModifyTime(DateUtils.format(record.getModifyTime()));
                vos.add(vo);
            }
            return NtmResult.success(PaginationResult.of(page, vos));
        }
        return NtmResult.success(PaginationResult.empty(dto.getPageNum(), dto.getPageSize()));
    }

    @Transactional
    public NtmResult addApiParam(ApiParamDto dto) throws Exception {
        Long apiId = dto.getApiId();
        String name = dto.getName();
        NtmApiParam record = queryParamByAppidAndName(apiId, name);
        if (record != null) {
            return NtmResult.fail("接口参数已经存在");
        }
        record = new NtmApiParam();
        record.setId(SnowFlakeGenerator.Factory.creategGnerator(5L, 5L).nextId());
        record.setPid(dto.getPid());
        record.setApiId(apiId);
        record.setName(name);
        record.setStatus(NtmConstant.EFF_STATUS);
        record.setType(dto.getType());
        record.setIsRequired(dto.getIsRequired());
        record.setErrMsg(dto.getErrMsg());
        record.setDesp(dto.getDesp());
        record.setExample(dto.getExample());
        record.setCreateTime(new Date());
        record.setModifyTime(record.getCreateTime());
        masterNtmApiParamDao.insert(record);
        ntmApiCache.updateApiParamsCache(record);
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult modifyApiParam(ApiParamDto dto) throws Exception {
        Long id = dto.getApiParamId();
        NtmApiParam record = queryParamById(id);
        String name = dto.getName();
        if (name != null) {
            NtmApiParam recordCheck = queryParamByAppidAndName(record.getApiId(), name);
            if (recordCheck != null && !recordCheck.getId().equals(id)) {
                return NtmResult.fail("接口参数已经存在");
            }
            record.setName(name);
        }
        record.setPid(dto.getPid());
        record.setType(dto.getType());
        record.setIsRequired(dto.getIsRequired());
        record.setErrMsg(dto.getErrMsg());
        record.setDesp(dto.getDesp());
        record.setExample(dto.getExample());

        record.setModifyTime(new Date());
        masterNtmApiParamDao.updateById(record);
        ntmApiCache.updateApiParamsCache(record);
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult deleteApiParam(ApiParamDto dto) throws Exception {
        NtmApiParam param = masterNtmApiParamDao.selectById(dto.getApiParamId());
        if (param != null) {
            masterNtmApiParamDao.deleteById(dto.getApiParamId());
            ntmApiCache.updateApiParamsCache(param);
        }
        return NtmResult.SUCCESS;
    }


    public List<NtmApi> queryByDictId(Long dictId) throws Exception {
        LambdaQueryWrapper<NtmApi> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NtmApi::getDictId, dictId);
        return masterNtmApiDao.selectList(wrapper);
    }

    @Transactional
    public NtmResult moveDict(List<Long> apiIds, Long dictId) {
        masterNtmApiDao.updateDict(apiIds, dictId);
        // 目录不影响网关check 所有不需要更新缓存
        return NtmResult.SUCCESS;
    }

    /** 接口文档 */
    public NtmResult apiDoc(Long apiId) throws Exception {
        NtmApiDocVo docVo = new NtmApiDocVo();
        NtmApi api = masterNtmApiDao.selectById(apiId);
        docVo.setApiId(String.valueOf(api.getId()));
        docVo.setDictId(String.valueOf(api.getDictId()));
        docVo.setUnique(api.getUnique());
        docVo.setName(api.getName());
        docVo.setVersion(api.getVersion());
        docVo.setMethod(api.getMethod());
        docVo.setAppid(api.getAppid());
        docVo.setInnerInterface(api.getInnerInterface());
        docVo.setInnerMethod(api.getInnerMethod());
        docVo.setIsIa(api.getIsIa());
        docVo.setIsAsync(api.getIsAsync());
        docVo.setDailyFlowLimit(api.getDailyFlowLimit());
        docVo.setResult(api.getResult());
        docVo.setStatus(api.getStatus());
        docVo.setCreateTime(DateUtils.format(api.getCreateTime()));
        docVo.setModifyTime(DateUtils.format(api.getModifyTime()));

        LambdaQueryWrapper<NtmApiParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NtmApiParam::getApiId, apiId)
               .orderByDesc(NtmApiParam::getModifyTime);
        List<NtmApiParam> apiParamList = slaveNtmApiParamDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(apiParamList)) {
            List<NtmApiParamVo> napvos = new ArrayList<>();
            for (NtmApiParam record : apiParamList) {
                NtmApiParamVo napv = new NtmApiParamVo();
                napv.setApiParamId(String.valueOf(record.getId()));
                napv.setPid(String.valueOf(record.getPid()));
                napv.setApiId(String.valueOf(record.getApiId()));
                napv.setName(record.getName());
                napv.setStatus(record.getStatus());
                napv.setType(record.getType());
                napv.setIsRequired(record.getIsRequired());
                napv.setErrMsg(record.getErrMsg());
                napv.setDesp(record.getDesp());
                napv.setExample(record.getExample());
                napv.setCreateTime(DateUtils.format(record.getCreateTime()));
                napv.setModifyTime(DateUtils.format(record.getModifyTime()));
                napvos.add(napv);
            }
            docVo.setParms(napvos);
        }
        return NtmResult.success(docVo);
    }
}
