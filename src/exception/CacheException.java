package exception;

/**
 * 
 * @author Hunter
 * @createDate April 20th,2015
 *
 */
public class CacheException extends RuntimeException {
    
    private static final long serialVersionUID = 2371033813954986340L;

    private String message;
    
    private Throwable cause;

    public CacheException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
    
    
}
