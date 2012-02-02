package com.magnetstreet.swt.beanwidget.datagrid2;

import com.magnetstreet.swt.beanwidget.datagrid2.filter.ColumnFilter;
import com.magnetstreet.swt.beanwidget.datagrid2.header.ColumnHeaderProvider;
import com.magnetstreet.swt.beanwidget.datagrid2.sorter.TableViewComparator;
import com.magnetstreet.swt.util.BeanUtil;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
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
 * AbstractDataTableGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
public abstract class AbstractDataTableGrid<T> extends AbstractDataGrid<T> implements DataTableGrid<T> {
    private Logger logger = Logger.getLogger(AbstractDataTableGrid.class.getSimpleName());

    private boolean initialized = false;

    protected int tableStyle = 0;
    protected Collection<T> beans = new ArrayList<T>();

    protected Map<String, ColumnHeaderProvider> columnHeaderDefinitions = new LinkedHashMap<String, ColumnHeaderProvider>();
    protected Map<String, ColumnLabelProvider> columnDefinitions = new LinkedHashMap<String, ColumnLabelProvider>();
    protected Map<String, Callable<String>> legacyRegexFilterDefinitions = new LinkedHashMap<String, Callable<String>>();
    protected Map<String, ColumnFilter> filterDefinitions = new LinkedHashMap<String, ColumnFilter>();
    protected Map<String, EditingSupport> cellEditorDefinitions = new LinkedHashMap<String, EditingSupport>();
    protected Map<String, ICellEditorValidator> cellEditorValidatorDefinitions = new LinkedHashMap<String, ICellEditorValidator>();
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

