package ru.niir.dispatcher.services.filters;

import java.util.HashMap;

import org.jdom.Document;
import org.jdom.JDOMException;

import ru.niir.dispatcher.NodeType;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ScannerResultsEvent;

public class CategoryCountFilter extends HtmlFilter {
	private final HashMap<String, NodeType> nodeTypeIdMap = new HashMap<String, NodeType>();
	
	
	public CategoryCountFilter(final Document doc) throws JDOMException {
		super(doc, "//td[starts-with(@id, 'numberOf')]");
		nodeTypeIdMap.put("numberOfBlinds", NodeType.BLIND);
		nodeTypeIdMap.put("numberOfInvalids", NodeType.INVALID);
		nodeTypeIdMap.put("numberOfUsers", NodeType.USER);
	}

	@Override
	public boolean onEventForEach(DispatcherEvent _event) {
		if (_event instanceof ScannerResultsEvent) {
			final ScannerResultsEvent event = (ScannerResultsEvent) _event;
			final NodeType elementNodeType = nodeTypeIdMap.get(getId());
			if (elementNodeType != null) {
				final int count = countCategory(event.getScannerResults(), elementNodeType);
				if (count > 0) return switchText(String.valueOf(count));
				else return switchText("—");
			} else return false;
		} else return false;
	}
	
	private final int countCategory(final HashMap<String, NodeType> scannerResults, final NodeType nodeType) {
		int res = 0;
		for (NodeType type : scannerResults.values()) {
			if (type == nodeType) res++;
		}
		return res;
	}
}
