package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.exception.InvalidGridViewSetupException;
import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import com.magnetstreet.swt.beanwidget.callback.SaveBeanCallback;
import com.magnetstreet.swt.beanwidget.dataview.DataView;
import com.magnetstreet.swt.beanwidget.listener.SingleAndDblClickListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AbstractEditableDataGrid
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 17, 2009
 * @since Dec 17, 2009
 */
public abstract class AbstractEditableDataGrid<T> extends AbstractDataGrid<T> implements EditableDataGrid<T> {
    private static Log log = LogFactory.getLog(AbstractEditableDataGrid.class);

    private Map<T, DataView<T>> dataGridViews = new ConcurrentHashMap<T, DataView<T>>();
    protected boolean editable;
    protected Map<Control, FocusListener> activeTableFocusLostListeners = new HashMap<Control, FocusListener>();
    protected Long lastClickTime = 0L;
    protected FocusListener activeTableFocusListener;
    protected SaveBeanCallback<T> saveBeanCallback = new SaveBeanCallback<T>() {
        @Override public T doCallback(T bean) {
            log.warn("Using default save bean callback object, bean is NOT saved.");
            return bean;
        }
    };

    public class ActiveControl {
        public int columnIndex, rowIndex;
        public Control control;
        public String originalText;
        public DataView dataView;
        public TableItem tableItem;
        public AtomicBoolean locked = new AtomicBoolean(false);
    }
    private volatile ActiveControl preActivatedControl;
    private volatile ActiveControl activeControl;

