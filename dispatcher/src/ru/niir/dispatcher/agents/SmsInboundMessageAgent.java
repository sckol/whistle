package ru.niir.dispatcher.agents;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.smslib.AGateway;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Message.MessageTypes;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SmsInboundMessageAgent implements IInboundMessageNotification {
	private final static String SHOP_ORDER = "ShopOrder:";	
	
	private final String menufile;
	
	public SmsInboundMessageAgent(final String menufile) {
		super();
		this.menufile = menufile;
	}
	@Override
	public void process(final AGateway gateway, final MessageTypes msgType,
			final InboundMessage msg) {
		final String text= msg.getText().trim();
		if (text.startsWith(SHOP_ORDER)) {
			try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document doc = builder.parse(new File(menufile));
			final XPathFactory xPathfactory = XPathFactory.newInstance();
			final XPath xpath = xPathfactory.newXPath();
			final XPathExpression expr = xpath.compile("//Type[@type_id=\"4218\"]");
			final NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			System.out.println(nl.item(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
