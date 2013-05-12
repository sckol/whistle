package ru.niir.dispatcher.services;

import java.util.Timer;
import java.util.TimerTask;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.SensorChangedEvent;

public class TimerStateChangerService implements DispatcherService {
	private final EventBus bus;
	private static final long DELAY = 30000;
	private Timer timer;

	public TimerStateChangerService(final EventBus bus) {
		this.bus = bus;
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		if (_event instanceof SensorChangedEvent) {
			timer = new Timer();
			final SensorChangedEvent event = (SensorChangedEvent) _event;
			if (event.getButtonPressed() == 1) {
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						bus.fireEvent(new SensorChangedEvent(event
								.getSensorId(), 2));

					}
				}, DELAY);

			}
		} else if (_event instanceof ResetEvent) {
			if (timer != null) timer.cancel();
		}
	}

}
