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

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeConfiguration;

public class DispatcherServer {
	public static void main(String[] args) throws Exception {
		final Properties conf = new Properties();
		conf.load(new FileInputStream("dispatcher.conf"));
		final EventBus bus = new EventBus();
		bus.addListener(new LoggerService());
		bus.addListener(new StateMonitorService(bus));
		new Thread(new ConsoleAgent(bus)).start();
		final SvgService svgService = new SvgService(bus,
				conf.getProperty("Svg.fileName"));
		bus.addListener(svgService);
		final StateBoardService stateBoardService = new StateBoardService(bus,
				conf.getProperty("StateBoard.fileName"));
		final WebSocketService webSocketService = new WebSocketService();
		bus.addListener(stateBoardService);
		bus.addListener(webSocketService);
		final Server server = new Server(Integer.valueOf(conf
				.getProperty("Jetty.port")));
		final HandlerList handlerList = new HandlerList();
		final ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase(".");
		final ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(svgService), "/plan.svg");
		context.addServlet(new ServletHolder(stateBoardService),
				"/stateBoard.html");
		context.addServlet(new ServletHolder(new ExternalEmergencyAgent(bus)),
				"/declareGasAttack");
		context.addServlet(new ServletHolder(webSocketService), "/webSocket");
		handlerList.addHandler(context);
		handlerList.addHandler(resourceHandler);
		server.setHandler(handlerList);
		server.start();
		final XBee xbee = new XBee(
				new XBeeConfiguration().withStartupChecks(false));
		xbee.open(conf.getProperty("XBee.comPort"),
				Integer.parseInt(conf.getProperty("XBee.baudRate")));
		new Thread(new XBeeAgent(bus, xbee)).start();
		final XBeeScannerService xbeeScannerService = new XBeeScannerService(
				bus, xbee, Integer.parseInt(conf
						.getProperty("XbeeScannerService.timeout")));
		bus.addListener(xbeeScannerService);
		final Timer scannerTimer = new Timer();
		scannerTimer.schedule(xbeeScannerService.getTimerTask(), 0,
				Long.parseLong(conf
						.getProperty("XbeeScannerService.scanFrequency")));
		final Service service = Service.getInstance();
		SerialModemGateway gateway = new SerialModemGateway("modem", "COM1",
				9600, "Siemens", "MC35i");
		gateway.setOutbound(true);
		service.addGateway(gateway);
		service.startService();
		System.out.println("helllloooo");
		final SmsService smsService = new SmsService(service);
		smsService.addPhone(new Phone("+79851980192", 50042, 1, 0));
		bus.addListener(smsService);
		bus.fireEvent(new ResetEvent());
	}
}
