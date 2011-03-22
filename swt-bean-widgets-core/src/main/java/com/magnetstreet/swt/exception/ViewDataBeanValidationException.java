package com.magnetstreet.swt.exception;

/**
 * View Data Bean Validation Exception
 *
 * Thrown when a data view updates its bean with control input from a user
 * that is invalid. This means that an errorMap control gets set that is retrievable
 * and the bean is not updated.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Aug 7, 2009
 * @since Aug 7, 2009
 */
public class ViewDataBeanValidationException extends Exception {
    public ViewDataBeanValidationException() {
    }
    public ViewDataBeanValidationException(String s) {
        super(s);
    }
    public ViewDataBeanValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public ViewDataBeanValidationException(Throwable throwable) {
        super(throwable);
    }
}
