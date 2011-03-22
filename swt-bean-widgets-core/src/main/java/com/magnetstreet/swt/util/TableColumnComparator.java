package com.magnetstreet.swt.util;

import org.eclipse.swt.widgets.TableItem;

import java.util.Comparator;

/**
 * TableColumnComparator
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 3/22/11
 */
public interface TableColumnComparator extends Comparator<TableItem> {
    public void setReverseBit(boolean r);
    public int compare(TableItem a, TableItem b);
}
