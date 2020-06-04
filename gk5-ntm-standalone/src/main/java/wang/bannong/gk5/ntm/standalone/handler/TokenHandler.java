package wang.bannong.gk5.ntm.standalone.handler;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

import wang.bannong.gk5.ntm.common.model.AuthToken;

public class TokenHandler {

    /** APP 一周 */
    public final static int UPDATE_HOUR_SECONDS = 345600; // 四天
    public final static int EXPIRE_HOUR_SECONDS = 604800; // 一周

    /** ia:appid:token */
    public final static String CACHE_TOKEN   = "ia:%s:%s";
    public final static long   TOKEN_TIMEOUT = 604800;

    /** ADMIN 15分钟 */
    public final static int UPDATE_HOUR_SECONDS_ADMIN = 1000;
    public final static int EXPIRE_HOUR_SECONDS_ADMIN = 2000;

    public static String generateToken() {
        String param = UUID.randomUUID().toString();
        String tmp = UUID.fromString(UUID.nameUUIDFromBytes(param.getBytes()).toString()).toString();
        return StringUtils.remove(tmp, "-");
    }

    /**
     * 判断token信息是否失效
     *
     * @param authToken
     * @return
     */
    public static boolean isExpireToken(AuthToken authToken) {
        long now = System.currentTimeMillis();
        long accessMills = authToken.getAccessTime().getTime();
        long threshold = 0L;
        if (AuthToken.Role.user == authToken.getRole()) {
            threshold = EXPIRE_HOUR_SECONDS;
        } else {
            threshold = EXPIRE_HOUR_SECONDS_ADMIN;
        }

        if ((now - accessMills) / 1000 > threshold) return true;
        return false;
    }

    /**
     * 改变token，但不退出登录
     *
     * @param authToken
     * @return
     */
    public static boolean isNeedUpdateToken(AuthToken authToken) {
        long now = System.currentTimeMillis();
        long accessMills = authToken.getAccessTime().getTime();
        long threshold = 0L;
        if (AuthToken.Role.user == authToken.getRole()) {
            threshold = UPDATE_HOUR_SECONDS;
        } else {
            threshold = UPDATE_HOUR_SECONDS_ADMIN;
        }

        if ((now - accessMills) / 1000 > threshold) return true;

        return false;
    }

}
