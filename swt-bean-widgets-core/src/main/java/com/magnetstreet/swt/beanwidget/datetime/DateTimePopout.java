package com.magnetstreet.swt.beanwidget.datetime;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Date Time Popout
 *
 * Provides a compact TextBox like date entry widget, which on click
 * pops open a traditional calendar widget that can be used to visually
 * select a date.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Oct 26, 2009
 * @since Oct 26, 2009
 */
public class DateTimePopout extends DateTime {
    public static AtomicBoolean popoutOpen = new AtomicBoolean(false);
    private Logger logger = Logger.getLogger(DateTimePopout.class.getSimpleName());
    protected long lastClicked = 0;
    protected Shell dialog;

    public static void main(String[] args) {
        Shell s = new Shell(Display.getDefault(), SWT.SHELL_TRIM);
        s.setLayout(new FillLayout());
        DateTimePopout dtp = new DateTimePopout(s, SWT.TIME|SWT.LONG);
        s.pack();
        s.open();
        while(!s.isDisposed())
            if(s.getDisplay().readAndDispatch()) s.getDisplay().sleep();
    }

    public DateTimePopout(Composite composite, int i) {
        super(composite, i);
        applyPopoutListener();
    }

    public DateTimePopout getSelf() { return this; }

    @Override protected void checkSubclass() {}

    public void showPopout() {
        if(popoutOpen.getAndSet(true)) {
            logger.warning("Datetime widget is already popped out, it will not honor this request to open the calendar view again until the other popout is closed.");
            return;
        }
        dialog = new Shell (getShell(), SWT.ON_TOP);
        dialog.setLocation(getXCoordPosition(), getYCoordPosition());
        dialog.setLayout (new FormLayout());

        final DateTime calendar = new DateTime (dialog, SWT.CALENDAR);
        DateTimeUtil.copyDateTime(getSelf(), calendar);
        FormData calendarLData = new FormData();
        calendarLData.left = new FormAttachment(0,100,0);
        calendarLData.right = new FormAttachment(100,100,0);
        calendarLData.top = new FormAttachment(0,100,0);
        calendarLData.bottom = new FormAttachment(100,100,-25);
        calendar.setLayoutData(calendarLData);
        final DateTime time = new DateTime (dialog, SWT.TIME);
        DateTimeUtil.copyDateTime(getSelf(), time);
        FormData timeLData = new FormData(90,20);
        timeLData.left = new FormAttachment(0,100,0);
        timeLData.bottom = new FormAttachment(100,100,0);
        time.setLayoutData(timeLData);

        Button ok = new Button (dialog, SWT.PUSH);
        ok.setText ("OK");
        FormData okLData = new FormData(50,25);
        okLData.right = new FormAttachment(100,100,0);
        okLData.bottom = new FormAttachment(100,100,0);
        ok.setLayoutData(okLData);
        ok.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                dialog.close();
            }
        });
        dialog.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                DateTimeUtil.copyDateTime(calendar, getSelf());
                DateTimeUtil.copyDateTime(time, getSelf());
                popoutOpen.set(false);
                event.doit = true;
            }
        });
        dialog.setDefaultButton (ok);
        dialog.pack ();
        dialog.open ();
    }

    public void hidePopout() {
        if(dialog!=null && !dialog.isDisposed()) {
            dialog.close();
        }
        dialog = null;
    }

    public Shell getPopout() {
        return dialog;
    }
    protected int getXCoordPosition() {
        int x = getLocation().x;
        Control parent = getParent();
        while(parent != null) {
            x += parent.getLocation().x;
            if(parent instanceof Shell) // Relative in Shell position Bug
                break;
            parent = parent.getParent();
        }

        return x + getBounds().width;
    }

    protected int getYCoordPosition() {
        int y = getLocation().y;
        Control parent = getParent();
        while(parent != null) {
            y += parent.getLocation().y;
            if(parent instanceof Shell) // Relative in Shell position Bug
                break;
            parent = parent.getParent();
        }

        return y + 2 * getBounds().height;
    }

    protected void applyPopoutListener() {
        this.addMouseListener(new MouseAdapter(){
            @Override public void mouseUp(MouseEvent mouseEvent) {
                long dbl = System.currentTimeMillis() - lastClicked;
                lastClicked = System.currentTimeMillis();
                if(dbl > 600) return;
                showPopout();
            }
        });
    }
}
