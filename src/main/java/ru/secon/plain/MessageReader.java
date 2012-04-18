package ru.secon.plain;

import static ru.secon.Constants.ADD_ORDER;
import static ru.secon.Constants.ADD_ORDER_LENGTH;
import static ru.secon.Constants.ID_LENGTH;
import static ru.secon.Constants.PRICE_LENGTH;
import static ru.secon.Constants.QTY_LENGTH;
import static ru.secon.Constants.SYMBOL_LENGTH;
import static ru.secon.Utils.inc;

import java.nio.ByteBuffer;

import ru.secon.AsciiByteUtils;
import ru.secon.Constants;
import ru.secon.MessageCounter;

public class MessageReader {

	private final TopOfBook tob;
	private final MessageCounter counter;

	public MessageReader(TopOfBook tob, MessageCounter counter) {
		this.tob = tob;
		this.counter = counter;
	}

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

				Order order = new Order();

				order.id = AsciiByteUtils.parseInt(buffer, buffer.position(), ID_LENGTH);
				inc(buffer, ID_LENGTH);

				int symbolOffset = buffer.position();
				order.symbol = new String(buffer.array(), symbolOffset, SYMBOL_LENGTH);
				inc(buffer, SYMBOL_LENGTH);

				if (buffer.get() == Constants.SELL) {
					order.side = Side.SELL;
				} else {
					order.side = Side.BUY;
				}

				order.price = AsciiByteUtils.parsePrice(buffer, buffer.position(), PRICE_LENGTH);
				inc(buffer, PRICE_LENGTH);

				order.qty = AsciiByteUtils.parseInt(buffer, buffer.position(), QTY_LENGTH);
				inc(buffer, QTY_LENGTH);

				tob.addOrder(order);

				counter.processed();

				inc(buffer, 1); // for new line
			}
		}
	}

}
