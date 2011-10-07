package ru.niir.protowhistle.io;

import javax.bluetooth.*;

import java.io.IOException;
import java.util.Vector;

public class BeaconDiscoverer {
	private static final UUID[] RFCOMM_UUID = new UUID[] { new UUID(0x0003) };
	private static final int STEP_TIME = 300;
	private final DiscoveryAgent discoveryAgent;
	private Vector deviceList = new Vector();
	private final BeaconDiscovererListener discovererListener = new BeaconDiscovererListener();

	private BeaconDiscoverer(final DiscoveryAgent discoveryAgent) {
		this.discoveryAgent = discoveryAgent;
	}

	public static BeaconDiscoverer createBeaconDiscoverer()
			throws BluetoothStateException {
		return new BeaconDiscoverer(LocalDevice.getLocalDevice()
				.getDiscoveryAgent());
	}

	public void refreshDeviceList(final int delay)
			throws BluetoothStateException {
		deviceList = new Vector();
		discovererListener.resetFinished();
		final InquiryRunnable inquiryRunnable = new InquiryRunnable();
		new Thread(inquiryRunnable).start();
		int delayLeft = delay;
		while (!discovererListener.isFinished()) {
			if (inquiryRunnable.getException() != null)
				throw inquiryRunnable.getException();
			if (delayLeft > 0) {
				delayLeft -= STEP_TIME;
				try {
					Thread.sleep(STEP_TIME);
				} catch (InterruptedException e) {
				}
			} else {
				discoveryAgent.cancelInquiry(discovererListener);
				break;
			}
		}
	}

	public String getUrl(final int index) throws BluetoothStateException {
		discovererListener.resetFinished();
		final int transId = discoveryAgent.searchServices(null, RFCOMM_UUID,
				(RemoteDevice) deviceList.elementAt(index), discovererListener);
		int delayLeft = 10000;
		while (!discovererListener.isFinished()) {
			if (delayLeft > 0) {
				delayLeft -= STEP_TIME;
				try {
					Thread.sleep(STEP_TIME);
				} catch (InterruptedException e) {
				}
			} else {
				discoveryAgent.cancelServiceSearch(transId);
				break;
			}
		}
		return discovererListener.getUrl();
	}

	public String[] getDeviceNames() {
		final String[] ret = new String[deviceList.size()];
		for (int i = 0; i < ret.length; i++) {
			final RemoteDevice rd = (RemoteDevice) deviceList.elementAt(i);
			try {
				ret[i] = rd.getFriendlyName(false)
						+ " ("
						+ rd.getBluetoothAddress().substring(
								rd.getBluetoothAddress().length() - 4) + ")";
			} catch (IOException e) {
				ret[i] = "<Unknown>";
			}
		}
		return ret;
	}

	private class BeaconDiscovererListener implements DiscoveryListener {
		private boolean finished;
		private String url;

		public void deviceDiscovered(final RemoteDevice remoteDevice,
				final DeviceClass deviceClass) {
			deviceList.addElement(remoteDevice);
		}

		public void inquiryCompleted(final int i) {
			finished = true;
		}

		public void servicesDiscovered(final int transID,
				final ServiceRecord[] records) {
			for (int i = 0; i < records.length; i++) {
				final String foundUrl = records[i].getConnectionURL(
						ServiceRecord.AUTHENTICATE_ENCRYPT, false);
				if (foundUrl.startsWith("btspp")) {
					url = foundUrl;
					discoveryAgent.cancelServiceSearch(transID);
					break;
				}
			}
		}

		public void serviceSearchCompleted(final int i, final int i1) {
			finished = true;
		}

		public void resetFinished() {
			url = null;
			finished = false;
		}

		public boolean isFinished() {
			return finished;
		}

		public String getUrl() {
			return url;
		}
	}

	private class InquiryRunnable implements Runnable {
		private BluetoothStateException exception;

		public void run() {
			exception = null;
			try {
				discoveryAgent.startInquiry(DiscoveryAgent.GIAC,
						discovererListener);
			} catch (BluetoothStateException e) {
				exception = e;
			}
		}

		public BluetoothStateException getException() {
			return exception;
		}
	}
}
