package ru.niir.dispatcher.services;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocketServlet;

import ru.niir.dispatcher.events.ContentChangedEvent;
import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.events.ShopSubmittedEvent;

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
		if (_event instanceof ContentChangedEvent || _event instanceof ShopSubmittedEvent) {
			for (Connection connection : members) {
				try {
					connection.sendMessage(_event.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
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
