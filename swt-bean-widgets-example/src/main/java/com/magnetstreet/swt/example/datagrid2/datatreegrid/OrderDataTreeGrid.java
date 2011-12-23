package com.magnetstreet.swt.example.datagrid2.datatreegrid;

import com.google.common.collect.Sets;
import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.header.ColumnHeaderProvider;
import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.AbstractDataTreeGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.contextmenu.ContextMenuAction;
import com.magnetstreet.swt.example.bean.CustomerRecord;
import com.magnetstreet.swt.example.bean.Division;
import com.magnetstreet.swt.example.bean.Order;
import com.magnetstreet.swt.example.bean.OrderItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Order Tree View
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/12/11
 */
public class OrderDataTreeGrid extends AbstractDataTreeGrid<Order> {
    public AtomicInteger actionCounter = new AtomicInteger(0);

    public OrderDataTreeGrid(Composite composite, int i) {
        super(composite, i);
    }

    @Override protected void preInit() {
        defineColumn_id();
        defineColumn_description();
        defineColumn_quantity();
        defineColumn_cost();

        defineContextMenu();
    }

    protected void defineContextMenu() {
        bindContextMenuAction(new ActionContributionItem(new ContextMenuAction() {
            @Override public String getText() {
                Collection selectedModelObjs = getSelectedContextModel();
                if(selectedModelObjs!=null)
                    return "Select: " + selectedModelObjs.size();
                return "Select: 0";
            }
            @Override public void run() {
                TreeItem treeItem = ((TreeViewer) getViewer()).getTree().getItem(getSelectedBeans(Object.class).size()-1);
                ((TreeViewer) getViewer()).getTree().setSelection(treeItem);
            }
        }));
        bindContextMenuAction(new ActionContributionItem(new Action("Normal A&ction@CTRL+N") {
            @Override public void run() {
                System.out.println("Normal Action Invoked " + actionCounter.incrementAndGet() + " times.");
            }
        }));
        bindContextMenuAction(new ActionContributionItem(new ContextMenuAction((HashSet)Sets.newHashSet(Order.class)) {
            @Override public String getText() { return "Order Only Action"; }
            @Override public void run() { System.out.println("Order Action Invoked " + actionCounter.incrementAndGet() + " times."); }
        }));
        bindContextMenuAction(new ActionContributionItem(new ContextMenuAction((HashSet)Sets.newHashSet(OrderItem.class)) {
            @Override public String getText() { return "Item Only Action"; }
            @Override public void run() { System.out.println("Item Action Invoked " + actionCounter.incrementAndGet() + " times."); }
        }));
    }

    protected void defineColumn_id() {
        String identifier = "ID";
        bindHeader(identifier, new ColumnHeaderProvider() {
            @Override public String getTitle() { return "ID"; }
            @Override public int getWidth() { return 90; }
        });
        bindViewer(Order.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ""+((Order)element).getId();
            }
        });
        bindViewer(OrderItem.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ""+((OrderItem)element).getId();
            }
        });
        bindSorter(Order.class, identifier, new Comparator<Order>() {
            @Override public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        bindSorter(OrderItem.class, identifier, new Comparator<OrderItem>() {
            @Override public int compare(OrderItem o1, OrderItem o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
    }

    protected void defineColumn_description() {
        String identifier = "Description";
        bindHeader(identifier, new ColumnHeaderProvider() {
            @Override public String getTitle() { return "Description"; }
            @Override public int getWidth() { return 290; }
        });
        bindViewer(Order.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ((Order)element).getCustomer().getName() + " - " + ((Order)element).getDivision().getName();
            }
        });
        bindViewer(OrderItem.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ((OrderItem)element).getProductName();
            }
        });
    }
    protected void defineColumn_quantity() {
        String identifier = "Quantity";
        bindHeader(identifier, new ColumnHeaderProvider() {
            @Override public String getTitle() { return "Quantity"; }
            @Override public int getWidth() { return 100; }
        });
        bindViewer(Order.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                int qty = 0;
                for(OrderItem item: ((Order)element).getItems()) {
                    qty += item.getQuantity();
                }
                return ""+qty;
            }
        });
        bindViewer(OrderItem.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ""+((OrderItem)element).getQuantity();
            }
        });
    }
    protected void defineColumn_cost() {
        String identifier = "Cost";
        bindHeader(identifier, new ColumnHeaderProvider() {
            @Override public String getTitle() { return "Cost"; }
            @Override public int getWidth() { return 150; }
        });
        bindViewer(Order.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ((Order)element).getTotalCost().toString();
            }
        });
        bindViewer(OrderItem.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return new BigDecimal(((OrderItem)element).getQuantity()).multiply(((OrderItem)element).getProductUnitPrice()).toString();
            }
        });
    }

    @Override protected TreeNode createTreeNode(Order bean) {
        return createOrderToOrderItemTreeNode(bean);
    }

    protected TreeNode createOrderToOrderItemTreeNode(Order bean) {
        TreeNode parent = new TreeNode(bean);
        parent.setChildren(recursiveGenerateChildrenTreeNodes(parent, "items"));
        return parent;
    }

    public static void main(String[] args) {
        ApplicationWindow window = new ApplicationWindow(null) {
            @Override protected Control createContents(Composite parent) {
                Composite container = new Composite(parent, SWT.EMBEDDED);
                container.setLayout(new FillLayout());
                OrderDataTreeGrid orderDataTreeGrid = new OrderDataTreeGrid(container, SWT.MULTI) {
                    @Override protected void preInit() {
                        super.preInit();    //To change body of overridden methods use File | Settings | File Templates.
                    }
                };
                Order orderA = new Order();
                orderA.setId(123);
                orderA.setCustomer(new CustomerRecord());
                orderA.getCustomer().setName("Jerry Sckyle");
                orderA.setDivision(new Division());
                orderA.getDivision().setName("DivisionA");

                OrderItem itemA = new OrderItem();
                itemA.setId(111);
                itemA.setProductName("Product A");
                itemA.setProductUnitPrice(new BigDecimal(".25"));
                itemA.setQuantity(100);
                itemA.setOrder(orderA);
                OrderItem itemB = new OrderItem();
                itemB.setId(222);
                itemB.setProductName("Product B");
                itemB.setProductUnitPrice(new BigDecimal(".10"));
                itemB.setQuantity(100);
                itemB.setOrder(orderA);

                orderA.setItems(new ArrayList<OrderItem>());
                orderA.getItems().add(itemA);
                orderA.getItems().add(itemB);
                orderA.setTotalCost(new BigDecimal("35.00"));

                orderDataTreeGrid.addBean(orderA);
                orderDataTreeGrid.refresh();
                return container;
            }
        };
        window.setBlockOnOpen(true);
        window.open();
    }
}
