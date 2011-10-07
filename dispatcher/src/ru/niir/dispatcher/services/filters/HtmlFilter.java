package ru.niir.dispatcher.services.filters;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import ru.niir.dispatcher.events.DispatcherEvent;

public abstract class HtmlFilter {
	private final List<Element> elements;
	protected Element currentElement; 

	@SuppressWarnings("unchecked")
	public HtmlFilter(final Document doc, final String xpath) throws JDOMException {
		super();
		elements = XPath.selectNodes(doc, xpath);
	}
	
	public int getElementNumber() {
		return elements.size();
	}

	public boolean onEvent(DispatcherEvent _event) {
		boolean somethingChanged = false;
		for (Element element : elements) {
			currentElement = element;
			if(onEventForEach(_event)) somethingChanged = true;
		}
		return somethingChanged;
	}
	
	public abstract boolean onEventForEach(final DispatcherEvent _event);
	
	protected boolean switchClass(final String newClassValue) {
		if (newClassValue.equals(currentElement.getAttribute("class"))) return false;
		currentElement.setAttribute("class", newClassValue);
		return true;
	}
	
	protected String getNodeId() {
		return currentElement.getAttributeValue("nodeID");
	}
	
	protected String getId() {
		return currentElement.getAttributeValue("id");
	}
	
	protected String getCssClass() {
		return currentElement.getAttributeValue("class");
	}
	
	protected void setCssClass(final String value) {
		currentElement.setAttribute("class", value);
	}
	
	protected boolean switchVisible(final boolean visible) {
		String style = currentElement.getAttributeValue("style");
		if (style == null) style = "";
		final boolean alreadyVisible = !style.contains(";display:none;");
		if (visible == alreadyVisible) return false;
		else if (visible) {
			currentElement.setAttribute("style", style.replaceFirst(";display:none;", ""));
		} else {
			currentElement.setAttribute("style", style + ";display:none;");
		}
		return true;
	}
	
	protected boolean switchText(final String text) {
		if (text.equals(currentElement.getText())) return false;
		currentElement.setText(text);
		return true;
	}
}
