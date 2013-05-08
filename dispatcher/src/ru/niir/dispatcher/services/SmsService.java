package ru.niir.dispatcher.services;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.smslib.AGateway;
import org.smslib.IQueueSendingNotification;
import org.smslib.OutboundMessage;
import org.smslib.Service;

import ru.niir.dispatcher.Phone;
import ru.niir.dispatcher.agents.SmsOutboundMessageAgent.SmsAgentTask;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ExitEvent;
import ru.niir.dispatcher.events.StateChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent.EmergencyType;

public class SmsService implements DispatcherService {
	private final List<Phone> phones = new ArrayList<Phone>();
	private final Service smsService;

	public SmsService(final Service smsService) {
		super();
		this.smsService = smsService;
		smsService.setQueueSendingNotification(new IQueueSendingNotification() {
			@Override
			public void process(final AGateway gateway,
					final OutboundMessage msg) {
				System.out.println("Message " + msg.getText() + " sent "
						+ msg.getMessageStatus() + " from "
						+ gateway.getGatewayId() + " to " + msg.getRecipient());
			}
		});
	}

	@Override
	public void onEvent(final DispatcherEvent _event) {
		if (_event instanceof StateChangedEvent) {
			final StateChangedEvent event = (StateChangedEvent) _event;
			final boolean firstNotify = event.getOldState() == 0;
			final PriorityQueue<SmsAgentTask> transaction = new PriorityQueue<SmsAgentTask>();
			for (Phone phone : phones) {
				transaction.add(new SmsAgentTask(phone.getNumber(), phone
						.getPort(), "(emergency " + typeToInt(event.getType())
						+ " " + (event.getNewState() - 1) + ")",
						firstNotify ? phone.getNotifyPriority() : phone
								.getStateUpdatePriority()));
			}
			for (SmsAgentTask task : transaction) {
				final OutboundMessage message = new OutboundMessage(
						task.getNumber(), task.getMessage());
				message.setSrcPort(0);
				message.setDstPort(task.getPort());
				message.setFlashSms(true);
				smsService.queueMessage(message);
			}
		} else if (_event instanceof ExitEvent) {
			try {
				smsService.stopService();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static int typeToInt(final EmergencyType type) {
		switch (type) {
		case FIRE:
			return 0;
		case GAS_ATTACK:
			return 1;
		}
		return 0;
	}

	public void addPhone(final Phone phone) {
		phones.add(phone);
	}
}
