package ru.secon;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static ru.secon.ExchangeEmulator.Order.SELL;
import static ru.secon.ExchangeEmulator.Order.order;
import static ru.secon.ExchangeEmulator.Symbol.symbol;
import static ru.secon.testutils.CustomMatchers.containsBytes;
import static ru.secon.testutils.Utils.contentEqual;

import java.nio.ByteBuffer;

import org.junit.Test;

import ru.secon.ExchangeEmulator.Order;

public class ExchangeEmulatorTest {

	@Test
	public void execute_matching_orders() throws Exception {
		ExchangeEmulator emulator = new ExchangeEmulator();

		// should cause place order message
		Order order1 = order.symbol(symbol.fromString("123ABC")).side(SELL).price(34.5).qty(100);
		ByteBuffer msg = ByteBuffer.allocate(ExchangeEmulator.MAX_MSG_LENGTH);
		emulator.placeOrder(msg, order1);
		msg.flip();
		System.out.println(new String(msg.array()));
		assertThat(msg, containsBytes("A??????????123ABCS000034.5000000100"));
		byte[] orderId = extractOrderId(msg);

		msg.clear();
		assertTrue(emulator.executeOrder(msg, symbol.fromString("123ABC"), SELL));
		msg.flip();
		assertThat(msg, containsBytes("E??????????"));
		assertTrue(contentEqual(orderId, 0, msg.array(), 1, ExchangeEmulator.ID_LENGTH));

		assertFalse(emulator.executeOrder(msg, symbol.fromString("123ABC"), SELL));
	}

	private byte[] extractOrderId(ByteBuffer msg) {
		byte[] result = new byte[ExchangeEmulator.ID_LENGTH];
		System.arraycopy(msg.array(), 1, result, 0, result.length);
		return result;
	}

	private ByteBuffer wrap(String string) {
		return ByteBuffer.wrap(string.getBytes());
	}

}
