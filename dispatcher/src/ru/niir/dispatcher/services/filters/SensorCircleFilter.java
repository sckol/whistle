package ru.niir.dispatcher.services.filters;

import org.jdom.Document;
import org.jdom.JDOMException;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.SensorChangedEvent;

public class SensorCircleFilter extends HtmlFilter {
	private static final String PRESSED_CLASS = "sensor_flashing",
			OK_CLASS = "sensor_green", RELEASED_CLASS = "sensor_red";

	public SensorCircleFilter(final Document doc) throws JDOMException {
		super(doc, "//svg:path[@nodeID and starts-with(@id, 'sensor')]");
	}

	@Override
	public boolean onEventForEach(DispatcherEvent _event) {
		if (_event instanceof SensorChangedEvent) {
			final SensorChangedEvent event = (SensorChangedEvent) _event;
			if (event.getSensorId().equals(getNodeId())) {
				if (event.getButtonPressed() > 0) {
					return switchClass(PRESSED_CLASS);
				} else if (PRESSED_CLASS.equals(getCssClass())) {
					setCssClass(RELEASED_CLASS);
					return true;
				} else
					return false;
			} else
				return false;
		} else if (_event instanceof ResetEvent) {
			return switchClass(OK_CLASS);
		} else
			return false;
	}
}
