package ru.niir.dispatcher.agents;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.smslib.GatewayException;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;


public class SmsAgent implements Runnable {
	private final BlockingQueue<SmsAgentTask> queue;
	private final Service smsService;

	public SmsAgent(final Service smsService, final BlockingQueue<SmsAgentTask> queue) {
		super();
		this.queue = queue;
		this.smsService = smsService;
	}

	@Override
	public void run() {
		while (true) {
			try {
				final SmsAgentTask task = queue.take();
				final OutboundMessage message = new OutboundMessage(
						task.getNumber(), task.getMessage());
				message.setSrcPort(0);
				message.setDstPort(task.getPort());
				message.setFlashSms(true);
				if (smsService.sendMessage(message))
					System.out.println("Message sent (" + task.getNumber() + ")");
				else
					System.out.println("Message was not sent!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (GatewayException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static class SmsAgentTask implements Comparable<SmsAgentTask> {
		private final String number, message;
		private final int port, priority;

		public SmsAgentTask(final String number, final int port,
				final String message, final int priority) {
			super();
			this.number = number;
			this.message = message;
			this.priority = priority;
			this.port = port;
		}

		@Override
		public int compareTo(final SmsAgentTask o) {
			return priority - o.priority;
		}

		public String getNumber() {
			return number;
		}

		public String getMessage() {
			return message;
		}

		public int getPort() {
			return port;
		}
	}
}