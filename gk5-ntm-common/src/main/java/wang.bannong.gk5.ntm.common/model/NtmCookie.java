package wang.bannong.gk5.ntm.common.model;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Data;

import static wang.bannong.gk5.ntm.common.constant.NtmConstant.ROOT_PATH;

@Data
public class NtmCookie implements Serializable {
    private static final long serialVersionUID = -9089182204209902339L;

    private Map<String, Cookie> cookieMap;

    public static NtmCookie of(HttpServletRequest servletRequest) {
        NtmCookie ntmCookie = new NtmCookie();
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = servletRequest.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        ntmCookie.cookieMap = cookieMap;
        return ntmCookie;
    }

    public Cookie getCookie(String name) {
        return StringUtils.isNotBlank(name) ? cookieMap.get(name) : null;
    }

    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        return cookie != null ? cookie.getValue() : null;
    }

    public boolean delCookie(HttpServletResponse servletResponse, String name) {
        if (MapUtils.isNotEmpty(cookieMap) && cookieMap.containsKey(name)) {
            Cookie cookie = cookieMap.get(name);
            cookie.setValue(null);
            cookie.setMaxAge(0);
            cookie.setPath(ROOT_PATH);
            servletResponse.addCookie(cookie);
            return true;
        }
        return false;
    }


    public boolean setCookie(HttpServletResponse servletResponse, Map<String, String> kv, int expiry, String domain) {
        if (MapUtils.isNotEmpty(kv)) {
            Set<Map.Entry<String, String>> entries = kv.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                Cookie cookie = new Cookie(entry.getKey(), entry.getValue());
                cookie.setPath(ROOT_PATH);
                cookie.setDomain(domain);
                cookie.setMaxAge(expiry);
                servletResponse.addCookie(cookie);
            }
            return true;
        }
        return false;
    }

}
