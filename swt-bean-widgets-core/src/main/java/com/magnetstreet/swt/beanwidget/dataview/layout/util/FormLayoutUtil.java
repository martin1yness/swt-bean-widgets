package com.magnetstreet.swt.beanwidget.dataview.layout.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.LinkedList;

/**
 * FormLayoutUtil
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 7/26/11
 */
public class FormLayoutUtil {
    private Composite parent;
    private LinkedList<CLabel> initializedLabels = new LinkedList<CLabel>();
    public static final FormData FD_FILL = new FormData();
    public static final FormAttachment FA_FILL_TOP = new FormAttachment(0,100,2);
    public static final FormAttachment FA_FILL_LEFT = new FormAttachment(0,100,2);
    public static final FormAttachment FA_FILL_RIGHT = new FormAttachment(100,100,-2);
    public static final FormAttachment FA_FILL_BOTTOM = new FormAttachment(100,100,-2);

    public FormLayoutUtil(Composite parent) {
        this.parent = parent;
        FD_FILL.top = new FormAttachment(0, 2);
        FD_FILL.bottom = new FormAttachment(1000, 1000, -2);
        FD_FILL.left = new FormAttachment(0, 1000, 2);
        FD_FILL.right = new FormAttachment(1000, 1000, -2);
    }

    public static FormData createFormData(FormAttachment top, FormAttachment bottom, FormAttachment left, FormAttachment right) {
        FormData fd = new FormData();
        fd.top = top;
        fd.bottom = bottom;
        fd.left = left;
        fd.right = right;
        return fd;
    }

    private CLabel initLabelLayout(CLabel label) {
        FormData fdLabel = new FormData();
        if(initializedLabels.size()==0)
            fdLabel.top = new FormAttachment(0,100,2);
        else
            fdLabel.top = new FormAttachment(initializedLabels.getLast(), 2);
        fdLabel.left = new FormAttachment(0,100,2);
        fdLabel.right = new FormAttachment(33,100,0);
        label.setLayoutData(fdLabel);
        return label;
    }

    private Control initControlLayout(Control control, Control left, int right) {
        FormData fdControl = new FormData();
        if(initializedLabels.size()==0)
            fdControl.top = new FormAttachment(0, 100, 2);
        else
            fdControl.top = new FormAttachment(initializedLabels.getLast(), 2);
        fdControl.left = new FormAttachment(left,2);
        fdControl.right = new FormAttachment(right,100,-2);
        control.setLayoutData(fdControl);

        return control;
    }

    public void initComponentLayout(CLabel label, int[] widthPerc, Control[] controls) {
        if(initializedLabels==null)
            initializedLabels = new LinkedList<CLabel>();

        Control left = initLabelLayout(label);
        int right = 33;
        for(int i=0; i<controls.length; i++) {
            right += widthPerc[i];
            left = initControlLayout(controls[i], left, right);
        }

        initializedLabels.addLast(label);
    }

    public void initComponentLayout(CLabel label, Control...controls) {
        if(initializedLabels==null)
            initializedLabels = new LinkedList<CLabel>();

        Control left = initLabelLayout(label);
        int perc = Math.round(66 / controls.length);
        for(int i=1; i<=controls.length; i++) {
            left = initControlLayout(controls[i-1], left, 33+(perc*i));
        }

        initializedLabels.addLast(label);
    }

    public CLabel createDefaultCLabel(String text) {
        CLabel label = new CLabel(parent, SWT.NONE);
        label.setBackgroundMode(SWT.INHERIT_FORCE);
        label.setAlignment(SWT.RIGHT);
        label.setText(text);
        return label;
    }
}
