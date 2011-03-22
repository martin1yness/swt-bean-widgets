package com.magnetstreet.swt.beanwidget.dataview;

import com.magnetstreet.swt.beanwidget.dataview.layout.BaseLayout;
import com.magnetstreet.swt.beanwidget.dataview.layout.ColumnLayout;
import org.eclipse.swt.widgets.Composite;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data View Factory
 *
 * Builds an instance of a DataView using SWTWidget annotations on a
 * java object.
 * @see com.magnetstreet.swt.annotation.SWTWidget
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 23, 2009
 * @since Nov 23, 2009
 */
public class DataViewFactory {
    private static DataViewFactory instance;
    private Map<Class, Class> staticDataViews = new ConcurrentHashMap<Class, Class>();
    private BaseLayout defaultLayout = new ColumnLayout();

    private DataViewFactory() {

    }

    public static DataViewFactory getInstance() {
        if(instance == null) instance = new DataViewFactory();
        return instance;
    }


    public void setDefaultLayout(BaseLayout layout) {
        this.defaultLayout = layout;
    }

    public static void registerStaticDataView(Class beanType, Class dataViewClass) {
        if(!StaticDataView.class.isAssignableFrom(dataViewClass))
            throw new RuntimeException("Data view class must extend StaticDataView");
        getInstance().staticDataViews.put(beanType, dataViewClass);
    }

    public DataView<?> getDataView(Object bean, Composite parentContainer, int styles) {
        return getDataView(bean, parentContainer, styles, defaultLayout);
    }
    /**
     * Builds a DataView out of the given annotated object
     * @param bean The object to build data view off of
     * @return a data view object representing the bean given
     */
    public DataView<?> getDataView(Object bean, Composite parentContainer, int styles, BaseLayout layout) {
        if(staticDataViews.containsKey(bean.getClass())) {
            try {
                Constructor c = staticDataViews.get(bean.getClass()).getConstructor(Composite.class, int.class);
                DataView view = (DataView)c.newInstance(parentContainer, styles);
                view.setViewDataObject(view);
                return view; 
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        DynamicDataView view = new DynamicDataView(parentContainer, styles);
        view.setViewDataObject(bean);
        view.setLayout(layout);
        view.layout();
        view.pack();
        return view;
    }
}