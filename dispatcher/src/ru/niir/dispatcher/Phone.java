package ru.niir.dispatcher;

public class Phone {
	private final String number;
	private final int port;
	private final int notifyPriority, stateUpdatePriority;

	public Phone(final String number, final int port, final int notifyPriority,
			final int stateUpdatePriority) {
		super();
		this.number = number;
		this.port = port;
		this.notifyPriority = notifyPriority;
		this.stateUpdatePriority = stateUpdatePriority;
	}

	public String getNumber() {
		return number;
	}

	public int getPort() {
		return port;
	}

	public int getNotifyPriority() {
		return notifyPriority;
	}

	public int getStateUpdatePriority() {
		return stateUpdatePriority;
	}
}
