package ru.niir.protowhistle.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

import ru.niir.protowhistle.io.MediaManager;
import ru.niir.protowhistle.io.StorageManager;
import ru.niir.protowhistle.ui.CanvasListener;
import ru.niir.protowhistle.ui.CanvasWithListeners;
import ru.niir.protowhistle.ui.UIController;
import ru.niir.protowhistle.ui.Vibrator;

public class Alarm implements Component {
	public static final int STATE_AMOUNT = 3;
	private int state = -1;
	private final MediaManager mediaManager;
	private boolean onScreen = false;
	private boolean needRepaint = true;
	private final String rootDirectory;
	private final StorageManager storageManager;
	private int type = -1;
	private final CanvasWithListeners canvas;

	public Alarm(final MediaManager mediaManager,
			final StorageManager storageManager, final String rootDirectory) {
		this.mediaManager = mediaManager;
		this.rootDirectory = rootDirectory;
		this.storageManager = storageManager;
		canvas = mediaManager.getCanvas();
		canvas.setFullScreenMode(true);
	}

	public void show(final Display display, final UIController controller) {
		if (!onScreen) {
			needRepaint = true;
			Vibrator.vibrate(display);
			onScreen = true;
			display.setCurrent(canvas);
			canvas.setKeyPressedListener(new CanvasListener() {
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
			play();
		}
	}

	public void updateState(final int newState, final int type) {
		if (newState > state) {
			this.state = newState;
			this.type = type;
			needRepaint = true;
		}
	}

	protected void paint(Graphics g) {
		if (mediaManager.getImage() != null) {
			g.drawImage(mediaManager.getImage(), 0, 0, Graphics.LEFT
					| Graphics.TOP);
		}
	}

	private void play() {
		mediaManager.playMedia(getFileBase(type, state, storageManager.loadCategory(), 'e'));
		canvas.repaint();
		needRepaint = false;
	}

	private String getFileBase(final int type, final int state, char category,
			char language) {
		return rootDirectory + type + state + category + language;
	}
}
