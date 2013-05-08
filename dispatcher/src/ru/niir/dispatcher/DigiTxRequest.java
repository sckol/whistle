package ru.niir.dispatcher;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeRequest;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;
import com.rapplogic.xbee.util.IntArrayOutputStream;

@SuppressWarnings("serial")
public class DigiTxRequest extends XBeeRequest {
	public final static int ZNET_MAX_PAYLOAD_SIZE = 72;
	public final static int DEFAULT_BROADCAST_RADIUS = 0;
	private XBeeAddress64 destAddr64;
	private int broadcastRadius;
	private int[] payload;
	private int maxPayloadSize;

	public DigiTxRequest(int frameId, XBeeAddress64 dest64,
			int broadcastRadius, int[] payload) {
		this.setFrameId(frameId);
		this.destAddr64 = dest64;
		this.broadcastRadius = broadcastRadius;
		this.payload = payload;
	}

	/**
	 * Abbreviated constructor for sending a unicast TX packet
	 * 
	 * @param dest64
	 * @param payload
	 */
	public DigiTxRequest(XBeeAddress64 dest64, int[] payload) {
		this(XBeeRequest.DEFAULT_FRAME_ID, dest64,
				ZNetTxRequest.DEFAULT_BROADCAST_RADIUS, payload);
	}

	@Override
	public int[] getFrameData() {
		return this.getFrameDataAsIntArrayOutputStream().getIntArray();
	}

	protected IntArrayOutputStream getFrameDataAsIntArrayOutputStream() {
		if (this.getMaxPayloadSize() > 0
				&& payload.length > this.getMaxPayloadSize()) {
			throw new IllegalArgumentException(
					"Payload exceeds user-defined maximum payload size of "
							+ this.getMaxPayloadSize()
							+ " bytes.  Please package into multiple packets");
		}
		IntArrayOutputStream out = new IntArrayOutputStream();
		// api id
		out.write(this.getApiId().getValue());
		// frame id (arbitrary byte that will be sent back with ack)
		out.write(this.getFrameId());
		// add 64-bit dest address
		out.write(destAddr64.getAddress());
		out.write(0xFF);
		out.write(0xFE);
		// write broadcast radius
		out.write(broadcastRadius);
		out.write(0x0);
		out.write(payload);
		return out;
	}

	public int getMaxPayloadSize() {
		return maxPayloadSize;
	}

	public ApiId getApiId() {
		return ApiId.ZNET_TX_REQUEST;
	}
}
