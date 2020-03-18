package wang.bannong.gk5.ntm.common.dto;

import com.google.common.base.Splitter;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;

/**
 * Created by bn. on 2019/8/12 3:56 PM
 */
public class DynamicDto implements Serializable {
    private static final long serialVersionUID = 8000871365988125861L;

    private long                subjectId = 0;
    private String              channel;
    private int                 pageNum   = 1;
    private int                 pageSize  = 10;
    private Map<String, String> params;

    private DynamicDto() {
    }

    public static DynamicDto of(NtmInnerRequest innerRequest) {
        return of(innerRequest.getSubjectId(), innerRequest.getDataStore(), innerRequest.getRequest().getChannel());
    }

    public static DynamicDto of(long entityId, Map<String, String> params) {
        DynamicDto dto = new DynamicDto();
        dto.setSubjectId(entityId);
        dto.setParams(params);
        if (params != null) {
            if (StringUtils.isNotBlank(params.get(NtmConstant.PAGE_NUM))) {
                dto.setPageNum(Integer.parseInt(params.get(NtmConstant.PAGE_NUM)));
            }
            if (StringUtils.isNotBlank(params.get(NtmConstant.PAGE_SIZE))) {
                dto.setPageSize(Integer.parseInt(params.get(NtmConstant.PAGE_SIZE)));
            }
        }

        return dto;
    }

    public static DynamicDto of(long entityId, Map<String, String> params, String channel) {
        DynamicDto dto = new DynamicDto();
        dto.setSubjectId(entityId);
        dto.setChannel(channel);
        dto.setParams(params);
        if (params != null) {
            if (StringUtils.isNotBlank(params.get(NtmConstant.PAGE_NUM))) {
                dto.setPageNum(Integer.parseInt(params.get(NtmConstant.PAGE_NUM)));
            }
            if (StringUtils.isNotBlank(params.get(NtmConstant.PAGE_SIZE))) {
                dto.setPageSize(Integer.parseInt(params.get(NtmConstant.PAGE_SIZE)));
            }
        }
        return dto;
    }

    public static DynamicDto of(long entityId) {
        return of(entityId, null);
    }

    public static DynamicDto of(Map<String, String> params) {
        return of(0, params);
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    public String get(String key) {
        return params != null ? params.get(key) : null;
    }

    public int getInt(String key) {
        String v = get(key);
        return v != null ? Integer.parseInt(v) : 0;
    }

    public Integer getInteger(String key) {
        String v = get(key);
        return v != null ? Integer.valueOf(v) : 0;
    }

    public long getLongValue(String key) {
        String v = get(key);
        return v != null ? Long.parseLong(v) : 0;
    }

    public Long getLong(String key) {
        String v = get(key);
        return v != null ? Long.valueOf(v) : 0;
    }

    public boolean getBooleanValue(String key) {
        String v = get(key);
        return v != null ? Boolean.parseBoolean(v) : false;
    }

    public Boolean getBoolean(String key) {
        String v = get(key);
        return v != null ? Boolean.valueOf(v) : false;
    }

    public JSONObject getJson(String key) {
        String v = get(key);
        return v != null ? JSONObject.parseObject(v) : null;
    }


    public List<String> getList(String key, String separator) {
        String v = get(key);
        if (v == null) {
            return null;
        }
        return Splitter.on(separator).trimResults().splitToList(v);
    }

    public List<Long> getLongList(String key, String separator) {
        String v = get(key);
        if (v == null) {
            return null;
        }
        return Splitter.on(separator).trimResults().splitToList(v)
                       .stream()
                       .map(i -> Long.valueOf(i))
                       .collect(Collectors.toList());
    }

    public List<Integer> getIntegerList(String key, String separator) {
        String v = get(key);
        if (v == null) {
            return null;
        }
        return Splitter.on(separator).trimResults().splitToList(v)
                       .stream()
                       .map(i -> Integer.valueOf(i))
                       .collect(Collectors.toList());
    }

    /**
     * dto的成员变量类型有限
     *
     * @param clazz class
     * @return T model
     */
    public <T> T get(Class<T> clazz) {
        // 即使params为空也需要创建一个对象
        // if (params == null || params.size() == 0)
        //    return null;
        T t = null;
        try {
            t = clazz.newInstance();

            Field[] fields = t.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                String fieldType = field.getType().getSimpleName();
                String value = params.get(field.getName());
                if (StringUtils.isNotBlank(value)) {
                    switch (fieldType) {
                        case "String":
                            field.set(t, value);
                            break;
                        case "int":
                            field.set(t, Integer.parseInt(value));
                            break;
                        case "Integer":
                            field.set(t, Integer.valueOf(value));
                            break;
                        case "long":
                            field.set(t, Long.parseLong(value));
                            break;
                        case "Long":
                            field.set(t, Long.valueOf(value));
                            break;
                        case "Double":
                            field.set(t, Double.valueOf(value));
                            break;
                        case "double":
                            field.set(t, Double.parseDouble(value));
                            break;
                        case "byte":
                            field.set(t, Byte.parseByte(value));
                            break;
                        case "Byte":
                            field.set(t, Byte.valueOf(value));
                            break;
                        case "String[]":
                            field.set(t, Splitter.on(NtmConstant.COMMA).trimResults().splitToList(value).toArray());
                            break;
                        case "List":
                            field.set(t, Splitter.on(NtmConstant.COMMA).trimResults().splitToList(value));
                            break;
                    }
                }

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public String toString() {
        return "DynamicDto{" +
                "subjectId=" + subjectId +
                ", channel='" + channel + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", params=" + params +
                '}';
    }

}
