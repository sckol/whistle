package ru.niir.protowhistle.io;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ru.niir.protowhistle.util.Console;

public class ImageManager {
	public static final int IMAGE_AMOUNT = 3;
	private final Console console = Console.getInstance();
	// variables to avoid loop "Image repaint->security request->Image repaint"
	// appearing in unsigned MIDlets.
	private int currentIndex;
	private char currentCategory;
	private Image currentImage = null;
	private final String rootDirectory;

	public ImageManager(final String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public void drawImage(final int index, final char category, Graphics g) {
		if (index < 0
				|| index >= IMAGE_AMOUNT
				|| category < StorageManager.FIRST_CATEGORY
				|| category >= StorageManager.FIRST_CATEGORY
						+ StorageManager.CATEGORY_NUM) {
			console.println("Wrong image requested (" + index
					+ String.valueOf(category) + ")");
			return;
		}
		try {
			if (!(currentImage != null && currentIndex == index && currentCategory == category)) {
				currentIndex = index;
				currentCategory = category;
				currentImage = Image.createImage(Connector
						.openDataInputStream(rootDirectory + index
								+ String.valueOf(category) + ".png"));
			}
			g.drawImage(currentImage, 0, 0, Graphics.LEFT | Graphics.TOP);
		} catch (IOException e) {
			console.printThrowable(e, "Cannot open image");
		}
	}
}
