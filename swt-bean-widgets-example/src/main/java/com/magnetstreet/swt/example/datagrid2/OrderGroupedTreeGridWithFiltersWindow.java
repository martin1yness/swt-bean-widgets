package com.magnetstreet.swt.example.datagrid2;

import com.magnetstreet.swt.beanwidget.datagrid2.GroupedDataTreeGrid;
import com.magnetstreet.swt.beanwidget.datagrid2.filter.ColumnFilter;
import com.magnetstreet.swt.example.bean.Order;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * OrderReflectiveWithFiltersWindow
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 5/5/11
 */
public class OrderGroupedTreeGridWithFiltersWindow extends ApplicationWindow {
    protected Group filtersGroup;
    protected Label orderIdFilterLabel, itemFilterLabel, discountTotalFilterLabel, totalCostFilterLabel;
    protected Text orderIdFilterText, itemFilterText;
    protected Spinner discountTotalFilterSpinner, totalCostFilterSpinner;
    protected Button paidFilterButton;
    protected Collection<Order> orders;

    protected OrderGroupedDataTreeGrid orderGroupedDataTreeGrid;

    public OrderGroupedTreeGridWithFiltersWindow(Shell parentShell) {
        super(parentShell);
    }

    protected Control createContents(Composite parent) {
        parent.setLayout(new FormLayout());

        {
            filtersGroup = new Group(parent, SWT.NONE);
            filtersGroup.setText("Filter(s)");
            FormData fd = new FormData(600,50);
            fd.left = new FormAttachment(0,100,5);
            fd.right = new FormAttachment(100,100,-5);
            fd.top = new FormAttachment(0,100,5);
            filtersGroup.setLayoutData(fd);
            filtersGroup.setLayout(new FormLayout());
        }
        {
            {   // Label
                orderIdFilterLabel = new Label(filtersGroup, SWT.RIGHT);
                orderIdFilterLabel.setText("ID:");
                FormData fd = new FormData(30, 20);
                fd.left = new FormAttachment(0,100,5);
                fd.top = new FormAttachment(0,100,5);
                orderIdFilterLabel.setLayoutData(fd);
            }
            {   // Text
                orderIdFilterText = new Text(filtersGroup, SWT.BORDER);
                FormData fd = new FormData(60,20);
                fd.left = new FormAttachment(orderIdFilterLabel, 5);
                fd.top = new FormAttachment(0,100,5);
                orderIdFilterText.setLayoutData(fd);
            }
        }
        {
            {   // Label
                itemFilterLabel = new Label(filtersGroup, SWT.RIGHT);
                itemFilterLabel.setText("Item(s):");
                FormData fd = new FormData(70,20);
                fd.left = new FormAttachment(orderIdFilterText, 5);
                fd.top = new FormAttachment(0,100,5);
                itemFilterLabel.setLayoutData(fd);
            }
            {   // Text
                itemFilterText = new Text(filtersGroup, SWT.BORDER);
                FormData fd = new FormData(150,20);
                fd.left = new FormAttachment(itemFilterLabel, 5);
                fd.top = new FormAttachment(0,100,5);
                itemFilterText.setLayoutData(fd);
            }
        }
        {
            {   // Label
                discountTotalFilterLabel = new Label(filtersGroup, SWT.RIGHT);
                discountTotalFilterLabel.setText("Discount:");
                FormData fd = new FormData(70,20);
                fd.left = new FormAttachment(itemFilterText, 5);
                fd.top = new FormAttachment(0,100,5);
                discountTotalFilterLabel.setLayoutData(fd);
            }
            {
                discountTotalFilterSpinner = new Spinner(filtersGroup, SWT.NONE);
                discountTotalFilterSpinner.setDigits(2);
                discountTotalFilterSpinner.setIncrement(1);
                discountTotalFilterSpinner.setPageIncrement(100);
                FormData fd = new FormData(70,20);
                fd.left = new FormAttachment(discountTotalFilterLabel, 5);
                fd.top = new FormAttachment(0,100,5);
                discountTotalFilterSpinner.setLayoutData(fd);
            }
        }
        {
            {   // Label
                totalCostFilterLabel = new Label(filtersGroup, SWT.RIGHT);
                totalCostFilterLabel.setText("Cost:");
                FormData fd = new FormData(60,20);
                fd.left = new FormAttachment(discountTotalFilterSpinner, 5);
                fd.top = new FormAttachment(0,100,5);
                totalCostFilterLabel.setLayoutData(fd);
            }
            {
                totalCostFilterSpinner = new Spinner(filtersGroup, SWT.NONE);
                totalCostFilterSpinner.setValues(1000, 0, 999999, 2, 1, 0);
                FormData fd = new FormData(80,20);
                fd.left = new FormAttachment(totalCostFilterLabel, 5);
                fd.top = new FormAttachment(0,100,5);
                totalCostFilterSpinner.setLayoutData(fd);
            }
        }
        {
            {
                paidFilterButton = new Button(filtersGroup, SWT.CHECK);
                paidFilterButton.setText("Included Paid?");
                FormData fd = new FormData(100,20);
                fd.left = new FormAttachment(totalCostFilterSpinner, 5);
                fd.top = new FormAttachment(0,100,5);
                paidFilterButton.setLayoutData(fd);
            }
        }

        {
            orderGroupedDataTreeGrid = new OrderGroupedDataTreeGrid(parent, SWT.FULL_SELECTION|SWT.MULTI);
            FormData fd = new FormData(600,300);
            fd.top = new FormAttachment(filtersGroup,5);
            fd.left = new FormAttachment(0,100,5);
            fd.right = new FormAttachment(100,100,-5);
            fd.bottom = new FormAttachment(100,100,-5);
            orderGroupedDataTreeGrid.setLayoutData(fd);
        }

        defineOrderGroupings();
        bindFilters();
        applyEventListeners();
        orderGroupedDataTreeGrid.setBeans(orders);
        orderGroupedDataTreeGrid.refresh();

        return parent;
    }

    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
    }

    protected void defineOrderGroupings() {
        orderGroupedDataTreeGrid.addGroup(new GroupedDataTreeGrid.Group<Order>("High Value", 10) {
            @Override public boolean matches(Order bean) {
                return bean.getTotalCost().compareTo(new BigDecimal("50.00")) >= 0;
            }
        });
        orderGroupedDataTreeGrid.addGroup(new GroupedDataTreeGrid.Group<Order>("Low Value", 5) {
            @Override public boolean matches(Order bean) {
                return bean.getTotalCost().compareTo(new BigDecimal("50.00")) < 0 && bean.getTotalCost().doubleValue() > 0;
            }
        });
        orderGroupedDataTreeGrid.addGroup(new GroupedDataTreeGrid.Group<Order>("No Value", 1) {
            @Override public boolean matches(Order bean) {
                return bean.getTotalCost().doubleValue() == 0;
            }
        });
    }

    protected void bindFilters() {
        orderGroupedDataTreeGrid.bindFilter(Order.class, "id", new ColumnFilter<Order>() {
            @Override
            public boolean checkModelProperty(Order modelObjectProperty) {
                return modelObjectProperty.getId().toString().contains(orderIdFilterText.getText());
            }
        });
    }

    protected void applyEventListeners() {
        ModifyListener applyFiltersModifyListener = new ModifyListener() {
            @Override public void modifyText(ModifyEvent modifyEvent) {
                orderGroupedDataTreeGrid.refresh();
            }
        };
        orderIdFilterText.addModifyListener(applyFiltersModifyListener);
        itemFilterText.addModifyListener(applyFiltersModifyListener);
        discountTotalFilterSpinner.addModifyListener(applyFiltersModifyListener);
        totalCostFilterSpinner.addModifyListener(applyFiltersModifyListener);
        paidFilterButton.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent selectionEvent) {
                orderGroupedDataTreeGrid.refresh();
            }
        });
    }
}
