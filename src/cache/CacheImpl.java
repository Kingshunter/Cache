package cache;

import java.util.Map;

public interface CacheImpl {
    
    public boolean add(String key, Object value, int expiration);

    public boolean safeAdd(String key, Object value, int expiration);

    public void set(String key, Object value, int expiration);

    public boolean safeSet(String key, Object value, int expiration);

    public void replace(String key, Object value, int expiration);

    public boolean safeReplace(String key, Object value, int expiration);

    public CacheItem get(String key);    

    public Map<String, Object> get(String[] keys);

    public long incr(String key, int by);

    public long decr(String key, int by);

    public void clear();

    public void delete(String key);

    public boolean safeDelete(String key);

    public void stop();
}