    protected ViewerFilter legacyRegexViewerFilter = new ViewerFilter() {
        @Override public boolean select(Viewer viewer, Object o, Object o1) {
            for(String property: legacyRegexFilterDefinitions.keySet()) {
                try {
                    String filterValue = legacyRegexFilterDefinitions.get(property).call();
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

    protected ViewerFilter defaultViewerFilter = new ViewerFilter() {
        @Override public boolean select(Viewer viewer, Object parentElement, Object element) {
            for(String property: filterDefinitions.keySet()) {
                try {
                    Object propertyValue = BeanUtil.getFieldChainValueWithGetters(element, property);
                    return filterDefinitions.get(property).checkModelProperty(propertyValue);
                } catch (Throwable t) {
                    logger.log(Level.SEVERE, "Unexepect exception while retrieving filter data.", t);
                }
            }
            return true;
        }
    };

    public AbstractDataTableGrid(Composite composite, int i) {
        super(composite, SWT.NONE);
        setLayout(new FillLayout());
        tableStyle = i;
        setViewer(new TableViewer(this, tableStyle|SWT.FULL_SELECTION));
        preInit();
        initialize();
    }

    @Override public TableViewer getTableViewer() { return (TableViewer)getViewer(); }

    protected void generateViewerColumns() {
        for(final String property: columnHeaderDefinitions.keySet()) {
            ColumnHeaderProvider headerProvider = columnHeaderDefinitions.get(property);
            TableViewerColumn columnViewer = new TableViewerColumn(getTableViewer(), SWT.NONE);
            TableColumn column = columnViewer.getColumn();
            column.setText(headerProvider.getTitle());
            column.setWidth(headerProvider.getWidth());
            column.setMoveable(headerProvider.isMoveable());
            column.setResizable(headerProvider.isResizable());
            column.addSelectionListener(new SelectionAdapter() {
                @Override public void widgetSelected(SelectionEvent selectionEvent) {
                    defaultSorter.setProperty(property);
                    getTableViewer().refresh();
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

    /**
     * Method invoked before the class is initialized after instantiation. All pre-creation tasks must be completed
     * in this method or an Exception will likely be encountered.
     */
    protected abstract void preInit();

    protected void initialize() {
        generateViewerColumns();
        Table table = getTableViewer().getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        getTableViewer().setContentProvider(new ArrayContentProvider());
        getTableViewer().addFilter(legacyRegexViewerFilter);
        getTableViewer().addFilter(defaultViewerFilter);
        getTableViewer().setComparator(defaultSorter);

        initialized = true;
    }

    @Override public void refresh() {
        getTableViewer().setInput(beans);
        getTableViewer().refresh();
    }

    /**
     * Defines how a column bound to the given property should be sorted when the user chooses to sort by that
     * column. The comparator should function as defined in the Java 1.6 spec.
     * @param property The String name of the property in the bean class that should consider the comparator
     *        when sorted by
     * @param comparator The Comparator to execute when sorted by the particular bean property
     */
    protected void bindSorter(String property, Comparator<T> comparator) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        sortingDefinitions.put(property, comparator);
    }
    /**
     * Defines how a column can providing editing support to each row's cell. Uses the JFaces editing support
     * interface to define the mapping between the GUI component and the backing object as well as the
     * control to be used to display the editable column.
     * @param property The name of the property in the bean to bind this editor
     * @param editingSupportDef The JFace EditingSupport implementation
     */
    protected void bindEditor(String property, EditingSupport editingSupportDef) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        cellEditorDefinitions.put(property, editingSupportDef);
    }
    /**
     * Defines how a bean property is displayed in the table cell normally, each viewable column requires
     * a bound viewer!
     * @param property The property to bind the column against
     * @param columnLabelProvider
     */
    protected void bindViewer(String property, ColumnLabelProvider columnLabelProvider) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        columnDefinitions.put(property, columnLabelProvider);
    }
    /**
     * Defines the header and column properties for a particular field, for every header defined
     * there is an expected viewer bound to the same property.
     * @param property The property to bind the column header to
     * @param columnHeaderProvider The column definition including header title
     */
    protected void bindHeader(String property, ColumnHeaderProvider columnHeaderProvider) {
        if(initialized)
            throw new RuntimeException("Cannot bind after the data grid has been initialized.");
        columnHeaderDefinitions.put(property, columnHeaderProvider);
    }


    @Override public void bindFilter(String property, Callable<String> valueGetter) {
        legacyRegexFilterDefinitions.put(property, valueGetter);
    }
    @Override public void bindFilter(Field property, Callable<String> valueGetter) {
        bindFilter(property.getName(), valueGetter);
    }
    @Override public void bindFilter(Method getter, Callable<String> valueGetter) {
        bindFilter(BeanUtil.getPropertyNameFromGetter(getter), valueGetter);
    }

    @Override public void bindFilter(String property, ColumnFilter valueGetter) {
        filterDefinitions.put(property, valueGetter);
    }
    @Override public void bindFilter(Field property, ColumnFilter valueGetter) {
        bindFilter(property.getName(), valueGetter);
    }
    @Override public void bindFilter(Method getter, ColumnFilter valueGetter) {
        bindFilter(BeanUtil.getPropertyNameFromGetter(getter), valueGetter);
    }

    @Override public void unbindFilter(String property) {
        legacyRegexFilterDefinitions.remove(property);
    }
    @Override public void unbindFilter(Field property) {
        unbindFilter(property.getName());
    }
    @Override public void unbindFilter(Method getter) {
        unbindFilter(BeanUtil.getPropertyNameFromGetter(getter));
    }

    @Override public void addDoubleClickListener(IDoubleClickListener listener) {getTableViewer().addDoubleClickListener(listener);}

    @Override
    public List<T> getSelectedBeans() {
        List<T> selectedBeans = new LinkedList<T>();
        for(TableItem ti: getTableViewer().getTable().getSelection())
            selectedBeans.add((T)ti.getData());
        return selectedBeans;
    }

    @Override public void setBeans(Collection<T> beans) { this.beans = beans; }
    @Override public Collection<T> getBeans() { return this.beans; }
    @Override public void addBean(T bean) { this.beans.add(bean); }
    @Override public void removeBean(T bean) { this.beans.remove(bean); }
    @Override public void removeAllBeans() { this.beans = new ArrayList<T>(); }
}
