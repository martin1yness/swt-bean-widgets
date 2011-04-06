package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.exception.InvalidGridViewSetupException;
import com.magnetstreet.swt.util.DateUtil;
import com.magnetstreet.swt.util.TableColumnComparator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    public Method beanPropertyGetter = null;
    public int widthHint = 40;
    public TableColumnComparator sortAlgorithm = null;
    public boolean editable = true;
    public boolean visible = true;

    public GridColumn(String title, Field beanProperty, int widthHint) {
        this.title = title;
        this.beanProperty = beanProperty;
        try {
            this.beanPropertyGetter = beanProperty.getDeclaringClass().getMethod("get"+Character.toUpperCase(beanProperty.getName().charAt(0))+beanProperty.getName().substring(1));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Given bean property '"+beanProperty.getName()+"' does not have a tradtional getter.");
        }
        this.widthHint = widthHint;
    }

    public Object getDisplayValue(T bean) {
        try {
            beanPropertyGetter.setAccessible(true);
            Object propVal = beanPropertyGetter.invoke(bean);
            if(beanProperty.getType() == Calendar.class)
                return DateUtil.getDefaultDateTimeFormatted((Calendar)propVal);
            if(beanProperty.getType() == BigDecimal.class)
                return (propVal==null) ? "" : ((BigDecimal)propVal).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            else
                return propVal;
        } catch(Throwable e) {
            throw new InvalidGridViewSetupException("Getter '"+beanPropertyGetter.getName()+"' for member name '"+beanProperty.getName()+"' of column '"+title +"' could not be dynamically loaded.", e);
        }
    }
}
