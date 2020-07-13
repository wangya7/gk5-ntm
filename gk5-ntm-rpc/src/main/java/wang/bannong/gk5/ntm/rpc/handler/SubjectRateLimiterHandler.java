package wang.bannong.gk5.ntm.rpc.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public final class SubjectRateLimiterHandler {

    private static final String KEY_FOEMAT = "limiter_%s_%s";

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
            System.out.println("   result=" + SubjectRateLimiterHandler.isActionAllowed(subjectId, apiUnique, 1, 500));
            System.out.println("END========> " + System.currentTimeMillis());
        }
    }

}
