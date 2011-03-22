package com.magnetstreet.swt.exception;

/**
 * InvalidGridViewSetupException
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 17, 2009
 * @since Dec 17, 2009
 */
public class InvalidGridViewSetupException extends RuntimeException {
    public InvalidGridViewSetupException() {
    }
    public InvalidGridViewSetupException(String s) {
        super(s);
    }
    public InvalidGridViewSetupException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public InvalidGridViewSetupException(Throwable throwable) {
        super(throwable);
    }
}
