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
import org.eclipse.swt.widgets.*;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
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
public class OrderAndItemDataTreeGridWindowSWTBotTest {
    private static Thread t;
    private static ApplicationWindow window;
    private static OrderDataTreeGrid orderDataTreeGrid;
    private static List<Order> beans;

    static {
        beans = createTestData();
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

                        orderDataTreeGrid = new OrderDataTreeGrid(container, SWT.MULTI|SWT.CHECK);
                        orderDataTreeGrid.setBeans(beans);
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

    @Before public void setupSWTBotAndSetToInitialState() {
        while(bot==null) {
            try {
                bot = new SWTBot();
            } catch(Throwable t) { }
        }
        bot.comboBox().setSelection(0);
        bot.tree().select(new String[0]);
        for(SWTBotTreeItem item: bot.tree().getAllItems())
            item.uncheck();
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

    @Ignore("Can't get specific columns from SWTBot yet :S, comment out and verify manually.")
    @Test public void testGetAndSetColumnWidths() throws Exception {
        final StringBuilder sb = new StringBuilder();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                sb.append(orderDataTreeGrid.captureSerializedColumnWidths());
            }
        });
        Thread.sleep(2000);
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                orderDataTreeGrid.applySerializedColumnWidths(sb.toString().replaceAll("[0-9]+", "25"));
            }
        });
        Thread.sleep(2000);
    }

    @Test public void testUncheckAndCheckAllItems() throws Exception {
        assertThat(bot.tree().rowCount(), is(100));
        final Collection<Order> checkedOrders = new ArrayList<Order>();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                checkedOrders.addAll(orderDataTreeGrid.getCheckedRootBeans());
            }
        });
        assertThat(checkedOrders.size(), is(0));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                checkedOrders.clear();
                orderDataTreeGrid.checkAllItems();
                checkedOrders.addAll(orderDataTreeGrid.getCheckedRootBeans());
            }
        });
        assertThat(checkedOrders.size(), is(100));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                checkedOrders.clear();
                orderDataTreeGrid.uncheckAllItems();
                checkedOrders.addAll(orderDataTreeGrid.getCheckedRootBeans());
            }
        });
        assertThat(checkedOrders.size(), is(0));
    }

    @Test public void testCheckSpecificBeans() throws Exception {
        assertThat(bot.tree().rowCount(), is(100));
        final Collection<Order> checkedOrders = new ArrayList<Order>();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                checkedOrders.addAll(orderDataTreeGrid.getCheckedRootBeans());
            }
        });
        assertThat(checkedOrders.size(), is(0));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                checkedOrders.clear();
                orderDataTreeGrid.checkBeans(beans);
                checkedOrders.addAll(orderDataTreeGrid.getCheckedRootBeans());
            }
        });
        assertThat(checkedOrders.size(), is(100));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                checkedOrders.clear();
                orderDataTreeGrid.uncheckAllItems();
                checkedOrders.addAll(orderDataTreeGrid.getCheckedRootBeans());
            }
        });
        assertThat(checkedOrders.size(), is(0));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                checkedOrders.clear();
                ArrayList list = new ArrayList();
                list.add(beans.get(0));
                list.add(beans.get(2));
                list.add(beans.get(10));
                orderDataTreeGrid.checkBeans(list);
                checkedOrders.addAll(orderDataTreeGrid.getCheckedRootBeans());
            }
        });
        assertThat(checkedOrders.size(), is(3));
        assertThat(bot.tree().getTreeItem(bot.tree().cell(0, 0)).isChecked(), is(true));
        assertThat(bot.tree().getTreeItem(bot.tree().cell(2, 0)).isChecked(), is(true));
        assertThat(bot.tree().getTreeItem(bot.tree().cell(10, 0)).isChecked(), is(true));
        assertThat(bot.tree().getTreeItem(bot.tree().cell(11, 0)).isChecked(), is(false));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                checkedOrders.clear();
                ArrayList list = new ArrayList();
                list.add(beans.get(1));
                list.add(beans.get(3));
                list.add(beans.get(11));
                orderDataTreeGrid.checkBeans(list);
                checkedOrders.addAll(orderDataTreeGrid.getCheckedRootBeans());
            }
        });
        assertThat(checkedOrders.size(), is(6));
    }

    @Test public void testSelectAndUnselectAllItems() throws Exception {
        assertThat(bot.tree().rowCount(), is(100));
        final Collection<Order> selectedOrders = new ArrayList<Order>();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                selectedOrders.addAll(orderDataTreeGrid.getSelectedRootBeans());
            }
        });
        assertThat(selectedOrders.size(), is(0));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                selectedOrders.clear();
                orderDataTreeGrid.selectAllItems();
                selectedOrders.addAll(orderDataTreeGrid.getSelectedRootBeans());
            }
        });
        assertThat(selectedOrders.size(), is(100));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                selectedOrders.clear();
                orderDataTreeGrid.deselectAllItems();
                selectedOrders.addAll(orderDataTreeGrid.getSelectedRootBeans());
            }
        });
        assertThat(selectedOrders.size(), is(0));
    }

    @Test public void testSelectSpecificBeans() throws Exception {
        assertThat(bot.tree().rowCount(), is(100));
        final Collection<Order> selectedOrders = new ArrayList<Order>();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                selectedOrders.addAll(orderDataTreeGrid.getSelectedRootBeans());
            }
        });
        assertThat(selectedOrders.size(), is(0));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                selectedOrders.clear();
                ArrayList beansToSelect = new ArrayList();
                beansToSelect.add(beans.get(0));
                beansToSelect.add(beans.get(10));
                beansToSelect.add(beans.get(99));
                orderDataTreeGrid.selectBeans(beansToSelect);
                selectedOrders.addAll(orderDataTreeGrid.getSelectedRootBeans());
            }
        });
        assertThat(selectedOrders.size(), is(3));
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                selectedOrders.clear();
                ArrayList beansToSelect = new ArrayList();
                beansToSelect.add(beans.get(3));
                beansToSelect.add(beans.get(12));
                beansToSelect.add(beans.get(89));
                orderDataTreeGrid.selectBeans(beansToSelect);
                selectedOrders.addAll(orderDataTreeGrid.getSelectedRootBeans());
            }
        });
        assertThat(selectedOrders.size(), is(6));
    }
}
