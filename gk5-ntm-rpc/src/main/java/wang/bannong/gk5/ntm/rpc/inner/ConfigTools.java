package wang.bannong.gk5.ntm.rpc.inner;

import com.google.common.base.Splitter;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import wang.bannong.gk5.ntm.common.domain.NtmConfigSetting;
import wang.bannong.gk5.ntm.rpc.dao.NtmConfigSettingDao;
import wang.bannong.gk5.util.Constant;
import wang.bannong.gk5.util.SpringBeanUtils;

/**
 * Created by bn. on 2018/8/4 下午3:12
 */
public final class ConfigTools {

    private static NtmConfigSettingDao masterNtmConfigSettingDao;

    public static String get(String key) {
        Objects.requireNonNull(key, "key cannot be null");
        if (null == masterNtmConfigSettingDao) {
            masterNtmConfigSettingDao = SpringBeanUtils.getBean("masterNtmConfigSettingDao", NtmConfigSettingDao.class);
        }
        try {
            NtmConfigSetting cs = masterNtmConfigSettingDao.selectValueByKeyEff(key);
            return null != cs ? cs.getConfigValue() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get(String key, String valueDefault) {
        String s = get(key);
        return StringUtils.isNotBlank(s) ? s : valueDefault;
    }


    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static int getInt(String key, int valueDefault) {
        String s = get(key);
        return StringUtils.isNotBlank(s) ? Integer.parseInt(s) : valueDefault;
    }

    public static Integer getIntValue(String key) {
        return Integer.valueOf(get(key));
    }

    public static Integer getIntValue(String key, Integer valueDefault) {
        String s = get(key);
        return StringUtils.isNotBlank(s) ? Integer.valueOf(s) : valueDefault;
    }

    public static byte getByte(String key) {
        return Byte.parseByte(get(key));
    }

    public static byte getByte(String key, byte valueDefault) {
        String s = get(key);
        return StringUtils.isNotBlank(s) ? Byte.parseByte(s) : valueDefault;
    }

    public static Byte getByteValue(String key) {
        return Byte.valueOf(get(key));
    }

    public static Byte getByteValue(String key, Byte valueDefault) {
        String s = get(key);
        return StringUtils.isNotBlank(s) ? Byte.valueOf(s) : valueDefault;
    }

    public static long getLong(String key) {
        return Long.parseLong(get(key));
    }

    public static long getLong(String key, long valueDefault) {
        String s = get(key);
        return StringUtils.isNotBlank(s) ? Long.parseLong(s) : valueDefault;
    }

    public static Long getLongValue(String key) {
        return Long.valueOf(get(key));
    }

    public static Long getLongValue(String key, Long valueDefault) {
        String s = get(key);
        return StringUtils.isNotBlank(s) ? Long.valueOf(s) : valueDefault;
    }

    public static JSONObject getJSONObject(String key) {
        String s = get(key);
        if (StringUtils.isNotBlank(s)) {
            return JSONObject.parseObject(s);
        }
        return null;
    }


    /**
     * 分隔符 ","
     *
     * @param key       配置
     * @return
     */
    public static Set<String> getSet(String key) {
        return getSet(key, Constant.COMMA);
    }

    /**
     * @param key       配置
     * @param seperator 分隔符
     * @return
     */
    public static Set<String> getSet(String key, String seperator) {
        String s = get(key);
        if (StringUtils.isBlank(s)) {
            return Collections.emptySet();
        }
        return new HashSet<>(Splitter.on(seperator).splitToList(key));
    }


    public static final String INSURANCE_DETAIL_URL = "INSURANCE_DETAIL_URL";
    public static final String INSURANCE_DETAIL_URL_DEFAULT = "http://web-devp.moguyunbao.com/orderDetail.html?id=%s";

    public static String insuranceDetail(String... args) {
        String url = get(INSURANCE_DETAIL_URL, INSURANCE_DETAIL_URL_DEFAULT);
        return String.format(url, args);
    }

    public static Boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static Boolean getBoolean(String key, Boolean valueDefault) {
        String s = get(key);
        return StringUtils.isNotBlank(s) ? Boolean.parseBoolean(s) : valueDefault;
    }

}
