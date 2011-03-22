package com.magnetstreet.swt.beanwidget.combo.exception;

/**
 * Thrown whenever an AbstractSpecificCombo implementing class has an issue retrieving
 * data from the defined object type because of an expected condition that was not met.
 * For example, if an implemented selects an item by Id and provides an invalid Getter Name,
 * or the object doesn't contain a getId() function, this exception will be thrown.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @version 0.1.0
 * @since 7/13/2009
 */
public class IncompatibleSpecificComboObject extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IncompatibleSpecificComboObject() {
		// TODO Auto-generated constructor stub
	}

	public IncompatibleSpecificComboObject(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public IncompatibleSpecificComboObject(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public IncompatibleSpecificComboObject(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
