package ru.niir.dispatcher.services;

import java.io.IOException;
import java.io.OutputStream;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ExitEvent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.StateChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent.EmergencyType;
import ru.niir.dispatcher.events.UserLocationChangedEvent;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class BluetoothService implements DispatcherService {
	private final SerialPort port;
	private final OutputStream os;

	public BluetoothService(final String portname, final int baudrate)
			throws PortInUseException, NoSuchPortException, IOException,
			UnsupportedCommOperationException {
		final CommPortIdentifier portId = CommPortIdentifier
				.getPortIdentifier(portname);
		port = (SerialPort) portId.open("NIIR Dispatcher", 5000);
		port.setSerialPortParams(baudrate, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		os = port.getOutputStream();
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		String s = null;
		if (_event instanceof StateChangedEvent) {
			final StateChangedEvent event = (StateChangedEvent) _event;
			if (event.getType() == EmergencyType.GAS_ATTACK) {
				s = "llarmout#100#";
			} else {
				switch (event.getNewState()) {
				case 1:
					s = String.format("llarm%s#", event.getReason(), event.getReason());
					break;
				case 2:
					s = "aatastrofa1#";
					break;
				case 3:
					s = "aatastrofa2#";
					break;
				}
			}
		} else if (_event instanceof UserLocationChangedEvent) {
			final UserLocationChangedEvent event = (UserLocationChangedEvent) _event;
			s = String.format("%s#", event.getNewLocation());
		} else if (_event instanceof ResetEvent) {
			s = "nnd#";
		} else if (_event instanceof ExitEvent) {
			port.close();
		}
		if (s != null) {
			try {
				System.out.println(s);
				os.write(s.getBytes());
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
