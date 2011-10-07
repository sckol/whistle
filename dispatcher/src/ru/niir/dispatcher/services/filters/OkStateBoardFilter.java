package ru.niir.dispatcher.services.filters;

import org.jdom.Document;
import org.jdom.JDOMException;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.StateChangedEvent;

public class OkStateBoardFilter extends HtmlFilter {
	public OkStateBoardFilter(final Document doc) throws JDOMException {
		super(doc, "//div[@id=\"ok\"]");
	}

	@Override
	public boolean onEventForEach(final DispatcherEvent _event) {
		if (_event instanceof StateChangedEvent) {
			return switchVisible(false);
		} else if (_event instanceof ResetEvent) {
			return switchVisible(true);
		} else return false;
	}
}
