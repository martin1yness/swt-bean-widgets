/**
 * Copyright &copy; 2009 MagnetStreet <magnetstreet.com>
 */
package com.magnetstreet.swt.beanwidget.datetime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;

import java.util.Calendar;

/**
 * DateTimeUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Oct 21, 2009
 * @since Oct 21, 2009
 */
public class DateTimeUtil {
    /**
     * @param widget The DateTime widget to convert to calendar
     * @return a Calendar object that represents the date and time of the given widget.
     */
    public static Calendar getCalendar(DateTime widget) {
        Calendar r = Calendar.getInstance();
        r.set(Calendar.YEAR, widget.getYear());
        r.set(Calendar.MONTH, widget.getMonth());
        r.set(Calendar.DAY_OF_MONTH, widget.getDay());
        r.set(Calendar.HOUR_OF_DAY, widget.getHours());
        r.set(Calendar.MINUTE, widget.getMinutes());
        r.set(Calendar.SECOND, widget.getSeconds());
        return r;
    }

    /**
     * Updates a DateTime widget with the data in a calendar object 
     * @param widget
     * @param cal
     */
    public static void setDateTime(DateTime widget, Calendar cal) {
        if(cal == null || widget == null) return;
        widget.setYear(cal.get(Calendar.YEAR));
        widget.setMonth(cal.get(Calendar.MONTH));
        widget.setDay(cal.get(Calendar.DAY_OF_MONTH));
        widget.setHours(cal.get(Calendar.HOUR_OF_DAY));
        widget.setMinutes(cal.get(Calendar.MINUTE));
        widget.setSeconds(cal.get(Calendar.SECOND));
    }

    /**
     * Copies the selected data from One Date time to the other
     * based on its type.
     * @param from The datetime to pull data from
     * @param to The datetime to set the data to
     */
    public static void copyDateTime(DateTime from, DateTime to) {
        if( (from.getStyle() & SWT.DATE) != 0 ) {
            to.setYear(from.getYear());
            to.setMonth(from.getMonth());
            if( (from.getStyle() & SWT.SHORT) == 0 )
                to.setDay(from.getDay());
        } else if( (from.getStyle() & SWT.TIME) != 0 ) {
            to.setHours(from.getHours());
            to.setMinutes(from.getMinutes());
            if( (from.getStyle() & SWT.SHORT) == 0 )
                to.setSeconds(from.getSeconds());
        } else if( (from.getStyle() & SWT.CALENDAR) != 0 ) {
            to.setYear(from.getYear());
            to.setMonth(from.getMonth());
            to.setDay(from.getDay());
        }
    }
}
