package com.magnetstreet.swt.exception;

/**
 * Invalid Grid Style Exception
 *
 * Throw in case of a mis-configured grid, as all wigets accept a style integer defining hints or functionality.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 2011-11-10
 */
public class InvalidGridStyleException extends RuntimeException {
    public InvalidGridStyleException() {
    }

    public InvalidGridStyleException(String message) {
        super(message);
    }

    public InvalidGridStyleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGridStyleException(Throwable cause) {
        super(cause);
    }
}
