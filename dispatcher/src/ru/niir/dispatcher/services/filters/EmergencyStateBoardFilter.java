package ru.niir.dispatcher.services.filters;

import org.jdom.Document;
import org.jdom.JDOMException;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.StateChangedEvent;

public class EmergencyStateBoardFilter extends HtmlFilter {
	public EmergencyStateBoardFilter(final Document doc) throws JDOMException {
		super(doc, "//div[@id='emergency']");
	}
	
	@Override
	public boolean onEventForEach(final DispatcherEvent _event) {
		if (_event instanceof StateChangedEvent) {
			return switchVisible(true);
		} else if (_event instanceof ResetEvent) {
			return switchVisible(false);
		} else return false;
	}
}
