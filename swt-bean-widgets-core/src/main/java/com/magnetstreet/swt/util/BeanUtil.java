package com.magnetstreet.swt.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

    public static void setFieldChainValueWithSetter(Object baseObject, String fieldNameChain, Object value) {
        int last = fieldNameChain.lastIndexOf(".");
        if(last != -1)
            baseObject = getFieldChainValueWithGetters(fieldNameChain.subSequence(0, last), fieldNameChain);
        try {
            setFieldValueWithSetter(baseObject, fieldNameChain.substring((last==-1)?0:last), value);
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Unable to set value.", e);
        }
    }

    public static void setFieldValueWithSetter(Object obj, String fieldName, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getSetterMethodForField(obj, getSetterMethodNameForField(fieldName), value.getClass());
        method.invoke(obj, value);
    }

    public static Method getSetterMethodForField(Object obj, String fieldName, Class type) throws NoSuchMethodException {
        return obj.getClass().getDeclaredMethod(fieldName, type);
    }

    public static Object getFieldChainValueWithGetters(Object baseObject, String fieldNameChain) {
        String[] chain = fieldNameChain.split("[.]");
        if(chain.length == 1)
            return getFieldValueWithGetter(baseObject, chain[0]);

        return getFieldChainValueWithGetters(getFieldValueWithGetter(baseObject, chain[0]), fieldNameChain.substring(chain[0].length() + 1));
    }

    public static Object getFieldValueWithGetter(Object obj, Field field) {
        try {
            Method m = getGetterMethodForField(obj, field);
            m.setAccessible(true);
            return m.invoke(obj);
        } catch (NoSuchMethodException e) {
            log.log(Level.INFO, "No getter for field: "+ field.getName() +", using field it's self", e);
            try {
                field.setAccessible(true);
                return field.get(obj);
            } catch(Throwable iae) {
                log.log(Level.SEVERE,"Unable to access field even after enabling access.", iae);
            }
        } catch (InvocationTargetException e) {
            log.log(Level.SEVERE,"Unable to invoke getter on supplied object: " + obj +", field: " + field.getName(), e);
        } catch (IllegalAccessException e) {
            log.log(Level.SEVERE,"Access not allowed on explicitly marked accessible field through reflection.", e);
        }
        return null;
    }

    public static Object getFieldValueWithGetter(Object obj, String fieldName) {
        try {
            return getFieldValueWithGetter(obj, obj.getClass().getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            log.log(Level.SEVERE, "Unable to find field on object", e);
            return null;
        }
    }

    public static Method getGetterMethodForField(Object obj, Field field) throws NoSuchMethodException {
        try {
            return obj.getClass().getDeclaredMethod(getGetterMethodNameForField(obj, field));
        } catch(NoSuchMethodException nsme) {
            log.log(Level.WARNING, "Can't find method.", nsme);
            throw nsme;
        }
    }

    public static Method getGetterMethodForField(Object obj, String fieldName) throws NoSuchMethodException, NoSuchFieldException {
        return obj.getClass().getDeclaredMethod(getGetterMethodNameForField(fieldName, obj.getClass().getDeclaredField(fieldName)));
    }

    public static String getGetterMethodNameForField(Object obj, Field field) {
        if(field.getType() == Boolean.class || field.getType() == boolean.class)
            return "is"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
        return "get"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
    }

    public static String getSetterMethodNameForField(String fieldName) {
        return "set"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
    }



    public static List<Method> getMethodsNamed(String methodName, Object obj) {
        Method[] methods = obj.getClass().getDeclaredMethods();
        List<Method> methodList = new LinkedList<Method>();
        for(Method method: methods) {
            if(method.getName().equals(methodName))
                methodList.add(method);
        }
        return methodList;
    }
}
