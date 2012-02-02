package com.magnetstreet.swt.beanwidget.datagrid2.editor;

import com.magnetstreet.swt.beanwidget.datetime.DateTimeUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
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
    private DateTime dateTime;

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
        dateTime = new DateTime(parent, getStyle());

        dateTime.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });
        dateTime.addTraverseListener(new TraverseListener() {
            @Override public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });
        dateTime.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                DateTimeCellEditor.this.focusLost();
            }
        });

        return dateTime;
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


}
