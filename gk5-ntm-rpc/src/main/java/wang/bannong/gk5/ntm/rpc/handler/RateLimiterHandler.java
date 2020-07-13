package wang.bannong.gk5.ntm.rpc.handler;

import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterHandler {

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


    public static void main(String[] args) {
        RateLimiterHandler handler = new RateLimiterHandler(5, 2000);
        for (int i = 0; i < 12; i++) {
            System.out.println("BEGIN======> " + System.currentTimeMillis());
            System.out.println(handler.isActionAllowed());
            System.out.println("END========> " + System.currentTimeMillis());
            if (4 == i) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
