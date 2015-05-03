package cache;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hunter
 * @createDate April 20th,2015
 * 
 */
public class CacheItem implements Serializable {

    private static final long serialVersionUID = -1069906283629841117L;

    private Object data;

    private long hitCount;

    private Long creationTime;

    private Long expirationTime;
    
    private Long lastAccessTime;

    private AtomicInteger version;

    public CacheItem() {
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public AtomicInteger getVersion() {
        return version;
    }

    public void setVersion(AtomicInteger version) {
        this.version = version;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public Long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public String toString() {
        return "CacheItem [data=" + data + ", hitCount=" + hitCount + ", creationTime="
                + creationTime + ", expirationTime=" + expirationTime + ", lastAccessTime="
                + lastAccessTime + ", version=" + version + "]";
    }

}
