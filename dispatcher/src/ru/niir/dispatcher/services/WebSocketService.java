package ru.niir.dispatcher.services;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocketServlet;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ExitEvent;
import ru.niir.dispatcher.events.Jsonable;

@SuppressWarnings("serial")
public class WebSocketService extends WebSocketServlet implements
		DispatcherService {

	private final Set<Connection> members = new CopyOnWriteArraySet<WebSocket.Connection>();

	@Override
	public WebSocket doWebSocketConnect(final HttpServletRequest request,
			final String protocol) {
		return new Notifier();
	}

	@Override
	public void onEvent(final DispatcherEvent _event) {
		for (Connection connection : members) {
			if (_event instanceof Jsonable) {
				try {
					connection.sendMessage(((Jsonable) _event).toJson());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (_event instanceof ExitEvent) {
			this.destroy();
		}
	}

	private class Notifier implements WebSocket {
		private Connection connection;

		@Override
		public void onClose(final int closeCode, final String message) {
			members.remove(connection);
		}

		@Override
		public void onOpen(final Connection conn) {
			this.connection = conn;
			members.add(connection);
		}
	}
}
