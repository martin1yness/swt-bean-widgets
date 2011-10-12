package com.magnetstreet.swt.beanwidget.datatreegrid.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import java.text.Collator;

/**
 * DataTreeGridSorter
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/12/11
 */
public abstract class DataTreeGridSorter extends ViewerSorter {
    protected String identifier = "";
    protected boolean direction = false; // False DESC, True ASC

    protected DataTreeGridSorter() { }
    protected DataTreeGridSorter(Collator collator) {
        super(collator);
    }

    public void setIdentifier(String identifier) {
        if(this.identifier.equals(identifier))
            direction = !direction;
        else {
            this.identifier = identifier;
            direction = false;
        }
    }

    @Override public abstract int compare(Viewer viewer, Object e1, Object e2);
}
