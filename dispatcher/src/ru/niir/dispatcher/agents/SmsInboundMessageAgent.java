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
import org.smslib.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.events.ShopComplaintEvent;
import ru.niir.dispatcher.events.ShopOrderEvent;

public class SmsInboundMessageAgent implements IInboundMessageNotification {
	private final static String SHOP_ORDER = "order";
	private final static String SHOP_COMPLAINT = "complaint";

	private final Service smslibService;
	private final EventBus bus;
	private final String menufile;

	public SmsInboundMessageAgent(final EventBus bus,
			final Service smslibService, final String menufile) {
		super();
		this.bus = bus;
		this.smslibService = smslibService;
		this.menufile = menufile;
	}

	@Override
	public void process(final AGateway gateway, final MessageTypes msgType,
			final InboundMessage msg) {
		final String text = msg.getText().trim();
		System.out.println("Received SMS");
		if (text.startsWith(SHOP_ORDER)) {
			try {
				final int index = Integer.parseInt(text.split("\\s+")[1]);
				final DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				final DocumentBuilder builder = factory.newDocumentBuilder();
				final Document doc = builder.parse(new File(menufile));
				final XPathFactory xPathfactory = XPathFactory.newInstance();
				final XPath xpath = xPathfactory.newXPath();
				final XPathExpression expr = xpath
						.compile("//product[@index=\"" + index + "\"][1]");
				final Element el = (Element) expr.evaluate(doc,
						XPathConstants.NODE);
				bus.fireEvent(new ShopOrderEvent(el.getAttribute("shortName"),
						index, Double.parseDouble(el.getAttribute("price"))));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					smslibService.deleteMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (text.startsWith(SHOP_COMPLAINT)) {
			bus.fireEvent(new ShopComplaintEvent());
		}
	}
}
