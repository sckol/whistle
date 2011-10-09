package ru.niir.dispatcher.services.filters;

import org.jdom.Document;
import org.jdom.JDOMException;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.StateChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent.EmergencyType;

public class EmergencyTypeFilter extends HtmlFilter {
	public EmergencyTypeFilter(final Document doc) throws JDOMException {
		super(doc, "//tr[starts-with(@id, 'emergencyType')]");
	}

	@Override
	public boolean onEventForEach(DispatcherEvent _event) {
		if (_event instanceof StateChangedEvent) {
			final StateChangedEvent event = (StateChangedEvent) _event;
			if (event.getType().equals(EmergencyType.FIRE)
					&& "emergencyTypeFire".equals(getId()))
				return switchVisible(true);
			if (event.getType().equals(EmergencyType.GAS_ATTACK)
					&& "emergencyTypeGas".equals(getId()))
				return switchVisible(true);
		} else if (_event instanceof ResetEvent) {
			return switchVisible(false);
		}
		return false;
	}
}
