package ru.secon.plain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Order {

	public String symbol;
	public int id;
	public Side side;
	public int price;
	public int qty;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Order) {
			Order other = (Order) obj;
			return new EqualsBuilder().append(symbol, other.symbol).append(id, other.id)
					.append(side, other.side).append(price, other.price).append(qty, other.qty).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name)
	}
}
