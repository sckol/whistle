package ru.niir.dispatcher.events;

public class ShopOrderEvent implements DispatcherEvent {
	private final double price;
	private final String position;
	public ShopOrderEvent(double price, String position) {
		super();
		this.price = price;
		this.position = position;
	}
	public double getPrice() {
		return price;
	}
	public String getPosition() {
		return position;
	}
}
