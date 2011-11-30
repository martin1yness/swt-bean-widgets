package com.magnetstreet.swt.beanwidget.datagrid2.filter;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * SimpleInclusiveExclusiveKeywordColumnFilterTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 2011-11-30
 */
public class SimpleInclusiveExclusiveKeywordColumnFilterTest {
    private String filterText = "";
    private SimpleInclusiveExclusiveKeywordColumnFilter filterDefault;

    @Before public void setupTestFilters() {
        filterDefault = new SimpleInclusiveExclusiveKeywordColumnFilter() {
            @Override protected String getFilterText() { return filterText; }
        };
    }

    @Test public void testCheckModelProperty_withCaseInsenstivityEnabled_bothUpperAndLowerVerisionsMatch() throws Exception {
        filterText = "loweronly";
        assertTrue(filterDefault.checkModelProperty("loweronly"));
        assertTrue(filterDefault.checkModelProperty("lowerOnly"));
        assertTrue(filterDefault.checkModelProperty("LOWERONLY"));
        assertTrue(filterDefault.checkModelProperty("lOWERONLy"));

        filterText = "LowerAndUpper";
        assertTrue(filterDefault.checkModelProperty("LowerAndUpper"));
        assertTrue(filterDefault.checkModelProperty("lowerAndUpper"));
        assertTrue(filterDefault.checkModelProperty("lowerandupper"));
        assertTrue(filterDefault.checkModelProperty("lowerandupper".toUpperCase()));

        filterText = "UPPERONLY";
        assertTrue(filterDefault.checkModelProperty("UPPERONLY"));
        assertTrue(filterDefault.checkModelProperty("upperonly"));
        assertTrue(filterDefault.checkModelProperty("upperOnly"));
        assertTrue(filterDefault.checkModelProperty("UpperOnly"));
    }
}
