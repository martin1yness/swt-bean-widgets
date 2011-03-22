package com.magnetstreet.swt.beanwidget.dataview.coverter;

import com.magnetstreet.swt.WidgetPropertyMappingDefinition;
import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import com.magnetstreet.swt.beanwidget.dataview.AbstractDataView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

/**
 * StringTextConverter
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 30, 2009
 * @since Nov 30, 2009
 */
public class StringTextWidgetPropertyMappingDefinition extends WidgetPropertyMappingDefinition<Text, String> {
    /**
     * {@inheritDoc}
     */
    public void convertPropertyToWidget(String property, Text widget) {
        widget.setText(property);
    }
    /**
     * {@inheritDoc}
     */
    public String convertWidgetToProperty(Text widget) throws ViewDataBeanValidationException {
        return widget.getText();
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
