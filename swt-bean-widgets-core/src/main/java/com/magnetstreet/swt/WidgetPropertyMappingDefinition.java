package com.magnetstreet.swt;

import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import com.magnetstreet.swt.beanwidget.dataview.AbstractDataView;
import org.eclipse.swt.SWT;

/**
 * StringConverter
 *
 * Closure-esque stateless object for converting String values pulled in from the user to thier
 * corresponding bean property types.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 30, 2009
 * @since Nov 30, 2009
 * @param <W> The widget type
 * @param <P> The bean property type
 */
public abstract class WidgetPropertyMappingDefinition<W, P> {
    /**
     * Copies a bean property to a widget
     * @param property The bean property that is being converted
     * @param widget The widget to copy the value of the bean to
     */
    public abstract void convertPropertyToWidget(P property, W widget);

    /**
     * Takes a widget and pulls out the user entered value and returns an object of
     * the same type P as the bean property. 
     * @param widget The widget to get value from
     * @return an object of the bean property type
     * @throws ViewDataBeanValidationException When a widget does not contain a valid entry
     */
    public abstract P convertWidgetToProperty(W widget) throws ViewDataBeanValidationException;

    /**
     * Convience function for creating a widget without any style
     * @param parent
     * @return
     */
    public W createWidget(AbstractDataView parent) {
        return createWidget(parent, SWT.NONE);
    }

    /**
     * Builds a widget in the given dataview
     * @param parent The dataview that the widget will be created inside of
     * @param style The SWT style int
     * @return The widget that was created.
     */
    public abstract W createWidget(AbstractDataView parent, int style);
    
}
