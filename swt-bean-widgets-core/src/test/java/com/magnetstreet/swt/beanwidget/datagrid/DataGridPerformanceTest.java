package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.annotation.SWTEntity;
import com.magnetstreet.swt.annotation.SWTWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * Date: Mar 22, 2010
 * Time: 9:52:07 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DataGridPerformanceTest {
    @SWTEntity
    static class TestBean {
        @SWTWidget(labelText = "Id")
        private Integer id = 1;

        @SWTWidget(labelText = "Test Value")
        private String testValueOne = "test value";

        @SWTWidget(labelText = "Test Value 2")
        private String testValueTwo = "test value 2";

        @SWTWidget(labelText = "Cost")
        private BigDecimal cost = new BigDecimal("12.54");

        @SWTWidget(labelText = "Owner")
        private TestBeanOwner owner = new TestBeanOwner();

        @SWTWidget(labelText = "Date")
        private Calendar createdOn = Calendar.getInstance();
    }

    @SWTEntity
    static class TestBeanOwner {
        @SWTWidget(labelText = "Id")
        private Integer id = 1;

        @SWTWidget(labelText = "Name")
        private String name = "Johnny Appleseed";
    }

    private static Thread swtThread;

    private static Shell shell;
    private static SWTBot bot;
    private static EditableDataGrid editableDataGrid;
    private static AtomicLong createTime = new AtomicLong();

    static {
        swtThread = new Thread() {
            @Override public void run() {
                shell = new Shell();
                shell.setLayout(new FillLayout());
                List<TestBean> beans = getThousandBeans();
                synchronized (createTime) {
                    long start = System.currentTimeMillis();
                    editableDataGrid = DataGridFactory.getInstance().getEditableDataGrid(beans, null, shell, SWT.NONE);
                    createTime.set(System.currentTimeMillis() - start);
                }
                shell.pack();
                shell.open();
                while(!shell.isDisposed()) {
                    if(shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
                }
            }
        };
        swtThread.start();
    }

    private static List<TestBean> getThousandBeans() {
        List<TestBean> beans = new LinkedList<TestBean>();
        for(int i=0; i<1000; i++) {
            TestBean bean = new TestBean();
            bean.id = i;
            beans.add(bean);
        }
        return beans;
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        bot = new SWTBot();
        // slow down tests
        SWTBotPreferences.PLAYBACK_DELAY = 10;
        SWTBotPreferences.TYPE_INTERVAL = 1000;
    }

    @AfterClass
    public static void afterClass() throws Exception {
        swtThread.interrupt();
    }

    @Test
    public void testColumnCount() throws InterruptedException {
        assertEquals(6, bot.table().columnCount());
    }

    @Test
    public void testLoadTimeLessThanTwoSeconds() throws InterruptedException {
        assertTrue(createTime.get() < 2000L);
        Thread.sleep(10000);
    }

    @Test
    public void testViewCreationForEditLessThanOneSecond() {
        long start = System.currentTimeMillis();
        bot.table().click(1, 1);
        assertTrue(bot.text().isVisible());
        long time = System.currentTimeMillis() - start;
        assertTrue("Took over a second to create dataview for editing row contents!", time < 1000);
    }
}
