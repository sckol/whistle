package ru.niir.protowhistle.ui;

import javax.microedition.lcdui.Canvas;

public abstract class CanvasWithListeners extends Canvas {
	private CanvasListener keyPressedListener, keyReleasedListener, keyRepeatedListener;
	
	public void setKeyPressedListener(final CanvasListener listener) {
		this.keyPressedListener = listener; 
	}

	public void setKeyReleasedListener(CanvasListener keyReleasedListener) {
		this.keyReleasedListener = keyReleasedListener;
	}

	public void setKeyRepeatedListener(CanvasListener keyRepeatedListener) {
		this.keyRepeatedListener = keyRepeatedListener;
	}

	protected void keyPressed(final int keyCode) {
		if (keyPressedListener != null) keyPressedListener.proceedKeyEvent(keyCode);
	}

	protected void keyReleased(int keyCode) {
		if (keyReleasedListener != null) keyReleasedListener.proceedKeyEvent(keyCode);
	}

	protected void keyRepeated(int keyCode) {
		if (keyRepeatedListener != null) keyRepeatedListener.proceedKeyEvent(keyCode);
	}
	
	
}
