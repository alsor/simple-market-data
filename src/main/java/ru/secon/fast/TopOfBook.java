package ru.secon.fast;

import static ru.secon.Constants.SELL;
import static ru.secon.Constants.SYMBOL_LENGTH;

import java.nio.ByteBuffer;

import ru.undev.FixedByteSlice2IntOpenHashMap;

public class TopOfBook {

	public static final int INITIAL_SIZE = 10 * 1000;
	private final UpdateListener updateListener;

	public TopOfBook(UpdateListener updateListener) {
		this.updateListener = updateListener;
	}

	// private static class Top {
	// public int sellPrice;
	// public int sellQty;
	// public int buyPrice;
	// public int buyQty;
	// }

	private FixedByteSlice2IntOpenHashMap symbol2top = new FixedByteSlice2IntOpenHashMap(
			SYMBOL_LENGTH);

	// private Bytes2BytesOpenHashMap symbol2top = new
	// Bytes2BytesOpenHashMap(SYMBOL_LENGTH, 4 * 4);
	private int[] tops = new int[INITIAL_SIZE * 4];
	private int nextPos = 0;
	
	public void onAddOrder(ByteBuffer src, int orderId, int symbolOffset, byte side, int price, int qty) {
		boolean updated = false;
		int entryPos = symbol2top.getPos(src, symbolOffset);
		// Top top = symbol2top.get(src, symbolOffset);
		
		// int pos = symbol2top.getPos(src, symbolOffset);
		int pos;

		if (entryPos == -1) {
			pos = nextPos++;
			if (pos >= ((tops.length - 1) / 4)) {
				int[] newTops = new int[(int) (tops.length * 2)];
				System.arraycopy(tops, 0, newTops, 0, tops.length);
				tops = newTops;
			}

			// top = new Top();
			// symbol2top.put(src, symbolOffset, top);

			// if (side == SELL) {
			// top.sellPrice = price;
			// top.sellQty = qty;
			// } else {
			// top.buyPrice = price;
			// top.buyQty = qty;
			// }

			symbol2top.put(src, symbolOffset, pos);
			// pos = symbol2top.putKey(src, symbolOffset);
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

			// if (side == SELL) {
			// if (top.sellPrice == price) {
			// top.sellQty += qty;
			// updated = true;
			// } else if (top.sellPrice == 0 || price < top.sellPrice) {
			// top.sellPrice = price;
			// top.sellQty = qty;
			// updated = true;
			// }
			// } else {
			// if (top.buyPrice == price) {
			// top.buyQty += qty;
			// updated = true;
			// } else if (top.buyPrice == 0 || price > top.buyPrice) {
			// top.buyPrice = price;
			// top.buyQty = qty;
			// updated = true;
			// }
			// }

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
			// updateListener.onUpdate(src, symbolOffset, top.sellPrice,
			// top.sellQty, top.buyPrice,
			// top.buyQty);
			updateListener.onUpdate(src, symbolOffset, getSellPrice(pos), getSellQty(pos),
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

	// private void setSellPrice(int pos, int price) {
	// symbol2top.putInt(pos, 0, price);
	// }
	//
	// private int getSellPrice(int pos) {
	// return symbol2top.getInt(pos, 0);
	// }
	//
	// private void setSellQty(int pos, int qty) {
	// symbol2top.putInt(pos, 4, qty);
	// }
	//
	// private int getSellQty(int pos) {
	// return symbol2top.getInt(pos, 4);
	// }
	//
	// private void setBuyPrice(int pos, int price) {
	// symbol2top.putInt(pos, 8, price);
	// }
	//
	// private int getBuyPrice(int pos) {
	// return symbol2top.getInt(pos, 8);
	// }
	//
	// private void setBuyQty(int pos, int qty) {
	// symbol2top.putInt(pos, 12, qty);
	// }
	//
	// private int getBuyQty(int pos) {
	// return symbol2top.getInt(pos, 12);
	// }

}
