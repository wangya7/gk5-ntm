package wang.bannong.gk5.ntm.core.handler;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

import wang.bannong.gk5.cache.CacheManager;
import wang.bannong.gk5.cache.CacheResult;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.model.AuthToken;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.ResultCode;
import wang.bannong.gk5.util.SpringBeanUtils;
import wang.bannong.gk5.ntm.common.model.Subject;

public class AuthTokenHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthTokenHandler.class);

    private static CacheManager cacheManager = null;

    private static void initCacheManager() {
        if (null == cacheManager) {
            // TODO 采用持久化的Redis缓存
            cacheManager = SpringBeanUtils.getBean("cacheManager", CacheManager.class);
        }
    }

    public static NtmResult checkAuthToken(NtmApi ntmApi, NtmInnerRequest innerRequest) {
        innerRequest.setNtmApi(ntmApi);
        boolean isIa = ntmApi.getIsIa();
        NtmRequest request = innerRequest.getRequest();
        String ia = request.getIa();
        initCacheManager();
        AuthToken authToken = getAuthToken(innerRequest.getRequest().getAppid(), ia);
        innerRequest.setAuthToken(authToken);
        if (isIa) {
            if (null == authToken || TokenHandler.isExpireToken(authToken)) {
                return NtmResult.of(ResultCode.ia_invalid);
            }
        }

        AuthToken authTokenNew = updateAuthToken(authToken, ia);
        if (authTokenNew != null) {
            innerRequest.setAuthToken(authTokenNew);
            request.setIa(authTokenNew.getIa());
        }

        return NtmResult.SUCCESS;
    }

    /**
     * 创建Token
     *
     * @param subject  关联实体ID
     * @param role     角色
     * @param extend   扩展字段
     * @param request  网关请求
     */
    public static void creteAuthToken(Subject subject, AuthToken.Role role, Map<String, String> extend, NtmRequest request) {
        initCacheManager();
        String ia = TokenHandler.generateToken();
        String appid = request.getAppid();
        AuthToken authToken = new AuthToken();
        authToken.setAppid(appid);
        authToken.setIa(ia);
        authToken.setSubjectId(subject.getId());
        authToken.setMobile(subject.getMobile());
        authToken.setName(subject.getName());
        authToken.setRole(role);
        if (MapUtils.isNotEmpty(extend)) {
            authToken.setExtend(extend);
        }

        Date now = new Date();
        authToken.setCreateTime(now);
        authToken.setAccessTime(now);
        authToken.setLastAccessTime(null);

        String key = getIaKey(appid, ia);
        LOGGER.info("[IA]create token, key={}, value={}, expire={}",key, authToken, TokenHandler.TOKEN_TIMEOUT);
        cacheManager.put(key, authToken, TokenHandler.TOKEN_TIMEOUT);
        cacheManager.expire(key, TokenHandler.TOKEN_TIMEOUT);
        request.setIa(authToken.getIa());
    }

    /**
     * 更新auth token
     *
     * @param authToken authToken
     * @param ia        ia
     * @return
     */
    public static AuthToken updateAuthToken(AuthToken authToken, String ia) {
        if (authToken == null || StringUtils.isBlank(ia)) {
            return null;
        }
        initCacheManager();
        Date now = new Date();
        if (TokenHandler.isNeedUpdateToken(authToken)) {
            AuthToken authTokenNew = new AuthToken();
            authTokenNew.setSubjectId(authToken.getSubjectId());
            String iaNew = TokenHandler.generateToken();
            authTokenNew.setIa(iaNew);
            authTokenNew.setRole(authToken.getRole());
            String appid = authToken.getAppid();
            authTokenNew.setAppid(appid);


            authTokenNew.setAccessTime(now);
            authTokenNew.setCreateTime(now);
            authTokenNew.setLastAccessTime(authToken.getAccessTime());
            authTokenNew.setExtend(authToken.getExtend());

            cacheManager.del(getIaKey(appid, ia));
            String key = getIaKey(appid, iaNew);
            LOGGER.info("[IA]update token, new key={}, value={}, expire={}", key, authTokenNew, TokenHandler.TOKEN_TIMEOUT);
            cacheManager.put(key, authTokenNew, TokenHandler.TOKEN_TIMEOUT);
            cacheManager.expire(key, TokenHandler.TOKEN_TIMEOUT);

            return authTokenNew;
        } else {
            authToken.setLastAccessTime(authToken.getAccessTime());
            authToken.setAccessTime(now);
            String key = getIaKey(authToken.getAppid(), ia);
            LOGGER.info("[IA]update token, old key={}, value={}, expire={}", key, authToken, TokenHandler.TOKEN_TIMEOUT);
            cacheManager.put(key, authToken, TokenHandler.TOKEN_TIMEOUT);
            cacheManager.expire(key, TokenHandler.TOKEN_TIMEOUT);
            return authToken;
        }
    }

    private static AuthToken getAuthToken(String appid,String ia) {
        if (StringUtils.isBlank(ia)) return null;

        CacheResult<AuthToken> result = cacheManager.getObject(getIaKey( appid,ia));
        if (result.isSucc() && result.getModule() != null) {
            return result.getModule();
        }
        return null;
    }

    public static String getIaKey(String appid, String ia) {
        return String.format(TokenHandler.CACHE_TOKEN, appid, ia);
    }

    /** 退出清楚 */
    public static void clearIa(String appid,String ia) {
        cacheManager.del(getIaKey(appid,ia));
    }
}