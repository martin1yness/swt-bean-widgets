package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.annotation.SWTWidget;
import com.magnetstreet.swt.annotation.SWTEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * DataGridFactoryTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 17, 2009
 * @since Dec 17, 2009
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DataGridFactoryTest {
    @SWTEntity
    static class TestBean {
        @SWTWidget(labelText = "Test Value")
        private String testValueOne = "test value";

        @SWTWidget(labelText = "Date")
        private Calendar date = Calendar.getInstance();
    }

    private static Shell shell;
    private static SWTBot bot;
    private static EditableDataGrid editableDataGrid;

    static {
        new Thread() {
            @Override public void run() {
                shell = new Shell();
                shell.setLayout(new FillLayout());
                List<TestBean> beans = new ArrayList<TestBean>();
                beans.add(new TestBean());
                beans.add(new TestBean());
                beans.add(new TestBean());
                editableDataGrid = DataGridFactory.getInstance().getEditableDataGrid(beans, null, shell, SWT.NONE);
                shell.pack();
                shell.open();
                while(!shell.isDisposed()) {
                    if(shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
                }
            }
        }.start();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        bot = new SWTBot();
        // slow down tests
        SWTBotPreferences.PLAYBACK_DELAY = 10;
        SWTBotPreferences.TYPE_INTERVAL = 1000;
    }

    @Test public void testColumnCount() throws InterruptedException {
        assertEquals(2, bot.table().columnCount());
    }
}