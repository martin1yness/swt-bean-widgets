package com.magnetstreet.swt.beanwidget.dataview;

/**
 * GenericDataViewDialog
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0 Jan 12, 2010
 * @since Jan 12, 2010
 */
public interface GenericDataViewDialog {
    /**
     * Defines the dataview that will be displayed in the dialog form.
     * @param view The dataview object to be displayed
     */
    public void setDataView(DataView view);

    /**
     * @return Retrieve dataview back from dialog
     */
    public DataView getDataView();

    /**
     * Used to open the dialog in a blocking mode mimicing the SWT Dialog, based
     * off of the Window API.
     */
    public void openBlocking();
}
