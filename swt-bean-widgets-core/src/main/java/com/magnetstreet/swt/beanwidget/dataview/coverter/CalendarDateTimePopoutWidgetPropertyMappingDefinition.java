package com.magnetstreet.swt.beanwidget.dataview.coverter;

import com.magnetstreet.swt.WidgetPropertyMappingDefinition;
import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import com.magnetstreet.swt.beanwidget.dataview.AbstractDataView;
import com.magnetstreet.swt.beanwidget.datetime.DateTimePopout;
import com.magnetstreet.swt.beanwidget.datetime.DateTimeUtil;
import org.eclipse.swt.SWT;

import java.util.Calendar;

/**
 * CalendarDateTimePopoutConverter
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 30, 2009
 * @since Nov 30, 2009
 */
public class CalendarDateTimePopoutWidgetPropertyMappingDefinition extends WidgetPropertyMappingDefinition<DateTimePopout, Calendar> {
    /**
     * {@inheritDoc}
     */
    public void convertPropertyToWidget(Calendar property, DateTimePopout widget) {
        DateTimeUtil.setDateTime(widget, property);
    }
    /**
     * {@inheritDoc}
     */
    public Calendar convertWidgetToProperty(DateTimePopout widget) throws ViewDataBeanValidationException {
        return DateTimeUtil.getCalendar(widget);
    }
    /**
     * {@inheritDoc}
     */
    @Override public DateTimePopout createWidget(AbstractDataView parent) {
        return createWidget(parent, SWT.BORDER);
    }
    /**
     * {@inheritDoc}
     */
    public DateTimePopout createWidget(AbstractDataView parent, int style) {
        return new DateTimePopout(parent, style);
    }
}
