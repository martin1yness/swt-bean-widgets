package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.contextmenu;

import com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.contextmenu.ContextMenuAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.Viewer;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ContextMenuManager
 *
 * Provides a context model object to the menu so that on invocation it can reference it's context
 * to determine what to do.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/14/11
 */
public class ContextMenuManager extends MenuManager {
    protected final Viewer viewer;
    protected final Collection selectedContextModel = new ConcurrentLinkedQueue();

    public ContextMenuManager(Viewer viewer) {
        this.viewer = viewer;
    }

    public Viewer getViewer() {
        return viewer;
    }
    public Collection getSelectedContextModel() {
        return selectedContextModel;
    }
    public void setSelectedContextModel(Collection selectedContextModel) {
        this.selectedContextModel.clear();
        this.selectedContextModel.addAll(selectedContextModel);
    }

    @Override public void add(IAction action) {
        if (action instanceof ContextMenuAction) {
            ((ContextMenuAction)action).setSelectedContextModel(getSelectedContextModel());
            ((ContextMenuAction)action).setViewer(viewer);
        }

        super.add(action);
    }
}