    protected AbstractEditableDataGrid(Composite composite, int i) {
        super(composite, i);
        log.debug("Creating " + this.getClass().getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    public void setColumnEditable(int colNumber, boolean editable) {
        dataGridColumns[colNumber].editable = editable;
    }

    @Override public void redraw() {
        super.redraw();
        if(dataBeans == null || dataBeans.size() < 1) return;
        updateDataViewWidgets();
        removeExistingEditActionListeners();
        applyEditActionListeners();
    }
    /**
     * Synchronizes the data views with the data in the table.
     */
    private void updateDataViewWidgets() {
        for(DataView<T> view: dataGridViews.values()) {
            view.resetViewDataObject();
        }
    }
    /**
     * Removes all listeners created by the applyEditActionListeners()
     */
    private void removeExistingEditActionListeners() {
        if(activeTableFocusListener !=null)
            dataGridTable.removeFocusListener(activeTableFocusListener);
        if(activeTableClickListener !=null)
            dataGridTable.removeMouseListener(activeTableClickListener);
    }

    public void setSaveBeanCallback(SaveBeanCallback<T> saveBeanCallback) {
        this.saveBeanCallback = saveBeanCallback;
    }
    
    /**
     * Attaches listeners to the internal table to track editing requests.
     */
    @Override protected void applyEditActionListeners() {
        activeTableFocusListener = new FocusAdapter() {
            @Override public void focusGained(FocusEvent focusEvent) {
                deactivateActiveControl();
            }
        };

        activeTableClickListener = new SingleAndDblClickListener() {
            @Override protected void handlePreSingleClick(MouseEvent mouseEvent) {
                handlePreSingleClickEvent(mouseEvent);
            }
            @Override protected void handleSingleClick(MouseEvent mouseEvent) {
                handleRowSingleClickEvent(mouseEvent);
            }
            @Override protected void handleDoubleClick(MouseEvent mouseEvent) {
                handleRowDblClickEvent(mouseEvent);
                if(preActivatedControl!=null) {
                    while(preActivatedControl.locked.get()) {
                        try { Thread.sleep(50); } catch (InterruptedException e) { log.warn("Interrupted while waiting on lock for pre activated control so it can be cleaned up.", e); }
                    }
                    preActivatedControl = null;
                }
            }
        };
        dataGridTable.addMouseListener(activeTableClickListener);
        dataGridTable.addFocusListener(activeTableFocusListener);
    }

    /**
     * Handles preparing for a single click event incase a double click doesn't occur. Used
     * for performance reasons only.
     * @param event
     */
    protected void handlePreSingleClickEvent(MouseEvent event) {
        log.debug("Triggered pre-single click event handler");
        if(preActivatedControl != null || getSelectedBeans()==null || getSelectedBeans().size() > 1 || !dataGridTable.isFocusControl() )
            return;
        preActivatedControl = new ActiveControl();
        preActivatedControl.locked.set(true);
        Rectangle clientArea = dataGridTable.getClientArea();
        Point pt = new Point(event.x, event.y);
        int rowIndex = dataGridTable.getTopIndex();
        while (rowIndex < dataGridTable.getItemCount()) {
            boolean visible = false;
            TableItem item = dataGridTable.getItem(rowIndex);
            for (int colIndex = 0; colIndex < dataGridColumns.length; colIndex++) {
                Rectangle rect = item.getBounds(colIndex);
                if (rect.contains(pt)) {
                    loadDataView(item);
                    preActivatedControl.columnIndex = colIndex;
                    preActivatedControl.rowIndex = rowIndex;
                    preActivatedControl.dataView = dataGridViews.get(item.getData());
                    preActivatedControl.tableItem = item;
                    preActivatedControl.locked.set(false);
                }
                if (!visible && rect.intersects(clientArea)) {
                    visible = true;
                }
            }
            if (!visible)
                return;
            rowIndex++;
        }
    }

    private void loadDataView(TableItem item) {
        T bean = (T)item.getData();
        if(!dataGridViews.containsKey(bean)) {
            dataGridViews.put(bean, createDataView(bean));
            ((Composite)dataGridViews.get(bean)).setVisible(false);
        }
    }

    /**
     * Handles the single click event of editing a cell in the table, will
     * wait on the handlePreSingleClickEvent(MouseEvent event) to complete.
     * @param event The mouse event used to get click coords
     */
    protected void handleRowSingleClickEvent(MouseEvent event) {
        log.debug("Triggered single click event handler");
        if(activeControl != null || preActivatedControl==null || getSelectedBeans()==null || getSelectedBeans().size() > 1 || !dataGridTable.isFocusControl() )
            return;
        while(preActivatedControl.locked.get()) {
            try { Thread.sleep(50); } catch (InterruptedException e) { log.warn("Interrupted while waiting for pre activation control to load.", e); }
        }
        showUnderlyingControl(preActivatedControl.tableItem, preActivatedControl.columnIndex, preActivatedControl.rowIndex);
        preActivatedControl = null;
    }

    /**
     * @return The updated text
     * @throws ViewDataBeanValidationException When user entered data could not be validated.
     */
    private String getActiveControlText() throws ViewDataBeanValidationException {
        T bean = (T)activeControl.dataView.getViewDataObject();
        Object value = dataGridColumns[activeControl.columnIndex].getDisplayValue(bean);
        return (value==null) ? "" : value.toString();
    }

    private void deactivateActiveControl() {
        if(activeControl == null) return;
        synchronized (activeControl) {
            try {
                T updatedBean = saveActiveControl();
                activeControl.control.setVisible(false);
                activeControl.dataView.resetViewDataObject();

                if(updatedBean!=null) {
                    /*removeBean((T)activeControl.tableItem.getData());
                    addDataBean(updatedBean);*/
                    redraw();
                }
                activeControl = null;
            } catch(ViewDataBeanValidationException e) {
                log.warn(e);
                if(activeControl.dataView.getValidationErrorMap().containsKey(activeControl.control))
                    activeControl.control.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
                activeControl.dataView.showInputErrors();
                activeControl.dataView.resetViewDataObject();
            } catch (Throwable t) {
                log.error("Unknown problem deactivating active control.", t);
            }
        }
    }

    private void showUnderlyingControl(TableItem item, int col, int row) {
        if(dataGridColumns[col].editable != true) {
            log.warn("The field you attempted to edit has been marked as being uneditable.");
            return;
        }
        T bean = (T)item.getData();
        if(!dataGridViews.containsKey(bean)) {
            log.error("Data view for given table item doesn't exist: "+item+", (col:"+col+",row:"+row+")");
            return;
        }
        DataView<T> view = dataGridViews.get(bean);
        try {
            activeControl = new ActiveControl();
            activeControl.columnIndex = col;
            activeControl.rowIndex = row;
            activeControl.dataView = view;
            activeControl.tableItem = item;
            activeControl.originalText = getActiveControlText();
            activeControl.control = view.findWidget(dataGridColumns[col].beanProperty);
            activeControl.control.setParent(dataGridTable.getParent());
            activeControl.control.moveAbove(dataGridTable);
            activeControl.control.setBounds(item.getBounds(col));
            activeControl.control.setVisible(true);
            activeControl.control.setEnabled(dataGridColumns[col].editable);
            activeControl.control.redraw();
            activeControl.control.setFocus();
        } catch (Throwable t) {
            throw new InvalidGridViewSetupException("Unable to dynamically get data view control.", t);
        }
    }

    /**
     * Persists a change to a cell in the grid's table.
     */
    private T saveActiveControl() throws ViewDataBeanValidationException {
        if(activeControl == null) return null;
        activeControl.dataView.setViewDataObject(activeControl.dataView.getViewDataObject());
        if(!getActiveControlText().trim().equals(activeControl.originalText.trim())) {
            try {
                return saveBeanCallback.doCallback((T)activeControl.tableItem.getData());
            } catch (Exception e) {
                log.error("Unable to execute save bean callback.", e);
            }
        }
        return null;
    }

    /**
     * Builds a data views which are represented as rows in the data grid.
     * @param bean The POJOs view is based on filled with data to be displayed
     */
    protected abstract DataView<T> createDataView(T bean);

    /**
     * {@inheritDoc}
     */
    @Override public void addDataBean(T bean) {
        super.addDataBean(bean);
    }
    /**
     * {@inheritDoc}
     */
    @Override public void removeBean(T bean) {
        deactivateActiveControl();
        super.removeBean(bean);
        if(dataGridViews.containsKey(bean)) {
            ((Composite)dataGridViews.get(bean)).dispose();
            dataGridViews.remove(bean);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override public void removeAllBeans() {
        super.removeAllBeans();
        dataGridViews = new HashMap<T, DataView<T>>();
    }
}

