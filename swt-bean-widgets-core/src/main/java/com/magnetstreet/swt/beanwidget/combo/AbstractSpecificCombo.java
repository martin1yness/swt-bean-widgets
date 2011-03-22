package com.magnetstreet.swt.beanwidget.combo;

import com.magnetstreet.swt.beanwidget.combo.exception.IncompatibleSpecificComboObject;
import org.eclipse.swt.widgets.Composite;

import java.lang.reflect.Method;
import java.util.SortedSet;

/**
 * Abstract Specific Combo
 * 
 * Provides interface and partial implementation for all object specific 
 * quick search combo boxes. 
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0
 * @since 7/13/2009
 */
public abstract class AbstractSpecificCombo<T> extends QuickSearchCombo {
	protected SortedSet<T> comboObjects;
	/**
	 * @param arg0
	 * @param arg1
	 */
	public AbstractSpecificCombo(Composite arg0, int arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * Defines the parent objects that correspond to the values in the combo box.
	 * @param objs
	 */
	public void setComboObjects(SortedSet<T> objs) {
		this.comboObjects = objs;
		for(T obj: objs)
			add(getObjectsComboValue(obj));
	}
	
	/**
	 * Defines the algorithm for getting the object's String represetation that can
	 * be inserted as a selectable item in this combo box.
	 * @param obj The object to generate display string for
	 * @return The display string used to identify the object in the combo
	 */
	public abstract String getObjectsComboValue(T obj);
	
	/**
	 * Defines the comparison to make with the object type T that qualifies a
	 * match in the list.
	 * @param object The T object to find a match in combo against.
	 */
	public void select(T object) {
        if(object == null) return;
        String text = getObjectsComboValue(object);
        if(text == null) return;
        select(indexOf(text));
	}
	
	/**
	 * Defines the comparison algorithm to make to get the object that is 
	 * currently corresponding the selected item in the combo.
	 * @return currently corresponding object that maps to the selected item in the combo.
	 */
	public T getSelectedObject() {
        if(getSelectionIndex() < 0 || getSelectionIndex() > getItemCount()) return null;
		for(T obj: comboObjects) {
			String text = getObjectsComboValue(obj);
			if(text.equals(getItem(getSelectionIndex()))) return obj;
		}
		return null;
	}
	
	/**
	 * Selects an item from the combo based on a default getId() getter pattern, use
	 * overloaded method to specify getter name for id comparison.
	 * @param id
	 */
	public void selectByObjectId(Integer id) { selectByObjectId("getId", id); }
	
	public void selectByObjectId(String getterName, Integer id) {
		try {
			Method meth = null;
			for(T obj: comboObjects) {
				if(meth == null) 
					meth = obj.getClass().getDeclaredMethod(getterName, null);
				Integer objId = (Integer)meth.invoke(obj, null);
				if(objId.compareTo(id) == 0) 
					select(obj);
			}
		} catch (Throwable t) {
			throw new IncompatibleSpecificComboObject(t);
		}
	}

    public void selectByArbitrary(String getterName, Comparable match) {
        if(match == null) return;
		try {
			Method meth = null;
			for(T obj: comboObjects) {
				if(meth == null)
					meth = obj.getClass().getDeclaredMethod(getterName, null);
				Comparable objId = (Comparable)meth.invoke(obj, null);
				if(objId!=null && objId.compareTo(match) == 0)
					select(obj);
			}
		} catch (Throwable t) {
			throw new IncompatibleSpecificComboObject(t);
		}
	}
}
