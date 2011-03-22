package com.magnetstreet.swt.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EditableDataGridUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Dec 17, 2009
 * @since Dec 17, 2009
 */
public class EditableDataGridUtil {
    /**
     * Attempts to find any fields that conatain the given name in any part of their
     * own name.
     * @param obj The object to search
     * @param name The name to search
     * @param typeRestrict The a map of classes whether to force inclusion or force exclusion.
     *                     A class with a true value will force that the field is a child, a
     *                     class with a false will force that the field is NOT a child.
     * @return Null if non found, otherwise a list of fields that matched name
     * @since Oct 10, 2009
     * @deprecated Originally from internal library, but this function should no longer be needed as
     *             the widgets it finds are now staticly defined by the dynamic data view widget.
     */
    @Deprecated
    public static List<Field> findSimilarlyNamedField(Class obj, String name, Map<Class, Boolean> typeRestrict) {
        List<Field> fieldList = new ArrayList<Field>();
        for(Field f: obj.getDeclaredFields()) {
            boolean restricted = false;
            for(Class type: typeRestrict.keySet()) {
                if(typeRestrict.get(type) && !type.isAssignableFrom(f.getType())) restricted = true;
                if(!typeRestrict.get(type) && type.isAssignableFrom(f.getType())) restricted = true;
            }
            if(restricted) continue;
            if( f.getName().toLowerCase().contains(name.trim().toLowerCase()) )
                fieldList.add(f);
        }
        if(obj.getSuperclass()!=Object.class)
            fieldList.addAll(findSimilarlyNamedField(obj.getSuperclass(), name, typeRestrict));
        return fieldList;
    }
}
