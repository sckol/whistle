package ru.niir.protowhistle.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

import ru.niir.protowhistle.io.MediaManager;
import ru.niir.protowhistle.ui.CanvasListener;
import ru.niir.protowhistle.ui.CanvasWithListeners;
import ru.niir.protowhistle.ui.UIController;
import ru.niir.protowhistle.ui.Vibrator;
import ru.niir.protowhistle.util.Console;

public class Alarm extends CanvasWithListeners implements Component {
	public static final int STATE_AMOUNT = 3;
	private int state = -1;
	private final MediaManager mediaManager;
	private final Console console = Console.getInstance();
	private boolean onScreen = false;
	private boolean needRepaint = true;

	public Alarm(final MediaManager mediaManager) {
		this.mediaManager = mediaManager;
		setFullScreenMode(true);
	}

	public void show(final Display display, final UIController controller) {
		if (!onScreen) {
			needRepaint = true;
			Vibrator.vibrate(display);
			onScreen = true;
			display.setCurrent(this);
			setKeyPressedListener(new CanvasListener() {
				public void proceedKeyEvent(int keyCode) {
					switch (keyCode) {
					case Canvas.KEY_STAR:
						onScreen = false;
						state = 0;
						controller.showMainMenu();
						break;
					default:
						play();
					}
				}
			});
		}
		if (needRepaint) {
			repaint();
			play();
		}
	}

	public void updateState(int newState) {
		if (newState < 0 || newState >= STATE_AMOUNT) {
			console.println("Wrong state: " + newState);
		}
		if (newState > state) {
			state = newState;
			needRepaint = true;
		}
	}

	protected void paint(Graphics g) {
		if (needRepaint) {
			mediaManager.playMedia(0, state, g, this);
			needRepaint = false;
		}
	}

	private void play() {
		mediaManager.replay();
	}
}
