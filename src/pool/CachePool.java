package pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class CachePool {
    
    private static CachePool pool;

    private static ThreadPoolExecutor es;

    private Object lock = new Object();

    private CachePool() {
        synchronized (lock) {
            if (es == null) {
                es = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
            }
        }
    }

    public static CachePool init() {
        if (pool == null) {
            pool = new CachePool();
        }
        return pool;
    }

    public void execute(Runnable runnable) {
        try {
            if (!es.isShutdown()) {
                es.execute(runnable);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            es.remove(runnable);
        }
    }
}
