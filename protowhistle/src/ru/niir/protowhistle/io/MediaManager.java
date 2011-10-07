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

import ru.niir.protowhistle.util.Console;

public class MediaManager {
	private Player currentPlayer;
	private final Console console = Console.getInstance();
	private final String rootDirectory;
	private final StorageManager storageManager;

	public MediaManager(final String rootDirectory,
			final StorageManager storageManager) {
		this.rootDirectory = rootDirectory;
		this.storageManager = storageManager;
	}

	public void playMedia(final int type, final int state, final Graphics g,
			final Canvas c) {
		final String fileBase = getFileBase(type, state,
				storageManager.loadCategory(), 'r');
		try {
			final String imageFileName = fileBase + ".png";
			if (((FileConnection) Connector.open(imageFileName, Connector.READ)).exists()) {
				drawImage(imageFileName, g);
				playAudio(fileBase + ".amr");
			} else {
				playVideo(fileBase + ".mp4", c);
			}
		} catch (IOException e) {
			console.println("Cannot play media");
		} catch (MediaException e) {
			console.println("Error while playing media");
		}
	}
	
	public void replay() {}

	private String getFileBase(final int type, final int state, char category,
			char language) {
		return rootDirectory + type + state + category + language;
	}

	private void playVideo(final String fileName, final Canvas c)
			throws MediaException, IOException {
		//FIX HERE!!!
		final Player player = Manager.createPlayer(
				Connector.openInputStream(fileName), "video/mpeg");
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

	private void playAudio(final String fileName) throws IOException,
			MediaException {
		final Player newPlayer = Manager.createPlayer(
				Connector.openInputStream(fileName), "audio/amr");
		newPlayer.prefetch();
		if (currentPlayer != null)
			currentPlayer.stop();
		newPlayer.start();
		if (currentPlayer != null)
			currentPlayer.setMediaTime(0);
		currentPlayer = newPlayer;
	}

	private void drawImage(final String fileName, Graphics g) throws IOException {
		g.drawImage(Image.createImage(Connector.openInputStream(fileName)), 0,
				0, Graphics.LEFT | Graphics.TOP);
	}
}
