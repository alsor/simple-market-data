package ru.secon;

import static ru.secon.AsciiByteUtils.putInt;
import static ru.secon.AsciiByteUtils.putIntAsFloat;
import static ru.secon.Utils.doubleAsInt;

import java.nio.ByteBuffer;


public class ExchangeEmulator {

	public static final int MAX_MSG_LENGTH = 100;
	public static final byte ORDER_ADDED = 'A';
	public static final int ID_LENGTH = 10;
	public static final int SYMBOL_LENGTH = 6;
	private static final int QTY_LENGTH = 6;

	private int id = 1;

	public static class Symbol {
		public static Symbol symbol = new Symbol();
		public byte[] bytes = new byte[SYMBOL_LENGTH];

		public Symbol fromString(String s) {
			System.arraycopy(s.getBytes(), 0, bytes, 0, SYMBOL_LENGTH);
			return this;
		}
	}
	
	public static class Order {
		public static final byte SELL = 'S';
		public static final byte BUY = 'B';

		public static Order order = new Order();

		public Symbol symbol = new Symbol();
		public long id;
		public int price;
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
			this.price = doubleAsInt(price);
			return this;
		}

		public Order qty(int qty) {
			this.qty = qty;
			return this;
		}
	}

	public void placeOrder(ByteBuffer buf, Order order) {
		buf.put(ORDER_ADDED);
		putInt(buf, id++, ID_LENGTH);
		buf.put(order.symbol.bytes, 0, SYMBOL_LENGTH);
		buf.put(order.side);
		putIntAsFloat(buf, order.price);
		putInt(buf, order.qty, QTY_LENGTH);
	}
	
	
}
