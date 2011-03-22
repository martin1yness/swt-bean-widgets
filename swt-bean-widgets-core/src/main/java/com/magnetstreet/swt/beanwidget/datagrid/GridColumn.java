package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.exception.InvalidGridViewSetupException;
import com.magnetstreet.swt.util.DateUtil;
import com.magnetstreet.swt.util.TableColumnComparator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * GridColumn
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 17, 2009
 * @since Dec 17, 2009
 */
public class GridColumn<T> {
    public String title = "";
    public Field beanProperty = null;
    public int widthHint = 40;
    public TableColumnComparator sortAlgorithm = null;
    public boolean editable = true;
    public boolean visible = true;

    public GridColumn(String title, Field beanProperty, int widthHint) {
        this.title = title;
        this.beanProperty = beanProperty;
        this.widthHint = widthHint;
    }

    public Object getDisplayValue(T bean) {
        try {
            beanProperty.setAccessible(true);
            if(beanProperty.getType() == Calendar.class)
                return DateUtil.getDefaultDateTimeFormatted((Calendar)beanProperty.get(bean));
            if(beanProperty.getType() == BigDecimal.class)
                return (((BigDecimal)beanProperty.get(bean))==null) ? "" : ((BigDecimal)beanProperty.get(bean)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            else
                return beanProperty.get(bean);
        } catch(Throwable e) {
            throw new InvalidGridViewSetupException("Getter for member name '"+beanProperty.getName()+"' of column '"+title +"' could not be dynamically loaded.", e);
        }
    }
}
