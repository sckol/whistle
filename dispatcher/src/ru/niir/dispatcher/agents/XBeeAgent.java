package ru.niir.dispatcher.agents;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.events.SensorChangedEvent;
import ru.niir.dispatcher.events.SensorDetectedEvent;

import com.rapplogic.xbee.api.AtCommandResponse;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

public class XBeeAgent implements Runnable {
	private final EventBus eventBus;
	private final XBee xbee;
	private static final int IO_LSB_START_BYTE = 7, ND_LSB_START_BYTE = 6,
			ND_NAME_START_BYTE = 10;

	public XBeeAgent(final EventBus eventBus, final XBee xbee) {
		super();
		this.eventBus = eventBus;
		this.xbee = xbee;
	}

	@Override
	public void run() {
		try {
			while (true) {
				final XBeeResponse _response = xbee.getResponse();
				if (_response instanceof ZNetRxIoSampleResponse) {
					ZNetRxIoSampleResponse response = (ZNetRxIoSampleResponse) _response;
					eventBus.fireEvent(new SensorChangedEvent(getSensorId(
							response.getProcessedPacketBytes(),
							IO_LSB_START_BYTE), getButtonPressed(response)));
				} else if (_response instanceof AtCommandResponse) {
					AtCommandResponse response = (AtCommandResponse) _response;
					if (response.getCommand().equals("ND")) {
						eventBus.fireEvent(new SensorDetectedEvent(getSensorId(
								response.getValue(), ND_LSB_START_BYTE),
								getSensorName(response.getValue())));
					}
				} else {
					System.out.println("HELL" +
							"LO");
				}
			}
		} catch (XBeeException e) {
			e.printStackTrace();
		}
	}

	private int getButtonPressed(final ZNetRxIoSampleResponse resp) {
		if (resp.containsDigital()) {
			if (!resp.isDigitalOn(2) && resp.isDigitalOn(1)
					&& resp.isDigitalOn(0))
				return 3;
			else if (!resp.isDigitalOn(1) && resp.isDigitalOn(2)
					&& resp.isDigitalOn(0))
				return 2;
			else if (!resp.isDigitalOn(0) && resp.isDigitalOn(2)
					&& resp.isDigitalOn(1))
				return 1;
			return 0;
		} else {
			System.out.println("ANALOG: " + resp.getAnalog1());
			if (resp.getAnalog1() < 240)
				return 1;
			else
				return 0;
		}
	}

	private static String getSensorName(final int[] val) {
		final int length = getNameLength(val);
		final char[] charar = new char[length];
		for (int i = 0; i < length; i++) {
			charar[i] = (char) val[ND_NAME_START_BYTE + i];
		}
		return new String(charar);
	}

	private static int getNameLength(final int[] val) {
		for (int i = ND_NAME_START_BYTE; i < val.length; i++) {
			if (val[i] == 0)
				return i - ND_NAME_START_BYTE;
		}
		return 0;
	}

	private static String getSensorId(int[] bytes, int offset) {
		return String.format("%08x", (long) bytes[offset] * 0x1000000l
				+ (long) bytes[offset + 1] * 0x10000l
				+ (long) bytes[offset + 2] * 0x100l + (long) bytes[offset + 3]);
	}
}
