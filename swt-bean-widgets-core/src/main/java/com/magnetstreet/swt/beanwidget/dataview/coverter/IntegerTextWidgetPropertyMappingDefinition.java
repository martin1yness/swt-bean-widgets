package com.magnetstreet.swt.beanwidget.dataview.coverter;

import com.magnetstreet.swt.WidgetPropertyMappingDefinition;
import com.magnetstreet.swt.beanwidget.dataview.AbstractDataView;
import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import java.util.logging.Logger;

/**
 * StringTextConverter
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 30, 2009
 * @since Nov 30, 2009
 */
public class IntegerTextWidgetPropertyMappingDefinition extends WidgetPropertyMappingDefinition<Text, Integer> {
    private Logger logger = Logger.getLogger(IntegerTextWidgetPropertyMappingDefinition.class.getSimpleName());
    /**
     * {@inheritDoc}
     */
    public void convertPropertyToWidget(Integer property, Text widget) {
        widget.setText(property.toString());
    }
    /**
     * {@inheritDoc}
     */
    public Integer convertWidgetToProperty(Text widget) throws ViewDataBeanValidationException {
        try {
            return new Integer(widget.getText());
        } catch(NumberFormatException nfe) {
            throw new ViewDataBeanValidationException("Widget value '"+widget.getText()+"' is not a valid number, data is incorrect or widget mapping to Integer is incorrect.", nfe);
        }
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
