package cache;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * @author Hunter
 * @createDate April 20th,2015
 */
public abstract class AutoCacheRefreshManager {
    private static Logger logger = Logger.getLogger("xx");

    private char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F' };

    protected static final String OBJECT_EXPIRE_KEY_PREFIX = "object_expire_key_";

    /**
     * The cache key.
     */
    protected String cacheKey;

    /**
     * The interface for refreshing data operation.
     */
    protected DataOperation dataOperation;

    /**
     * Prefix key of cache key.
     */
    protected String prefix;

    /**
     * The lock timeout , unit:second
     */
    protected final String LOCKKEY_TIMEOUT = "10s";

    /**
     * The alarm time, unit:second
     */
    protected final Integer expireTime = 600;
    /**
     * A distributed lock key.
     */
    protected final String atomKey;

    /**
     * The constructor of CacheManager
     * 
     * @param dataKey Cache key.
     * @param prefix Prefix key of the cache key.
     * @param dataOperation Define the data operation for add or update the cache item.
     */
    public AutoCacheRefreshManager(String cacheKey, String prefix, DataOperation dataOperation) {
        this.prefix = prefix;
        this.dataOperation = dataOperation;
        // Replace blank character to prevent mc occur invalid key error.
        this.cacheKey = cacheKey.trim();
        this.atomKey = OBJECT_EXPIRE_KEY_PREFIX + encryptKey(cacheKey);
    }

    public abstract <T> T operateCache();

    /**
     * Whether has the permission to update the cache and utilize the mc's add command to implement the distributed lock mechanism.
     */
    protected abstract boolean hasUpdatePermission(String atomKey);

    protected void disposeCacheData(Object data) {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("refreshing cache start");
            }
            if (data == null) {
                Cache.delete(cacheKey);
            } else {
                Cache.set(cacheKey, data, "2h");
            }
            if (logger.isInfoEnabled()) {
                logger.info("refreshing cache end");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Encrypt key using the md5 algorithm.
     */
    protected String encryptKey(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] b = md.digest(key.getBytes("utf-8"));
            int j = b.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = b[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            logger.error("no such Algorithm");
        } catch (UnsupportedEncodingException e) {
            logger.error("unsupport encoding");
        }
        return null;
    }

    protected abstract class CacheRefreshWorker implements Runnable {

        public void run() {
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("asynchronous thread start");
                }
                Object data = dataOperation.operate();
                disposeCacheData(data);
                //删除临时缓存key
                Cache.delete(atomKey);
                if (logger.isInfoEnabled()) {
                    logger.info("asynchronous thread end");
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        
    }

}
