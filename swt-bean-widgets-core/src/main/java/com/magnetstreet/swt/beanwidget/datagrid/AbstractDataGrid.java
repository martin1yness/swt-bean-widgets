package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.beanwidget.dataview.DataView;
import com.magnetstreet.swt.beanwidget.dataview.DataViewFactory;
import com.magnetstreet.swt.beanwidget.dataview.GenericDataViewDialog;
import com.magnetstreet.swt.beanwidget.dataview.GenericDataViewDialogImpl;
import com.magnetstreet.swt.beanwidget.listener.SingleAndDblClickListener;
import com.magnetstreet.swt.exception.InvalidGridViewSetupException;
import com.magnetstreet.swt.extra.SortableTable;
import com.magnetstreet.swt.util.TableColumnComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AbstractDataTableGrid
 *
 * Abstractly defines a table like widget specifically for Data Beans to display
 * designed to be extended to add additional features.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jul 28, 2009
 * @since Jul 28, 2009
 */
public abstract class AbstractDataGrid<T> extends Composite implements DataGrid<T> {
    private static Logger log = Logger.getLogger(AbstractDataGrid.class.getSimpleName());

    protected SortableTable dataGridTableSortWrapper;
    protected Table dataGridTable;
    protected SingleAndDblClickListener activeTableClickListener;
    protected GridColumn[] dataGridColumns;
    protected Set<DataGridFilter<T>> dataGridFilters = new HashSet<DataGridFilter<T>>();
    protected ArrayList<T> dataBeans = new ArrayList<T>();
    protected GenericDataViewDialog popupDataViewDialog;
    protected Class<DataView<T>> staticDataViewImpl;

    protected int width = 400;
    protected int height = 200;

    protected AbstractDataGrid(Composite composite, int i) {
        super(composite, i);
        initialize();
    }

