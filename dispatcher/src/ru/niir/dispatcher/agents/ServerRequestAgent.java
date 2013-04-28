package ru.niir.dispatcher.agents;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.niir.dispatcher.EventBus;
import ru.niir.dispatcher.events.ResetEvent;
import ru.niir.dispatcher.events.ShopSubmittedEvent;
import ru.niir.dispatcher.events.StateChangedEvent;
import ru.niir.dispatcher.events.StateChangedEvent.EmergencyType;

@SuppressWarnings("serial")
public class ServerRequestAgent extends HttpServlet {
	private final EventBus eventBus;

	public ServerRequestAgent(final EventBus eventBus) {
		super();
		this.eventBus = eventBus;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		final String cmd = req.getParameter("cmd");
		if (cmd != null)
		if (cmd.equals("gasAttack")) {
			eventBus.fireEvent(new StateChangedEvent(1, 0, EmergencyType.GAS_ATTACK, "Gas atack"));
		} else if (cmd.equals("reset")) {
			eventBus.fireEvent(new ResetEvent());
		} else if (cmd.equals("submitShop")) {
			eventBus.fireEvent(new ShopSubmittedEvent());
		}
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/html");
		resp.getWriter().write("Done");
	}	
}
