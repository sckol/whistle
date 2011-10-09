package ru.niir.dispatcher.services.filters;

import org.jdom.Document;
import org.jdom.JDOMException;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.SensorChangedEvent;

public class SensorCircleFilter extends HtmlFilter {
	private static final String PRESSED_CLASS = "sensor_flashing",
			OK_CLASS = "sensor_green", BUT1_CLASS = "sensor_gray",
			BUT2_CLASS = "sensor_yellow", BUT3_CLASS = "sensor_red";

	public SensorCircleFilter(final Document doc) throws JDOMException {
		super(doc, "//svg:path[@nodeID and starts-with(@id, 'sensor')]");
	}

	@Override
	public boolean onEventForEach(DispatcherEvent _event) {
		if (_event instanceof SensorChangedEvent) {
			final SensorChangedEvent event = (SensorChangedEvent) _event;
			if (event.getSensorId().equals(getNodeId())) {
				final int button = event.getButtonPressed();
				if (button > 0) {
					return switchClass(getColorClass(button)) | appendCssClass(PRESSED_CLASS);
				} else {
					return removeCssClass(PRESSED_CLASS);
				} 
			} else
				return false;
		} else if (_event instanceof ResetEvent) {
			return switchClass(OK_CLASS);
		} else
			return false;
	}
	
	private static String getColorClass(final int button) {
		if (button == 1) return BUT1_CLASS;
		else if (button == 2) return BUT2_CLASS;
		else if (button == 3) return BUT3_CLASS;
		else return OK_CLASS;
	}
}
