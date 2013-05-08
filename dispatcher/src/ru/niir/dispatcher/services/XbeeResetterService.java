package ru.niir.dispatcher.services;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;

import ru.niir.dispatcher.DigiTxRequest;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ExitEvent;
import ru.niir.dispatcher.events.ResetEvent;

public class XbeeResetterService implements DispatcherService {
	private final XBee xbee;

	public XbeeResetterService(XBee xbee) {
		super();
		this.xbee = xbee;
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		if (_event instanceof ResetEvent) {
			try {
				xbee.sendAsynchronous(new DigiTxRequest(
						new XBeeAddress64("00 13 A2 00 40 76 52 0F"), new int[] { 1 }));
			} catch (XBeeException e) {
				e.printStackTrace();
			}
		} else if (_event instanceof ExitEvent) {
			xbee.close();
		}
	}
}
