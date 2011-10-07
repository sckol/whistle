package ru.niir.protowhistle.ui.component;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

public class WaitingAlert extends Alert {

	public WaitingAlert(final String alertText) {
		super("Пожалуйста, подождите...", alertText, null, AlertType.INFO);
		setTimeout(Alert.FOREVER);
	}
}