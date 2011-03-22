/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.magnetstreet.swt.extra.splash;

import org.eclipse.swt.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 *
 * @author mlyness
 */
public class CancelableLoadingSplashSWTBotTest {
    private static Shell shell;
    private static SWTBot bot;

    private static abstract class TestShell extends Composite {
        Button button;
        Label label;
        public TestShell(Composite parent, int style) {
            super(parent, style);
            setSize(500,100);
            FormLayout layout = new FormLayout();
            setLayout(layout);
            label = new Label(this, SWT.CENTER);
            label.setText("MAIN APPLICATION");
            FormData fd = new FormData(100,50);
            fd.left = new FormAttachment(0,100,4);
            fd.top = new FormAttachment(0,100,4);
            label.setLayoutData(fd);

            button = new Button(this, SWT.PUSH);
            button.setText("Start Loading Action");
            FormData fd2 = new FormData(100,50);
            fd2.left = new FormAttachment(0,100,4);
            fd2.top = new FormAttachment(0,100,58);
            button.setLayoutData(fd2);
            button.addSelectionListener(new SelectionAdapter() {
                @Override public void widgetSelected(SelectionEvent e) {
                    CancelableLoadingSplash.splash(getParent().getDisplay(), 5000, new Thread[]{
                        new Thread("Some task...") {
                            public void run() {
                                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                            }
                        },
                        new Thread("Processing some other task...") {
                            public void run() {
                                try { Thread.sleep(10000); } catch (InterruptedException e) {}
                            }
                        },
                        new Thread("Third task in series") {
                            public void run() {
                                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                            }
                        },
                        new Thread("Finally, we are at the last task...") {
                            public void run() {
                                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                            }
                        }
                   });
                }
            });
            pack();
        }
    }

    static {
        new Thread() {
            @Override public void run() {
                shell = new Shell();
                new TestShell(shell, SWT.NONE) {};
                shell.pack();
                shell.open();
                while(!shell.isDisposed()) {
                    if(!shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
                }
            }
        }.start();
    }

    @BeforeClass public static void beforeClass() throws Exception {
        bot = new SWTBot();
        // slow down tests
        SWTBotPreferences.PLAYBACK_DELAY = 10;
        SWTBotPreferences.TYPE_INTERVAL = 1000;
    }

    @Test public void testCancelAction() throws InterruptedException {
        bot.button("Start Loading Action").click();
        bot.button("Cancel").click();
        try {
            SWTBotButton b = bot.button("Cancel");
            fail("Cancel button still active after loading action was canceled!");
        }catch(Throwable t) {
            assertTrue(true);
        }
    }

    @Test public void testFullLoad() {
        bot.button("Start Loading Action").click();
        bot.sleep(14000);
        try {
            SWTBotButton b = bot.button("Cancel");
            fail("Cancel button still active after loading action should have completed!");
        }catch(Throwable t) {
            assertTrue(true);
        }
    }
}
