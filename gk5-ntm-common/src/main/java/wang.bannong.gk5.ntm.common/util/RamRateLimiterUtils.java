package wang.bannong.gk5.ntm.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于内存的简单过滤器
 */
public final class RamRateLimiterUtils {
    
    public static final String KEY_FOEMAT = "limiter_%s_%s";

    /**
     * 大对象    空间=用户量*所有操作*SIZE(RateLimiterHandler)
     */
    private static Map<String, RateLimiterHandler> limiterHub = new ConcurrentHashMap<>();

    /**
     * 同一个用户对同一个操作限流
     *
     * @param subjectId 用户
     * @param apiUnique 操作
     * @param maxCount  最大操作数
     * @param periodMs  周期 毫秒
     */
    public static boolean isActionAllowed(String subjectId, String apiUnique, int maxCount, long periodMs) {
        String key = String.format(KEY_FOEMAT, subjectId, apiUnique);
        RateLimiterHandler handler = limiterHub.get(key);
        if (handler == null) {
            handler = new RateLimiterHandler(maxCount, periodMs);
            limiterHub.put(key, handler);

        }
        return handler.isActionAllowed();
    }


    public static class RateLimiterHandler {

        private final int  maxCount;
        private final long periodMs;

        private AtomicInteger count      = new AtomicInteger(0);
        private long          recentlyTs = System.currentTimeMillis();

        public RateLimiterHandler(int maxCount, long periodMs) {
            this.maxCount = maxCount;
            this.periodMs = periodMs;
        }

        /**
         * 同一个ip对所有的api做限流
         * 毫秒级别的限流
         */
        public boolean isActionAllowed() {
            count.getAndAdd(1);
            // 清空
            if (count.get() == 1) {
                recentlyTs = System.currentTimeMillis();
                return true;
            }

            // 超过周期限制，刷新
            long nowTs = System.currentTimeMillis();
            if (nowTs - recentlyTs > periodMs) {
                count.set(1);
                recentlyTs = nowTs;
                return true;
            }

            // 还在间隔时间内，比较超过限流的个数
            return maxCount >= count.get();
        }
    }

    public static void main(String[] args) {
        List<String> subjectIds = Arrays.asList("9527", "9677");
        List<String> ops = Arrays.asList("ADD", "MDF", "DEL");
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            int pos = random.nextInt(subjectIds.size());
            System.out.println("BEGIN======> " + System.currentTimeMillis());
            String subjectId = subjectIds.get(pos);
            pos = random.nextInt(ops.size());
            String apiUnique = ops.get(pos);
            System.out.println("   " + subjectId + "_" + apiUnique);
            System.out.println("   result=" + RamRateLimiterUtils.isActionAllowed(subjectId, apiUnique, 1, 500));
            System.out.println("END========> " + System.currentTimeMillis());
        }
    }

}
