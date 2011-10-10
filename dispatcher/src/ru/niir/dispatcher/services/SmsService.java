package ru.niir.dispatcher.services;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.smslib.GatewayException;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;

import ru.niir.dispatcher.Phone;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.StateChangedEvent;

public class SmsService implements DispatcherService {
	private final LinkedList<Phone> phonesNotify = new LinkedList<Phone>(),
			phonesUpdateState = new LinkedList<Phone>();
	private final Service service;

	public SmsService(final Service service) {
		super();
		this.service = service;
	}

	@Override
	public void onEvent(final DispatcherEvent _event) {
		if (_event instanceof StateChangedEvent) {
			final StateChangedEvent event = (StateChangedEvent) _event;
			final Iterator<Phone> iterator = event.getOldState() == 0 ? phonesNotify
					.iterator() : phonesUpdateState.iterator();
			while (iterator.hasNext()) {
				Phone phone = iterator.next();
				final OutboundMessage message = new OutboundMessage(
						phone.getNumber(), "(emergency 0 "
								+ (event.getNewState() - 1) + ")");
				message.setSrcPort(0);
				message.setDstPort(phone.getPort());
				message.setFlashSms(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							if (service.sendMessage(message))
								System.out.println("Message sent");
							else
								System.out.println("Message was not sent!");
						} catch (TimeoutException e) {
							e.printStackTrace();
						} catch (GatewayException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	}

	public void addPhone(final Phone phone) {
		if (phone.getNotifyPriority() > 0)
			phonesNotify.addFirst(phone);
		else
			phonesNotify.addLast(phone);
		if (phone.getStateUpdatePriority() > 0)
			phonesUpdateState.addFirst(phone);
		else
			phonesUpdateState.addLast(phone);
	}
}
