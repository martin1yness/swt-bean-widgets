package com.magnetstreet.swt.beanwidget.datagrid2.sorter.comparator;

import java.math.BigDecimal;

/**
 * BigDecimalPropertyComparator
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/13/11
 */
public class BigDecimalPropertyComparator<T> extends AbstractPropertyComparator<T, BigDecimal> {
    public BigDecimalPropertyComparator(String propertyChain) {
        super(propertyChain);
    }
}
