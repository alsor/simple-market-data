package ru.secon;

import static ru.secon.AsciiByteUtils.putInt;
import static ru.secon.AsciiByteUtils.putIntAsFloat;
import static ru.secon.ExchangeEmulator.Order.BUY;
import static ru.secon.ExchangeEmulator.Order.SELL;
import static ru.secon.ExchangeEmulator.Order.order;
import static ru.secon.ExchangeEmulator.Symbol.symbol;
import static ru.secon.Utils.doubleAsInt;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntHeapPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;

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

import ru.undev.FixedByteSlice2ObjectOpenHashMap;


public class ExchangeEmulator {

	public static final byte NL = (byte) '\n';
	public static final int MAX_MSG_LENGTH = 100;
	public static final byte ORDER_ADDED = 'A';
	public static final byte ORDER_EXECUTED = 'E';
	public static final int ID_LENGTH = 10;
	public static final int SYMBOL_LENGTH = 6;
	public static final int QTY_LENGTH = 6;
	public static final int FILE_BUFFER_SIZE = 1024 * 1024;
	public static final int SYMBOLS_SIZE = 150 * 1000;
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
		int orderId = id++;

		orderId2price.put(orderId, order.price);

		if (SELL == order.side) {
			IntPriorityQueue sellOrderIds = symbol2sell.get(order.symbol.bytes);
			if (sellOrderIds == null) {
				sellOrderIds = new IntHeapPriorityQueue(sellComparator);
				symbol2sell.put(order.symbol.bytes, sellOrderIds);
			}
			sellOrderIds.enqueue(orderId);
		} else {
			IntPriorityQueue buyOrderIds = symbol2buy.get(order.symbol.bytes);
			if (buyOrderIds == null) {
				buyOrderIds = new IntHeapPriorityQueue(buyComparator);
				symbol2buy.put(order.symbol.bytes, buyOrderIds);
			}
			buyOrderIds.enqueue(orderId);
		}

		buf.put(ORDER_ADDED);
		putInt(buf, orderId, ID_LENGTH);
		buf.put(order.symbol.bytes, 0, SYMBOL_LENGTH);
		buf.put(order.side);
		putIntAsFloat(buf, order.price);
		putInt(buf, order.qty, QTY_LENGTH);
	}

	FixedByteSlice2ObjectOpenHashMap<IntPriorityQueue> symbol2sell =
			new FixedByteSlice2ObjectOpenHashMap<IntPriorityQueue>(SYMBOL_LENGTH);

	FixedByteSlice2ObjectOpenHashMap<IntPriorityQueue> symbol2buy =
			new FixedByteSlice2ObjectOpenHashMap<IntPriorityQueue>(SYMBOL_LENGTH);

	Int2IntMap orderId2price = new Int2IntOpenHashMap();

	public boolean executeOrder(ByteBuffer buf, Symbol symbol, byte side) {

		int orderId;

		if (SELL == side) {
			IntPriorityQueue sellOrderIds = symbol2sell.get(symbol.bytes);
			if (sellOrderIds == null || sellOrderIds.isEmpty()) return false;
			orderId = sellOrderIds.dequeueInt();
		} else {
			IntPriorityQueue buyOrderIds = symbol2buy.get(symbol.bytes);
			if (buyOrderIds == null || buyOrderIds.isEmpty()) return false;
			orderId = buyOrderIds.dequeueInt();
		}

		orderId2price.remove(orderId);

		buf.put(ORDER_EXECUTED);
		putInt(buf, orderId, ID_LENGTH);
		return true;
	}

	public static void main(String[] args) throws IOException {
		generateSymbols();

		FileChannel channel = new FileOutputStream(new File("data.txt")).getChannel();
		ExchangeEmulator emulator = new ExchangeEmulator();
		ByteBuffer buf = ByteBuffer.allocate(FILE_BUFFER_SIZE);


		int msgToGenerate = 30 * 1000 * 1000;
		for (int msgCount = 0; msgCount < msgToGenerate; msgCount++) {

			if (rnd.nextFloat() < 0.7) {

				emulator.placeOrder(buf, order.symbol(randomSymbol()).side(randomSide()).price(randomPrice())
						.qty(randomQty()));
				buf.put(NL);

			} else {
				if (emulator.executeOrder(buf, randomSymbol(), randomSide())) {
					buf.put(NL);
				}
			}

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

	private IntComparator sellComparator = new IntComparator() {

		public int compare(Integer o1, Integer o2) {
			return compare(o1.intValue(), o2.intValue());
		}

		public int compare(int id1, int id2) {
			int price1 = orderId2price.get(id1);
			int price2 = orderId2price.get(id2);

			return (price1 < price2 ? -1 : (price1 == price2 ? 0 : 1));
		}
	};

	private IntComparator buyComparator = new IntComparator() {

		public int compare(Integer o1, Integer o2) {
			return compare(o1.intValue(), o2.intValue());
		}

		public int compare(int id1, int id2) {
			int price1 = orderId2price.get(id1);
			int price2 = orderId2price.get(id2);

			return (price1 > price2 ? -1 : (price1 == price2 ? 0 : 1));
		}
	};

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
