package ru.secon.fast;

import java.nio.ByteBuffer;

import ru.secon.AsciiByteUtils;

public class MessageReader {

	public static final int ID_LENGTH = 10;
	public static final int SYMBOL_LENGTH = 6;
	public static final int SIDE_LENGTH = 1;
	public static final int PRICE_LENGTH = 11;
	public static final int QTY_LENGTH = 6;
	public static final int ADD_ORDER_LENGTH = ID_LENGTH + SYMBOL_LENGTH + SIDE_LENGTH + PRICE_LENGTH
			+ QTY_LENGTH + 1;

	public static final byte ADD_ORDER = 'A';

	private final TopOfBook tob;

	public MessageReader(TopOfBook tob) {
		this.tob = tob;
	}

	/**
	 * We always expecting that current position of the buffer is the beginning of the message
	 * 
	 */
	public void processBuffer(ByteBuffer buffer) {

		while (true) {
			if (buffer.remaining() < 1) { // not even a header
				buffer.compact();
				return;
			}

			byte type = buffer.get(buffer.position());
			if (type == ADD_ORDER) {

				if (buffer.remaining() < ADD_ORDER_LENGTH) {
					buffer.compact();
					return;
				}
				inc(buffer, 1); // for type

				int orderId = AsciiByteUtils.parseInt(buffer, buffer.position(), ID_LENGTH);
				inc(buffer, ID_LENGTH);

				int symbolOffset = buffer.position();
				inc(buffer, SYMBOL_LENGTH);

				byte side = buffer.get();

				int price = AsciiByteUtils.parsePrice(buffer, buffer.position(), PRICE_LENGTH);
				inc(buffer, PRICE_LENGTH);

				int qty = AsciiByteUtils.parseInt(buffer, buffer.position(), QTY_LENGTH);
				inc(buffer, QTY_LENGTH);

				tob.onAddOrder(buffer, orderId, symbolOffset, side, price, qty);
				inc(buffer, 1); // for new line
			}
		}
	}

	private void inc(ByteBuffer buffer, int length) {
		buffer.position(buffer.position() + length);
	}

}
