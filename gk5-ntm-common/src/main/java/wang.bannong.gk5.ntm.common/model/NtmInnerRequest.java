package wang.bannong.gk5.ntm.common.model;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import wang.bannong.gk5.ntm.common.domain.NtmApi;

public class NtmInnerRequest {

    private NtmRequest          request;
    private AuthToken           authToken;
    private long                subjectId = 0;
    private NtmApi              ntmApi;
    private Map<String, String> dataStore = new HashMap<>();

    public static NtmInnerRequest of(NtmRequest request) {
        NtmInnerRequest innerRequest = new NtmInnerRequest();
        innerRequest.setRequest(request);

        Map<String, String> dataMap = request.getData();
        if (MapUtils.isNotEmpty(dataMap)) {
            Map<String, String> dataStore = new HashMap<>();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                String key, value;
                if (StringUtils.isNotBlank(key = entry.getKey()) && StringUtils.isNotBlank(value = entry.getValue())) {
                    dataStore.put(key, value);
                }
            }
            innerRequest.setDataStore(dataStore);
        }

        return innerRequest;
    }

    public NtmRequest getRequest() {
        return request;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public NtmApi getNtmApi() {
        return ntmApi;
    }

    public String get(String key) {
        return dataStore.get(Objects.requireNonNull(key, "inner-request key cannot be null"));
    }

    public Map<String, String> getDataStore() {
        return dataStore;
    }

    private void setRequest(NtmRequest request) {
        this.request = request;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
        if (authToken != null) {
            this.subjectId = authToken.getSubjectId();
        }
    }

    public void setNtmApi(NtmApi ntmApi) {
        this.ntmApi = ntmApi;
    }

    private void setDataStore(Map<String, String> dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public String toString() {
        return "NtmInnerRequest{" +
                "request=" + request +
                ", authToken=" + authToken +
                ", subjectId=" + subjectId +
                ", ntmApi=" + ntmApi +
                ", dataStore=" + dataStore +
                '}';
    }
}