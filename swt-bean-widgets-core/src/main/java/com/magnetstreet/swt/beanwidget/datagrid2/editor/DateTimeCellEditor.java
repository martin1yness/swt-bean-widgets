package com.magnetstreet.swt.beanwidget.datagrid2.editor;

import com.magnetstreet.swt.beanwidget.datetime.DateTimePopout;
import com.magnetstreet.swt.beanwidget.datetime.DateTimeUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;

import java.util.Calendar;

/**
 * DateTimeCellEditor
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/2/11
 */
public class DateTimeCellEditor extends CellEditor {

    public DateTimeCellEditor() {
        super();
    }
    public DateTimeCellEditor(Composite parent) {
        super(parent);
    }
    public DateTimeCellEditor(Composite parent, int style) {
        super(parent, style);
    }

    @Override protected Control createControl(Composite parent) {
        return new DateTimePopout(parent, getStyle());
    }
    @Override protected Object doGetValue() {
        return DateTimeUtil.getCalendar((DateTime)getControl());
    }
    @Override protected void doSetFocus() {
        getControl().setFocus();
    }
    @Override protected void doSetValue(Object value) {
        DateTimeUtil.setDateTime((DateTime)getControl(), (Calendar)value);
    }

    @Override public void activate() {
        ((DateTimePopout)getControl()).showPopout();
    }
}
