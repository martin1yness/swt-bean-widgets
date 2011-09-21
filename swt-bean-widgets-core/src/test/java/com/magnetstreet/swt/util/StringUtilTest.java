package com.magnetstreet.swt.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * StringUtilTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 8/24/11
 */
public class StringUtilTest {
    @Test public void testCamelCaseToTitle() throws Exception {
        assertThat(StringUtil.camelCaseToTitle("someVar"), is("Some Var"));
        assertThat(StringUtil.camelCaseToTitle("SomeVar"), is("Some Var"));
        assertThat(StringUtil.camelCaseToTitle("someVarThree"), is("Some Var Three"));
        assertThat(StringUtil.camelCaseToTitle("SomeVarThree"), is("Some Var Three"));
        assertThat(StringUtil.camelCaseToTitle("compound.someVar"), is("Compound Some Var"));
        assertThat(StringUtil.camelCaseToTitle("compoundVarSome.someVarSome"), is("Compound Var Some Some Var Some"));
    }
}
