package ru.secon.fast;

import static ru.secon.Constants.ADD_ORDER;
import static ru.secon.Constants.ADD_ORDER_LENGTH;
import static ru.secon.Constants.EXECUTE_ORDER;
import static ru.secon.Constants.EXECUTE_ORDER_LENGTH;
import static ru.secon.Constants.ID_LENGTH;
import static ru.secon.Constants.PRICE_LENGTH;
import static ru.secon.Constants.QTY_LENGTH;
import static ru.secon.Constants.SYMBOL_LENGTH;
import static ru.secon.Utils.inc;

import java.nio.ByteBuffer;

import ru.secon.AsciiByteUtils;
import ru.secon.MessageCounter;

public class MessageReader {


	private final TopOfBook tob;
	private final MessageCounter counter;

	public MessageReader(TopOfBook tob, MessageCounter counter) {
		this.tob = tob;
		this.counter = counter;
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
				counter.processed();
				inc(buffer, 1); // for new line
			} else if (type == EXECUTE_ORDER) {
				if (buffer.remaining() < EXECUTE_ORDER_LENGTH) {
					buffer.compact();
					return;
				}
				inc(buffer, 1); // for type

				int orderId = AsciiByteUtils.parseInt(buffer, buffer.position(), ID_LENGTH);
				inc(buffer, ID_LENGTH);

				tob.executeOrder(orderId);
				counter.processed();
				inc(buffer, 1); // for new line
			}
		}
	}

}
