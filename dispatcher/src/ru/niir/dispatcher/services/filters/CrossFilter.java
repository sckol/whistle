package ru.niir.dispatcher.services.filters;

import org.jdom.Document;
import org.jdom.JDOMException;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.ScannerResultsEvent;

public class CrossFilter extends HtmlFilter {
	public CrossFilter(final Document doc) throws JDOMException {
		super(doc, "//svg:g[@nodeID and starts-with(@id, 'cross')]");
	}

	@Override
	public boolean onEventForEach(DispatcherEvent _event) {
		if (_event instanceof ScannerResultsEvent) {
			final ScannerResultsEvent event = (ScannerResultsEvent) _event;
			if (event.getScannerResults().containsKey(getNodeId()))
				return switchVisible(false);
			else
				return switchVisible(true);
		} else if (_event instanceof ResetEvent) {
			return switchVisible(false);
		} else
			return false;
	}
}
