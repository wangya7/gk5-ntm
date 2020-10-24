package wang.bannong.gk5.ntm.rpc.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.boot.redis.CacheOpr;
import wang.bannong.gk5.boot.redis.CacheResult;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.domain.NtmApiParam;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.rpc.dao.NtmApiDao;
import wang.bannong.gk5.ntm.rpc.dao.NtmApiParamDao;
import wang.bannong.gk5.util.SpringBeanUtils;

/**
 * 重启一次性加载到内存中
 */
@Slf4j
@Component
public class NtmApiCache {
    public static final String UNDERLINE             = "_";
    public static final String CACHE_API_KEY         = "ntm:api:%s";
    public static final String CACHE_API_PARAM_APIID = "ntm:api_param:%s";

    @Autowired
    private NtmApiDao      masterNtmApiDao;
    @Autowired
    private NtmApiParamDao masterNtmApiParamDao;
    @Autowired
    private CacheOpr       cacheOpr;

    public NtmResult refresh() {
        init();
        return NtmResult.SUCCESS;
    }

    @PostConstruct
    public void init() {
        log.info("caching api & apiParam beginning");
        cacheOpr.delBatch("ntm:api:*");
        cacheOpr.delBatch("ntm:api_param:*");

        if (masterNtmApiDao == null) {
            masterNtmApiDao = SpringBeanUtils.getBean("masterNtmApiDao", NtmApiDao.class);
            masterNtmApiParamDao = SpringBeanUtils.getBean("masterNtmApiParamDao", NtmApiParamDao.class);
        }

        masterNtmApiDao.all().parallelStream()
                       .filter(i -> i.getStatus().equals(NtmConstant.EFF_STATUS))
                       .forEach(item -> {
                           String key = String.format(CACHE_API_KEY, item.getUnique() + UNDERLINE
                                   + item.getVersion() + UNDERLINE + item.getAppid());
                           cacheOpr.put(key, item);
                       });

        Map<Long, List<NtmApiParam>> params = masterNtmApiParamDao.selectList(new LambdaQueryWrapper<>())
                                                                  .parallelStream()
                                                                  .collect(Collectors.groupingBy(NtmApiParam::getApiId));
        for (Map.Entry<Long, List<NtmApiParam>> entry : params.entrySet()) {
            cacheOpr.put(String.format(CACHE_API_PARAM_APIID, entry.getKey()), new ArrayList<>(entry.getValue()));
        }

        log.info("caching api & apiParam finishing");
    }

    public NtmApi queryApi(String unique, int version, String appid) {
        String key = String.format(CACHE_API_KEY, unique + UNDERLINE + version + UNDERLINE + appid);
        CacheResult<NtmApi> cacheResult = cacheOpr.getObject(key);
        if (cacheResult.isSucc() && cacheResult.getModule() != null) {
            return cacheResult.getModule();
        }

        LambdaQueryWrapper<NtmApi> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NtmApi::getUnique, unique)
               .eq(NtmApi::getVersion, version)
               .eq(NtmApi::getAppid, appid);
        NtmApi ntmApi = masterNtmApiDao.selectOne(wrapper);
        if (ntmApi == null) {
            return null;
        }
        cacheOpr.put(key, ntmApi);
        return ntmApi;
    }

    public void updateApiCache(NtmApi api) {
        String key = String.format(CACHE_API_KEY, api.getUnique() + UNDERLINE +
                api.getVersion() + UNDERLINE + api.getAppid());
        cacheOpr.put(key, api);
    }

    public List<NtmApiParam> queryApiParams(Long apiId) {
        String key = String.format(CACHE_API_PARAM_APIID, apiId);
        CacheResult<ArrayList<NtmApiParam>> cacheResult = cacheOpr.getObject(key);
        if (cacheResult.isSucc() && CollectionUtils.isNotEmpty(cacheResult.getModule())) {
            return cacheResult.getModule();
        }

        LambdaQueryWrapper<NtmApiParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NtmApiParam::getApiId, apiId);
        List<NtmApiParam> params = masterNtmApiParamDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(params)) {
            return Collections.EMPTY_LIST;
        }
        cacheOpr.put(key, new ArrayList<>(params));
        return params;
    }

    public void updateApiParamsCache(NtmApiParam apiParam) {
        Long apiId = apiParam.getApiId();
        LambdaQueryWrapper<NtmApiParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NtmApiParam::getApiId, apiId);
        List<NtmApiParam> params = masterNtmApiParamDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(params)) {
            cacheOpr.put(String.format(CACHE_API_PARAM_APIID, apiId), new ArrayList<>(params));
        }
    }

}
