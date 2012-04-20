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

	private FixedByteSlice2IntOpenHashMap symbol2top = new FixedByteSlice2IntOpenHashMap(SYMBOL_LENGTH);
	private int[] tops = new int[INITIAL_SIZE];
	private int nextPos = 0;
	
	public void onAddOrder(ByteBuffer src, int orderId, int symbolOffset, byte side, int price, int qty) {
		boolean updated = false;
		int pos = symbol2top.getPos(src, symbolOffset);
		
		if (pos == -1) {
			pos = nextPos++;
			symbol2top.put(src, symbolOffset, pos);

			if (side == SELL) {
				setSellPrice(pos, price);
				setSellQty(pos, qty);
			} else {
				setBuyPrice(pos, price);
				setBuyQty(pos, qty);
			}

			updated = true;
		} else {
			if (side == SELL) {
				if (getSellPrice(pos) == price) {
					incSellQty(pos, qty);
					updated = true;
				} else if (getSellPrice(pos) == 0 || price < getSellPrice(pos)) {
					setSellPrice(pos, price);
					setSellQty(pos, qty);
					updated = true;
				}
			} else {
				if (getBuyPrice(pos) == price) {
					incBuyQty(pos, qty);
					updated = true;
				} else if (getBuyPrice(pos) == 0 || price > getBuyPrice(pos)) {
					setBuyPrice(pos, price);
					setBuyQty(pos, qty);
					updated = true;
				}
			}
		}
		
		if (updated) {
			updateListener.onUpdate(src, symbolOffset, getSellPrice(pos), getSellQty(pos), getBuyPrice(pos),
					getBuyQty(pos));
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

	private void incSellQty(int pos, int qty) {
		tops[pos * 4 + 1] += qty;
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

	private void incBuyQty(int pos, int qty) {
		tops[pos * 4 + 3] += qty;
	}

	private int getBuyQty(int pos) {
		return tops[pos * 4 + 3];
	}

}
