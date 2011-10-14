package ru.niir.protowhistle.ui.component;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import ru.niir.protowhistle.ui.UIController;
import ru.niir.protowhistle.util.Console;

public class VideoPlayer extends Canvas implements Component {
	private final Console console = Console.getInstance();

	public VideoPlayer() {
		super();
		setFullScreenMode(true);
	}

	public void show(final Display display, final UIController controller) {
		try {
			final Player player = Manager.createPlayer(System.getProperty("fileconn.dir.memorycard")
					+ "Images/whistle/00ar.mp4");
			player.realize();
			 player.setLoopCount(-1);
			 VideoControl videoControl = (VideoControl) player
			 .getControl("javax.microedition.media.control.VideoControl");
			 if (videoControl == null)
			 throw new MediaException("No VideoControl!!");
			 videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO,
			 this);
			 videoControl.setDisplayFullScreen(true);
			 videoControl.setVisible(true);
			 display.setCurrent(this);
			 player.start();
		} catch (IOException e) {
			console.printThrowable(e, "Cannot play video");
		} catch (Exception e) {
			console.printThrowable(e, "Error while playing video");
		}
	}

	// public void playerUpdate(Player player, String event, Object eventData) {
	// if (event.equals(PlayerListener.STARTED)
	// && new Long(0L).equals((Long) eventData)) {
	// VideoControl vc = null;
	// if ((vc = (VideoControl) player.getControl("VideoControl")) != null) {
	// Item videoDisp = (Item) vc.initDisplayMode(
	// vc.USE_GUI_PRIMITIVE, this);
	// try {
	// vc.setDisplayFullScreen(true);
	// } catch (MediaException e) {
	// console.printThrowable(e, "Error while playing video");
	// }
	// }
	// display.setCurrent(this);
	// }
	// }
	protected void paint(Graphics arg0) {
		// TODO Auto-generated method stub
	}
}
