package com.magnetstreet.swt.example.datagrid2;

import com.magnetstreet.swt.example.bean.CustomerRecord;
import com.magnetstreet.swt.example.bean.Order;
import com.magnetstreet.swt.example.bean.OrderItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotDateTime;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableColumn;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * OrderManualWithFiltersWindowSWTBotTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class OrderManualWithFiltersWindowSWTBotTest {
    private static Shell shell;
    private static OrderManualWithFiltersWindow inst;
    private static SWTBot bot;

    static {
        Thread t = new Thread(new Runnable() {
            public void run() {
                shell = new Shell(Display.getDefault());
                inst = new OrderManualWithFiltersWindow(shell);
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
        bot.table().header("Paid?").click();
        bot.table().header("ID").click();
        bot.table().header("ID").click();

        inst.orderManualDataGrid.discountTotalSaveCount.set(0);
    }

    @Test public void testFilters() throws InterruptedException {
        SWTBotTable tbl = bot.table();
        assertThat(tbl.rowCount(), is(1));

        bot.checkBox().click();
        assertThat(tbl.rowCount(), is(100));

        bot.textWithLabel("Item(s):").setText("1001");
        assertThat(tbl.rowCount(), is(10));
    }

    @Test public void testSortingFunctionality() throws InterruptedException {
        if(!bot.checkBox().isChecked())
            bot.checkBox().click();

        SWTBotTable tbl = bot.table();
        SWTBotTableColumn idCol = tbl.header("ID");
        idCol.click();
        assertThat(tbl.getTableItem(0).getText(0), is("1"));
        idCol.click();
        assertThat(tbl.getTableItem(0).getText(0), is("100"));
        idCol.click();
        assertThat(tbl.getTableItem(0).getText(0), is("1"));
    }

    @Test public void testEditingEditableField() throws Exception {
        SWTBotTable tbl = bot.table();
        tbl.click(0, 2);
        bot.text("50").setText("666");
        tbl.select(0);

        assertThat(tbl.cell(0, 2), is("666"));

        tbl.click(0, 2);
        bot.text("666").setText("50");
        tbl.click(0, 0);
    }

    @Test public void testColumnDefaultOrder() throws Exception {
        SWTBotTable tbl = bot.table();
        List<String> columns = tbl.columns();
        assertThat(columns.get(0), is("ID"));
        assertThat(columns.get(1), is("Item(s)"));
        assertThat(columns.get(2), is("Discount"));
        assertThat(columns.get(3), is("Total"));
        assertThat(columns.get(4), is("Paid?"));
        assertThat(columns.get(5), is("Date"));
    }

    @Test public void testCurrencyValidator() throws Exception {
        SWTBotTable tbl = bot.table();
        tbl.click(0,3);
        bot.text("50").setText("666");
        tbl.click(0,0);
        assertThat(tbl.cell(0,3), is("666"));

        tbl.click(0,3);
        bot.text("666").setText("2t");
        tbl.click(0,0);
        assertThat(tbl.cell(0,3), is("666"));

        tbl.click(0,3);
        bot.text("666").setText("50");
        tbl.click(0,0);
        assertThat(tbl.cell(0,3), is("50"));
    }

    @Test public void testDateTimePopoutCustomCellEditor() throws Exception {
        SWTBotTable tbl = bot.table();
        assertThat(tbl.getTableItem(0).getText(5), is("12/24/1999"));

        tbl.click(0,5);
        SWTBotDateTime dateTime = bot.dateTime();
        Calendar placedOn = Calendar.getInstance();
        placedOn.set(2000, 8, 9);
        dateTime.setDate(placedOn.getTime());

        tbl.click(0,0);
        assertThat(tbl.getTableItem(0).getText(5), is("09/09/2000"));
        try {
            bot.dateTime();
            fail("Date time is still retrievable, it was supposed to be destroyed.");
        } catch(Throwable t) { }
    }

    @Test public void testHasNotChangedFeatureDoesntTriggerUpdateOnModel() throws Exception {
        SWTBotTable tbl = bot.table();
        assertThat(tbl.cell(0,2), is("50"));
        tbl.click(0,2);
        bot.text("50").setText("50");
        tbl.click(0,0);
        assertThat(tbl.cell(0,2), is("50"));
        assertThat(inst.orderManualDataGrid.discountTotalSaveCount.get(), is(0));

        tbl.click(0,2);
        bot.text("50").setText("51");
        tbl.click(0,0);
        assertThat(tbl.cell(0,2), is("51"));
        assertThat(inst.orderManualDataGrid.discountTotalSaveCount.get(), is(1));
    }

    @Ignore(value = "Wait until SWTBot supports drag and drop to write test for this.")
    @Test public void testReOrderColumnAndSort() throws Exception {
        SWTBotTable tbl = bot.table();
        Thread.sleep(20000);
    }
}
