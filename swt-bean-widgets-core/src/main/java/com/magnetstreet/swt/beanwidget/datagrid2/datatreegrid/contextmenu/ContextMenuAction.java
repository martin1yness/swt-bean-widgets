package com.magnetstreet.swt.beanwidget.datagrid2.datatreegrid.contextmenu;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.omg.CORBA.Context;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;

/**
 * ContextMenuAction
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 10/14/11
 */
public class ContextMenuAction extends Action implements Cloneable {
    protected Collection selectedContextModel;
    protected Viewer viewer;
    protected HashSet<Class> applicableTo = new HashSet<Class>();

    public ContextMenuAction() { }

    public ContextMenuAction(HashSet<Class> applicableTo) {
        this.applicableTo = applicableTo;
    }

    public ContextMenuAction(String text, HashSet<Class> applicableTo) {
        super(text);
        this.applicableTo = applicableTo;
    }

    public ContextMenuAction(String text, ImageDescriptor image, HashSet<Class> applicableTo) {
        super(text, image);
        this.applicableTo = applicableTo;
    }

    public ContextMenuAction(String text, int style, HashSet<Class> applicableTo) {
        super(text, style);
        this.applicableTo = applicableTo;
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
    public HashSet<Class> getApplicableTo() {
        return applicableTo;
    }
    public void setApplicableTo(HashSet<Class> applicableTo) {
        this.applicableTo = applicableTo;
    }

    public ContextMenuAction clone(){
        try {
            return (ContextMenuAction)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
