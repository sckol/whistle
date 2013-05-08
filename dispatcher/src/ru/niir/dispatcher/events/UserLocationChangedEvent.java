package ru.niir.dispatcher.events;

public class UserLocationChangedEvent implements DispatcherEvent {
	private final String newLocation;

	public UserLocationChangedEvent(String newLocation) {
		super();
		this.newLocation = newLocation;
	}

	public String getNewLocation() {
		return newLocation;
	}
}
