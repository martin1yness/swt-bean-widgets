package com.magnetstreet.swt.example.datagrid2;

import com.magnetstreet.swt.beanwidget.datagrid2.AbstractDataGrid;
import com.magnetstreet.swt.example.bean.Order;
import com.magnetstreet.swt.example.bean.OrderItem;
import com.magnetstreet.swt.viewers.ColumnHeaderProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * OrderManualDataGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
public class OrderManualDataGrid extends AbstractDataGrid<Order> {
    public OrderManualDataGrid(Composite composite, int i) {
        super(composite, i);

        createIdBinds();
        createItemsBinds();
        createDiscountTotalBinds();
        createTotalCostBinds();
        createPaidBinds();
        initialize();
    }

    protected void createIdBinds() {
        bindHeader("id", new ColumnHeaderProvider("ID", "The order's unique identifier", 60, true, true, null));
        bindViewer("id", new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ((Order)element).getId().toString();
            }
        });
        bindSorter("id", new Comparator<Order>() {
            @Override public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
    }

    protected void createItemsBinds() {
        bindHeader("items", new ColumnHeaderProvider("Item(s)", "The order's items", 200, true, true, null));
        bindViewer("items", new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                String itemStr = "";
                List<OrderItem> orderItems = ((Order)element).getItems();
                if(orderItems!=null) {
                    for(OrderItem item: orderItems)
                        itemStr += item.getProductName()+"("+item.getQuantity()+","+item.getProductUnitPrice()+") ";
                }
                return itemStr;
            }
        });
        bindSorter("items", new Comparator<Order>() {
            @Override public int compare(Order o1, Order o2) {
                return o2.getItems().size() - o1.getItems().size();
            }
        });
    }

    protected void createDiscountTotalBinds() {
        bindHeader("discountTotal", new ColumnHeaderProvider("Discount", "Amount discounted from the base order total", 70, true, true, null));
        bindViewer("discountTotal", new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ((Order)element).getDiscountTotal().toString();
            }
        });
        bindSorter("discountTotal", new Comparator<Order>() {
            @Override public int compare(Order o1, Order o2) {
                return o1.getDiscountTotal().compareTo(o2.getDiscountTotal());
            }
        });
        bindEditor("discountTotal", new EditingSupport(tableViewer) {
            @Override protected CellEditor getCellEditor(Object o) {
                return new TextCellEditor(tableViewer.getTable());
            }
            @Override protected boolean canEdit(Object o) {
                return true;
            }
            @Override protected Object getValue(Object o) {
                return ((Order)o).getDiscountTotal().toString();
            }
            @Override protected void setValue(Object o, Object o1) {
                ((Order)o).setDiscountTotal(new BigDecimal((String)o1));
                tableViewer.refresh();
            }
        });
    }

    protected void createTotalCostBinds() {
        bindHeader("totalCost", new ColumnHeaderProvider("Total", "The final cost of the order", 70, true, true, null));
        bindViewer("totalCost", new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ((Order)element).getTotalCost().toString();
            }
        });
        bindSorter("totalCost", new Comparator<Order>() {
            @Override public int compare(Order o1, Order o2) {
                return o1.getTotalCost().compareTo(o2.getTotalCost());
            }
        });
        bindEditor("totalCost", new EditingSupport(tableViewer) {
            @Override protected CellEditor getCellEditor(Object o) {
                return new TextCellEditor(tableViewer.getTable());
            }
            @Override protected boolean canEdit(Object o) {
                return true;
            }
            @Override protected Object getValue(Object o) {
                return ((Order)o).getTotalCost().toString();
            }
            @Override protected void setValue(Object o, Object o1) {
                try {
                    ((Order)o).setTotalCost(new BigDecimal((String) o1));
                } catch(NumberFormatException nfe) {
                    ((Order)o).setTotalCost(null);
                    ((Order)o).setPaid(false);
                }
                tableViewer.refresh();
            }
        });
    }

    protected void createPaidBinds() {
        bindHeader("paid", new ColumnHeaderProvider("Paid?", "The Order's Unique Identifier", 50, true, true, null));
        bindViewer("paid", new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                return ((Order)element).isPaid()+"";
            }
        });
        bindSorter("paid", new Comparator<Order>() {
            @Override public int compare(Order o1, Order o2) {
                if(o1.isPaid() && o2.isPaid()) return 0;
                if(!o1.isPaid() && !o2.isPaid()) return 0;
                if(o1.isPaid() && !o2.isPaid()) return -1;
                else
                    return 1;
            }
        });
        bindEditor("paid", new EditingSupport(tableViewer) {
            @Override protected CellEditor getCellEditor(Object o) {
                return new CheckboxCellEditor(tableViewer.getTable(), SWT.CHECK|SWT.READ_ONLY);
            }
            @Override protected boolean canEdit(Object o) {
                if( ((Order)o).getTotalCost()!=null && ((Order)o).getTotalCost().doubleValue() > 0 )
                    return true;
                return false;
            }
            @Override protected Object getValue(Object o) {
                return ((Order)o).isPaid();
            }
            @Override protected void setValue(Object o, Object o1) {
                ((Order)o).setPaid((Boolean)o1);
                tableViewer.refresh();
            }
        });
    }
}
