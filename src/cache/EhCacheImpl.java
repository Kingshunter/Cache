package cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * EhCache implementation.
 * @author Hunter
 * @createTime April 17th,2015
 * <p>
 * Ehcache is an open source, standards-based cache used to boost performance, offload the database and simplify scalability. Ehcache is
 * robust, proven and full-featured and this has made it the most widely-used Java-based cache.
 * </p>
 * 
 * @see http://ehcache.org/
 * 
 * expiration is specified in seconds
 */
public class EhCacheImpl implements CacheImpl {
    
    private static EhCacheImpl uniqueInstance;

    CacheManager cacheManager;

    Cache cache;
    
    private static ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<String, AtomicInteger>();

    private static final String cacheName = "lbsp";

    private EhCacheImpl() {
        this.cacheManager = CacheManager.create();
        this.cacheManager.addCache(cacheName);
        this.cache = cacheManager.getCache(cacheName);
    }

    public static EhCacheImpl getInstance() {
        return uniqueInstance;
    }

    public static EhCacheImpl newInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new EhCacheImpl();
        }
        return uniqueInstance;
    }

    public boolean add(String key, Object value, int expiration) {
        AtomicInteger atomicInteger = null;
    	if (cache.get(key) != null) {
            return false;
        }
    	
    	if (map.containsKey(key)) {
    		atomicInteger = map.get(key);
    		int count = atomicInteger.getAndIncrement();
    		if (count == 1) {
    			Element element = new Element(key, value);
    	        element.setTimeToLive(expiration);
    	        cache.put(element);
    	        map.remove(key);
    	        return true;
    		}
    	} else {
    		atomicInteger = new AtomicInteger(0);
    		map.put(key, atomicInteger);
    		return false;
    	}
    	return false;
    }

    public void clear() {
        cache.removeAll();
    }

    public synchronized long decr(String key, int by) {
        Element e = cache.get(key);
        if (e == null) {
            return -1;
        }
        long newValue = ((Number) e.getObjectValue()).longValue() - by;
        Element newE = new Element(key, newValue);
        newE.setTimeToLive(e.getTimeToLive());
        cache.put(newE);
        return newValue;
    }

    public void delete(String key) {
        cache.remove(key);
    }
    
    public CacheItem get(String key) {
        Element e = cache.get(key);
        if (e == null)
            return null;
        CacheItem cacheItem = new CacheItem();
        cacheItem.setData(e.getObjectValue());
        cacheItem.setCreationTime(e.getCreationTime());
        cacheItem.setExpirationTime(e.getExpirationTime());
        cacheItem.setHitCount(e.getHitCount());
        cacheItem.setLastAccessTime(e.getLastAccessTime());
        Integer version = Long.valueOf(e.getVersion()).intValue();
        AtomicInteger atomicInteger = new AtomicInteger(version);
        cacheItem.setVersion(atomicInteger);
        return cacheItem;
    }

    public Map<String, Object> get(String[] keys) {
        Map<String, Object> result = new HashMap<String, Object>(keys.length);
        for (String key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    public synchronized long incr(String key, int by) {
        Element e = cache.get(key);
        if (e == null) {
            return -1;
        }
        long newValue = ((Number) e.getObjectValue()).longValue() + by;
        Element newE = new Element(key, newValue);
        newE.setTimeToLive(e.getTimeToLive());
        cache.put(newE);
        return newValue;

    }

    public void replace(String key, Object value, int expiration) {
        if (cache.get(key) == null) {
            return;
        }
        Element element = new Element(key, value);
        element.setTimeToLive(expiration);
        cache.put(element);
    }

    public boolean safeAdd(String key, Object value, int expiration) {
        try {
            add(key, value, expiration);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean safeDelete(String key) {
        try {
            delete(key);
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public boolean safeReplace(String key, Object value, int expiration) {
        try {
            replace(key, value, expiration);
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public boolean safeSet(String key, Object value, int expiration) {
        try {
            set(key, value, expiration);
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public void set(String key, Object value, int expiration) {
        Element element = new Element(key, value);
        element.setTimeToLive(expiration);
        cache.put(element);
    }

    public void stop() {
        cacheManager.shutdown();
    }
}
