package ru.secon.plain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Update {

	public String symbol;
	public int sellPrice;
	public int sellQty;
	public int buyPrice;
	public int buyQty;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Update) {
			Update other = (Update) obj;
			return new EqualsBuilder().append(symbol, other.symbol).append(sellPrice, other.sellPrice)
					.append(sellQty, other.sellQty).append(buyPrice, other.buyPrice)
					.append(buyQty, other.buyQty).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(symbol).append(sellPrice).append(sellQty).append(buyPrice)
				.append(buyQty).toHashCode();
	}
}
