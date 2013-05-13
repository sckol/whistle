package ru.niir.dispatcher.events;

public class StateChangedEvent implements DispatcherEvent, Jsonable {
	private final int newState, oldState;
	private final String reason;
	private final EmergencyType type;

	public StateChangedEvent(final int newState, final int oldState,
			final EmergencyType type, final String reason) {
		super();
		this.newState = newState;
		this.oldState = oldState;
		this.reason = reason;
		this.type = type;
	}

	public int getNewState() {
		return newState;
	}

	public int getOldState() {
		return oldState;
	}

	public EmergencyType getType() {
		return type;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public String toString() {
		return "StateChangedEvent (newState=" + newState + ", oldState="
				+ oldState + ", type=" + type + ", reason=\"" + reason + "\")";
	}
	public enum EmergencyType {
		FIRE("FIRE"), GAS_ATTACK("GAS_ATTACK");
		EmergencyType(final String message) {
			this.message = message;
		}
		private final String message;

		public String getMessage() {
			return message;
		}
	}

	@Override
	public String toJson() {
		return String.format("{\"type\": \"StateChangedEvent\", "
				+ "\"newState\": \"%d\",\"oldState\": \"%d\", "
				+ "\"reason\": \"s\", \"emergencyType\": \"%s\"}", newState, oldState,
				reason, type.getMessage());
	}
}
