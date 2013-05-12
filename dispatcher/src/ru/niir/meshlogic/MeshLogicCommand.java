package ru.niir.meshlogic;

public enum MeshLogicCommand {
	GET_SERIAL_NUMBER ((byte) 0x04),
	READ_PACKET ((byte) 0x0B),
	NETWORK_ID ((byte) 0x02),
	TRANSMITION_PARAMS ((byte) 0x06),
	BASE_STATION ((byte) 0x05),
	SAVE ((byte) 0x20);
	
	private final byte value;
	
	private MeshLogicCommand(final byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return value;
	}
}
