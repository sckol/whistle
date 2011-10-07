package ru.niir.dispatcher.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import ru.niir.dispatcher.events.DispatcherEvent;
import ru.niir.dispatcher.services.filters.CrossFilter;
import ru.niir.dispatcher.services.filters.HtmlFilter;
import ru.niir.dispatcher.services.filters.SensorCircleFilter;

public class SvgService extends HttpServlet implements DispatcherService {
	private final File file;
	private final Document doc;
	private final XMLOutputter outputter = new XMLOutputter();
	private final List<HtmlFilter> filters = new ArrayList<HtmlFilter>();

	public SvgService(final String fileName) throws JDOMException, IOException {
		super();
		this.file = new File(fileName);
		doc = new SAXBuilder().build(file);
		filters.add(new SensorCircleFilter(doc));
		filters.add(new CrossFilter(doc));
	}

	@Override
	public void onEvent(DispatcherEvent _event) {
		boolean somethingChanged = false;
		for (HtmlFilter filter : filters) {
			if (filter.onEvent(_event))
				somethingChanged = true;
		}
		// if (somethingChanged) writeFile();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("image/svg+xml");
		resp.setStatus(HttpServletResponse.SC_OK);
		outputter.output(doc, resp.getOutputStream());
	}

	private void writeFile() {
		try {
			outputter.output(doc, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
