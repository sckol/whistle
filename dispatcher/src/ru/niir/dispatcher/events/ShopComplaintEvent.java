package ru.niir.dispatcher.events;

public class ShopComplaintEvent implements DispatcherEvent, Jsonable {
	@Override
	public String toString() {
		return "ShopComplaintEvent";
	}

	@Override
	public String toJson() {
		return String.format("{\"type\": \"ShopComplaintEvent\"\"}");
	}	
}
