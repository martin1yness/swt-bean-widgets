package com.magnetstreet.swt.exception;

/**
 * BeanAnnotationException
 *
 * Thrown when a bean is not annotated properly but is being passed into the
 * dynamic GUI gernation tools.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 30, 2009
 * @since Nov 30, 2009
 */
public class BeanAnnotationException extends RuntimeException {
    public BeanAnnotationException() {
        super();
    }
    public BeanAnnotationException(String s) {
        super(s);
    }
    public BeanAnnotationException(String s, Throwable throwable) {
        super(s, throwable);
    }
    public BeanAnnotationException(Throwable throwable) {
        super(throwable);
    }
}
