package org.purc.purcforms.client.widget;


/**
 * This interface is used to communicate events that happen 
 * during hiding and showing of widgets due to skip logic rules.
 * 
 * @author danielkayiwa
 *
 */
public interface WidgetListener {
	
	/**
	 * Called after a widget, which was hidden due to skip logic, has been shown.
	 * 
	 * @param widget the widget that has been shown.
	 * @param increment the increment in height due to the showing of the widget.
	 */
	public void onWidgetShown(RuntimeWidgetWrapper widget, int increment);
	
	/**
	 * Called after a widget, which was shown due to skip logic, has been hidden.
	 * 
	 * @param widget the widget that has been hidden
	 * @param decrement the decrease in height due to the hiding of the widget.
	 */
	public void onWidgetHidden(RuntimeWidgetWrapper widget, int decrement);
	
}
