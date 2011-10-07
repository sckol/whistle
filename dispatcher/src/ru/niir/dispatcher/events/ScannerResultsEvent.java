package ru.niir.dispatcher.events;

import java.util.HashMap;

import ru.niir.dispatcher.NodeType;

public class ScannerResultsEvent implements DispatcherEvent {
	private final HashMap<String, NodeType> scannerResults;

	public ScannerResultsEvent(HashMap<String, NodeType> scannerResults) {
		super();
		this.scannerResults = scannerResults;
	}

	public HashMap<String, NodeType> getScannerResults() {
		return scannerResults;
	}

	@Override
	public String toString() {
		return "ScannerResultEvent(" + scannerResults.toString() + ")";
	}
}
