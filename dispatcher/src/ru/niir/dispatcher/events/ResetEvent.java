package ru.niir.dispatcher.events;

public class ResetEvent implements DispatcherEvent {
	private final boolean emergencyOnly;
	
	public ResetEvent(final boolean emergencyOnly) {
		this.emergencyOnly = emergencyOnly;
	}
	public ResetEvent() {
		emergencyOnly = false;
	}
	
	public boolean isEmergencyOnly() {
		return emergencyOnly;
	}
	@Override
	public String toString() {
		return "Reset event" + (emergencyOnly ? " (emergency only)" : "");
	}
}
