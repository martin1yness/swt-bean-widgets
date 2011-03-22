package com.magnetstreet.swt.beanwidget.list;

import com.magnetstreet.swt.beanwidget.list.exception.IncompatibleSpecificListObject;
import com.magnetstreet.swt.beanwidget.list.exception.RequestedObjectNotFoundInListException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import java.lang.reflect.Method;
import java.util.*;

/**
 * MultiSelectList
 *
 * A List widget customly designed to work with Hibernate annotated
 * objects.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Nov 12, 2009
 * @since Nov 12, 2009
 */
public abstract class AbstractSpecificList<T> extends List {
    protected Map<String, T> objectMap = new HashMap<String, T>();

    public AbstractSpecificList(Composite composite, int i) {
        super(composite, i);
    }

    @Override protected void checkSubclass() { return; }
    /**
	 * Defines the algorithm for getting the object's String represetation that can
	 * be inserted as a selectable item in this List widget.
	 * @param obj The object to generate display string for
	 * @return The display string used to identify the object in the list
	 */
	public abstract String getObjectsListValue(T obj);

    @Override
    @Deprecated
    public void add(String s) {
        throw new RuntimeException("Incompatible method, please use: add(T obj).");
    }
    @Override
    @Deprecated
    public void add(String s, int i) {
        throw new RuntimeException("Incompatible method, please use: add(T obj, int i).");
    }
    @Override public void remove(int[] ints) {
        for(int i: ints)
            objectMap.remove(getItem(i));
        super.remove(ints);
    }
    @Override public void remove(int i) {
        objectMap.remove(getItem(i));
        super.remove(i);
    }
    @Override public void remove(int i, int i1) {
        for(int j=i; j<i1; j++) {
            objectMap.remove(getItem(j));
            super.remove(j);
        }
    }
    @Override public void remove(String s) {
        objectMap.remove(s);
        super.remove(s);
    }
    @Override public void removeAll() {
        objectMap = new HashMap<String, T>();
        super.removeAll();
    }
    @Deprecated
    @Override public void setItem(int i, String s) {
        throw new RuntimeException("Incompatible method, and not implemented.");
    }
    @Deprecated
    @Override public void setItems(String[] strings) {
        throw new RuntimeException("Incompatible method, please use: setItems(T[] objs)");
    }
    /**
	 * Defines the parent objects that correspond to the values in the list box.
	 * @param obj
	 */
	public void add(T obj) {
        String v = getObjectsListValue(obj);
		objectMap.put(v, obj);
        super.add(v);
	}
    /**
	 * Defines the parent objects that correspond to the values in the list box.
	 * @param obj
     * @param i The index location to insert
	 */
	public void add(T obj, int i) {
        String v = getObjectsListValue(obj);
		objectMap.put(v, obj);
        super.add(v, i);
	}
    /**
     * Adds a collection of objects to the list.
     * @param objs
     */
    public void setItems(Collection<T> objs) {
        removeAll();
        for(T obj: objs) add(obj);
    }

    /**
	 * Defines the comparison to make with the object type T that qualifies a
	 * match in the list.
	 * @param object The T object to find a match in list against.
	 */
	public void select(T object) throws RequestedObjectNotFoundInListException {
        if(object == null)
            throw new RequestedObjectNotFoundInListException("Cannot search for a null object.");
        String text = getObjectsListValue(object);
        if(text == null)
            throw new RequestedObjectNotFoundInListException("Unable to determine key for object: " + object);
        if(indexOf(text)==-1)
            throw new RequestedObjectNotFoundInListException("Unable to find object to select using key: " + text);
        select(indexOf(text));
	}

    /**
	 * Defines the comparison algorithm to make to get the objects that are
	 * currently corresponding the selected items in the list.
	 * @return currently corresponding object that maps to the selected item(s) in the list,
     *         linked hash set to ensure same order as they where in list.
	 */
	public Set<T> getSelectedObjects() {
        Set<T> rset = new LinkedHashSet<T>();
        for (int i: getSelectionIndices())
            rset.add(objectMap.get(getItem(i)));
		return rset;
	}

    /**
	 * Selects an item from the combo based on a default getId() getter pattern, use
	 * overloaded method to specify getter name for id comparison.
	 * @param id
	 */
	public void selectByObjectId(Integer id) throws RequestedObjectNotFoundInListException { selectByObjectId("getId", id); }

	public void selectByObjectId(String getterName, Integer id) throws RequestedObjectNotFoundInListException {
		try {
			Method meth = null;
			for(T obj: objectMap.values()) {
				if(meth == null)
					meth = obj.getClass().getDeclaredMethod(getterName, null);
				Integer objId = (Integer)meth.invoke(obj, null);
				if(objId.compareTo(id) == 0)
					select(obj);
			}
		} catch (Throwable t) {
			throw new IncompatibleSpecificListObject(t);
		}
        throw new RequestedObjectNotFoundInListException("Object for id '"+id+"' was not found using getter '"+getterName+"()'.");
	}
}
