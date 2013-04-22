package ru.niir.dispatcher.services;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.StateChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent.EmergencyType;

public class SnmpService implements DispatcherService {
	private final CommunityTarget target = new CommunityTarget();
	private final PDU pdu = new PDU();
	private final Snmp snmp;
	private final VariableBinding binding = new VariableBinding(
			new OID(
					".1.3.6.1.4.1.5166.2.1.130.10.1.2.25.97.112.112.67.111.110.116.114.111.108.83.116.97.114.116.68.86.66.67.104.97.110.110.101.108"));

	public SnmpService() throws IOException {
		super();
		snmp = new Snmp(new DefaultUdpTransportMapping());
		target.setCommunity(new OctetString("public"));
		target.setAddress(GenericAddress.parse("udp:194.54.133.171/161"));
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version1);
		pdu.setType(PDU.SET);
		pdu.add(binding);
		pdu.setNonRepeaters(0);
	}

	@Override
	public void onEvent(final DispatcherEvent _event) {
		if (_event instanceof StateChangedEvent) {
			final StateChangedEvent event = (StateChangedEvent) _event;
			binding.setVariable(new OctetString(emergencyTypeToChannel(event
					.getType())));
			new Thread(new Runnable() {
				public void run() {
					try {
						snmp.send(pdu, target);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private static final String emergencyTypeToChannel(final EmergencyType type) {
		switch (type) {
		case FIRE:
			return "1";
		case GAS_ATTACK:
			return "2";
		}
		return "";
	}
}
