package ru.niir.dispatcher.events;

public class ShopOrderEvent implements DispatcherEvent {
	private final double price;
	private final String position;
	private final int positionId;

	public ShopOrderEvent(final String position, final int positionId,
			final double price) {
		super();
		this.price = price;
		this.position = position;
		this.positionId = positionId;
	}

	public double getPrice() {
		return price;
	}

	public String getPosition() {
		return position;
	}

	public int getPositionId() {
		return positionId;
	}

	@Override
	public String toString() {
		return String.format("ShopOrderEvent(%s (%d), %.2f руб.)", position,
				positionId, price);
	}

	public String toJson() {
		return String.format("{\"type\": \"ShopOrderEvent\", "
				+ "\"position\": \"%s\", " + "\"positionId\": \"%d\", "
				+ "\"price\": \"%.2f руб.\"}", position, positionId, price);
	}
}
