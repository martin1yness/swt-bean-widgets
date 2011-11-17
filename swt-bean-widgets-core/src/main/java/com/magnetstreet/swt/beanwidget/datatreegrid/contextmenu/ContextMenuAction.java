package com.magnetstreet.swt.beanwidget.datatreegrid.contextmenu;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;

import java.util.Collection;

/**
 * ContextMenuAction
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/14/11
 */
public class ContextMenuAction extends Action {
    protected Collection selectedContextModel;
    protected Viewer viewer;

    public ContextMenuAction() { }

    public ContextMenuAction(String text) {
        super(text);
    }

    public ContextMenuAction(String text, ImageDescriptor image) {
        super(text, image);
    }

    public ContextMenuAction(String text, int style) {
        super(text, style);
    }

    public Collection getSelectedContextModel() {
        return selectedContextModel;
    }
    public void setSelectedContextModel(Collection selectedContextModel) {
        this.selectedContextModel = selectedContextModel;
    }
    public Viewer getViewer() {
        return viewer;
    }
    public void setViewer(Viewer viewer) {
        this.viewer = viewer;
    }
}
