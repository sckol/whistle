package ru.niir.dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.smslib.GatewayException;
import org.smslib.InboundMessage;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;

import ru.niir.dispatcher.agents.ConsoleAgent;
import ru.niir.dispatcher.agents.ServerRequestAgent;
import ru.niir.dispatcher.agents.SmsInboundMessageAgent;
import ru.niir.dispatcher.agents.XBeeAgent;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.gui.Console;
import ru.niir.dispatcher.services.BluetoothService;
import ru.niir.dispatcher.services.DvbService;
import ru.niir.dispatcher.services.LoggerService;
import ru.niir.dispatcher.services.MeshLogicService;
import ru.niir.dispatcher.services.SmsService;
import ru.niir.dispatcher.services.SnmpService;
import ru.niir.dispatcher.services.StateBoardService;
import ru.niir.dispatcher.services.StateMonitorService;
import ru.niir.dispatcher.services.SvgService;
import ru.niir.dispatcher.services.TimerStateChangerService;
import ru.niir.dispatcher.services.WebSocketService;
import ru.niir.dispatcher.services.XBeeScannerService;
import ru.niir.dispatcher.services.XbeeResetterService;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeConfiguration;
import com.rapplogic.xbee.api.XBeeException;

public class DispatcherServer {
	public static void main(String[] args) throws Exception {
		System.out.println("Hello world");
		new Console();	
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		
        
        addLibraryPath(new File("lin32").getAbsolutePath());
        addLibraryPath(new File("lin64").getAbsolutePath());
        addLibraryPath(new File("win32").getAbsolutePath());
        addLibraryPath(new File("win64").getAbsolutePath());
		
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		 
        URL[] urls = ((URLClassLoader)cl).getURLs();
 
        for(URL url: urls){
        	System.out.println(url.getFile());
        }

        
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
		Service smslibService = null;
		context.setContextPath("/");
		if (conf.getProperty("Services.Logger").equals("enable")) {
			bus.addListener(new LoggerService());
		}
		if (conf.getProperty("Services.StateMonitor").equals("enable")) {
			bus.addListener(new StateMonitorService(bus));
		}
		if (conf.getProperty("Services.TimerStateChanger").equals("enable")) {
			bus.addListener(new TimerStateChangerService(bus));
		}
		if (conf.getProperty("Services.Svg").equals("enable")) {
			final SvgService svgService = new SvgService(bus,
					conf.getProperty("Svg.fileName"));
			bus.addListener(svgService);
			context.addServlet(new ServletHolder(svgService), "/plan.svg");
		}
		if (conf.getProperty("Services.StateBoard").equals("enable")) {
			final StateBoardService stateBoardService = new StateBoardService(
					bus, conf.getProperty("StateBoard.fileName"));
			bus.addListener(stateBoardService);
			context.addServlet(new ServletHolder(stateBoardService),
					"/stateBoard.html");
		}
		if (conf.getProperty("Services.WebSocket").equals("enable")) {
			final WebSocketService webSocketService = new WebSocketService();
			bus.addListener(webSocketService);
			context.addServlet(new ServletHolder(webSocketService),
					"/webSocket");
		}
		if (conf.getProperty("Services.XbeeScanner").equals("enable")) {
			if (xbee == null)
				xbee = getXbee(conf);
			final XBeeScannerService xbeeScannerService = new XBeeScannerService(
					bus, xbee, Integer.parseInt(conf
							.getProperty("XbeeScannerService.timeout")));
			bus.addListener(xbeeScannerService);
			final Timer scannerTimer = new Timer();
			scannerTimer.schedule(xbeeScannerService.getTimerTask(), 0, Long
					.parseLong(conf
							.getProperty("XbeeScannerService.scanFrequency")));
		}
		if (conf.getProperty("Services.XbeeResetter").equals("enable")) {
			if (xbee == null)
				xbee = getXbee(conf);
			bus.addListener(new XbeeResetterService(xbee));
		}
		if (conf.getProperty("Services.Sms").equals("enable")) {
			if (smslibService == null) {
				smslibService = getSmsService(conf);
			}
			final SmsService smsService = new SmsService(smslibService);
			smsService.addPhone(new Phone("+79851980192", 50045, 1, 2));
			bus.addListener(smsService);
		}
		if (conf.getProperty("Services.Snmp").equals("enable")) {
			bus.addListener(new SnmpService());
		}
		if (conf.getProperty("Services.Dvb").equals("enable")) {
			bus.addListener(new DvbService(conf.getProperty("Dvb.octavepath"),
					conf.getProperty("Dvb.octaweWorkingDir")));
		}
		if (conf.getProperty("Services.Bluetooth").equals("enable")) {
			bus.addListener(new BluetoothService(conf.getProperty("Bluetooth.comPort"),
					Integer.parseInt(conf.getProperty("Bluetooth.baudrate"))));
		}
		if (conf.getProperty("Services.MeshLogic").equals("enable")) {
			final MeshLogicService meshLogicService = new MeshLogicService(bus, conf.getProperty("MeshLogic.comPort"),
					Integer.parseInt(conf.getProperty("MeshLogic.baudrate")));
			new Thread(meshLogicService).start();
			bus.addListener(meshLogicService);
			
		}
		if (conf.getProperty("Agents.Console").equals("enable")) {
			new Thread(new ConsoleAgent(bus)).start();
		}
		if (conf.getProperty("Agents.ServerRequest").equals("enable")) {
			final ServerRequestAgent externalEmergencyAgent = new ServerRequestAgent(
					bus);
			context.addServlet(new ServletHolder(externalEmergencyAgent),
					"/cmd");
		}
		if (conf.getProperty("Agents.Xbee").equals("enable")) {
			if (xbee == null)
				xbee = getXbee(conf);
			new Thread(new XBeeAgent(bus, xbee)).start();
		}
		if (conf.getProperty("Agents.SmsInboundMessage").equals("enable")) {
			if (smslibService == null)
				smslibService = getSmsService(conf);
			smslibService
					.setInboundMessageNotification(new SmsInboundMessageAgent(
							bus, smslibService,
							conf.getProperty("SmsInboundMessageAgent.menuFile")));
		}
		handlerList.addHandler(context);
		handlerList.addHandler(resourceHandler);
		server.setHandler(handlerList);
		server.start();
		bus.fireEvent(new ResetEvent());
	}

