package wang.bannong.gk5.ntm.common.model;

import org.apache.ibatis.reflection.ArrayUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * 判断一个API是都一致，注意这里包含了业务请求参数
 * 场景举例：限流使用
 */
@Getter
@ToString
public class NtmApiKey implements Serializable {
    private static final long serialVersionUID = 1101522895730726933L;

    private static final int DEFAULT_MULTIPLYER = 37;
    private static final int DEFAULT_HASHCODE   = 17;

    private final int  multiplier;
    private       int  hashcode;
    private       long checksum;
    private       int  count;

    private List<Object> params;

    public NtmApiKey() {
        this.hashcode = DEFAULT_HASHCODE;
        this.multiplier = DEFAULT_MULTIPLYER;
        this.count = 0;
        this.params = new ArrayList<>();
    }

    public void update(Object object) {
        int baseHashCode = object == null ? 1 : ArrayUtil.hashCode(object);

        count++;
        checksum += baseHashCode;
        baseHashCode *= count;

        hashcode = multiplier * hashcode + baseHashCode;

        params.add(object);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof NtmApiKey)) {
            return false;
        }

        final NtmApiKey cacheKey = (NtmApiKey) object;

        if (hashcode != cacheKey.hashcode) {
            return false;
        }
        if (checksum != cacheKey.checksum) {
            return false;
        }
        if (count != cacheKey.count) {
            return false;
        }

        for (int i = 0; i < params.size(); i++) {
            Object thisObject = params.get(i);
            Object thatObject = cacheKey.params.get(i);
            if (!ArrayUtil.equals(thisObject, thatObject)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hashcode;
    }

}
