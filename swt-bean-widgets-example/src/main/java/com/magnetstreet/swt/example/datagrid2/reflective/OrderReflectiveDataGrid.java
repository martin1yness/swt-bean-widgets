package com.magnetstreet.swt.example.datagrid2.reflective;

import com.magnetstreet.swt.beanwidget.datagrid2.reflective.ReflectiveDataGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.reflective.editor.BigDecimalEditingSupport;
import com.magnetstreet.swt.beanwidget.datagrid2.reflective.editor.BooleanEditingSupport;
import com.magnetstreet.swt.beanwidget.datagrid2.reflective.editor.CalendarEditingSupport;
import com.magnetstreet.swt.beanwidget.datagrid2.reflective.editor.SelectableObjectEditingSupport;
import com.magnetstreet.swt.beanwidget.datagrid2.reflective.header.TemplatedColumnHeaderProvider;
import com.magnetstreet.swt.beanwidget.datagrid2.reflective.viewer.TemplatedColumnLabelProvider;
import com.magnetstreet.swt.example.bean.Division;
import com.magnetstreet.swt.example.bean.Order;
import com.magnetstreet.swt.example.bean.OrderItem;
import org.eclipse.swt.widgets.Composite;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OrderReflectiveDataGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public class OrderReflectiveDataGrid extends ReflectiveDataGrid<Order> {
    public AtomicInteger discountTotalSaveCount = new AtomicInteger(0);

    public OrderReflectiveDataGrid(Composite composite, int i) {
        super(composite, i);
    }
    @Override protected void preInit() {
        defineColumn_ID();
        defineColumn_Items();
        defineColumn_Division();
        defineColumn_DiscountTotal();
        defineColumn_TotalCost();
        defineColumn_Paid();
        defineColumn_PlacedOn();
    }

    protected void defineColumn_ID() {
        bindColumnWithDefaults("id", true);
    }

    protected void defineColumn_Items() {
        TemplatedColumnHeaderProvider<Order> headerProvider = getDefaultColumnHeaderTemplate("items");
        headerProvider.setWidth(200);
        bindHeader("items", headerProvider);
        bindViewer("items", new TemplatedColumnLabelProvider<Order>() {
            @Override protected String doGetText(String propertyName, Order modelObject) {
                String itemStr = "";
                List<OrderItem> orderItems = modelObject.getItems();
                if(orderItems!=null) {
                    for(OrderItem item: orderItems)
                        itemStr += item.getProductName()+"("+item.getQuantity()+","+item.getProductUnitPrice()+") ";
                }
                return itemStr;
            }
        });
        bindSorter("items", getDefaultColumnSorterComparable("items"));
    }



    protected void defineColumn_Division() {
        bindColumnWithDefaults("division", true);
        bindViewer("division", new TemplatedColumnLabelProvider<Order>() {
            @Override protected String doGetText(String propertyName, Order modelObject) {
                return modelObject.getDivision().getName();
            }
        });
        bindEditor("division", new SelectableObjectEditingSupport<Order, Division>("division", this) {
            @Override protected Division[] getSelectables() {
                Division[] divisions = new Division[3];
                divisions[0] = new Division();
                divisions[0].setId(1);
                divisions[0].setName("Division A");
                divisions[1] = new Division();
                divisions[1].setId(2);
                divisions[1].setName("Division B");
                divisions[2] = new Division();
                divisions[2].setId(3);
                divisions[2].setName("Division C");

                return divisions;
            }
            @Override protected String selectableToString(Division obj) {
                return obj.getName();
            }
        });
    }

    protected void defineColumn_DiscountTotal() {
        bindColumnWithDefaults("discountTotal", true);
        bindEditor("discountTotal", new BigDecimalEditingSupport<Order>("discountTotal", this) {
            @Override protected void setModelValue(Order modelObject, String newValidValueFromControl) {
                super.setModelValue(modelObject, newValidValueFromControl);
                discountTotalSaveCount.incrementAndGet();
            }
        });
    }

    protected void defineColumn_TotalCost() {
        bindColumnWithDefaults("totalCost", true);
        bindEditor("totalCost", new BigDecimalEditingSupport<Order>("totalCost", this));
    }

    protected void defineColumn_Paid() {
        bindColumnWithDefaults("paid", true);
        bindEditor("paid", new BooleanEditingSupport<Order>("paid", this));
    }

    protected void defineColumn_PlacedOn() {
        bindColumnWithDefaults("placedOn", true);
        bindViewer("placedOn", new TemplatedColumnLabelProvider<Order>() {
            @Override protected String doGetText(String propertyName, Order modelObject) {
                SimpleDateFormat defaultDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                return defaultDateFormat.format(modelObject.getPlacedOn().getTime());
            }
        });
        bindEditor("placedOn", new CalendarEditingSupport<Order>("placedOn", this));
    }
}
