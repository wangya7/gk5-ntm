package wang.bannong.gk5.ntm.core.inner;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.constant.ApiConfig;
import wang.bannong.gk5.ntm.common.constant.HttpMethod;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.domain.NtmApiParam;
import wang.bannong.gk5.ntm.common.dto.ApiDto;
import wang.bannong.gk5.ntm.common.dto.ApiParamDto;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.PaginationResult;
import wang.bannong.gk5.ntm.common.vo.NtmApiParamVo;
import wang.bannong.gk5.ntm.common.vo.NtmApiVo;
import wang.bannong.gk5.ntm.core.dao.NtmApiDao;
import wang.bannong.gk5.ntm.core.dao.NtmApiParamDao;
import wang.bannong.gk5.ntm.core.handler.RequestHandler;
import wang.bannong.gk5.util.DateUtils;
import wang.bannong.gk5.util.OkHttpUtils;

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

    public NtmApi queryByUniqueAndVersionAppid(String unique, int version, String appid) {
        QueryWrapper<NtmApi> wrapper = new QueryWrapper<>();
        wrapper.lambda()
               .eq(NtmApi::getUnique, unique)
               .eq(NtmApi::getVersion, version)
               .eq(NtmApi::getAppid, appid);
        return masterNtmApiDao.selectOne(wrapper);
    }

    public List<NtmApiParam> queryParams(Long apiId) {
        QueryWrapper<NtmApiParam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(NtmApiParam::getApiId, apiId);
        return masterNtmApiParamDao.selectList(wrapper);
    }

    public List<NtmApiParam> queryParamsByApiId(Long apiId) {
        QueryWrapper<NtmApiParam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(NtmApiParam::getApiId, apiId);
        return masterNtmApiParamDao.selectList(wrapper);
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
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult modifyApi(ApiDto dto) throws Exception {
        Long id = dto.getApiId();
        NtmApi record = queryById(id);
        if (record == null) {
            return NtmResult.fail("接口不存在");
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

        if (dto.getStatus() != null) {
            record.setStatus(dto.getStatus());
        } else {
            record.setName(dto.getName());
            record.setMethod(dto.getMethod());
            record.setInnerInterface(dto.getInnerInterface());
            record.setInnerMethod(dto.getInnerMethod());
            record.setIsIa(dto.getIsIa());
            record.setIsAsync(dto.getIsAsync());
            record.setDailyFlowLimit(dto.getDailyFlowLimit());
            record.setResult(dto.getResult());
        }
        record.setModifyTime(new Date());
        masterNtmApiDao.updateById(record);
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult deleteApi(ApiDto dto) throws Exception {
        masterNtmApiDao.deleteById(dto.getApiId());
        LambdaQueryWrapper<NtmApiParam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(NtmApiParam::getApiId, dto.getApiId());
        masterNtmApiParamDao.delete(lambdaQueryWrapper);
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

        if (dto.getStatus() != null) {
            record.setStatus(dto.getStatus());
        } else {
            record.setPid(dto.getPid());
            record.setType(dto.getType());
            record.setIsRequired(dto.getIsRequired());
            record.setErrMsg(dto.getErrMsg());
            record.setDesp(dto.getDesp());
            record.setExample(dto.getExample());
        }

        record.setModifyTime(new Date());
        masterNtmApiParamDao.updateById(record);
        return NtmResult.success(record);
    }

    @Transactional
    public NtmResult deleteApiParam(ApiParamDto dto) throws Exception {
        masterNtmApiParamDao.deleteById(dto.getApiParamId());
        return NtmResult.SUCCESS;
    }

    /**
     * 接口测试
     *
     * @param innerRequest innerRequest
     */
    public NtmResult testApi(NtmInnerRequest innerRequest) throws Exception {
        Map<String, String> dataStore = innerRequest.getDataStore();
        Long apiId = Long.valueOf(innerRequest.get(ApiConfig.API_ID));
        NtmApi api = queryById(apiId);
        if (api.getUnique().equals(innerRequest.getRequest().getApi())) {
            return NtmResult.fail("Test接口不支持自测");
        }
        Map<String, String> paramsHeader = new HashMap<>();
        String ts = dataStore.get(ApiConfig.TS);
        paramsHeader.put(ApiConfig.TS, StringUtils.isBlank(ts) ? "20190801" : ts);
        dataStore.remove(ApiConfig.TS);

        paramsHeader.put(ApiConfig.LNG, "22");
        paramsHeader.put(ApiConfig.LAT, "22");
        dataStore.remove(ApiConfig.LNG);
        dataStore.remove(ApiConfig.LAT);

        String ttid = dataStore.get(ApiConfig.TTID);
        paramsHeader.put(ApiConfig.TTID, StringUtils.isBlank(ttid) ? "Chrome/76.0.3809.100@H5@Macintosh; Intel Mac OS X 10_14_6@1.0.0" : ttid);
        dataStore.remove(ApiConfig.TTID);

        paramsHeader.put(ApiConfig.APPID, api.getAppid());
        dataStore.remove(ApiConfig.APPID);

        String channel = dataStore.get(ApiConfig.CHANNEL);
        paramsHeader.put(ApiConfig.CHANNEL, StringUtils.isBlank(channel) ? "hz" : channel);
        dataStore.remove(ApiConfig.CHANNEL);

        paramsHeader.put(ApiConfig.IA, RequestHandler.format(dataStore.get(ApiConfig.IA)));
        dataStore.remove(ApiConfig.IA);
        paramsHeader.put(ApiConfig.OIA, RequestHandler.format(dataStore.get(ApiConfig.OIA)));
        dataStore.remove(ApiConfig.OIA);

        String baseTestURL = ConfigTools.get(NtmConstant.BASE_TEST_URL);
        if (StringUtils.isBlank(baseTestURL)) {
            return NtmResult.fail("系统缺少 BASE_TEST_URL 配置项！！！");
        }

        Map<String, String> params = new HashMap<>();
        params.put(ApiConfig.SIGN, dataStore.get(ApiConfig.SIGN));
        dataStore.remove(ApiConfig.SIGN);
        params.put(ApiConfig.API, api.getUnique());
        dataStore.remove(ApiConfig.API);
        params.put(ApiConfig.V, String.valueOf(api.getVersion()));
        dataStore.remove(ApiConfig.V);
        params.put(ApiConfig.DATA, JSONObject.toJSONString(dataStore));

        JSONObject result = new JSONObject();
        String result_ = null;

        log.info("结构测试, url={}, params={}, paramsHeader={}", baseTestURL, params, paramsHeader);
        if (api.getStatus().equals(HttpMethod.GET)) {
            result_ = OkHttpUtils.get(baseTestURL, params, paramsHeader);
        } else if (api.getStatus().equals(HttpMethod.POST)) {
            result_ = OkHttpUtils.post(baseTestURL, params, paramsHeader);
        }

        log.info("结构测试, result={}", result_);
        result.put("result", result_);
        return NtmResult.success(result);
    }
}
