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

public class SnmpService implements DispatcherService {
	private final CommunityTarget target = new CommunityTarget();
	private final PDU pdu = new PDU();
	private final Snmp snmp;

	public SnmpService() throws IOException {
		super();
		snmp = new Snmp(new DefaultUdpTransportMapping());
		target.setCommunity(new OctetString("public"));
		target.setAddress(GenericAddress.parse("udp:192.168.1.101/161"));
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version1);
		pdu.setType(PDU.SET);
		pdu.add(new VariableBinding(
				new OID(
						"1.3.6.1.4.1.5166.2.1.130.10.1.2.25.97.112.112.67.111.110.116.114.111.108.83.116.97.114.116.68.86.66.67.104.97.110.110.101.108"),
				new OctetString("1")));
		pdu.setNonRepeaters(0);
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		if (_event instanceof StateChangedEvent) {
			try {
				final ResponseEvent resp = snmp.send(pdu, target);
				System.out.println(resp);
				System.out.println(resp.getPeerAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
