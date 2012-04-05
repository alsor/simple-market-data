package ru.secon;

import java.nio.ByteBuffer;


public class ExchangeEmulator {

	public static class Symbol {
		public static Symbol symbol = new Symbol();

		public Symbol fromString(String s) {
			return this;
		}
	}
	
	public static class Order {
		public static final byte SELL = 'S';
		public static final byte BUY = 'B';

		public static Order order = new Order();

		public Symbol symbol = new Symbol();
		public long id;
		public double price;
		public byte side;
		public int qty;

		public Order symbol(Symbol symbol) {
			this.symbol = symbol;
			return this;
		}

		public Order side(byte side) {
			this.side = side;
			return this;
		}

		public Order price(double price) {
			this.price = price;
			return this;
		}

		public Order qty(int qty) {
			this.qty = qty;
			return this;
		}
	}

	public static final int MAX_MSG_LENGTH = 100;


	private static final byte ORDER_ADDED = 'A';

	private static final int ID_LENGTH = 10;

	public static ByteBuffer msg = ByteBuffer.allocate(MAX_MSG_LENGTH);

	private int id;

	public void placeOrder(ByteBuffer buf, Order order) {
		buf.put(ORDER_ADDED);
		AsciiByteUtils.putInt(msg, id++, ID_LENGTH);
	}
	
	
}
