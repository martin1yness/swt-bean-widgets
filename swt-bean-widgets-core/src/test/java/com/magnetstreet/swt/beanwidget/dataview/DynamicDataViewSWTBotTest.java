package com.magnetstreet.swt.beanwidget.dataview;

import static junit.framework.Assert.assertEquals;

import com.magnetstreet.swt.annotation.SWTEntity;
import com.magnetstreet.swt.annotation.SWTWidget;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

/**
 * DataViewSWTBotTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 2, 2009
 * @since Dec 2, 2009
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DynamicDataViewSWTBotTest {
    @SWTEntity
    static class TestBean {
        @SWTWidget(labelText = "Test Value")
        private String testValueOne = "test value";

        @SWTWidget(labelText = "Date")
        private Calendar date = Calendar.getInstance();
    }

    Matcher<Widget> allComponents = new BaseMatcher() {
        public boolean matches(Object o) {
            return true;
        }
        public void describeTo(Description description) {
            return;
        }
    };

    private static Shell shell;
    private static SWTBot bot;

    private static DataView dataView;

    static {
        new Thread() {
            @Override public void run() {
                shell = new Shell();
                dataView = DataViewFactory.getInstance().getDataView(new TestBean(), shell, org.eclipse.swt.SWT.NONE);
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

    @Before public void setUp() throws Exception {
        
    }

    @Test public void testCreateDynamicDataView() throws InterruptedException {
        assertEquals("test value", bot.text(0).getText());
        Calendar tmpCal = Calendar.getInstance();
        Calendar tmpCal2 = Calendar.getInstance();
        tmpCal.setTime(bot.dateTime(0).getDate());
        tmpCal2.setTime(new Date());
        assertEquals(tmpCal2.get(Calendar.DAY_OF_MONTH), tmpCal.get(Calendar.DAY_OF_MONTH));
        
        assertEquals("Test Value", bot.clabel("Test Value").getText());
        assertEquals("Date", bot.clabel("Date").getText());
    }

    /**
     * Check that when selecting on the enable editing widget it changes the enabled
     * property of all dynamic widgets
     */
    @Test public void testEnableDisableWidgets() {
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
