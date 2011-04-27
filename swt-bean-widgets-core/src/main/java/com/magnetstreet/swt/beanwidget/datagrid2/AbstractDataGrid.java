package com.magnetstreet.swt.beanwidget.datagrid2;

import com.magnetstreet.swt.util.BeanUtil;
import com.magnetstreet.swt.viewers.ColumnHeaderProvider;
import com.magnetstreet.swt.viewers.TableViewComparator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AbstractDataGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
public class AbstractDataGrid<T> extends Composite implements DataGrid<T> {
    private Logger logger = Logger.getLogger(AbstractDataGrid.class.getSimpleName());

    private boolean initialized = false;

    protected int tableStyle = 0;
    protected TableViewer tableViewer;
    protected Collection<T> beans = new ArrayList<T>();

    protected Map<String, ColumnHeaderProvider> columnHeaderDefinitions = new LinkedHashMap<String, ColumnHeaderProvider>();
    protected Map<String, ColumnLabelProvider> columnDefinitions = new LinkedHashMap<String, ColumnLabelProvider>();
    protected Map<String, Callable<String>> filterDefinitions = new LinkedHashMap<String, Callable<String>>();
    protected Map<String, EditingSupport> cellEditorDefinitions = new LinkedHashMap<String, EditingSupport>();
    protected Map<String, Comparator<T>> sortingDefinitions = new LinkedHashMap<String, Comparator<T>>();

    protected Map<String, TableViewerColumn> tableViewerColumnMap = new LinkedHashMap<String, TableViewerColumn>();

    protected TableViewComparator defaultSorter = new TableViewComparator() {
        @Override public int compare(Viewer viewer, Object e1, Object e2) {
            Comparator<T> viewerComparator = sortingDefinitions.get(property);
            if(viewerComparator!=null) {
                int asc = viewerComparator.compare((T)e1, (T)e2);
                return (direction) ?  asc : -1 * asc ;
            }
            return 0;
        }
    };

    protected ViewerFilter defaultViewerFilter = new ViewerFilter() {
        @Override public boolean select(Viewer viewer, Object o, Object o1) {
            for(String property: filterDefinitions.keySet()) {
                try {
                    String filterValue = filterDefinitions.get(property).call();
                    String recordValue = columnDefinitions.get(property).getText(o1);
                    if(!recordValue.matches(filterValue))
                        return false;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Unexepect exception while retrieving filter data.", e);
                }
            }
            return true;
        }
    };

    public AbstractDataGrid(Composite composite, int i) {
        super(composite, SWT.NONE);
        tableStyle = i;
        tableViewer = new TableViewer(this, tableStyle|SWT.FULL_SELECTION);
    }

    protected void generateViewerColumns() {
        for(final String property: columnHeaderDefinitions.keySet()) {
            ColumnHeaderProvider headerProvider = columnHeaderDefinitions.get(property);
            TableViewerColumn columnViewer = new TableViewerColumn(tableViewer, SWT.NONE);
            TableColumn column = columnViewer.getColumn();
            column.setText(headerProvider.getTitle());
            column.setWidth(headerProvider.getWidth());
            column.setMoveable(headerProvider.isMoveable());
            column.setResizable(headerProvider.isResizable());
            column.addSelectionListener(new SelectionAdapter() {
                @Override public void widgetSelected(SelectionEvent selectionEvent) {
                    defaultSorter.setProperty(property);
                    tableViewer.refresh();
                }
            });
            if(headerProvider.getTooltip()!=null) column.setToolTipText(headerProvider.getTooltip());
            if(headerProvider.getImage()!=null) column.setImage(headerProvider.getImage());

            columnViewer.setLabelProvider(columnDefinitions.get(property));
            if(cellEditorDefinitions.containsKey(property))
                columnViewer.setEditingSupport(cellEditorDefinitions.get(property));

            tableViewerColumnMap.put(property, columnViewer);
        }
    }

    protected void initialize() {
        generateViewerColumns();
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.addFilter(defaultViewerFilter);
        tableViewer.setComparator(defaultSorter);

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        tableViewer.getControl().setLayoutData(gridData);
        initialized = true;
    }

    @Override public void refresh() {
        tableViewer.setInput(beans);
        tableViewer.refresh();
    }

    @Override public void bindSorter(String property, Comparator<T> comparator) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        sortingDefinitions.put(property, comparator);
    }
    @Override public void bindSorter(Field property, Comparator<T> comparator) {
        bindSorter(property.getName(), comparator);
    }
    @Override public void bindSorter(Method getter, Comparator<T> comparator) {
        bindSorter(BeanUtil.getPropertyNameFromGetter(getter), comparator);
    }

    @Override public void bindFilter(String property, Callable<String> valueGetter) {
        filterDefinitions.put(property, valueGetter);
    }
    @Override public void bindFilter(Field property, Callable<String> valueGetter) {
        bindFilter(property.getName(), valueGetter);
    }
    @Override public void bindFilter(Method getter, Callable<String> valueGetter) {
        bindFilter(BeanUtil.getPropertyNameFromGetter(getter), valueGetter);
    }

    @Override public void bindEditor(String property, EditingSupport editingSupportDef) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        cellEditorDefinitions.put(property, editingSupportDef);
    }
    @Override public void bindEditor(Field property, EditingSupport editingSupportDef) {
        bindEditor(property.getName(), editingSupportDef);
    }
    @Override public void bindEditor(Method getter, EditingSupport editingSupportDef) {
        bindEditor(BeanUtil.getPropertyNameFromGetter(getter), editingSupportDef);
    }

    @Override public void bindViewer(String property, ColumnLabelProvider columnLabelProvider) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        columnDefinitions.put(property, columnLabelProvider);
    }
    @Override public void bindViewer(Field property, ColumnLabelProvider columnLabelProvider) {
        bindViewer(property.getName(), columnLabelProvider);
    }
    @Override public void bindViewer(Method getter, ColumnLabelProvider columnLabelProvider) {
        bindViewer(BeanUtil.getPropertyNameFromGetter(getter), columnLabelProvider);
    }

    @Override public void bindHeader(String property, ColumnHeaderProvider columnHeaderProvider) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        columnHeaderDefinitions.put(property, columnHeaderProvider);
    }
    @Override public void bindHeader(Field property, ColumnHeaderProvider columnHeaderProvider) {
        bindHeader(property.getName(), columnHeaderProvider);
    }
    @Override public void bindHeader(Method getter, ColumnHeaderProvider columnHeaderProvider) {
        bindHeader(BeanUtil.getPropertyNameFromGetter(getter), columnHeaderProvider);
    }

    @Override
    public List<T> getSelectedBeans() {
        List<T> selectedBeans = new LinkedList<T>();
        for(TableItem ti: tableViewer.getTable().getSelection())
            selectedBeans.add((T)ti.getData());
        return selectedBeans;
    }

    @Override public void setBeans(Collection<T> beans) { this.beans = beans; }
    @Override public void addBean(T bean) { this.beans.add(bean); }
    @Override public void removeBean(T bean) { this.beans.remove(bean); }
    @Override public void removeAllBeans() { this.beans = new ArrayList<T>(); }
}
