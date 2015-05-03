package cache;

import pool.CachePool;

/**
 * 
 * @author Hunter
 * @createDate April 20th,2015
 *
 */
public class LocalAutoCacheRefreshManager extends AutoCacheRefreshManager {
    
    public LocalAutoCacheRefreshManager(String cacheKey, String prefix, DataOperation dataOperation) {
        super(cacheKey, prefix, dataOperation);
    }

    @SuppressWarnings("unchecked")
    public <T> T operateCache() {
        CacheItem cacheItem = Cache.get(cacheKey);
        Object data = null;
        if (cacheItem == null) {
            data = dataOperation.operate();
            if (data != null) {
                disposeCacheData(data); 
            }
        } else {
            data = cacheItem.getData();
            long lastAccessTime = cacheItem.getLastAccessTime();
            long expirationTime = cacheItem.getExpirationTime();
            if (expirationTime - lastAccessTime <= CacheConstants.CACHE_ALARM_TIME) {
                if (hasUpdatePermission(atomKey)) {
                    CacheRefreshWorker cacheRefreshWorker = new AutoCacheRefreshWorker();
                    CachePool pool = CachePool.init();
                    pool.execute(cacheRefreshWorker);
                }
            }
        }
        return (T) data;
    }

    protected boolean hasUpdatePermission(String atomKey) {
        return Cache.add(atomKey, 1, "10s");
    }
    
    
    class AutoCacheRefreshWorker extends CacheRefreshWorker {
        
    }

    
}
