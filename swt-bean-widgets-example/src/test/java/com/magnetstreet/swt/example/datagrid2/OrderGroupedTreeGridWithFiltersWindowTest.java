package com.magnetstreet.swt.example.datagrid2;

import com.magnetstreet.swt.beanwidget.datagrid2.AbstractDataTreeGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.sorter.DataTreeGridSorter;
import com.magnetstreet.swt.example.bean.CustomerRecord;
import com.magnetstreet.swt.example.bean.Division;
import com.magnetstreet.swt.example.bean.Order;
import com.magnetstreet.swt.example.bean.OrderItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * OrderReflectiveWithFiltersWindowTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class OrderGroupedTreeGridWithFiltersWindowTest {
    private static Shell shell;
    private static OrderGroupedTreeGridWithFiltersWindow inst;
    private static SWTBot bot;

    static {
        Thread t = new Thread(new Runnable() {
            public void run() {
                shell = new Shell(Display.getDefault());
                inst = new OrderGroupedTreeGridWithFiltersWindow(shell);
                inst.setBlockOnOpen(true);
                inst.setOrders(generateUniqueOrders());
                inst.open();
            }
        });
        t.start();
    }

    private static List<Order> generateUniqueOrders() {
        List<Order> orders = new ArrayList<Order>(100);
        for(int i=1; i<=100; i++) {
            Order o = new Order();
            o.setId(i);
            o.setDiscountTotal(new BigDecimal(i));
            o.setTotalCost(new BigDecimal(i));
            o.setItems(new ArrayList<OrderItem>());
            Division d = new Division();
            d.setId(1);
            d.setName("Division A");
            o.setDivision(d);
            for(int j=1; j<=3; j++) {
                OrderItem oi = new OrderItem();
                oi.setId(new Integer(i + "00" + j));
                oi.setOrder(o);
                oi.setProductName("Product "+oi.getId());
                oi.setProductUnitPrice(new BigDecimal(j).divide(BigDecimal.TEN));
                oi.setQuantity(j * 1000);
                o.getItems().add(oi);
            }
            if(i==50)
                o.setPaid(false);
            else
                o.setPaid(true);
            o.setCustomer(new CustomerRecord());
            Calendar placedOn = Calendar.getInstance();
            placedOn.set(1999, 11, 24);
            o.setPlacedOn(placedOn);
            orders.add(o);
        }
        return orders;
    }

    @BeforeClass
    public static void setupSwtBot() {
        bot = new SWTBot();
    }

    @Before
    public void setDefaultConfiguration() {
        bot.textWithLabel("ID:").setText("");
        bot.textWithLabel("Item(s):").setText("");
        bot.spinnerWithLabel("Discount:").setSelection(0);
        bot.spinnerWithLabel("Cost:").setSelection(0);
        bot.checkBox().deselect();
    }

    @Test public void testFilters() throws InterruptedException {
        SWTBotTree tbl = bot.tree();
        assertThat(tbl.rowCount(), is(1));

        bot.checkBox().click();
        assertThat(tbl.rowCount(), is(100));

        bot.textWithLabel("ID:").setText("100");
        assertThat(tbl.rowCount(), is(1));
    }

    @Test public void testSortingFunctionality() throws Exception {
        assertThat(bot.tree().getAllItems()[0].getText(), is("1"));

        Field treeViewerSorterField = AbstractDataTreeGrid.class.getDeclaredField("treeViewerSorter");
        treeViewerSorterField.setAccessible(true);
        ((DataTreeGridSorter)treeViewerSorterField.get(inst.orderGroupedDataTreeGrid)).setIdentifier("ID");

        shell.getDisplay().syncExec(new Runnable() {
            @Override public void run() {
                inst.orderGroupedDataTreeGrid.getTreeViewer().refresh();
            }
        });

        assertThat(bot.tree().getAllItems()[0].getText(), is("49"));

        ((DataTreeGridSorter)treeViewerSorterField.get(inst.orderGroupedDataTreeGrid)).setIdentifier("ID");

        shell.getDisplay().syncExec(new Runnable() {
            @Override public void run() {
                inst.orderGroupedDataTreeGrid.getTreeViewer().refresh();
            }
        });

        assertThat(bot.tree().getAllItems()[0].getText(), is("1"));

        bot.textWithLabel("ID:").setText("2");
        assertThat(bot.tree().rowCount(), is(19)); // 2, 12, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 32, 42, 52, 62, 72, 82, 92

        assertThat(bot.tree().getAllItems()[0].getText(), is("2"));
        assertThat(bot.tree().getAllItems()[1].getText(), is("12"));
        assertThat(bot.tree().getAllItems()[2].getText(), is("20"));
        assertThat(bot.tree().getAllItems()[3].getText(), is("21"));
        assertThat(bot.tree().getAllItems()[4].getText(), is("22"));
        assertThat(bot.tree().getAllItems()[5].getText(), is("23"));
        assertThat(bot.tree().getAllItems()[6].getText(), is("24"));
        assertThat(bot.tree().getAllItems()[7].getText(), is("25"));
        assertThat(bot.tree().getAllItems()[8].getText(), is("26"));
        assertThat(bot.tree().getAllItems()[9].getText(), is("27"));
        assertThat(bot.tree().getAllItems()[10].getText(), is("28"));
        assertThat(bot.tree().getAllItems()[11].getText(), is("29"));
        assertThat(bot.tree().getAllItems()[12].getText(), is("32"));
        assertThat(bot.tree().getAllItems()[13].getText(), is("42"));
        assertThat(bot.tree().getAllItems()[14].getText(), is("52"));
        assertThat(bot.tree().getAllItems()[15].getText(), is("62"));
        assertThat(bot.tree().getAllItems()[16].getText(), is("72"));
        assertThat(bot.tree().getAllItems()[17].getText(), is("82"));
        assertThat(bot.tree().getAllItems()[18].getText(), is("92"));

        ((DataTreeGridSorter)treeViewerSorterField.get(inst.orderGroupedDataTreeGrid)).setIdentifier("ID");

        shell.getDisplay().syncExec(new Runnable() {
            @Override public void run() {
                inst.orderGroupedDataTreeGrid.getTreeViewer().refresh();
            }
        });

        assertThat(bot.tree().getAllItems()[0].getText(), is("42"));
        assertThat(bot.tree().getAllItems()[1].getText(), is("32"));
        assertThat(bot.tree().getAllItems()[2].getText(), is("29"));
        assertThat(bot.tree().getAllItems()[3].getText(), is("28"));
        assertThat(bot.tree().getAllItems()[4].getText(), is("27"));
        assertThat(bot.tree().getAllItems()[5].getText(), is("26"));
        assertThat(bot.tree().getAllItems()[6].getText(), is("25"));
        assertThat(bot.tree().getAllItems()[7].getText(), is("24"));
        assertThat(bot.tree().getAllItems()[8].getText(), is("23"));
        assertThat(bot.tree().getAllItems()[9].getText(), is("22"));
        assertThat(bot.tree().getAllItems()[10].getText(), is("21"));
        assertThat(bot.tree().getAllItems()[11].getText(), is("20"));
        assertThat(bot.tree().getAllItems()[12].getText(), is("12"));
        assertThat(bot.tree().getAllItems()[13].getText(), is("2"));
        assertThat(bot.tree().getAllItems()[14].getText(), is("92"));
        assertThat(bot.tree().getAllItems()[18].getText(), is("52"));
        assertThat(bot.tree().getAllItems()[15].getText(), is("82"));
        assertThat(bot.tree().getAllItems()[16].getText(), is("72"));
        assertThat(bot.tree().getAllItems()[17].getText(), is("62"));
    }

}
