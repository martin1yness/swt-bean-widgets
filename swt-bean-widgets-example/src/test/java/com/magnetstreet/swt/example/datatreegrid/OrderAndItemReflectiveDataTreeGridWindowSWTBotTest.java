package com.magnetstreet.swt.example.datatreegrid;

import com.magnetstreet.swt.beanwidget.datagrid2.filter.ColumnFilter;
import com.magnetstreet.swt.example.bean.CustomerRecord;
import com.magnetstreet.swt.example.bean.Division;
import com.magnetstreet.swt.example.bean.Order;
import com.magnetstreet.swt.example.bean.OrderItem;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * OrderAndItemDataTreeGridWindowSWTBotTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/12/11
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class OrderAndItemReflectiveDataTreeGridWindowSWTBotTest {
    private static Thread t;
    private static ApplicationWindow window;
    private static OrderReflectiveDataTreeGrid orderDataTreeGrid;

    static {
        t = new Thread(new Runnable() {
            public void run() {
                window = new ApplicationWindow(null) {
                    @Override protected Control createContents(Composite parent) {
                        Composite container = new Composite(parent, SWT.EMBEDDED);
                        container.setLayout(new RowLayout(SWT.VERTICAL));

                        final Combo divisionComboFilter = new Combo(container, SWT.BORDER|SWT.READ_ONLY);
                        divisionComboFilter.add("-- ALL --");
                        divisionComboFilter.add("Division A");
                        divisionComboFilter.add("Division B");
                        divisionComboFilter.addModifyListener(new ModifyListener() {
                            @Override public void modifyText(ModifyEvent e) {
                                orderDataTreeGrid.refresh();
                            }
                        });

                        orderDataTreeGrid = new OrderReflectiveDataTreeGrid(container, SWT.MULTI|SWT.CHECK);
                        orderDataTreeGrid.setBeans(createTestData());
                        orderDataTreeGrid.refresh();

                        orderDataTreeGrid.bindFilter(Order.class, "Division", new ColumnFilter<Order>() {
                            @Override public boolean checkModelProperty(Order modelObjectProperty) {
                                if(divisionComboFilter.getText().equalsIgnoreCase("-- ALL --"))
                                    return true;
                                return modelObjectProperty.getDivision().getName().equals(divisionComboFilter.getText());
                            }
                        });
                        return container;
                    }
                };
                window.setBlockOnOpen(true);
                window.open();
            }
        });
        t.start();
    }

    public static List<Order> createTestData() {
        Division divisionA = new Division();
        divisionA.setId(1);
        divisionA.setName("Division A");
        divisionA.setDescription("Some super awesome division.");

        Division divisionB = new Division();
        divisionB.setId(2);
        divisionB.setName("Division B");
        divisionB.setDescription("Some super junky division.");

        CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setId(1);
        customerRecord.setName("Jeremy Blunt");

        List<Order> orders = new ArrayList<Order>();
        for(int i=1; i<=100; i++) {
            Order order = new Order();
            order.setId(i);
            order.setDivision( (i % 2 == 0) ? divisionA : divisionB );
            order.setCustomer(customerRecord);
            order.setPaid(true);
            order.setTotalCost(BigDecimal.ZERO);
            order.setItems(new ArrayList<OrderItem>());
            for(int j=1; j<5; j++) {
                OrderItem item = new OrderItem();
                item.setId(i*100 + j);
                item.setProductUnitPrice(new BigDecimal(".10"));
                item.setQuantity(j*100);
                item.setOrder(order);
                item.setProductName("Product "+i+","+j);
                order.setTotalCost(order.getTotalCost().add(new BigDecimal(item.getQuantity()).multiply(item.getProductUnitPrice())));
                order.getItems().add(item);
            }
            orders.add(order);
        }
        return orders;
    }

    private SWTBot bot;

    @Before public void setupSWTBot() {
        while(bot==null) {
            try {
                bot = new SWTBot();
            } catch(Throwable t) { }
        }
    }

    @Test public void testCellPopulation() throws InterruptedException {
        List<String> columnHeaders = bot.tree().columns();
        assertThat(columnHeaders.size(), is(4));
        assertThat(columnHeaders, hasItems("ID", "Description", "Quantity", "Cost"));

        assertThat(bot.tree().rowCount(), is(100));

        bot.tree().expandNode(bot.tree().cell(0, 0));
        assertThat(bot.tree().getTreeItem(bot.tree().cell(0, 0)).getItems().length, is(4));
    }

    @Ignore(value = "Unable to access tree column headers through SWTBot")
    @Test public void testSortingDataTreeGrid() throws Exception {
        bot.tree().expandNode(bot.tree().cell(0,0));
        assertThat(bot.tree().getTreeItem(bot.tree().cell(0, 0)).getItems().length, is(4));
        assertThat(bot.tree().cell(0, 0) , is("1"));
    }

    @Test public void testGetSelectedObjects() throws Exception {
        bot.tree().expandNode(bot.tree().cell(0, 0));
        bot.tree().select(bot.tree().getTreeItem(bot.tree().cell(0, 0)),
                bot.tree().getTreeItem(bot.tree().cell(0, 0)).getItems()[0],
                bot.tree().getTreeItem(bot.tree().cell(0, 0)).getItems()[1]);
        //bot.tree().select(1,2,3);
        //bot.tree().getAllItems()[0].select().select();
        final Collection selectedBeans = new ArrayList();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                selectedBeans.addAll(orderDataTreeGrid.getSelectedBeans(Object.class));
            }
        });
        assertThat(selectedBeans.size(), is(3));
        Iterator it = selectedBeans.iterator();
        assertThat(it.next(), is(Order.class));
        assertThat(it.next(), is(OrderItem.class));
        assertThat(it.next(), is(OrderItem.class));
    }

    @Test public void testGetCheckedObjects() throws Exception {
        bot.tree().expandNode(bot.tree().cell(0, 0));
        bot.tree().getTreeItem(bot.tree().cell(0, 0)).check();
        bot.tree().getTreeItem(bot.tree().cell(0, 0)).getItems()[0].check();
        bot.tree().getTreeItem(bot.tree().cell(0, 0)).getItems()[1].check();

        final Collection selectedBeans = new ArrayList();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                selectedBeans.addAll(orderDataTreeGrid.getCheckedBeans(Object.class));
            }
        });
        assertThat(selectedBeans.size(), is(3));
        Iterator it = selectedBeans.iterator();
        assertThat(it.next(), is(Order.class));
        assertThat(it.next(), is(OrderItem.class));
        assertThat(it.next(), is(OrderItem.class));
    }

    @Test public void testContextMenuOnSelectExecutesActionToSelectPositionNumberInSelectionCount() throws Exception {
        bot.tree().expandNode(bot.tree().cell(0, 0));
        bot.tree().select(bot.tree().getTreeItem(bot.tree().cell(0, 0)),
                bot.tree().getTreeItem(bot.tree().cell(0, 0)).getItems()[0],
                bot.tree().getTreeItem(bot.tree().cell(0, 0)).getItems()[1]);
        bot.tree().getTreeItem(bot.tree().cell(5,0)).contextMenu("Select: 1").click();
        assertThat(bot.tree().selectionCount(), is(1));
        assertThat(bot.tree().getTreeItem(bot.tree().cell(0,0)).isSelected(), is(true));
    }

    @Test public void testDefaultFilterFeature() throws Exception {
        assertThat(bot.tree().rowCount(), is(100));
        bot.comboBox().setSelection(2);
        assertThat(bot.tree().rowCount(), is(50));
    }

    @Test public void testCheckAlluncheckAll() throws Exception {

    }
}
