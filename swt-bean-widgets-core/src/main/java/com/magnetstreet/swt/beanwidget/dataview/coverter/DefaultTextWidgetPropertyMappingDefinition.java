package com.magnetstreet.swt.beanwidget.dataview.coverter;

import com.magnetstreet.swt.WidgetPropertyMappingDefinition;
import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import com.magnetstreet.swt.beanwidget.dataview.AbstractDataView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

/**
 * DefaultTextConverter
 *
 * Used whenever a property cannot be mapped to an appropriate widget and
 * converter, doesn't allow the object to be changed, but displays the toString.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 1, 2009
 * @since Dec 1, 2009
 */
public class DefaultTextWidgetPropertyMappingDefinition extends WidgetPropertyMappingDefinition<Text, Object> {
    /**
     * {@inheritDoc}
     */
    public void convertPropertyToWidget(Object property, Text widget) {
        widget.setText(property.toString());
        widget.setData(property);
    }
    /**
     * {@inheritDoc}
     */
    public Object convertWidgetToProperty(Text widget) throws ViewDataBeanValidationException {
        return widget.getData();
    }
    /**
     * {@inheritDoc}
     */
    @Override public Text createWidget(AbstractDataView parent) {
        return createWidget(parent, SWT.BORDER);
    }
    /**
     * {@inheritDoc}
     */
    public Text createWidget(AbstractDataView parent, int style) {
        return new Text(parent, style);
    }
}
