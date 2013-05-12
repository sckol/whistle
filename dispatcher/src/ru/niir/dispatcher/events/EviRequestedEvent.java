package ru.niir.dispatcher.events;

public class EviRequestedEvent implements DispatcherEvent {
	public final int packetNumber;
	
	public EviRequestedEvent(final int packetNumber) {
		this.packetNumber = packetNumber;
	}

	public int getPacketNumber() {
		return packetNumber;
	}

	@Override
	public String toString() {
		return "EviRequestedEvent(" + packetNumber + "packets)";
	}
}
