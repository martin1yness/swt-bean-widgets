package com.magnetstreet.swt.example.datagrid2.reflective;

import com.magnetstreet.swt.example.bean.CustomerRecord;
import com.magnetstreet.swt.example.bean.Division;
import com.magnetstreet.swt.example.bean.Order;
import com.magnetstreet.swt.example.bean.OrderItem;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeyLookup;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCCombo;
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
 * OrderReflectiveWithFiltersWindowTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class OrderReflectiveWithFiltersWindowTest {
    private static Shell shell;
    private static OrderReflectiveWithFiltersWindow inst;
    private static SWTBot bot;

    static {
        Thread t = new Thread(new Runnable() {
            public void run() {
                shell = new Shell(Display.getDefault());
                inst = new OrderReflectiveWithFiltersWindow(shell);
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
        bot.table().header("Paid").click();
        bot.table().header("Id").click();
        bot.table().header("Id").click();
        bot.table().click(0, 2);
        bot.ccomboBox().setSelection(0);
        bot.table().click(0, 1);

        inst.orderReflectiveDataGrid.discountTotalSaveCount.set(0);
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
        bot.checkBox().click();
        SWTBotTable tbl = bot.table();
        SWTBotTableColumn idCol = tbl.header("Id");
        idCol.click();
        Thread.sleep(10000);
        assertThat(tbl.getTableItem(0).getText(0), is("1"));
        idCol.click();
        assertThat(tbl.getTableItem(0).getText(0), is("100"));
        idCol.click();
        assertThat(tbl.getTableItem(0).getText(0), is("1"));
    }

    @Test public void testEditingEditableField() throws Exception {
        SWTBotTable tbl = bot.table();
        tbl.click(0, 3);
        bot.text("50").setText("666");
        tbl.select(0);

        assertThat(tbl.cell(0, 3), is("666"));

        tbl.click(0, 3);
        bot.text("666").setText("50");
        tbl.click(0, 0);
    }

    @Test public void testColumnDefaultOrder() throws Exception {
        SWTBotTable tbl = bot.table();
        List<String> columns = tbl.columns();
        assertThat(columns.get(0), is("Id"));
        assertThat(columns.get(1), is("Items"));
        assertThat(columns.get(2), is("Division"));
        assertThat(columns.get(3), is("Discount Total"));
        assertThat(columns.get(4), is("Total Cost"));
        assertThat(columns.get(5), is("Paid"));
        assertThat(columns.get(6), is("Placed On"));
    }

    @Test public void testCurrencyValidator() throws Exception {
        SWTBotTable tbl = bot.table();
        tbl.click(0,4);
        bot.text("50").setText("666");
        tbl.click(0,0);
        assertThat(tbl.cell(0,4), is("666"));

        tbl.click(0,4);
        bot.text("666").setText("2t");
        tbl.click(0,0);
        assertThat(tbl.cell(0,4), is("666"));

        tbl.click(0,4);
        bot.text("666").setText("50");
        tbl.click(0,0);
        assertThat(tbl.cell(0,4), is("50"));
    }

    @Test public void testDateTimeCustomCellEditor() throws Exception {
        SWTBotTable tbl = bot.table();
        assertThat(tbl.getTableItem(0).getText(6), is("12/24/1999"));

        tbl.click(0,6);
        SWTBotDateTime dateTime = bot.dateTime();
        Calendar placedOn = Calendar.getInstance();
        placedOn.set(2000, 8, 9);
        dateTime.setDate(placedOn.getTime());

        tbl.click(0,0);
        assertThat(tbl.getTableItem(0).getText(6), is("09/09/2000"));
        try {
            bot.dateTime();
            fail("Date time is still retrievable, it was supposed to be destroyed.");
        } catch(Throwable t) { }
    }

    @Test public void testHasNotChangedFeatureDoesntTriggerUpdateOnModel() throws Exception {
        SWTBotTable tbl = bot.table();
        assertThat(tbl.cell(0,3), is("50"));
        tbl.click(0,3);
        bot.text("50").setText("50");
        tbl.click(0,0);
        assertThat(tbl.cell(0,3), is("50"));
        assertThat(inst.orderReflectiveDataGrid.discountTotalSaveCount.get(), is(0));

        tbl.click(0,3);
        bot.text("50").setText("51");
        tbl.click(0,0);
        assertThat(tbl.cell(0,3), is("51"));
        assertThat(inst.orderReflectiveDataGrid.discountTotalSaveCount.get(), is(1));
    }

    @Test public void testSelectableObjectEditingSupport() throws Exception {
        SWTBotTable tbl = bot.table();
        assertThat(tbl.cell(0,2), is("Division A"));
        tbl.select(0);
        inst.getShell().getDisplay().syncExec(new Runnable() {
            public void run() {
                assertThat(inst.orderReflectiveDataGrid.getSelectedBeans().get(0).getDivision().getId(), is(1));
            }
        });
        tbl.click(0,2);
        SWTBotCCombo combo = bot.ccomboBox();
        combo.setSelection("Division B");
        tbl.click(0,0);
        assertThat(tbl.cell(0,2), is("Division B"));

        tbl.click(0,2);
        combo = bot.ccomboBox();
        combo.setSelection(2);
        tbl.click(0,0);
        assertThat(tbl.cell(0,2), is("Division C"));
    }

    @Test public void testCancelOutOfEditingCombo_noExceptionThrown() throws Exception {
        SWTBotTable tbl = bot.table();
        assertThat(tbl.cell(0,2), is("Division A"));
        tbl.click(0, 2);
        bot.ccomboBox().setSelection("Division B");
        bot.ccomboBox().pressShortcut(KeyStroke.getInstance(SWTKeyLookup.ESC_NAME));
        try {
            bot.shell("Error");
            fail("Found error dialog's OK button.");
        } catch (WidgetNotFoundException t) { }
        tbl.click(0, 1);
        assertThat(tbl.cell(0,2), is("Division A"));
    }
    
    @Test public void testGroupingSortedItemsWithCategoryMechanism() throws Exception {
        inst.getShell().getDisplay().syncExec(new Runnable() {
            @Override public void run() {
                inst.orderReflectiveDataGrid.groupOrdersByRevenue();
                inst.orderReflectiveDataGrid.refresh();
            }
        });
        bot.checkBox().select(); // show all

        SWTBotTable tbl = bot.table();
        SWTBotTableColumn idCol = tbl.header("Id");
        assertThat(tbl.getTableItem(0).getText(0), is("1"));
        idCol.click();
        assertThat(tbl.getTableItem(0).getText(0), is("10"));
        assertThat(tbl.getTableItem(10).getText(0), is("49"));
        idCol.click();
        assertThat(tbl.getTableItem(0).getText(0), is("1"));
    }

    @Ignore(value = "Wait until SWTBot supports drag and drop to write test for this.")
    @Test public void testReOrderColumnAndSort() throws Exception {
        SWTBotTable tbl = bot.table();
        Thread.sleep(20000);
    }
}
