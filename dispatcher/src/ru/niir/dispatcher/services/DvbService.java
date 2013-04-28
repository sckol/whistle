package ru.niir.dispatcher.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ShopSubmittedEvent;

public class DvbService implements DispatcherService {
	private final File controlFile;
	
	public DvbService(final String controlFileName) {
		controlFile = new File(controlFileName);
	}
	@Override
	public void onEvent(DispatcherEvent _event) {
		if(_event instanceof ShopSubmittedEvent) {
			try {
				controlFile.delete();
				controlFile.createNewFile();
				final FileOutputStream os = new FileOutputStream(controlFile);
				try {
					os.write("submit\n".getBytes());
				} catch(IOException e) {
					e.printStackTrace();
				} finally {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
