package ru.niir.dispatcher.services;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.NodeType;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ScannerResultsEvent;
import ru.niir.dispatcher.events.SensorDetectedEvent;

import com.rapplogic.xbee.api.AtCommand;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;

public class XBeeScannerService implements DispatcherService {
	private final int timeout;
	private final XBee xbee;
	private final EventBus eventBus;
	private final HashMap<String, NodeType> scannerResults = new HashMap<String, NodeType>();
	private final TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			try {
				scannerResults.clear();
				xbee.sendAsynchronous(new AtCommand("ND"));
				final Timer t = new Timer();
				t.schedule(new TimerTask() {
					@Override
					public void run() {
						eventBus.fireEvent(new ScannerResultsEvent(
								scannerResults));
					}
				}, timeout);
			} catch (XBeeException e) {
				e.printStackTrace();
			}
		}
	};

	public XBeeScannerService(final EventBus eventBus, final XBee xbee,
			final int timeout) {
		super();
		this.xbee = xbee;
		this.timeout = timeout;
		this.eventBus = eventBus;
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		if (_event instanceof SensorDetectedEvent) {
			final SensorDetectedEvent event = (SensorDetectedEvent) _event;
			final NodeType nodeType = parseNodeType(event.getSensorName());
			if (nodeType != null)
				scannerResults.put(event.getSensorId(), nodeType);
		}
	}

	public TimerTask getTimerTask() {
		return timerTask;
	}

	private static NodeType parseNodeType(final String nodeName) {
		if (nodeName.startsWith("SENSOR"))
			return NodeType.SENSOR;
		else if (nodeName.startsWith("USER"))
			return NodeType.USER;
		else if (nodeName.startsWith("BLIND"))
			return NodeType.BLIND;
		else if (nodeName.startsWith("INVALID"))
			return NodeType.INVALID;
		else if (nodeName.startsWith("SECURITY"))
			return NodeType.EMPLOYEE;
		else
			return null;
	}
}