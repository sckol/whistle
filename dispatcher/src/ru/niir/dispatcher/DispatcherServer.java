package ru.niir.dispatcher;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Timer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

import ru.niir.dispatcher.agents.ConsoleAgent;
import ru.niir.dispatcher.agents.ExternalEmergencyAgent;
import ru.niir.dispatcher.agents.XBeeAgent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.services.LoggerService;
import ru.niir.dispatcher.services.SmsService;
import ru.niir.dispatcher.services.SnmpService;
import ru.niir.dispatcher.services.StateBoardService;
import ru.niir.dispatcher.services.StateMonitorService;
import ru.niir.dispatcher.services.SvgService;
import ru.niir.dispatcher.services.WebSocketService;
import ru.niir.dispatcher.services.XBeeScannerService;
import ru.niir.dispatcher.services.XbeeResetterService;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeConfiguration;
import com.rapplogic.xbee.api.XBeeException;

public class DispatcherServer {
	public static void main(String[] args) throws Exception {
		final Properties conf = new Properties();
		conf.load(new FileInputStream("dispatcher.conf"));
		final EventBus bus = new EventBus();
		final Server server = new Server(Integer.valueOf(conf
				.getProperty("Jetty.port")));
		final HandlerList handlerList = new HandlerList();
		final ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase(".");
		final ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		XBee xbee = null;
		context.setContextPath("/");
		
		if (conf.getProperty("Services.Logger").equals("enable")) {
			bus.addListener(new LoggerService());	
		}
		if (conf.getProperty("Services.StateMonitor").equals("enable")) {
			bus.addListener(new StateMonitorService(bus));	
		}
		if (conf.getProperty("Services.Svg").equals("enable")) {
			final SvgService svgService = new SvgService(bus,
					conf.getProperty("Svg.fileName"));
			bus.addListener(svgService);
			context.addServlet(new ServletHolder(svgService), "/plan.svg");
		}
		if (conf.getProperty("Services.StateBoard").equals("enable")) {
			final StateBoardService stateBoardService = new StateBoardService(bus,
					conf.getProperty("StateBoard.fileName"));
			bus.addListener(stateBoardService);
			context.addServlet(new ServletHolder(stateBoardService),
					"/stateBoard.html");
		}
		if (conf.getProperty("Services.WebSocket").equals("enable")) {
			final WebSocketService webSocketService = new WebSocketService();
			bus.addListener(webSocketService);
			context.addServlet(new ServletHolder(webSocketService), "/webSocket");
		}
		if (conf.getProperty("Services.XbeeScanner").equals("enable")) {
			if (xbee == null) xbee = getXbee(conf);
			final XBeeScannerService xbeeScannerService = new XBeeScannerService(
					bus, xbee, Integer.parseInt(conf
							.getProperty("XbeeScannerService.timeout")));
			bus.addListener(xbeeScannerService);
			final Timer scannerTimer = new Timer();
			scannerTimer.schedule(xbeeScannerService.getTimerTask(), 0,
					Long.parseLong(conf
							.getProperty("XbeeScannerService.scanFrequency")));
		}
		if (conf.getProperty("Services.XbeeResetter").equals("enable")) {
			if (xbee == null) xbee = getXbee(conf);
			bus.addListener(new XbeeResetterService(xbee));
		}
		if (conf.getProperty("Services.Sms").equals("enable")) {
			final Service service = Service.getInstance();
			SerialModemGateway gateway1 = new SerialModemGateway("modem1", "COM58",
					9600, "Siemens", "MC35i");
			gateway1.setOutbound(true);
			service.addGateway(gateway1);
			service.startService();
			final SmsService smsService = new SmsService(service);
			smsService.addPhone(new Phone("+79851980192", 50045, 1, 2));
			bus.addListener(smsService);
		}
		if (conf.getProperty("Services.Snmp").equals("enable")) {
			bus.addListener(new SnmpService());
		}
		

		if (conf.getProperty("Agents.Console").equals("enable")) {
			new Thread(new ConsoleAgent(bus)).start();
		}
		if (conf.getProperty("Agents.ExternalEmergency").equals("enable")) {
			final ExternalEmergencyAgent externalEmergencyAgent = new ExternalEmergencyAgent(
					bus);
			context.addServlet(new ServletHolder(externalEmergencyAgent),
					"/declareGasAttack");
			context.addServlet(new ServletHolder(externalEmergencyAgent), "/reset");
		}
		if (conf.getProperty("Agents.Xbee").equals("enable")) {
			if (xbee == null) xbee = getXbee(conf);
			new Thread(new XBeeAgent(bus, xbee)).start();
		}
		
		handlerList.addHandler(context);
		handlerList.addHandler(resourceHandler);
		server.setHandler(handlerList);
		server.start();
		
		bus.fireEvent(new ResetEvent());
	}
	
	private static final XBee getXbee(final Properties conf) throws NumberFormatException, XBeeException {
		final XBee xbee = new XBee(new XBeeConfiguration().withStartupChecks(false));
		xbee.open(conf.getProperty("XBee.comPort"),
				Integer.parseInt(conf.getProperty("XBee.baudRate")));
		return xbee;
	}
}
