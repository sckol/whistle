package ru.niir.protowhistle.io;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import ru.niir.protowhistle.ui.CanvasWithListeners;
import ru.niir.protowhistle.util.Console;

public class MediaManager {
	private Player currentPlayer;
	private final Console console = Console.getInstance();
	private Image image;
	private Player player;
	private final CanvasWithListeners canvas = new CanvasWithListeners() {
		protected void paint(Graphics g) {
			if (getImage() != null) {
				g.drawImage(getImage(), 0, 0, Graphics.LEFT | Graphics.TOP);
			}
		}
	};

	public void playMedia(final String fileBase) {
		try {
			final String imageFileName = fileBase + ".png";
			if (((FileConnection) Connector.open(imageFileName, Connector.READ))
					.exists()) {
				if (player != null) {
					player.close();
				}
				image = Image.createImage(Connector
						.openInputStream(imageFileName));
				playAudio(fileBase + ".amr");
			} else {
				image = null;
				playVideo(fileBase + ".mp4", canvas);
			}
		} catch (IOException e) {
			console.println("Cannot play media");
		} catch (MediaException e) {
			console.println("Error while playing media");
		}
	}

	public Image getImage() {
		return image;
	}

	private void playVideo(final String fileName, final Canvas c)
			throws MediaException, IOException {
		player = Manager.createPlayer(fileName);
		player.realize();
		player.setLoopCount(-1);
		VideoControl videoControl = (VideoControl) player
				.getControl("javax.microedition.media.control.VideoControl");
		if (videoControl == null)
			throw new MediaException("No VideoControl!!");
		videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, c);
		videoControl.setDisplayFullScreen(true);
		videoControl.setVisible(true);
		player.start();
	}

	public CanvasWithListeners getCanvas() {
		return canvas;
	}

	private void playAudio(final String fileName) throws IOException,
			MediaException {
		final Player newPlayer = Manager.createPlayer(fileName);
		newPlayer.prefetch();
		if (currentPlayer != null)
			currentPlayer.stop();
		newPlayer.start();
		if (currentPlayer != null)
			currentPlayer.setMediaTime(0);
		currentPlayer = newPlayer;
	}

	public void stop() {
		if (player != null) {
			player.close();
		}
	}
}
