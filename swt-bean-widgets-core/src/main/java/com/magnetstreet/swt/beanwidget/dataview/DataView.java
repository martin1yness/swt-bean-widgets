package com.magnetstreet.swt.beanwidget.dataview;

import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * DataView
 *
 * Represents a data view that is attached to some type of data bean.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jul 28, 2009
 * @since Jul 28, 2009
 */
public interface DataView<T> {
    /**
     * Set whether elements can be edited or not, works globally.
     */
    public void toggleWidgetsEditable();

    /**
     * @return The data bean with any modifications made by the user.
     * @throws ViewDataBeanValidationException Occurs when the object can't be validated
     */
    public T getViewDataObject() throws ViewDataBeanValidationException;

    /**
     * Sets the data the view displays to the user for either readOnly or editable
     * access.
     * @param viewDataObject The data bean that data will be pulled from and saved to.
     */
    public void setViewDataObject(T viewDataObject);

    /**
     * Resets all controls that have been modified to the most current state of the
     * backing data bean.
     */
    public void resetViewDataObject();

    /**
     * @return The errorMap created during control validation phase when attempting to
     *         retrieve a data bean from a view.
     */
    public Map<Control, String> getValidationErrorMap();

    /**
     * pops up a display showing the validation error map to the user.
     */
    public void showInputErrors();

    /**
     * Searches for the widget mapped to a specific bean property that this view
     * represents.
     * @param beanProperty The field object to search for the widget for
     * @return The control representing the widget being searched for.
     */
    public Control findWidget(Field beanProperty);

    public Control[] getChildren();
    public void setLayout(Layout layout);
    public void pack();
}