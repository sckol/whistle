package ru.niir.dispatcher;

import java.util.ArrayList;
import java.util.List;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.services.DispatcherService;

public class EventBus {
	private final List<DispatcherService> services = new ArrayList<DispatcherService>();
	
	public void addListener(final DispatcherService service) {
		services.add(service);
	}
	
	public void removeListener(final DispatcherService service) {
		services.remove(service);
	}
	
	public void fireEvent(final DispatcherEvent event) {
		for (DispatcherService service : services) service.onEvent(event);
	}
}
