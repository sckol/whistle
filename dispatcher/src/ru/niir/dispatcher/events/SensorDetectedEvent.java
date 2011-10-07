package ru.niir.dispatcher.events;

public class SensorDetectedEvent implements DispatcherEvent {
	private final String sensorName, sensorId;

	public SensorDetectedEvent(final String sensorId, final String sensorName) {
		super();
		this.sensorName = sensorName;
		this.sensorId = sensorId;
	}

	public String getSensorName() {
		return sensorName;
	}

	public String getSensorId() {
		return sensorId;
	}

	@Override
	public String toString() {
		return "SensorDetectedEvent(sens#" + sensorId + ", name:" + sensorName
				+ ")";
	}
}
