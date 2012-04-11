package ru.secon;

import java.nio.ByteBuffer;

public interface TopOfBook {

	byte SELL = 'S';
	byte BUY = 'B';

	void onAddOrder(ByteBuffer src, int orderId, int symbolOffset, byte side, int price, int qty);

}
