package ru.niir.protowhistle.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.rms.RecordStoreException;

import ru.niir.protowhistle.util.Console;
import ru.niir.protowhistle.util.StoppableRunnable;

public class ConnectionManager {
	public static final char NORMAL_MODE = 'N', CALREQ_MODE = 'C';
	
	private final Console console = Console.getInstance();
	private boolean connected = false;
	private InputStream is;
	private OutputStream os;
	private GatewayReader rawReader;

	public boolean connect() {
		if (isConnected())
			close();
		String url = null;
		try {
			url = StorageManager.getStorageManager().loadURL();
		} catch (RecordStoreException e) {
			console.printThrowable(e, "Cannot open record store");
			connected = false;
			return connected;
		}
		if (url == null) {
			console.println("URL not specified");
			connected = false;
			return connected;
		}
		try {
			StreamConnection con = (StreamConnection) Connector.open(url);
			is = con.openInputStream();
			os = con.openOutputStream();
		} catch (IOException e) {
			console.printThrowable(e, "Cannot open URL: " + url);
			connected = false;
			return connected;
		}
		new Thread(connectionRunnable).start();
		connected = true;
		return connected;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setGatewayReader(final GatewayReader reader) {
		this.rawReader = reader;
	}
	
	public void switchToNormalMode() {
		switchToMode(NORMAL_MODE);
	}
	
	public void switchToMode(final char mode) {
		try {
			os.write(mode);
			os.flush();
		} catch (IOException e) {
			console.printThrowable(e);
		}
	}
	
	public void setUserCategory(final char category, final char serial) {
		try {
		os.write(category);
		os.flush();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		os.write(serial);
		os.flush();
		} catch (IOException e) {
			console.printThrowable(e, "Cannot upload user category to the terminal");
		}
	}

	private final StoppableRunnable connectionRunnable = new StoppableRunnable() {
		protected void whileRun() {
			if (rawReader != null) {
				try {
					final int read = is.read();
					if (read == -1) {
						stop();
						console.println("End of the input steam reached");
						return;
					}
					rawReader.onSymbolRead((char) read);
				} catch (IOException e) {
					if (!isFinished()) {
						console.printThrowable(e);
					}
					stop();
					return;
				}
			}
		}

		protected void onStop() {}

		protected void onReset() {}
	};

	public void close() {
		connectionRunnable.stop();
		if (is != null)
			try {
				is.close();
			} catch (IOException e) {
			} finally {
				is = null;
			}
	}

	public interface GatewayReader {
		public void onSymbolRead(char c);
	}
}
