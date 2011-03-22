package com.magnetstreet.swt.beanwidget;

/**
 * Composite Interface
 *
 * After thought interface for the composite widget, used to allow
 * interfaced grids and views to extend and have all methods available to
 * normal SWT widgets.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 6, 2010
 * @since Jan 6, 2010
 */
public interface Widget {
    public org.eclipse.swt.graphics.Point computeSize(int i, int i1, boolean b);
    public org.eclipse.swt.widgets.Layout getLayout();
    public boolean getLayoutDeferred();
    public boolean isLayoutDeferred();
    public void layout();
    public void layout(boolean b);
    public void layout(boolean b, boolean b1);
    public void layout(org.eclipse.swt.widgets.Control[] controls);
    public boolean setFocus();
    public void setLayout(org.eclipse.swt.widgets.Layout layout);
    public void setLayoutDeferred(boolean b);
    public org.eclipse.swt.graphics.Rectangle getClientArea();
    public org.eclipse.swt.widgets.ScrollBar getHorizontalBar();
    public org.eclipse.swt.widgets.ScrollBar getVerticalBar();
    public void addControlListener(org.eclipse.swt.events.ControlListener controlListener);    
    public void addDragDetectListener(org.eclipse.swt.events.DragDetectListener dragDetectListener);    
    public void addFocusListener(org.eclipse.swt.events.FocusListener focusListener);    
    public void addHelpListener(org.eclipse.swt.events.HelpListener helpListener);    
    public void addKeyListener(org.eclipse.swt.events.KeyListener keyListener);    
    public void addMenuDetectListener(org.eclipse.swt.events.MenuDetectListener menuDetectListener);    
    public void addMouseListener(org.eclipse.swt.events.MouseListener mouseListener);    
    public void addMouseTrackListener(org.eclipse.swt.events.MouseTrackListener mouseTrackListener);    
    public void addMouseMoveListener(org.eclipse.swt.events.MouseMoveListener mouseMoveListener);    
    public void addMouseWheelListener(org.eclipse.swt.events.MouseWheelListener mouseWheelListener);    
    public void addPaintListener(org.eclipse.swt.events.PaintListener paintListener);
    public void addTraverseListener(org.eclipse.swt.events.TraverseListener traverseListener);
    public boolean dragDetect(org.eclipse.swt.widgets.Event event);
    public boolean dragDetect(org.eclipse.swt.events.MouseEvent mouseEvent);
    public boolean forceFocus();
    public org.eclipse.swt.accessibility.Accessible getAccessible();
    public int getBorderWidth();
    public org.eclipse.swt.graphics.Rectangle getBounds();
    public org.eclipse.swt.graphics.Cursor getCursor();
    public boolean getDragDetect();
    public boolean getEnabled();
    public org.eclipse.swt.graphics.Font getFont();
    public org.eclipse.swt.graphics.Color getForeground();
    public java.lang.Object getLayoutData();
    public org.eclipse.swt.graphics.Point getLocation();
    public org.eclipse.swt.widgets.Menu getMenu();
    public org.eclipse.swt.widgets.Monitor getMonitor();
    public org.eclipse.swt.widgets.Composite getParent();
    public org.eclipse.swt.widgets.Shell getShell();
    public org.eclipse.swt.graphics.Point getSize();
    public java.lang.String getToolTipText();
    public boolean getVisible();
    public boolean isEnabled();
    public boolean isFocusControl();
    public boolean isReparentable();
    public boolean isVisible();
    public void moveAbove(org.eclipse.swt.widgets.Control control);
    public void moveBelow(org.eclipse.swt.widgets.Control control);
    public void pack();
    public void pack(boolean b);
    public void redraw();
    public void redraw(int i, int i1, int i2, int i3, boolean b);
    public void removeControlListener(org.eclipse.swt.events.ControlListener controlListener);
    public void removeDragDetectListener(org.eclipse.swt.events.DragDetectListener dragDetectListener);
    public void removeFocusListener(org.eclipse.swt.events.FocusListener focusListener);
    public void removeHelpListener(org.eclipse.swt.events.HelpListener helpListener);
    public void removeKeyListener(org.eclipse.swt.events.KeyListener keyListener);
    public void removeMenuDetectListener(org.eclipse.swt.events.MenuDetectListener menuDetectListener);
    public void removeMouseTrackListener(org.eclipse.swt.events.MouseTrackListener mouseTrackListener);
    public void removeMouseListener(org.eclipse.swt.events.MouseListener mouseListener);
    public void removeMouseMoveListener(org.eclipse.swt.events.MouseMoveListener mouseMoveListener);
    public void removeMouseWheelListener(org.eclipse.swt.events.MouseWheelListener mouseWheelListener);
    public void removePaintListener(org.eclipse.swt.events.PaintListener paintListener);
    public void removeTraverseListener(org.eclipse.swt.events.TraverseListener traverseListener);
    public void setBounds(int i, int i1, int i2, int i3);
    public void setBounds(org.eclipse.swt.graphics.Rectangle rectangle);
    public void setCapture(boolean b);
    public void setCursor(org.eclipse.swt.graphics.Cursor cursor);
    public void setDragDetect(boolean b);
    public void setEnabled(boolean b);
    public void setFont(org.eclipse.swt.graphics.Font font);
    public void setForeground(org.eclipse.swt.graphics.Color color);
    public void setLayoutData(java.lang.Object o);
    public void setLocation(int i, int i1);
    public void setLocation(org.eclipse.swt.graphics.Point point);
    public void setMenu(org.eclipse.swt.widgets.Menu menu);
    public void setRedraw(boolean b);
    public void setSize(int i, int i1);
    public void setSize(org.eclipse.swt.graphics.Point point);
    public void setToolTipText(java.lang.String s);
    public void setVisible(boolean b);
    public boolean setParent(org.eclipse.swt.widgets.Composite composite);
}
