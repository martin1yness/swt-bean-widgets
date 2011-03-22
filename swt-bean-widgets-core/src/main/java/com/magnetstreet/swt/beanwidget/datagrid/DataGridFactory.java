package com.magnetstreet.swt.beanwidget.datagrid;

import com.magnetstreet.swt.annotation.SWTEntity;
import com.magnetstreet.swt.annotation.SWTWidget;
import com.magnetstreet.swt.exception.BeanAnnotationException;
import com.magnetstreet.swt.beanwidget.dataview.DataView;
import com.magnetstreet.swt.beanwidget.dataview.DataViewFactory;
import com.magnetstreet.swt.beanwidget.dataview.DynamicDataView;
import com.magnetstreet.swt.beanwidget.dataview.layout.BaseLayout;
import org.eclipse.swt.widgets.Composite;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.List;

/**
 * Data Grid Factory
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 17, 2009
 * @since Dec 17, 2009
 */
public class DataGridFactory {
    private static DataGridFactory instance;

    private DataGridFactory() { }

    public static DataGridFactory getInstance() {
        if(instance == null) instance = new DataGridFactory();
        return instance;
    }

    private GridColumn[] generateGridColumns(Object bean) {
        ArrayList<GridColumn> gcList = new ArrayList<GridColumn>();
        if(bean.getClass().getAnnotation(SWTEntity.class) == null)
            throw new BeanAnnotationException("Bean(s) must be annotated with the SWTEntity annotation to have a dynamic GUI created for it.");

        for(Field field: bean.getClass().getDeclaredFields()) {
            Annotation a = field.getAnnotation(SWTWidget.class);
            if(a != null) {
                field.setAccessible(true);
                GridColumn column = new GridColumn(((SWTWidget)a).labelText(), field, 50);
                gcList.add(column);
            }
        }

        return gcList.toArray(new GridColumn[gcList.size()]);
    }

    public EditableDataGrid getEditableDataGrid(List beans, final GridColumn[] columnDefs, final Composite parent, final int styles) {
        EditableDataGrid grid = new AbstractEditableDataGrid(parent, styles){
            protected DataView createDataView(Object bean) {
                return DataViewFactory.getInstance().getDataView(bean, parent, styles, new BaseLayout(){
                    public void layout(SortedSet<DynamicDataView.DynamicWidget> widgets) { return; }
                });
            }
            protected GridColumn[] createColumnHeaders() {
                if( (columnDefs==null || columnDefs.length==0) && dataBeans!=null && dataBeans.size()>0)
                    return generateGridColumns(dataBeans.get(0));
                else
                    return columnDefs;
            }
        };
        for(Object bean: beans)
            grid.addDataBean(bean);
        grid.redraw();
        return grid;
    }
}
