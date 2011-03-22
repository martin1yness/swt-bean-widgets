package com.magnetstreet.swt.beanwidget.dataview;

import com.magnetstreet.swt.WidgetPropertyMappingDefinition;
import com.magnetstreet.swt.annotation.SWTEntity;
import com.magnetstreet.swt.annotation.SWTWidget;
import com.magnetstreet.swt.exception.BeanAnnotationException;
import com.magnetstreet.swt.exception.ViewDataBeanValidationException;
import com.magnetstreet.swt.beanwidget.dataview.coverter.BigDecimalGenericSpinnerWidgetPropertyMappingDefinition;
import com.magnetstreet.swt.beanwidget.dataview.coverter.CalendarDateTimePopoutWidgetPropertyMappingDefinition;
import com.magnetstreet.swt.beanwidget.dataview.coverter.CollectionDataGridWidgetPropertyMappingDefinition;
import com.magnetstreet.swt.beanwidget.dataview.coverter.DefaultTextWidgetPropertyMappingDefinition;
import com.magnetstreet.swt.beanwidget.dataview.coverter.IntegerTextWidgetPropertyMappingDefinition;
import com.magnetstreet.swt.beanwidget.dataview.coverter.StringTextWidgetPropertyMappingDefinition;
import com.magnetstreet.swt.beanwidget.dataview.layout.BaseLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * DataViewBase
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 14, 2009
 * @since Dec 14, 2009
 */
public class DynamicDataView extends AbstractDataView {
    private Log log = LogFactory.getLog(DynamicDataView.class);
    
    protected BaseLayout layout;
    /**
     * {@inheritDoc}
     */
    public Control findWidget(Field beanProperty) {
        beanProperty.setAccessible(true);
        for(DynamicWidget widget: widgetSet) {
            if(widget.beanProperty.equals(beanProperty)) return widget.widget;
        }
        log.warn("Could not find widget for bean property. DataView could be uninitialized or the bean property could have never been annotated. "+beanProperty.getName());
        return null;
    }

    /**
     * Stores the dynamically created widgets
     */
    public class DynamicWidget implements Comparable {
        public CLabel label;
        public Control widget;
        public Field beanProperty;
        public WidgetPropertyMappingDefinition widgetPropertyMappingDefinition;
        public int compareTo(Object o) {
            return 1; // FIFO
        }
    }

    /**
     * Mapping bean properties to their corresponding control
     */
    protected Map<Field, DynamicWidget> memberControlMap = new HashMap<Field, DynamicWidget>();
    /**
     * A sorted(by layout display) set of dynamic widgets
     */
    protected SortedSet<DynamicWidget> widgetSet = new TreeSet<DynamicWidget>();

    /*
     * Maintains all of the default converters, can be modified by implementing classes
     * but it will change the converter for entire application.
     */
    protected static Map<Type, WidgetPropertyMappingDefinition<?, ?>> defaultConverterMap = new HashMap<Type, WidgetPropertyMappingDefinition<?, ?>>();
    static {
        defaultConverterMap.put(BigDecimal.class, new BigDecimalGenericSpinnerWidgetPropertyMappingDefinition());
        defaultConverterMap.put(Calendar.class, new CalendarDateTimePopoutWidgetPropertyMappingDefinition());
        defaultConverterMap.put(String.class, new StringTextWidgetPropertyMappingDefinition());
        defaultConverterMap.put(Integer.class, new IntegerTextWidgetPropertyMappingDefinition());
        defaultConverterMap.put(Object.class, new DefaultTextWidgetPropertyMappingDefinition());
        defaultConverterMap.put(SWTEntity.Type.DATA_GRID.getClass(), new CollectionDataGridWidgetPropertyMappingDefinition());
    }


    protected DynamicDataView(Composite arg0, int arg1) {
        super(arg0, arg1);        
    }
    /**
     * Defines the layout engine to be used when placing widgets.
     * @param layout The layout engine extending BaseLayout
     */
    public void setLayout(BaseLayout layout) {
        this.layout = layout;
        if(widgetSet.size() > 0) layout.layout(widgetSet);
    }
    /**
     * {@inheritDoc}
     */
    @Override protected void preInitGUI() {
        setLayout(new FormLayout());
    }
    /**
     * {@inheritDoc}
     */
    @Override protected void initGUI() {

    }
    /**
     * {@inheritDoc}
     */
    @Override protected void postInitGUI() {
    }

