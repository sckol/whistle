package ru.niir.protowhistle.io;

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

import ru.niir.protowhistle.lisp.LispEvaluator;
import ru.niir.protowhistle.util.Console;

public class SmsProcessor implements MessageListener {
	private static final int BASE_SMS_PORT = 50045;
	private final MessageConnection[] messageConnections = new MessageConnection[StorageManager.CATEGORY_NUM];
	private final LispEvaluator evaluator;
	private final Console console = Console.getInstance(); 
	public static final int NOT_PROCESSED = 0, PROCESSED = 1, ERROR = 2;
	private int processState = 0;

	public SmsProcessor(final LispEvaluator evaluator)
			throws IOException {
		this.evaluator = evaluator;
		for (int i = 0; i < messageConnections.length; i++) {
			messageConnections[i] = ((MessageConnection) Connector
					.open("sms://:" + (BASE_SMS_PORT + i)));
			messageConnections[i].setMessageListener(this);
		}
	}

	public void notifyIncomingMessage(MessageConnection messageConnection) {
		try {
			processState = PROCESSED;
			evaluator.eval(((TextMessage) messageConnection.receive())
					.getPayloadText());
		} catch (InterruptedIOException e) {
			console.printThrowable(e);
		} catch (IOException e) {
			console.printThrowable(e);
		}
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
