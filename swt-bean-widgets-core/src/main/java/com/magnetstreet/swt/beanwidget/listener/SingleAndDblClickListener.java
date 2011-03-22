package com.magnetstreet.swt.beanwidget.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Display;


/**
 * A speical listener for allowing both single and double click actions
 * to spawn mutally exclusive actions. Int
 * @author Martin Dale Lyness<martin.lyness@gmail.com>
 */
public abstract class SingleAndDblClickListener implements MouseListener {
    private Log log = LogFactory.getLog(SingleAndDblClickListener.class);

    public static int dblClickWaitTime = Display.getDefault().getDoubleClickTime();
    private static long lastClick = 0L;
    private DblClickInterruptable interruptable = new DblClickInterruptable(null);

    protected abstract void handlePreSingleClick(MouseEvent mouseEvent);
    protected abstract void handleSingleClick(MouseEvent mouseEvent);
    protected abstract void handleDoubleClick(MouseEvent mouseEvent);

    private class DblClickInterruptable implements Runnable {
        private boolean ran = false;
        protected boolean doit = true;
        protected MouseEvent mouseEvent;
        public DblClickInterruptable(MouseEvent evt) { this.mouseEvent = evt; }
        public void run() {
            log.debug("validating single click, doit: " + doit + " - " + this);
            if(doit && !ran) {
                handleSingleClick(mouseEvent);
                log.debug("Single click executed " + this);
                ran = true;
            }
        }
        public void interrupt() { doit = false; log.debug("Interrupting " + this); }
    }

    public void mouseDoubleClick(MouseEvent mouseEvent) { }
    public void mouseDown(MouseEvent mouseEvent) {}
    public void mouseUp(final MouseEvent mouseEvent) {
        log.debug("a click occured" + (System.currentTimeMillis() - lastClick - dblClickWaitTime ));
        if(System.currentTimeMillis() - lastClick - dblClickWaitTime <= 0) {            
            interruptable.interrupt();
            lastClick = 0L;
            handleDoubleClick(mouseEvent);
            log.debug("Dbl click executed " + this);
        } else {
            interruptable = new DblClickInterruptable(mouseEvent);
            lastClick = System.currentTimeMillis();
            new Thread(new Runnable(){
                public void run() {
                    log.debug("single click execution possibility created " + interruptable);
                    Display.getDefault().asyncExec(new Runnable() {  public void run() { handlePreSingleClick(mouseEvent); }  });
                    try {Thread.sleep(dblClickWaitTime);} catch (InterruptedException e) { return; }
                    Display.getDefault().asyncExec(interruptable);
                }
            }).start();            
        }
    }
}
