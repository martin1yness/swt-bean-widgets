package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.annotation.SWTEntity;
import com.magnetstreet.swt.annotation.SWTWidget;
import com.magnetstreet.swt.beanwidget.callback.SaveBeanCallback;
import com.magnetstreet.swt.beanwidget.listener.SingleAndDblClickListener;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static junit.framework.Assert.assertEquals;

/**
 * DataGridSaveCallbackTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since Nov 30, 2010
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DataGridSaveCallbackTest {
    @SWTEntity
    static class TestBean implements Serializable {
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
    static class TestBeanOwner implements Serializable {
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
    private static AtomicInteger saveCount = new AtomicInteger(0);

    static {
        swtThread = new Thread() {
            @Override public void run() {
                shell = new Shell();
                shell.setLayout(new FillLayout());
                List<TestBean> beans = getThousandBeans();
                synchronized (createTime) {
                    long start = System.currentTimeMillis();
                    editableDataGrid = DataGridFactory.getInstance().getEditableDataGrid(beans, null, shell, SWT.NONE);
                    editableDataGrid.setSaveBeanCallback(new SaveBeanCallback<TestBean>() {
                        public TestBean doCallback(TestBean testBean) {
                            saveCount.incrementAndGet();
                            try {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                ObjectOutputStream oos = new ObjectOutputStream(bos);
                                oos.writeObject(testBean);

                                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                                ObjectInputStream ois = new ObjectInputStream(bis);
                                return (TestBean)ois.readObject();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
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

    @Test public void testUpdateColumn() throws InterruptedException {
        Thread.sleep(10000);
        bot.table().click(1, 1);
        bot.text().typeText("10");
        bot.table().setFocus();
        assertEquals(1, saveCount.get());

        Thread.sleep(SingleAndDblClickListener.dblClickWaitTime + 1);

        bot.table().click(1, 1);
        assertEquals("10", bot.text().getText());
        bot.table().setFocus();
        assertEquals(1, saveCount.get());
    }
}