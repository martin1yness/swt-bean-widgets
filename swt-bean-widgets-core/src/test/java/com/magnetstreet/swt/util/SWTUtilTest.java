package com.magnetstreet.swt.util;

import org.eclipse.swt.SWT;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * SWTUtilTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/14/11
 */
public class SWTUtilTest {
    @Test public void testHasStyle() throws Exception {
        int allStyles = SWT.CHECK|SWT.BORDER|SWT.FULL_SELECTION;
        assertThat(SWTUtil.hasStyle(allStyles, SWT.CHECK), is(true));
        assertThat(SWTUtil.hasStyle(allStyles, SWT.BORDER), is(true));
        assertThat(SWTUtil.hasStyle(allStyles, SWT.FULL_SELECTION), is(true));
        assertThat(SWTUtil.hasStyle(allStyles, SWT.MULTI), is(false));
        assertThat(SWTUtil.hasStyle(allStyles, SWT.ALL), is(false));
        assertThat(SWTUtil.hasStyle(allStyles, SWT.BAR), is(false));
    }
}
