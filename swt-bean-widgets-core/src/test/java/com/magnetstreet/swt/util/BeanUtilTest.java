package com.magnetstreet.swt.util;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * BeanUtilTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/12/11
 */
public class BeanUtilTest {
    public static class TestBeanA {
        List<TestBeanB> bBeans = new ArrayList<TestBeanB>();
        public List<TestBeanB> getbBeans() {
            return bBeans;
        }
    }

    public static class TestBeanB {
        String someValue = "someValue";
        public String getSomeValue() {
            return someValue;
        }
    }

    @Test public void testGetGetterForChainValue() throws NoSuchMethodException, NoSuchFieldException {
        Method someValueGetter = BeanUtil.getGetterMethodForChainValue(TestBeanA.class, "bBeans.someValue");
        assertThat(someValueGetter.getName(), is("getSomeValue"));
    }
}
