package ru.niir.dispatcher.services;

import java.util.HashMap;
import java.util.Map;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.SensorChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent.EmergencyType;

public class StateMonitorService implements DispatcherService {
	private final Map<String, Integer> stateMap = new HashMap<String, Integer>();
	private final EventBus eventBus;
	private int globalState = 0;

	public StateMonitorService(final EventBus eventBus) {
		super();
		this.eventBus = eventBus;
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		if (_event instanceof SensorChangedEvent) {
			final SensorChangedEvent event = (SensorChangedEvent) _event;
			final String sensorId = event.getSensorId();
			final Integer oldState = stateMap.get(sensorId);
			final int newState = event.getButtonPressed();
			if (oldState == null || newState > oldState) {
				stateMap.put(sensorId, newState);
				final int newGlobalState = stateMap.size() > 1 ? 3 : newState;
				if (newGlobalState > globalState) {
					final int oldGlobalState = globalState;
					globalState = newGlobalState;
					eventBus.fireEvent(new StateChangedEvent(globalState,
							oldGlobalState, EmergencyType.FIRE, sensorId));
					globalState = newGlobalState;
				}
			}
		} else if (_event instanceof ResetEvent) {
			stateMap.clear();
			globalState = 0;
		}
	}
}
