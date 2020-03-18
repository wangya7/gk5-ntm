package wang.bannong.gk5.ntm.common.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.domain.NtmApi;

/**
 * Created by bn. on 2019/10/11 4:34 PM
 */
@Data
public class NtmTraces implements Serializable {
    private static long serialVersionUID = -8084760043035212607L;

    /** request */
    private String api;    // api包含的v 区别与网管中的api字段
    private long   apiId = 0;
    private String ia;
    private String oia;    // 平滑更新ia，需要保存旧值
    private long   ts    = 0;
    private String lng;
    private String lat;
    private String appid;
    private String brand;
    private String platform;
    private String system;
    private String version;
    private String channel;
    private Map<String, String> requestData;

    private String method;
    private String ip;

    /** response */
    private int    code = -1;
    private String msg;
    private Object responseData;

    /** time */
    private long   createMs = 0;
    private String createTime;

    public static NtmTraces of(NtmRequest request, NtmResponse response, NtmApi ntmApi) {
        NtmTraces bo = new NtmTraces();
        if (ntmApi != null) bo.setApiId(ntmApi.getId());
        bo.setApi(request.getApi() + NtmConstant.UNDERLINE + request.getV());
        bo.setIa(request.getIa());
        bo.setOia(request.getOia());
        bo.setTs(request.getTs());
        bo.setLng(request.getLng());
        bo.setLat(request.getLat());
        bo.setAppid(request.getAppid());

        NtmRequest.Ttid ttid = request.getTtid();
        bo.setBrand(ttid.getBrand());
        bo.setPlatform(ttid.getPlatform());
        bo.setSystem(ttid.getSystem());
        bo.setVersion(ttid.getVersion());

        bo.setChannel(request.getChannel());
        bo.setRequestData(request.getData());
        bo.setMethod(request.getMethod().name());
        bo.setIp(request.getIp());

        bo.setCode(response.getCode());
        bo.setMsg(response.getMsg());
        bo.setResponseData(response.getData());
        return bo;
    }

}
