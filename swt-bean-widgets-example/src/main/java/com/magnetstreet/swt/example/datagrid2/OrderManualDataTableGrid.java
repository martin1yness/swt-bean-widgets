package com.magnetstreet.swt.example.datagrid2;

import com.magnetstreet.swt.beanwidget.datagrid2.AbstractDataTableGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.editor.AbstractDataGridCellEditingSupport;
import com.magnetstreet.swt.beanwidget.datagrid2.editor.DateTimeCellEditor;
import com.magnetstreet.swt.beanwidget.datagrid2.header.ColumnHeaderProvider;
import com.magnetstreet.swt.beanwidget.datagrid2.validator.AbstractTooltipDataGridCellValidator;
import com.magnetstreet.swt.beanwidget.datagrid2.validator.IDataGridCellValidator;
import com.magnetstreet.swt.example.bean.Order;
import com.magnetstreet.swt.example.bean.OrderItem;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OrderManualDataTableGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
public class OrderManualDataTableGrid extends AbstractDataTableGrid<Order> {
    public AtomicInteger discountTotalSaveCount = new AtomicInteger(0);

    public OrderManualDataTableGrid(Composite composite, int i) {
        super(composite, i);
    }

    @Override protected void preInit() {
        createIdBinds();
        createItemsBinds();
        createDiscountTotalBinds();
        createTotalCostBinds();
        createPaidBinds();
        createdPlacedOnBinds();
    }

    protected void createIdBinds() {
        bindHeader("id", new ColumnHeaderProvider() {
            //"ID", "The order's unique identifier", 60, true, true, null))
            @Override public String getTitle() { return "ID"; }
            @Override public String getTooltip() { return "The order's unique identifier"; }
            @Override public int getWidth() { return 60; }
            @Override public boolean isResizable() { return true; }
            @Override public boolean isMoveable() { return true; }
            @Override public Image getImage() { return null; }
        });
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
        bindHeader("items", new ColumnHeaderProvider() {
            //"Item(s)", "The order's items", 200, true, true, null))
            @Override public String getTitle() { return "Item(s)"; }
            @Override public String getTooltip() { return "The order's items"; }
            @Override public int getWidth() { return 200; }
            @Override public boolean isResizable() { return true; }
            @Override public boolean isMoveable() { return true; }
            @Override public Image getImage() { return null; }
        });
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
        bindHeader("discountTotal", new ColumnHeaderProvider() {
            //"Discount", "Amount discounted from the base order total", 70, true, true, null))
            @Override public String getTitle() { return "Discount"; }
            @Override public String getTooltip() { return "Amount discounted from the base order total"; }
            @Override public int getWidth() { return 70; }
            @Override public boolean isResizable() { return true; }
            @Override public boolean isMoveable() { return true; }
            @Override public Image getImage() { return null; }
        });
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
        bindEditor("discountTotal", new AbstractDataGridCellEditingSupport<Order,String>(this) {
            @Override protected CellEditor instantiateCellEditor(Table composite) {
                return new TextCellEditor(getTableViewer().getTable());
            }
            @Override protected IDataGridCellValidator instantiateValidator() {
                return new AbstractTooltipDataGridCellValidator<String>() {
                    @Override public String isValid(Object value) {
                        try {
                            Number number = NumberFormat.getNumberInstance().parse((String)value);
                            return null;
                        } catch (ParseException nfe) {
                            return "Unable to parse number: " + nfe.getMessage();
                        }
                    }
                };
            }
            @Override protected String getControlValue(Order modelObject) {
                return modelObject.getDiscountTotal().toString();
            }
            @Override protected void setModelValue(Order modelObject, String newValidValueFromControl) {
                discountTotalSaveCount.incrementAndGet();
                try {
                    Number number = NumberFormat.getNumberInstance().parse(newValidValueFromControl);
                    modelObject.setDiscountTotal(new BigDecimal(number.toString()));
                } catch (ParseException e) {
                    System.out.println("Error parsing input: " + newValidValueFromControl);
                }
            }
            @Override protected boolean canEdit(Object o) { return true; }
        });
    }

