package ru.secon;


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

	public byte[] placeOrder(Order order) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
