package com.magnetstreet.swt.beanwidget.datatreegrid.sorter.comparator;

import java.util.Calendar;

/**
 * CalendarPropertyComparator
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/13/11
 */
public class CalendarPropertyComparator<T> extends AbstractPropertyComparator<T, Calendar> {
    public CalendarPropertyComparator(String propertyChain) {
        super(propertyChain);
    }
}
