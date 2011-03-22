package com.magnetstreet.swt.beanwidget.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: mlyness
 * Date: Mar 22, 2010
 * Time: 3:01:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleAndDblClickListenerTest {
    private static SingleAndDblClickListener singleAndDblClickListener;

    private static Shell shell;
    private static SWTBot bot;
    static {
        new Thread() {
            @Override public void run() {
                singleAndDblClickListener = new SingleAndDblClickListener() {
                    @Override protected void handlePreSingleClick(MouseEvent mouseEvent) {
                        actionPerformed = ACTION.PRE_SINGLE_CLICK_ACTION;
                        preClicksHandled++;
                    }

                    @Override protected void handleSingleClick(MouseEvent mouseEvent) {
                        actionPerformed = ACTION.SINGLE_CLICK_ACTION;
                        clicksHandled++;
                    }
                    @Override protected void handleDoubleClick(MouseEvent mouseEvent) {
                        actionPerformed = ACTION.DOUBLE_CLICK_ACTION;
                        clicksHandled++;
                    }

                    @Override public void mouseUp(MouseEvent mouseEvent) {
                        actionPerformed = ACTION.NONE;
                        super.mouseUp(mouseEvent);
                        clicks++;
                    }
                };
                shell = new Shell();
                shell.setLayout(new FillLayout());
                Button b = new Button(shell, SWT.NONE);
                b.addMouseListener(singleAndDblClickListener);
                shell.pack();
                shell.open();
                while(!shell.isDisposed()) {
                    if(shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
                }
            }
        }.start();
    }
    private static enum ACTION{PRE_SINGLE_CLICK_ACTION, SINGLE_CLICK_ACTION, DOUBLE_CLICK_ACTION, NONE};
    private static ACTION actionPerformed = ACTION.NONE;
    private static int clicks, clicksHandled, preClicksHandled;




    @BeforeClass
    public static void beforeClass() throws Exception {
        bot = new SWTBot();
    }


    @Test public void testSingleDistinguishableFromDouble() throws Exception {
        SingleAndDblClickListener.dblClickWaitTime = 1000; // Screen shots take time

        SWTBotButton button = bot.button();
        button.click();
        assertEquals(1, preClicksHandled);

        Thread.sleep(SingleAndDblClickListener.dblClickWaitTime + 1);
        assertEquals(ACTION.SINGLE_CLICK_ACTION, actionPerformed);
        assertEquals(1, clicks);
        assertEquals(1, clicksHandled);

        button.click();
        button.click();
        assertEquals(2, preClicksHandled);
        assertEquals(ACTION.DOUBLE_CLICK_ACTION, actionPerformed);
        assertEquals(3, clicks);
        assertEquals(2, clicksHandled);

        button.click();
        assertEquals(3, preClicksHandled);
        Thread.sleep(SingleAndDblClickListener.dblClickWaitTime + 1);
        assertEquals(ACTION.SINGLE_CLICK_ACTION, actionPerformed);
        assertEquals(4, clicks);
        assertEquals(3, clicksHandled);

        button.click();
        assertEquals(4, preClicksHandled);
        Thread.sleep(SingleAndDblClickListener.dblClickWaitTime + 1);
        assertEquals(ACTION.SINGLE_CLICK_ACTION, actionPerformed);
        assertEquals(5, clicks);
        assertEquals(4, clicksHandled);
    }

    @Test public void testDoubleClick() {
        SingleAndDblClickListener.dblClickWaitTime = 1000; // Screen shots take time
        SWTBotButton button = bot.button();
        button.click();
        button.click();
        assertEquals(ACTION.DOUBLE_CLICK_ACTION, actionPerformed);
    }
}
