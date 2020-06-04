package wang.bannong.gk5.ntm.rpc.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.domain.NtmApiParam;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.rpc.dao.NtmApiDao;
import wang.bannong.gk5.ntm.rpc.dao.NtmApiParamDao;
import wang.bannong.gk5.util.Constant;
import wang.bannong.gk5.util.SpringBeanUtils;

/**
 * 重启一次性加载到内存中
 */
@Slf4j
@Component
public class NtmApiCache {

    @Autowired
    private NtmApiDao      masterNtmApiDao;
    @Autowired
    private NtmApiParamDao masterNtmApiParamDao;

    private static List<NtmApi>        ntmApis        = null;
    private static Map<Long, NtmApi>   ntmApiMap      = null;
    private static Map<String, NtmApi> ntmApiMapByUVA = null;


    private static List<NtmApiParam>            ntmApiParams          = null;
    private static Map<Long, NtmApiParam>       ntmApiParamMap        = null;
    private static Map<Long, List<NtmApiParam>> ntmApiParamMapByApiId = null;

    public NtmResult refresh() {
        init();
        return NtmResult.SUCCESS;
    }

    @PostConstruct
    public void init() {
        log.info("loading api & apiParam beginning");
        ntmApis = new ArrayList<>();
        ntmApiMap = new HashMap<>();
        ntmApiMapByUVA = new HashMap<>();

        ntmApiParams = new ArrayList<>();
        ntmApiParamMap = new HashMap<>();
        ntmApiParamMapByApiId = new HashMap<>();


        if (masterNtmApiDao == null) {
            masterNtmApiDao = SpringBeanUtils.getBean("masterNtmApiDao", NtmApiDao.class);
            masterNtmApiParamDao = SpringBeanUtils.getBean("masterNtmApiParamDao", NtmApiParamDao.class);
        }

        ntmApis = masterNtmApiDao.selectList(new LambdaQueryWrapper<>());
        ntmApis.parallelStream().forEach(item -> {
            ntmApiMap.put(item.getId(), item);
            // unique:version:appid
            ntmApiMapByUVA.put(item.getUnique() + Constant.COLON +
                    item.getVersion() + Constant.COLON + item.getAppid(), item);
        });

        ntmApiParams = masterNtmApiParamDao.selectList(new LambdaQueryWrapper<>());
        ntmApiParamMap = ntmApiParams.parallelStream()
                                     .collect(Collectors.toMap(NtmApiParam::getId, i -> i));
        ntmApiParamMapByApiId = ntmApiParams.parallelStream()
                                            .collect(Collectors.groupingBy(NtmApiParam::getApiId));
        log.info("loading api & apiParam finishing");
    }

    public static NtmApi queryApi(Long id) {
        return ntmApiMap.get(id);
    }

    public static List<NtmApi> queryApis(List<Long> ids) {
        List<NtmApi> apis = new ArrayList<>();
        for (Long id : ids) {
            apis.add(ntmApiMap.get(id));
        }
        return apis;
    }

    public static NtmApi queryApi(String unique, int version, String appid) {
        return ntmApiMapByUVA.get(unique + Constant.COLON + version + Constant.COLON + appid);
    }

    public static NtmApiParam queryApiParam(Long id) {
        return ntmApiParamMap.get(id);
    }

    public static List<NtmApiParam> queryApiParams(List<Long> ids) {
        List<NtmApiParam> apiParams = new ArrayList<>();
        for (Long id : ids) {
            apiParams.add(ntmApiParamMap.get(id));
        }
        return apiParams;
    }

    public static List<NtmApiParam> queryApiParams(Long apiId) {
        return ntmApiParamMapByApiId.get(apiId);
    }

}
