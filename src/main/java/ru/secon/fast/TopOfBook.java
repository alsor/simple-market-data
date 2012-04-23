package ru.secon.fast;

import static ru.secon.Constants.SELL;
import static ru.secon.Constants.SYMBOL_LENGTH;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;

import java.nio.ByteBuffer;

import ru.undev.FixedByteSlice2IntOpenHashMap;
import ru.undev.FixedByteSlice2ObjectOpenHashMap;
import ru.undev.Int2FixedByteSliceOpenHashMap;

public class TopOfBook {

	public static final int INITIAL_SIZE = 10 * 1000;
	private final UpdateListener updateListener;

	public TopOfBook(UpdateListener updateListener) {
		this.updateListener = updateListener;
	}

	private FixedByteSlice2IntOpenHashMap symbol2top = new FixedByteSlice2IntOpenHashMap(
			SYMBOL_LENGTH);

	private FixedByteSlice2ObjectOpenHashMap<Int2IntSortedMap> symbol2sell =
			new FixedByteSlice2ObjectOpenHashMap<Int2IntSortedMap>(SYMBOL_LENGTH);
	private FixedByteSlice2ObjectOpenHashMap<Int2IntSortedMap> symbol2buy =
			new FixedByteSlice2ObjectOpenHashMap<Int2IntSortedMap>(SYMBOL_LENGTH);
	
	private Int2ByteMap orderId2side = new Int2ByteOpenHashMap();
	private Int2IntMap orderId2price = new Int2IntOpenHashMap();
	private Int2IntMap orderId2qty = new Int2IntOpenHashMap();
	private Int2FixedByteSliceOpenHashMap orderId2symbol = new Int2FixedByteSliceOpenHashMap(SYMBOL_LENGTH);

	private int[] tops = new int[INITIAL_SIZE * 4];
	private int nextPos = 0;
	
	public void onAddOrder(ByteBuffer src, int orderId, int symbolOffset, byte side, int price, int qty) {

		orderId2side.put(orderId, side);
		orderId2price.put(orderId, price);
		orderId2qty.put(orderId, qty);
		orderId2symbol.put(orderId, src, symbolOffset);
		
		if (SELL == side) {
			Int2IntSortedMap prices = symbol2sell.get(src, symbolOffset);
			if (prices == null) {
				prices = new Int2IntLinkedOpenHashMap();
				prices.put(price, qty);
				symbol2sell.put(src, symbolOffset, prices);
			} else {
				int priceQty = prices.get(price);
				if (priceQty == prices.defaultReturnValue()) {
					prices.put(price, qty);
				} else {
					prices.put(price, priceQty + qty);
				}
			}
		} else {
			Int2IntSortedMap prices = symbol2buy.get(src, symbolOffset);
			if (prices == null) {
				prices = new Int2IntLinkedOpenHashMap();
				prices.put(price, qty);
				symbol2buy.put(src, symbolOffset, prices);
			} else {
				int priceQty = prices.get(price);
				if (priceQty == prices.defaultReturnValue()) {
					prices.put(price, qty);
				} else {
					prices.put(price, priceQty + qty);
				}
			}

		}

		boolean updated = false;
		int entryPos = symbol2top.getPos(src, symbolOffset);
		
		int pos;

		if (entryPos == -1) {
			pos = nextPos++;
			if (pos >= ((tops.length - 1) / 4)) {
				int[] newTops = new int[(int) (tops.length * 2)];
				System.arraycopy(tops, 0, newTops, 0, tops.length);
				tops = newTops;
			}

			symbol2top.put(src, symbolOffset, pos);
			if (side == SELL) {
				setSellPrice(pos, price);
				setSellQty(pos, qty);
				setBuyPrice(pos, 0);
				setBuyQty(pos, 0);
			} else {
				setSellPrice(pos, 0);
				setSellQty(pos, 0);
				setBuyPrice(pos, price);
				setBuyQty(pos, qty);
			}

			updated = true;
		} else {

			pos = symbol2top.getValue(entryPos);
			if (side == SELL) {
				if (getSellPrice(pos) == price) {
					setSellQty(pos, getSellQty(pos) + qty);
					updated = true;
				} else if (getSellPrice(pos) == 0 || price < getSellPrice(pos)) {
					setSellPrice(pos, price);
					setSellQty(pos, qty);
					updated = true;
				}
			} else {
				if (getBuyPrice(pos) == price) {
					setBuyQty(pos, getBuyQty(pos) + qty);
					updated = true;
				} else if (getBuyPrice(pos) == 0 || price > getBuyPrice(pos)) {
					setBuyPrice(pos, price);
					setBuyQty(pos, qty);
					updated = true;
				}
			}
		}
		
		if (updated) {
			updateListener.onUpdate(src.array(), symbolOffset, getSellPrice(pos), getSellQty(pos),
					getBuyPrice(pos), getBuyQty(pos));
		}
	}

	private void setSellPrice(int pos, int price) {
		tops[pos * 4] = price;
	}

	private int getSellPrice(int pos) {
		return tops[pos * 4];
	}

	private void setSellQty(int pos, int qty) {
		tops[pos * 4 + 1] = qty;
	}

	private int getSellQty(int pos) {
		return tops[pos * 4 + 1];
	}

	private void setBuyPrice(int pos, int price) {
		tops[pos * 4 + 2] = price;
	}

	private int getBuyPrice(int pos) {
		return tops[pos * 4 + 2];
	}

	private void setBuyQty(int pos, int qty) {
		tops[pos * 4 + 3] = qty;
	}

	private int getBuyQty(int pos) {
		return tops[pos * 4 + 3];
	}

	public void executeOrder(int orderId) {
		byte side = orderId2side.remove(orderId);
		int price = orderId2price.remove(orderId);
		int qty = orderId2qty.remove(orderId);
		byte[] symbolSrc = orderId2symbol.array();
		int symbolOffset = orderId2symbol.absoluteOffset(orderId2symbol.getPos(orderId));
		
		int topPos = symbol2top.getValue(symbol2top.getValue(symbol2top.getPos(symbolSrc, symbolOffset)));

		if (SELL == side) {
			Int2IntSortedMap prices = symbol2sell.get(symbolSrc, symbolOffset);
			int priceQty = prices.get(price);
			if (priceQty > qty) {
				int newQty = priceQty - qty;
				prices.put(price, newQty);
				setSellQty(topPos, newQty);
			} else {
				prices.remove(price);
				if (!prices.isEmpty()) {
					int newPrice = prices.firstIntKey();
					int newQty = prices.get(newPrice);
					setSellPrice(topPos, newPrice);
					setSellQty(topPos, newQty);
				} else {
					setSellPrice(topPos, 0);
					setSellQty(topPos, 0);
				}
			}
		} else {
			Int2IntSortedMap prices = symbol2buy.get(symbolSrc, symbolOffset);
			int priceQty = prices.get(price);
			if (priceQty > qty) {
				int newQty = priceQty - qty;
				prices.put(price, newQty);
				setBuyQty(topPos, newQty);
			} else {
				prices.remove(price);
				if (!prices.isEmpty()) {
					int newPrice = prices.lastIntKey();
					int newQty = prices.get(newPrice);
					setBuyPrice(topPos, newPrice);
					setBuyQty(topPos, newQty);
				} else {
					setBuyPrice(topPos, 0);
					setBuyQty(topPos, 0);
				}
			}
		}

		updateListener.onUpdate(symbolSrc, symbolOffset, getSellPrice(topPos), getSellQty(topPos),
				getBuyPrice(topPos), getBuyQty(topPos));

	}

}
