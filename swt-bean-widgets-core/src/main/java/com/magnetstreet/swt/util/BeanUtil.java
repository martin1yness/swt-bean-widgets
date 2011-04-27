package com.magnetstreet.swt.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BeanUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 4/27/11
 */
public class BeanUtil {
    private static Logger log = Logger.getLogger(BeanUtil.class.getSimpleName());

    /**
     * Converts a Getter method name to a property name based on simplified bean standards. (i.e. Always prefixed
     * with a 'get' followed by the property name in camel case 'PropertyName' ex: getPropertyName)
     * @param getter
     * @return
     */
    public static String getPropertyNameFromGetter(Method getter) {
        return Character.toLowerCase(getter.getName().charAt(3)) + getter.getName().substring(4);
    }

    public static String buildBeanBagStringByProperty(Collection c, String fieldNameChain, String delimiter) {
        String r = "";
        try {
            Iterator i = c.iterator();
            Object o = i.next();
            r += getFieldChainValueWithGetters(o, fieldNameChain);
            while(i.hasNext()) {
                r += delimiter + getFieldChainValueWithGetters(i.next(), fieldNameChain);
            }
        } catch(Throwable t) {
            log.log(Level.SEVERE , "Unable to build bean bag.", t);
            r += "ERROR: " + t.getMessage();
        }
        return r;
    }

    public static Object getFieldChainValueWithGetters(Object baseObject, String fieldNameChain) {
        String[] chain = fieldNameChain.split("[.]");
        if(chain.length == 1)
            return getFieldValueWithGetter(baseObject, chain[0]);

        return getFieldChainValueWithGetters(getFieldValueWithGetter(baseObject, chain[0]), fieldNameChain.substring(chain[0].length()+1));
    }

    public static Object getFieldValueWithGetter(Object obj, Field field) {
        return getFieldValueWithGetter(obj, field.getName());
    }

    public static Object getFieldValueWithGetter(Object obj, String fieldName) {
        try {
            Method m = getGetterMethodForField(obj, fieldName);
            m.setAccessible(true);
            return m.invoke(obj);
        } catch (NoSuchMethodException e) {
            log.log(Level.INFO, "No getter for field: "+ fieldName +", using field it's self", e);
            try {
                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch(Throwable iae) {
                log.log(Level.SEVERE,"Unable to access field even after enabling access.", iae);
            }
        } catch (InvocationTargetException e) {
            log.log(Level.SEVERE,"Unable to invoke getter on supplied object: " + obj +", field: " + fieldName, e);
        } catch (IllegalAccessException e) {
            log.log(Level.SEVERE,"Access not allowed on explicitly marked accessible field through reflection.", e);
        }
        return null;
    }

    public static Method getGetterMethodForField(Object obj, Field field) throws NoSuchMethodException {
        return obj.getClass().getDeclaredMethod(getGetterMethodNameForField(obj, field));
    }

    public static Method getGetterMethodForField(Object obj, String fieldName) throws NoSuchMethodException {
        return obj.getClass().getDeclaredMethod(getGetterMethodNameForField(obj, fieldName));
    }

    public static String getGetterMethodNameForField(Object obj, Field field) {
        return getGetterMethodNameForField(obj, field.getName());
    }

    public static String getGetterMethodNameForField(Object obj, String fieldName) {
        return "get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
    }
}
