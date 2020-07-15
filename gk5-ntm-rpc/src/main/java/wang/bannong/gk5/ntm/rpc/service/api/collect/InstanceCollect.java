package wang.bannong.gk5.ntm.rpc.service.api.collect;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.bo.NtmTracesBo;
import wang.bannong.gk5.ntm.common.constant.ApiConfig;
import wang.bannong.gk5.ntm.common.exception.NtmException;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.rpc.service.BaseInnerService;
import wang.bannong.gk5.ntm.rpc.trace.NtmTracesManager;
import wang.bannong.gk5.util.DateUtils;

@Slf4j
@Service
public class InstanceCollect implements BaseInnerService {

    @Autowired
    private NtmTracesManager ntmTracesManager;

    @Override
    public NtmResult api(NtmInnerRequest innerRequest) {
        NtmTracesBo bo = new NtmTracesBo();
        String pageNum;
        if (StringUtils.isNotBlank(pageNum = innerRequest.get("pageNum"))) {
            bo.setPageNum(Integer.parseInt(pageNum));
        }

        String pageSize;
        if (StringUtils.isNotBlank(pageSize = innerRequest.get("pageSize"))) {
            bo.setPageSize(Integer.parseInt(pageSize));
        }

        String unique;
        if (StringUtils.isNotBlank(unique = innerRequest.get("unique"))) {
            bo.setApi(unique);
        }

        String apiId;
        if (StringUtils.isNotBlank(apiId = innerRequest.get(ApiConfig.API_ID))) {
            bo.setApiId(Long.parseLong(apiId));
        }

        String appid;
        if (StringUtils.isNotBlank(appid = innerRequest.get(ApiConfig.APPID))) {
            bo.setAppid(appid);
        }

        String ip;
        if (StringUtils.isNotBlank(ip = innerRequest.get(ApiConfig.IP))) {
            bo.setIp(ip);
        }

        String ia;
        if (StringUtils.isNotBlank(ia = innerRequest.get(ApiConfig.IA))) {
            bo.setIa(ia);
        }

        String code;
        if (StringUtils.isNotBlank(code = innerRequest.get(ApiConfig.CODE))) {
            bo.setCode(Integer.parseInt(code));
        }

        String requestBeginTime, requestEndTime;
        if (StringUtils.isNotBlank(requestBeginTime = innerRequest.get("requestBeginTime")) &&
                StringUtils.isNotBlank(requestEndTime = innerRequest.get("requestEndTime"))) {
            bo.setRequestBeginTime(DateUtils.parse(DateUtils.YMDHMS2, requestBeginTime));
            bo.setRequestEndTime(DateUtils.parse(DateUtils.YMDHMS2, requestEndTime));
        }

        try {
            return NtmResult.success(ntmTracesManager.queryApi(bo));
        } catch (Exception e) {
            log.error("实时接口检测失败，bo={}，异常信息：", bo, e);
            throw new NtmException(e);
        }
    }
}
