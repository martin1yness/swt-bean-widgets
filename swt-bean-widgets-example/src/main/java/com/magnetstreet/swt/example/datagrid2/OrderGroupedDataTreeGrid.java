package com.magnetstreet.swt.example.datagrid2;

import com.magnetstreet.swt.beanwidget.datagrid2.GroupedDataTreeGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.contextmenu.ContextMenuAction;
import com.magnetstreet.swt.example.bean.CustomerRecord;
import com.magnetstreet.swt.example.bean.Division;
import com.magnetstreet.swt.example.bean.Order;
import com.magnetstreet.swt.example.bean.OrderItem;
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

/**
 * OrderGroupedDataTreeGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 2012-02-22
 */
public class OrderGroupedDataTreeGrid extends GroupedDataTreeGrid<Order> {

    public OrderGroupedDataTreeGrid(Composite composite, int i) {
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
    }

    protected void defineColumn_id() {
        String identifier = "ID";
        bindColumnIdentifier(Order.class, identifier, "id");
        bindColumnIdentifier(OrderItem.class, identifier, "id");
        bindColumnWithDefaultTemplates(identifier, true);
    }

    protected void defineColumn_description() {
        String identifier = "Description";
        bindColumnIdentifier(Order.class, identifier, null);
        bindColumnIdentifier(OrderItem.class, identifier, "productName");
        bindColumnWithDefaultTemplates(identifier, true);
        bindViewer(Order.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ((Order)element).getCustomer().getName() + " - " + ((Order)element).getDivision().getName();
            }
        });
    }
    protected void defineColumn_quantity() {
        String identifier = "Quantity";
        bindColumnIdentifier(Order.class, identifier, null);
        bindColumnIdentifier(OrderItem.class, identifier, "quantity");
        bindColumnWithDefaultTemplates(identifier, true);

        // Overrides default viewer
        bindViewer(Order.class, identifier, new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                int qty = 0;
                for(OrderItem item: ((Order)element).getItems()) {
                    qty += item.getQuantity();
                }
                return ""+qty;
            }
        });
    }
    protected void defineColumn_cost() {
        String identifier = "Cost";
        bindColumnIdentifier(Order.class, identifier, "totalCost");
        bindColumnIdentifier(OrderItem.class, identifier, null);
        bindColumnWithDefaultTemplates(identifier, true);

        // Overrides default viewer
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
                OrderReflectiveDataTreeGrid orderDataTreeGrid = new OrderReflectiveDataTreeGrid(container, SWT.MULTI) {
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