    /**
	 * {@inheritDoc}
	 */
    @SuppressWarnings("unchecked")
	protected void updateViewDataObjectFromWidgets() {
        errorMap.clear();
        for(DynamicWidget dw: widgetSet) {
            try {
                dw.beanProperty.setAccessible(true);
                dw.beanProperty.set(viewDataObject, dw.widgetPropertyMappingDefinition.convertWidgetToProperty(dw.widget));
            } catch(ViewDataBeanValidationException vdbve) {
                errorMap.put(dw.widget, vdbve.getMessage());
                log.info("Validation exception: " + vdbve.getMessage(), vdbve);
            } catch (Exception e) {
                log.error("Unable to update bean property("+dw.label.getText()+"), bean will be out of sync with data view widgets.", e);
            }
        }
    }

    /**
	 * {@inheritDoc}
	 */
    @SuppressWarnings("unchecked")
	protected void updateWidgetsFromViewDataObject() {
        if(widgetSet.size() == 0) buildWidgets();
        for(DynamicWidget dw: widgetSet) {
            try {
                dw.beanProperty.setAccessible(true);
                Object propertyValue = dw.beanProperty.get(viewDataObject);
                if(propertyValue!=null) dw.widgetPropertyMappingDefinition.convertPropertyToWidget(propertyValue, dw.widget);
            } catch (Exception e) {
                log.error("Unable to access bean property("+dw.label.getText()+") to update widgets, beans will be out of sync with data views.", e);
            }
        }
    }

    /**
     * Generates the dynamic widget set, creates a dynamic widget for each property in
     * the data view's bean that is marked with the @SWTWidget annotation.
     */
    protected void buildWidgets() {
        Button enableEditingButton = new Button(this, SWT.CHECK);
        enableEditingButton.setText("Enable Editing");
        FormData enableEditingWidgetLData = new FormData(130, 20);
        enableEditingWidgetLData.top = new FormAttachment(0,100,0);
        enableEditingWidgetLData.right = new FormAttachment(100,100,0);
        enableEditingButton.setLayoutData(enableEditingWidgetLData);

        if(viewDataObject.getClass().getAnnotation(SWTEntity.class) == null)
            throw new BeanAnnotationException("Beans must be annotated with the SWTEntity annotation to have a dynamic GUI created for it.");

        for(Field field: viewDataObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Annotation a = field.getAnnotation(SWTWidget.class);
            if(a != null) {
                DynamicWidget dw = new DynamicWidget();
                dw.label = getDefaultCLabel(((SWTWidget)a).labelText());
                dw.beanProperty = field;

                if(field.getType().isAssignableFrom(Collection.class))
                    buildDynamicWidgetCollection(dw, (SWTWidget)a, field);
                else
                    buildDynamicWidget(dw, (SWTWidget)a, field);

                try {
                    memberControlMap.put(field, dw);
                    widgetSet.add(dw);
                } catch(ClassCastException cce) {
                    log.error(cce);
                }
            }
        }

        registerEnableEditingWidget(enableEditingButton);
    }

    /**
     * Builds an SWT widget out of the SWTWidget annotation and property attached to
     * the bean this view was instantiated with.
     * @param dw The widget object being created
     * @param annotation The annotation onthe property
     * @param property The bean property widget is for
     */
    protected void buildDynamicWidget(DynamicWidget dw, SWTWidget annotation, Field property) {
        if( defaultConverterMap.containsKey(property.getType()) ) {
            dw.widgetPropertyMappingDefinition = defaultConverterMap.get(property.getType());
            dw.widget = (Control)defaultConverterMap.get(property.getType()).createWidget(this);
        } else {
            dw.widgetPropertyMappingDefinition = defaultConverterMap.get(Object.class);
            dw.widget = (Control)defaultConverterMap.get(Object.class).createWidget(this);
        }
    }

    /**
     * Handles building widgets for a collection of SWTEntity
     * @param dynamicWidget The widget object being created
     * @param annotation The annotation onthe property
     * @param property The bean property widget is for
     */
    protected void buildDynamicWidgetCollection(DynamicWidget dynamicWidget, SWTWidget annotation, Field property) {
        SWTEntity propAnno = property.getType().getAnnotation(SWTEntity.class);
        if(propAnno == null) {
            log.error("Unable to build a SWT widget collection from non SWTEntity annotated objects. Bean property: " +property.getName());
            buildDynamicWidget(dynamicWidget, annotation, property);
            return;
        }
        SWTEntity.Type collectionType = propAnno.defaultCollectionType();

        dynamicWidget.widgetPropertyMappingDefinition = defaultConverterMap.get(property.getType());
        dynamicWidget.widget = (Control)defaultConverterMap.get(property.getType()).createWidget(this);
    }
}