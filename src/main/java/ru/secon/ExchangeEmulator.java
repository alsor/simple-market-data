package ru.secon;

import static ru.secon.AsciiByteUtils.putInt;
import static ru.secon.AsciiByteUtils.putIntAsFloat;
import static ru.secon.ExchangeEmulator.Order.BUY;
import static ru.secon.ExchangeEmulator.Order.SELL;
import static ru.secon.ExchangeEmulator.Order.order;
import static ru.secon.ExchangeEmulator.Symbol.symbol;
import static ru.secon.Utils.doubleAsInt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class ExchangeEmulator {

	public static final byte NL = (byte) '\n';
	public static final int MAX_MSG_LENGTH = 100;
	public static final byte ORDER_ADDED = 'A';
	public static final int ID_LENGTH = 10;
	public static final int SYMBOL_LENGTH = 6;
	public static final int QTY_LENGTH = 6;
	public static final int FILE_BUFFER_SIZE = 1024 * 1024;
	public static final int SYMBOLS_SIZE = 100 * 1000;
	public static final int PRICE_POWERS = 7;
	public static final int MAX_QUANTITY = 100000;
	public static char[] letters = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'Z', 'Y',
			'Z' };

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
	
	public static void main(String[] args) throws IOException {
		generateSymbols();

		FileChannel channel = new FileOutputStream(new File("data.txt")).getChannel();
		ExchangeEmulator emulator = new ExchangeEmulator();
		ByteBuffer buf = ByteBuffer.allocate(FILE_BUFFER_SIZE);

		
		int msgToGenerate = 100000000;
		for (int msgCount = 0; msgCount < msgToGenerate; msgCount++) {
			emulator.placeOrder(buf, order.symbol(randomSymbol()).side(randomSide()).price(randomPrice())
					.qty(randomQty()));
			buf.put(NL);

			if (buf.remaining() < MAX_MSG_LENGTH + 1) {
				buf.flip();
				channel.write(buf);
				buf.clear();
			}
		}
		if (buf.position() > 0) {
			buf.flip();
			channel.write(buf);
		}
		channel.close();
	}

	private static int randomQty() {
		return rnd.nextInt(MAX_QUANTITY);
	}

	private static double randomPrice() {
		double price;
		while (true) {
			int power = rnd.nextInt(PRICE_POWERS);
			double base = rnd.nextDouble();
			price = base * power;
			if (price > 0.0) break;
		}
		return price;
	}

	private static byte randomSide() {
		if (rnd.nextFloat() > 0.5) {
			return SELL;
		}
		return BUY;
	}

	private static List<String> symbols;
	private static Random rnd = new Random();

	private static Symbol randomSymbol() {
		return symbol.fromString(symbols.get(rnd.nextInt(symbols.size())));
	}

	private static void generateSymbols() {
		Set<String> result = new HashSet<String>();
		
		int i = 0;
		while(i < SYMBOLS_SIZE) {
			StringBuffer sb = new StringBuffer(); 
			for (int j = 0; j < SYMBOL_LENGTH; j++) {
				char c = letters[rnd.nextInt(letters.length)];
				sb.append(c);
			}
			String symbol = sb.toString();
			if (!result.contains(symbol)) {
				result.add(symbol);
				i++;
			}
		}
		symbols = new ArrayList<String>(result);
	}
}
