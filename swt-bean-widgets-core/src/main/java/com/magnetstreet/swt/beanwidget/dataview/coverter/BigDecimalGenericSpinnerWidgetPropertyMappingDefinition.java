package com.magnetstreet.swt.beanwidget.dataview.coverter;

import com.magnetstreet.swt.WidgetPropertyMappingDefinition;
import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import com.magnetstreet.swt.beanwidget.dataview.AbstractDataView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Spinner;

import java.math.BigDecimal;

/**
 * Big Decimal Spinner
 *
 * Statically defined implementation of a converter for BigDecimal
 * and the Spinner widget.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 30, 2009
 * @since Nov 30, 2009
 */
public class BigDecimalGenericSpinnerWidgetPropertyMappingDefinition extends WidgetPropertyMappingDefinition<Spinner, BigDecimal> {
    /**
     * {@inheritDoc}
     */
    public void convertPropertyToWidget(BigDecimal property, Spinner widget) {
        int decimalPoints = property.scale();
        widget.setValues(property.unscaledValue().intValue(), 0, Integer.MAX_VALUE, decimalPoints, 1, (int)Math.pow(10, decimalPoints));
    }
    /**
     * {@inheritDoc}
     */
    public BigDecimal convertWidgetToProperty(Spinner widget) throws ViewDataBeanValidationException {
        try {
            return new BigDecimal(widget.getText());
        } catch(NumberFormatException nfe) {
            throw new ViewDataBeanValidationException(nfe);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override public Spinner createWidget(AbstractDataView parent) {
        return createWidget(parent, SWT.BORDER);
    }
    /**
     * {@inheritDoc}
     */
    public Spinner createWidget(AbstractDataView parent, int style) {
        return new Spinner(parent, style);
    }
}
