package ru.niir.meshlogic;

public class MeshLogicRxPacket {
	private final byte[] content;
	private final String address;
	private final int appID;

	public MeshLogicRxPacket(byte[] content, String address, int appID) {
		super();
		this.content = content;
		this.address = address;
		this.appID = appID;
	}

	public String getStringContent() {
		return new String(content);
	}
	
	public String getHexContent() {
		return MeshLogic.getHexFromBytes(content);
	}
	
	public byte[] getContent() {
		return content;
	}

	public String getAddress() {
		return address;
	}

	public int getAppID() {
		return appID;
	}

	@Override
	public String toString() {
		return "MeshLogicRxPacket [content=" + getStringContent() + ", address=" + address
				+ ", appID=" + appID + "]";
	}
}
