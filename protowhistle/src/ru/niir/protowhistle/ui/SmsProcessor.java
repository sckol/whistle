package ru.niir.protowhistle.ui;

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

import ru.niir.protowhistle.io.StorageManager;

public class SmsProcessor implements MessageListener {
	private final UIController controller;
	private final StorageManager storageManager;
	private int processState = NOT_PROCESSED;
	public static final int NOT_PROCESSED = 0, PROCESSED = 1, CANCEL = 2;
	private static final int BASE_SMS_PORT = 50042;
	private final MessageConnection[] messageConnections = new MessageConnection[StorageManager.CATEGORY_NUM];
	private static SmsProcessor instance = null;

	private SmsProcessor(final UIController controller,
			final StorageManager storageManager) throws IOException {
		this.controller = controller;
		this.storageManager = storageManager;
		for (int i = 0; i < messageConnections.length; i++) {
			messageConnections[i] = ((MessageConnection) Connector
					.open("sms://:" + (BASE_SMS_PORT + i)));
			messageConnections[i].setMessageListener(this);
		}
	}

	public static SmsProcessor getSmsProcesser(final UIController controller,
			final StorageManager storageManager) throws IOException {
		if (instance == null)
			return new SmsProcessor(controller, storageManager);
		else
			return instance;
	}

	public void notifyIncomingMessage(MessageConnection messageConnection) {
		processState = PROCESSED;
		int state = 0;
		try {
			final TextMessage message = ((TextMessage) messageConnection
					.receive());
			final String text = message.getPayloadText();
			if (text.length() != 0) {
				final Integer integer = Integer.valueOf(String.valueOf(text
						.charAt(0)));
				if (integer != null)
					state = integer.intValue();
			}
		} catch (InterruptedIOException e) {
		} catch (IOException e) {
		}
		if (state == -1) {
			controller.showVideo();
		}
		else if (state == 0 && storageManager.loadCategory() == 'b') {
			processState = CANCEL;
			return;
		}
		controller.showAlarm(state);
		
	}

	public int getProcessState() {
		return processState;
	}

	/**
	 * This function is especially important as exiting application without
	 * closing messageConnections may cause irreversible changes in Nokia
	 * devices ("Application conflicts" error).
	 */
	public void close() {
		for (int i = 0; i < messageConnections.length; i++) {
			if (messageConnections[i] != null)
				try {
					messageConnections[i].close();
				} catch (IOException e) {
				}
		}
	}
}
