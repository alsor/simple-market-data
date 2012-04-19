package ru.secon.fast;

import java.nio.ByteBuffer;

public class TopOfBook {

	public TopOfBook(UpdateListener updateListener) {
	}

	public void onAddOrder(ByteBuffer src, int orderId, int symbolOffset, byte side, int price, int qty) {
		int offset = tops.get(src, symbolOffset, SYMBOL_LENGTH);
		
		if (offset == -1) {
			int valOffset = tops.putKey(src, symbolOffset, SYMBOL_LENGTH);
			tops.putIntValue(valOffset, )
		}
	}

}