    /**
     * Initializes the data grid table and component layout.
     */
    private void initialize() {
        setLayout(new FormLayout());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 1000, 0);
        fd.left = new FormAttachment(0, 1000, 0);
        fd.right = new FormAttachment(1000, 1000, 0);
        fd.bottom = new FormAttachment(1000, 1000, 0);
        dataGridTable = new Table(this, getStyle());
        dataGridTable.setLayoutData(fd);
        dataGridTable.setHeaderVisible(true);
        dataGridTable.setLinesVisible(true);
    }

    /**
     * Attaches listeners to the internal table to track editing requests.
     */
    protected void applyEditActionListeners() {
        activeTableClickListener = new SingleAndDblClickListener() {
            @Override protected void handlePreSingleClick(MouseEvent mouseEvent) { }

            @Override protected void handleSingleClick(MouseEvent mouseEvent) {}

            @Override protected void handleDoubleClick(MouseEvent mouseEvent) {
                handleRowDblClickEvent(mouseEvent);
            }
        };
        dataGridTable.addMouseListener(activeTableClickListener);
    }


    public void registerPopupDataViewImpl(Class<DataView<T>> dataViewImpl) {
        this.staticDataViewImpl = dataViewImpl;
    }

    /**
     * Overriding checkSubclass allows this class to extend org.eclipse.swt.widgets.Composite
     */
    protected void checkSubclass() {}

    /**
     * {@inheritDoc}
     */
    public void setColumnComparator(int colNumber, TableColumnComparator comparatorAlgorithm) {
        dataGridColumns[colNumber].sortAlgorithm = comparatorAlgorithm;
    }
    /**
     * {@inheritDoc}
     */
    public void setColumnVisible(int colNumber, boolean visiable) {
        dataGridColumns[colNumber].visible = visiable;
    }


    /**
     * {@inheritDoc}
     */
    public void redraw() {
        if(dataBeans == null || dataBeans.size() < 1) return;
        dataGridColumns = createColumnHeaders();

        dataGridTable.setRedraw(false);
        dataGridTable.removeAll();
        buildTable();
        dataGridTable.setRedraw(true);
    }



    protected void buildTable() {
        buildTableColumns(dataGridTable);
        buildTableItems(dataGridTable);
        for(TableColumn column: dataGridTable.getColumns())
            column.pack();
        if(dataGridTableSortWrapper==null)
            wrapSortable(dataGridTable);
        else
            dataGridTableSortWrapper.resort();
    }

    protected void buildTableColumns(Table tbl) {
        if(tbl.getColumnCount() > 0) return;
        for(int i=0; i<dataGridColumns.length; i++) {
            if(dataGridColumns[i] == null || !dataGridColumns[i].visible) continue;
            TableColumn col = new TableColumn(tbl, SWT.NONE);
            col.setText(dataGridColumns[i].title);
        }
    }

    protected void buildTableItems(Table tbl) {
        for(T bean: dataBeans) {
            if(!showBean(bean)) continue;

            TableItem item = new TableItem(tbl, 0);
            for(int j=0; j<dataGridColumns.length; j++) {
                if(!dataGridColumns[j].visible) continue;
                try {
                    Object value = dataGridColumns[j].getDisplayValue(bean);
                    item.setText(j, (value == null) ? "" :  value.toString());
                    item.setData(bean);
                } catch (Throwable e) {
                    throw new InvalidGridViewSetupException("Getter for member name '"+dataGridColumns[j].beanProperty.getName()+"' of column '"+dataGridColumns[j].title +"' could not be dynamically loaded.", e);
                }
            }
        }
    }

    /**
     * Hanldes sorting the given table and providing sort functionality to the user.
     * @param tbl
     */
    protected void wrapSortable(Table tbl) {
        int sortIndex = 0;
        boolean direction = false;
        if(dataGridTableSortWrapper != null) {
            sortIndex = dataGridTableSortWrapper.getCurrentSortColumnId();
            direction = dataGridTableSortWrapper.getCurrentReverseBit();
        }
        dataGridTableSortWrapper = new SortableTable(dataGridTable);
        for(int i=0; i<dataGridColumns.length; i++)
            if(dataGridColumns[i].sortAlgorithm != null) dataGridTableSortWrapper.setComparator(i, dataGridColumns[i].sortAlgorithm);        
        dataGridTableSortWrapper.sortByColumn(sortIndex, direction);
    }

    /**
     * Handles the double click event of opening a bean row of the grid
     * into its own dynamic data view dialog window.
     * @param event The mouse event used to get click coords
     */
    protected void handleRowDblClickEvent(MouseEvent event) {
        log.logp(Level.FINER, "AbstractDataTableGrid", "handleRowDblClickEvent", "Triggered double click event handler");
        List<T> beans = getSelectedBeans();
        if(beans == null || beans.size() == 0) return;
        DataView<T> view = null;
        if(staticDataViewImpl!=null) {
            try {
                Constructor cons = staticDataViewImpl.getConstructor(Composite.class, int.class);
                view = (DataView<T>)cons.newInstance(this, SWT.NONE);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Unable to construct dataview", e);
            }
        } else {
            if(popupDataViewDialog == null)
                setupGenericDataViewDialogImpl();
            view = (DataView<T>)DataViewFactory.getInstance().getDataView(beans.get(0), this, SWT.NONE);
        }
        view.setViewDataObject(beans.get(0));
        popupDataViewDialog.setDataView(view);
        popupDataViewDialog.openBlocking();
    }

    /**
     * If the user doesn't explicitly set the data view dialog popout class,
     * this generic one will be created.
     */
    protected void setupGenericDataViewDialogImpl() {
        popupDataViewDialog = new GenericDataViewDialogImpl(getShell(), SWT.DIALOG_TRIM);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> getSelectedBeans() {
        List<T> rList = new ArrayList<T>();
        if(dataGridTable.getSelectionCount() <= 0) return null;
        for(TableItem item: dataGridTable.getSelection())
            rList.add((T) item.getData());
        return rList;
    }

    /**
     * {@inheritDoc}
     */
    public List<T> getAllBeans() {
        return dataBeans; 
    }

    /**
     * Loops through all defined filters to check if a bean should be displayed
     * in grid or not. All filters must say yes!
     * @param bean The bean to validate
     * @return True if bean should be displayed, false otherwise.
     */
    protected boolean showBean(T bean) {
        if(dataGridFilters == null || dataGridFilters.size() == 0) return true;
        for(DataGridFilter<T> filter: dataGridFilters)
            if(!filter.include(bean)) return false;
        return true;
    }

    /**
     * Creates the GridColumn array that displays the header like
     * table buttons.
     */
    protected abstract GridColumn[] createColumnHeaders();


    /**
     * Add a collection of beans in a single call
     * @param beans The collection of beans to be added to the data grid
     */
    public void addAllDataBeans(Collection<T> beans) {
        for(T bean: beans)
            addDataBean(bean);
    }
    /**
     * Adds a data to the grid
     * @param bean The row or bean that is to be added to the grid
     */
    public void addDataBean(T bean) {
        dataBeans.add(bean);
    }

    /**
     * Removes a data bean from the grid, essentially removing a dataView.
     * Does NOT redraw grid, so must be accompnied by a redraw command to
     * display removal.
     * @param bean The exact object that is represented by the data View to be removed.
     */
    public void removeBean(T bean) {
        dataBeans.remove(bean);
    }
    /**
     * {@inheritDoc}
     */
    public void removeAllBeans() {
        dataBeans = new ArrayList<T>();
    }

    /**
     * Adds a data bean filter which will filter out display results that would normally
     * be all displayed.
     * @param filter The DataGridFilter to use when building table.
     */
    public void addDataFilter(String name, String description, DataGridFilter<T> filter) {
        filter.name = name;
        filter.description = description;
        dataGridFilters.add(filter);
    }
    /**
     * Removes a data filter based on the name it was created with.
     * @param name
     */
    public void removeDataFilter(String name) {
        DataGridFilter<T> filter = new DataGridFilter<T>() {
            public boolean include(T bean) { return false; }
        };
        filter.name = name;
        dataGridFilters.remove(filter);
    }
    /**
     * Removes all data filters, on next redraw all data will be displayed.
     */
    public void clearDataFilters() {
        dataGridFilters.clear();
    }


    /***********
     *  Table Listener Delegated methods...
     */
    @Override
    public void addControlListener(ControlListener controlListener) {
        dataGridTable.addControlListener(controlListener);
    }

    @Override
    public void addDragDetectListener(DragDetectListener dragDetectListener) {
        dataGridTable.addDragDetectListener(dragDetectListener);
    }

    @Override
    public void addFocusListener(FocusListener focusListener) {
        dataGridTable.addFocusListener(focusListener);
    }

    @Override
    public void addHelpListener(HelpListener helpListener) {
        dataGridTable.addHelpListener(helpListener);
    }

    @Override
    public void addKeyListener(KeyListener keyListener) {
        dataGridTable.addKeyListener(keyListener);
    }

    @Override
    public void addMenuDetectListener(MenuDetectListener menuDetectListener) {
        dataGridTable.addMenuDetectListener(menuDetectListener);
    }

    @Override
    public void addMouseListener(MouseListener mouseListener) {
        dataGridTable.addMouseListener(mouseListener);
    }

    @Override
    public void addMouseTrackListener(MouseTrackListener mouseTrackListener) {
        dataGridTable.addMouseTrackListener(mouseTrackListener);
    }

    @Override
    public void addMouseMoveListener(MouseMoveListener mouseMoveListener) {
        dataGridTable.addMouseMoveListener(mouseMoveListener);
    }

    @Override
    public void addMouseWheelListener(MouseWheelListener mouseWheelListener) {
        dataGridTable.addMouseWheelListener(mouseWheelListener);
    }

    @Override
    public void addPaintListener(PaintListener paintListener) {
        dataGridTable.addPaintListener(paintListener);
    }

    @Override
    public void addTraverseListener(TraverseListener traverseListener) {
        dataGridTable.addTraverseListener(traverseListener);
    }

    @Override
    public void addListener(int i, Listener listener) {
        dataGridTable.addListener(i, listener);
    }

    @Override
    public void addDisposeListener(DisposeListener disposeListener) {
        dataGridTable.addDisposeListener(disposeListener);
    }

    @Override
    public void removeControlListener(ControlListener controlListener) {
        dataGridTable.removeControlListener(controlListener);
    }

    @Override
    public void removeDragDetectListener(DragDetectListener dragDetectListener) {
        dataGridTable.removeDragDetectListener(dragDetectListener);
    }

    @Override
    public void removeFocusListener(FocusListener focusListener) {
        dataGridTable.removeFocusListener(focusListener);
    }

    @Override
    public void removeHelpListener(HelpListener helpListener) {
        dataGridTable.removeHelpListener(helpListener);
    }

    @Override
    public void removeKeyListener(KeyListener keyListener) {
        dataGridTable.removeKeyListener(keyListener);
    }

    @Override
    public void removeMenuDetectListener(MenuDetectListener menuDetectListener) {
        dataGridTable.removeMenuDetectListener(menuDetectListener);
    }

    @Override
    public void removeMouseTrackListener(MouseTrackListener mouseTrackListener) {
        dataGridTable.removeMouseTrackListener(mouseTrackListener);
    }

    @Override
    public void removeMouseListener(MouseListener mouseListener) {
        dataGridTable.removeMouseListener(mouseListener);
    }

    @Override
    public void removeMouseMoveListener(MouseMoveListener mouseMoveListener) {
        dataGridTable.removeMouseMoveListener(mouseMoveListener);
    }

    @Override
    public void removeMouseWheelListener(MouseWheelListener mouseWheelListener) {
        dataGridTable.removeMouseWheelListener(mouseWheelListener);
    }

    @Override
    public void removePaintListener(PaintListener paintListener) {
        dataGridTable.removePaintListener(paintListener);
    }

    @Override
    public void removeTraverseListener(TraverseListener traverseListener) {
        dataGridTable.removeTraverseListener(traverseListener);
    }

    @Override
    public void removeListener(int i, Listener listener) {
        dataGridTable.removeListener(i, listener);
    }

    @Override
    public void removeDisposeListener(DisposeListener disposeListener) {
        dataGridTable.removeDisposeListener(disposeListener);
    }
}