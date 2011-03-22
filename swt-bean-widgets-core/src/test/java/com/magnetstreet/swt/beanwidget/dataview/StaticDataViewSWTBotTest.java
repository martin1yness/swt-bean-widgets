package com.magnetstreet.swt.beanwidget.dataview;

import com.magnetstreet.swt.annotation.SWTEntity;
import org.junit.Test;
import org.junit.BeforeClass;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swtbot.swt.finder.SWTBot;

import static junit.framework.Assert.assertEquals;

import com.magnetstreet.swt.annotation.SWTWidget;
import com.magnetstreet.swt.beanwidget.datetime.DateTimePopout;
import com.magnetstreet.swt.beanwidget.datetime.DateTimeUtil;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;

/**
 * StaticDataViewSWTBotTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 14, 2010
 * @since Jan 14, 2010
 */
public class StaticDataViewSWTBotTest {
    @SWTEntity
    static class TestBean {
        @SWTWidget(labelText = "Test Value")
        private String testValueOne = "test value";

        @SWTWidget(labelText = "Date")
        private Calendar date = Calendar.getInstance();
    }

    private static boolean running;
    private static Shell shell;
    private static Display display;
    private static SWTBot bot;


    private static DataView dataView;

    static {
        new Thread() {
            @Override public void run() {
                display = new Display();
                shell = new Shell(display);
                dataView = new StaticDataView<TestBean>(shell, SWT.NONE) {
                    protected Text testValueOneText;
                    protected DateTimePopout dateDateTimePopout;

                    protected Map validateUserInput() {
                        return new HashMap();
                    }
                    protected void preInitGUI() {
                        setLayout(new FormLayout());
                    }
                    @Override protected void initGUI() {
                        super.initGUI();
                        FormData testValueOneTextLData = new FormData(250, 20);
                        testValueOneTextLData.left = new FormAttachment(0,100,5);
                        testValueOneTextLData.right = new FormAttachment(100,100,-5);
                        testValueOneTextLData.top = new FormAttachment(0,100,5);
                        testValueOneText.setLayoutData(testValueOneTextLData);

                        FormData dateDateTimePopoutLData = new FormData();
                        dateDateTimePopoutLData.left = new FormAttachment(0,100,5);
                        dateDateTimePopoutLData.right = new FormAttachment(100,100,-5);
                        dateDateTimePopoutLData.top = new FormAttachment(0,100,30);
                        dateDateTimePopout.setLayoutData(dateDateTimePopoutLData);
                    }
                    protected void postInitGUI() {
                        pack();
                    }
                    protected void updateViewDataObjectFromWidgets() {
                        viewDataObject.testValueOne = testValueOneText.getText();
                        viewDataObject.date = DateTimeUtil.getCalendar(dateDateTimePopout);
                    }
                    protected void updateWidgetsFromViewDataObject() {
                        testValueOneText.setText(viewDataObject.testValueOne);
                        DateTimeUtil.setDateTime(dateDateTimePopout, viewDataObject.date);
                    }
                };
                shell.pack();
                shell.open();
                while(!shell.isDisposed()) {
                    if(!display.readAndDispatch()) display.sleep();
                }
                display.dispose();
            }
        }.start();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        bot = new SWTBot();
    }

    /**
     * Check that when selecting on the enable editing widget it changes the enabled
     * property of all dynamic widgets
     */
    @Test
    public void testEnableDisableWidgets() throws InterruptedException {
        assertEquals(false, bot.text(0).isEnabled());
        assertEquals(false, bot.checkBox().isChecked());
        bot.checkBox().click();
        assertEquals(true, bot.text(0).isEnabled());
        assertEquals(true, bot.checkBox().isChecked());
        bot.checkBox().click();
        assertEquals(false, bot.text(0).isEnabled());
        assertEquals(false, bot.checkBox().isChecked());
        bot.checkBox().click();
        assertEquals(true, bot.text(0).isEnabled());
        assertEquals(true, bot.checkBox().isChecked());
        bot.checkBox().click();
        assertEquals(false, bot.text(0).isEnabled());
        assertEquals(false, bot.checkBox().isChecked());
        bot.checkBox().click();
        assertEquals(true, bot.text(0).isEnabled());
        assertEquals(true, bot.checkBox().isChecked());
    }
}
