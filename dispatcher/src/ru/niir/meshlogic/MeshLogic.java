package ru.niir.meshlogic;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MeshLogic {

	private final CommPortIdentifier portId;
	private SerialPort serial;
	private InputStream is;
	private OutputStream os;

	public MeshLogic(final String portFile, final int baudRate)
			throws NoSuchPortException, PortInUseException,
			UnsupportedCommOperationException, IOException {
		portId = CommPortIdentifier.getPortIdentifier(portFile);
		serial = (SerialPort) portId.open("MeshLogic Terminal", 2000);
		serial.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		serial.setRTS(true);
		is = serial.getInputStream();
		os = serial.getOutputStream();
	}

	public void close() {
		if (serial != null) {
			serial.close();
		}
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
			}
		}
	}

	public String getID() throws IOException {
		byte[] ans = sendCommand(MeshLogicCommand.GET_SERIAL_NUMBER, null);
		return getHexFromBytes(ans);
	}

	public MeshLogicRxPacket readPacket() throws IOException {
		byte[] ans = sendCommand(MeshLogicCommand.READ_PACKET, null);
		if (ans.length == 0)
			return null;
		byte[] addrBytes = new byte[2];
		addrBytes[0] = ans[1];
		addrBytes[1] = ans[0];
		byte[] mesBytes = new byte[ans.length - 3];
		System.arraycopy(ans, 3, mesBytes, 0, mesBytes.length);
		return new MeshLogicRxPacket(mesBytes, getHexFromBytes(addrBytes),
				ans[2]);
	}

	public String getNetworkId() throws IOException {
		byte[] ans = sendCommand(MeshLogicCommand.NETWORK_ID, null);
		byte[] networkIdBytes = new byte[2];
		System.arraycopy(ans, 0, networkIdBytes, 0, networkIdBytes.length);
		return getHexFromBytes(networkIdBytes);
	}

	public String getAddr() throws IOException {
		byte[] ans = sendCommand(MeshLogicCommand.NETWORK_ID, null);
		byte[] addrBytes = new byte[2];
		System.arraycopy(ans, 2, addrBytes, 0, addrBytes.length);
		return getHexFromBytes(addrBytes);
	}

	public void setAddr(final String newAddr) throws IOException {
		setNetworkParams(getNetworkId(), newAddr);
	}

	public void setNetworkId(final String newId) throws IOException {
		setNetworkParams(newId, getAddr());
	}

	public void setNetworkParams(final String newId, final String newAddr) {
		if (newId.length() != 4 || newAddr.length() != 4)
			throw new IllegalArgumentException();
		byte[] params = new byte[4];
		System.arraycopy(getBytesFromHex(newId), 0, params, 0, 2);
		System.arraycopy(getBytesFromHex(newAddr), 0, params, 2, 2);
	}

	public int getLinkPeriod() throws IOException {
		byte[] ans = sendCommand(MeshLogicCommand.TRANSMITION_PARAMS, null);
		byte[] linkPeriodBytes = new byte[4];
		linkPeriodBytes[2] = ans[1];
		linkPeriodBytes[3] = ans[0];
		return ByteBuffer.wrap(linkPeriodBytes).getInt();
	}

	public int getRoutePeriod() throws IOException {
		byte[] ans = sendCommand(MeshLogicCommand.TRANSMITION_PARAMS, null);
		byte[] routePeriodBytes = new byte[4];
		routePeriodBytes[2] = ans[1];
		routePeriodBytes[3] = ans[0];
		return ByteBuffer.wrap(routePeriodBytes).getInt();
	}

	public int getBaseStationNumber() throws IOException {
		return sendCommand(MeshLogicCommand.BASE_STATION, null)[0];
	}

	public int getBaseStationPriority() throws IOException {
		return sendCommand(MeshLogicCommand.BASE_STATION, null)[0];
	}

	public void setBaseStationParams(final int newNumber, final int newPriority)
			throws IOException {
		sendCommand(MeshLogicCommand.BASE_STATION, new byte[] {
				(byte) newNumber, (byte) newPriority });
	}

	public void setBaseStationNumber(final int newNumber) throws IOException {
		setBaseStationParams(newNumber, getBaseStationPriority());
	}

	public void setBaseStationPriority(final int newPriority)
			throws IOException {
		setBaseStationParams(getBaseStationNumber(), newPriority);
	}

	public void setLinkPeriod(final int newLinkPeriod) throws IOException {
		setTranmissionParams(newLinkPeriod, getRoutePeriod());
	}

	public void setRoutePeriod(final int newRoutePeriod) throws IOException {
		setTranmissionParams(getLinkPeriod(), newRoutePeriod);
	}

	public void setTranmissionParams(final int newLinkPeriod,
			final int newRoutePeriod) throws IOException {
		byte[] linkPeriodBytes = ByteBuffer.allocate(4).putInt(newLinkPeriod)
				.array();
		byte[] routePeriodBytes = ByteBuffer.allocate(4).putInt(newRoutePeriod)
				.array();
		byte[] params = new byte[] { linkPeriodBytes[3], linkPeriodBytes[2],
				routePeriodBytes[3], routePeriodBytes[2] };
		sendCommand(MeshLogicCommand.TRANSMITION_PARAMS, params);
	}

	public void save() throws IOException {
		sendCommand(MeshLogicCommand.SAVE, null);
	}

	private byte[] sendCommand(final MeshLogicCommand cmd, final byte[] args)
			throws IOException {
		if (serial != null && is != null && os != null) {

			byte[] msg;
			if (args != null) {
				msg = new byte[1 + args.length];
				msg[0] = cmd.getValue();
				System.arraycopy(args, 0, msg, 1, args.length);
			} else {
				msg = new byte[] { cmd.getValue() };
			}
			while (!serial.isCTS()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			os.write(COBSCodec.encode(msg));
			os.write(new byte[] { 0 });
			byte[] buf = new byte[256];
			int read;
			int i = 0;
			do {
				read = is.read();
				if (read < 0)
					throw new IOException("Unexpected EOF in Com port");
				buf[i] = (byte) read;
				i++;
			} while (read != 0);
			ArrayByteList decoded = new ArrayByteList();
			COBSCodec.decode(buf, 0, i - 1, decoded);
			if (decoded.size() > 0
					&& (decoded.get(0) == cmd.getValue() || (cmd.getValue() == MeshLogicCommand.READ_PACKET
							.getValue() && decoded.get(0) == (byte) 0x8B))) {
				byte[] ret = new byte[decoded.size() - 1];
				System.arraycopy(decoded.asArray(), 1, ret, 0, ret.length);
				validateReturnSize(cmd, ret.length);
				return ret;
			} else if (decoded.size() == 1 && decoded.get(0) == (byte) 0x80)
				throw new IOException("MeshLogic error");
			else if (decoded.size() > 0 && decoded.get(0) != cmd.getValue()) {
				throw new IOException(String.format(
						"Unexpected answer code 0x%02X", decoded.get(0)));
			} else
				throw new IOException("MeshLogic returned an empty string");
		} else {
			throw new IOException("Com port is not connected");
		}
	}

	public static String getHexFromBytes(final byte[] bytes) {
		final StringBuffer buf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			buf.append(String.format("%02X", bytes[i]));
		}
		return buf.toString();
	}

	public static byte[] getBytesFromHex(final String hex) {
		byte[] ret = new byte[(int) Math.floor(hex.length() / 2)];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2),
					16);
		}
		return ret;
	}

	private static void validateReturnSize(final MeshLogicCommand cmd,
			int length) throws IOException {
		switch (cmd) {
		case GET_SERIAL_NUMBER:
			if (length == 6)
				return;
			break;
		case READ_PACKET:
			if (length == 0 || length >= 3)
				return;
			break;
		case NETWORK_ID:
			if (length == 0 || length == 4)
				return;
			break;
		case TRANSMITION_PARAMS:
			if (length == 0 || length == 4)
				return;
			break;
		case BASE_STATION:
			if (length == 0 || length == 2)
				return;
			break;
		case SAVE:
			if (length == 0 || length == 2)
				return;
			break;
		default:
			break;
		}
		throw new IOException(String.format(
				"Unexected return size of command 0x%02X: %d", cmd.getValue(),
				length));
	}

}
