package wang.bannong.gk5.ntm.core.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.core.inner.NtmApiMgr;
import wang.bannong.gk5.ntm.common.constant.ApiStatusEnum;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.domain.NtmApiParam;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.ResultCode;

@Slf4j
@Component
public class ApiHandler {

    @Autowired
    private NtmApiMgr ntmApiMgr;

    public NtmResult checkApi(String unique, int version, String appid) {
        NtmApi ntmApi = ntmApiMgr.queryByUniqueAndVersionAppid(unique, version, appid);
        if (ntmApi == null) {
            return NtmResult.of(ResultCode.api_not_exist);
        }

        ApiStatusEnum status = ApiStatusEnum.of(ntmApi.getStatus());
        if (status == ApiStatusEnum.delete) {
            return NtmResult.of(ResultCode.api_not_exist);
        } else if (status == ApiStatusEnum.forbid) {
            return NtmResult.of(ResultCode.api_forbid);
        }
        return NtmResult.success(ntmApi);
    }

    public NtmResult checkApiParams(NtmInnerRequest innerRequest, NtmApi ntmApi) {
        /**
         * 目前仅对必传的参数校验
         *
         *  1. 参数类型校验，比如一个Integer的参数是否加入传来“d”
         *  2. 阶梯参数校验，A存在校验B 否则B不考虑
         *  3. 参数转换DTO
         */
        List<NtmApiParam> params = ntmApiMgr.queryParams(ntmApi.getId());
        if (CollectionUtils.isNotEmpty(params)) {
            List<String> notNullParams = new ArrayList<>();
            for (NtmApiParam param : params) {
                if (param.getIsRequired()) {
                    String name = param.getName();
                    if (StringUtils.isBlank(innerRequest.get(name))) {
                        notNullParams.add(name);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(notNullParams)) {
                return NtmResult.of(ResultCode.api_param_missing, notNullParams.toString());
            }
        }
        return NtmResult.SUCCESS;
    }

}