	private static final XBee getXbee(final Properties conf)
			throws NumberFormatException, XBeeException {
		final XBee xbee = new XBee(
				new XBeeConfiguration().withStartupChecks(false));
		xbee.open(conf.getProperty("XBee.comPort"),
				Integer.parseInt(conf.getProperty("XBee.baudRate")));
		return xbee;
	}

	private static final Service getSmsService(final Properties conf)
			throws TimeoutException, SMSLibException, IOException,
			InterruptedException {
		final Service service = Service.getInstance();
		SerialModemGateway gateway1 = new SerialModemGateway("modem1",
				conf.getProperty("Sms.comPort"), Integer.parseInt(conf
						.getProperty("Sms.baudRate")), "Siemens", "MC35i");
		gateway1.setOutbound(true);
		gateway1.setInbound(true);
		service.addGateway(gateway1);
		service.startService();
		deleteAllSmsMessages(service);
		return service;
	}

	private static final void deleteAllSmsMessages(final Service service)
			throws TimeoutException, GatewayException, IOException,
			InterruptedException {
		final Set<InboundMessage> msgs = new HashSet<InboundMessage>();
		service.readMessages(msgs, MessageClasses.ALL);
		for (InboundMessage msg : msgs) {
			service.deleteMessage(msg);
		}
	}
	
	/**
	* Adds the specified path to the java library path
	*
	* @param pathToAdd the path to add
	* @throws Exception
	*/
	public static void addLibraryPath(String pathToAdd) throws Exception{
	    final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
	    usrPathsField.setAccessible(true);
	 
	    //get array of paths
	    final String[] paths = (String[])usrPathsField.get(null);
	 
	    //check if the path to add is already present
	    for(String path : paths) {
	        if(path.equals(pathToAdd)) {
	            return;
	        }
	    }
	 
	    //add the new path
	    final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
	    newPaths[newPaths.length-1] = pathToAdd;
	    usrPathsField.set(null, newPaths);
	}
}
