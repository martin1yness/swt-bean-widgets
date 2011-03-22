package com.magnetstreet.swt.beanwidget.list.exception;

/**
 * RequestedObjectNotFoundException
 *
 * Thrown whenver an object container is queried for an object that cannot befound. This
 * exception is designed to require being caught.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 12, 2009
 * @since Nov 12, 2009
 */
public class RequestedObjectNotFoundInListException extends Exception {
    public RequestedObjectNotFoundInListException() {
    }
    public RequestedObjectNotFoundInListException(String s) {
        super(s);
    }
    public RequestedObjectNotFoundInListException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public RequestedObjectNotFoundInListException(Throwable throwable) {
        super(throwable);
    }
}