    protected void createTotalCostBinds() {
        bindHeader("totalCost", new ColumnHeaderProvider() {
            //"Total", "The final cost of the order", 70, true, true, null
            @Override public String getTitle() { return "Total"; }
            @Override public String getTooltip() { return "The final cost of the order"; }
            @Override public int getWidth() { return 70; }
            @Override public boolean isResizable() { return true; }
            @Override public boolean isMoveable() { return true; }
            @Override public Image getImage() { return null; }
        });
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
        bindEditor("totalCost", new AbstractDataGridCellEditingSupport<Order,String>(this) {
            @Override protected IDataGridCellValidator instantiateValidator() {
                return new AbstractTooltipDataGridCellValidator() {
                    @Override public String isValid(Object value) {
                        try {
                            new BigDecimal(""+value);
                        } catch(Throwable t) {
                            return "Unable to create numeric representation of {0}";
                        }
                        return null;
                    }
                };
            }
            @Override protected CellEditor instantiateCellEditor(Table composite) {
                return new TextCellEditor(((TableViewer)getViewer()).getTable());
            }
            @Override protected String getControlValue(Order modelObject) {
                return modelObject.getTotalCost().toString();
            }
            @Override protected void setModelValue(Order modelObject, String newValidValueFromControl) {
                ((Order)modelObject).setTotalCost(new BigDecimal(newValidValueFromControl));
            }
            @Override protected boolean canEdit(Object element) { return true; }
        });
    }

    protected void createPaidBinds() {
        bindHeader("paid", new ColumnHeaderProvider() {
            //"Paid?", "The Order's Unique Identifier", 50, true, true, null))
            @Override public String getTitle() { return "Paid?"; }
            @Override public String getTooltip() { return "The Order's Unique Identifier"; }
            @Override public int getWidth() { return 50; }
            @Override public boolean isResizable() { return true; }
            @Override public boolean isMoveable() { return true; }
            @Override public Image getImage() { return null; }
        });
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
        bindEditor("paid", new EditingSupport(getTableViewer()) {
            @Override protected CellEditor getCellEditor(Object o) {
                return new CheckboxCellEditor(getTableViewer().getTable(), SWT.CHECK|SWT.READ_ONLY);
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
                getTableViewer().refresh();
            }
        });
    }

    protected void createdPlacedOnBinds() {
        bindHeader("placedOn", new ColumnHeaderProvider() {
            //"Paid?", "The Order's Unique Identifier", 50, true, true, null))
            @Override public String getTitle() { return "Date"; }
            @Override public String getTooltip() { return "The Order's Unique Identifier"; }
            @Override public int getWidth() { return 50; }
            @Override public boolean isResizable() { return true; }
            @Override public boolean isMoveable() { return true; }
            @Override public Image getImage() { return null; }
        });
        bindViewer("placedOn", new ColumnLabelProvider() {
            @Override public String getText(Object element) {
                if(((Order)element).getPlacedOn() == null)
                    return "";
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                return sdf.format(((Order) element).getPlacedOn().getTime());
            }
        });
        bindSorter("placedOn", new Comparator<Order>() {
            @Override public int compare(Order o1, Order o2) {
                return o1.getPlacedOn().compareTo(o2.getPlacedOn());
            }
        });
        bindEditor("placedOn", new AbstractDataGridCellEditingSupport<Order,Calendar>(this) {
            @Override protected CellEditor instantiateCellEditor(Table composite) {
                return new DateTimeCellEditor(getTableViewer().getTable(), SWT.DATE|SWT.MEDIUM);
            }
            @Override protected IDataGridCellValidator instantiateValidator() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override protected Calendar getControlValue(Order modelObject) {
                return modelObject.getPlacedOn();
            }
            @Override protected void setModelValue(Order modelObject, Calendar newValidValueFromControl) {
                modelObject.setPlacedOn(newValidValueFromControl);
            }
            @Override protected boolean canEdit(Object o) {
                return true;
            }
        });
    }
}
