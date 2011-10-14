package ru.niir.protowhistle.ui;

import javax.microedition.lcdui.Display;

public class Vibrator {
	public static void vibrate(final Display display) {
		new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 5; i++) {
					com.nokia.mid.ui.DeviceControl.setLights(0, 100);
					try {
						com.nokia.mid.ui.DeviceControl.startVibra(100, 300);
						Thread.sleep(600);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}
}
