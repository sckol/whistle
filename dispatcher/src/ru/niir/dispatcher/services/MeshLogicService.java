package ru.niir.dispatcher.services;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ExitEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.SensorChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent;
import ru.niir.dispatcher.events.UserLocationChangedEvent;
import ru.niir.meshlogic.MeshLogic;
import ru.niir.meshlogic.MeshLogicRxPacket;

public class MeshLogicService implements Runnable, DispatcherService {
	private boolean running = false;
	private final MeshLogic meshLogic;
	private final EventBus bus;
	private String currentLocation = "006";

	public MeshLogicService(final EventBus bus, final String portname,
			final int baudRate) throws NoSuchPortException, PortInUseException,
			UnsupportedCommOperationException, IOException {
		this.meshLogic = new MeshLogic(portname, baudRate);
		this.bus = bus;
	}

	@Override
	public void run() {
		running = true;

		try {
			while (running) {
				MeshLogicRxPacket p = meshLogic.readPacket();
				if (p == null) {
					Thread.sleep(1000);
				} else {
					switch (p.getAppID()) {
					case 0x0B:
						switch (p.getContent()[0]) {
						case (byte) 0xF0:
							bus.fireEvent(new SensorChangedEvent(
									addrToSensorId(p.getAddress()), 1));
							break;
						case 0x01:
							bus.fireEvent(new ResetEvent(true));
							break;
						}
						break;
					case 0x0C:
						final String newLocation = addrToSensorId("00"
								+ p.getHexContent());
						if (!currentLocation.equals(newLocation)) {
							currentLocation = newLocation;
							bus.fireEvent(new UserLocationChangedEvent(
									currentLocation));
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			running = false;
			meshLogic.close();
		}
	}

	private final String addrToSensorId(final String addr) {
		if (addr.equals("00D0"))
			return "008";
		else if (addr.equals("00CE"))
			return "006";
		else {
			System.out.println("Wrong address" + addr);
			return "006";
		}
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		if (_event instanceof StateChangedEvent) {
			new Timer().schedule(new TimerTask() {		
				@Override
				public void run() {
					bus.fireEvent(new UserLocationChangedEvent(currentLocation));					
				}
			}, 1000);

		}
		if (_event instanceof ExitEvent) {
			running = false;
		}

	}
}
