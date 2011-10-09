package ru.niir.dispatcher.services.filters;

import java.util.HashMap;

import org.jdom.Document;
import org.jdom.JDOMException;

import ru.niir.dispatcher.NodeType;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ScannerResultsEvent;

public class EmployeeFilter extends HtmlFilter {
	public EmployeeFilter(final Document doc)
			throws JDOMException {
		super(doc, "//tr[starts-with(@id, 'employee')]");
	}

	@Override
	public boolean onEventForEach(DispatcherEvent _event) {
		if (_event instanceof ScannerResultsEvent) {
			ScannerResultsEvent event = (ScannerResultsEvent) _event;
			final HashMap<String, NodeType> scannerResults = event.getScannerResults();
			if (scannerResults.containsKey(getNodeId())) {
				return switchVisible(true);
			} else{
				return switchVisible(false);
			}
		} else {
			return false;
		}
	}
}
