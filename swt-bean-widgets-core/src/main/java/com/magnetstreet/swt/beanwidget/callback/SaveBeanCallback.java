package com.magnetstreet.swt.beanwidget.callback;

/**
 * SaveBeanCallback
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since Nov 30, 2010
 */
public interface SaveBeanCallback<T> {
    /**
     * Allows persistence operations to happen in the parent application
     * @param bean The updated bean from the dataGrid.
     * @return The updated bean after it has been persisted and refreshed.
     */
    public T doCallback(T bean);
}
