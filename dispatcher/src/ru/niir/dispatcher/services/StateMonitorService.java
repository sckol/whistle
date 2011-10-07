package ru.niir.dispatcher.services;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.SensorChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent.EmergencyType;

public class StateMonitorService implements DispatcherService {
	private int state = 0;
	private final EventBus eventBus;

	public StateMonitorService(final EventBus eventBus) {
		super();
		this.eventBus = eventBus;
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		if (_event instanceof SensorChangedEvent) {
			final SensorChangedEvent event = (SensorChangedEvent) _event;
			if (event.getButtonPressed() > state) {
				int oldState = state;
				state = event.getButtonPressed();
				eventBus.fireEvent(new StateChangedEvent(state, oldState,
						EmergencyType.FIRE, event.getSensorId()));
			}
		} else if (_event instanceof ResetEvent) {
			state = 0;
		}
	}
}
