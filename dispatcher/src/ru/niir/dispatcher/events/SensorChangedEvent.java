package ru.niir.dispatcher.events;

public class SensorChangedEvent implements DispatcherEvent, Jsonable {
	private final String sensorId;
	private final int buttonPressed;

	public SensorChangedEvent(final String sensorId, final int buttonPressed) {
		super();
		this.sensorId = sensorId;
		this.buttonPressed = buttonPressed;
	}

	public int getButtonPressed() {
		return buttonPressed;
	}

	public String getSensorId() {
		return sensorId;
	}

	@Override
	public String toString() {
		return "SensorChanged Event(sens#" + sensorId + ", but#"
				+ buttonPressed + ")";
	}

	@Override
	public String toJson() {
		return String.format("{\"type\": \"SensorChangedEvent\", "
				+ "\"sensorId\": \"%s\", " + "\"buttonPressed\": \"%d\"}",
				sensorId, buttonPressed);
	}
}
