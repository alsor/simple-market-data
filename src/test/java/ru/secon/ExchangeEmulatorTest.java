package ru.secon;

import static org.junit.Assert.assertThat;
import static ru.secon.CustomMatchers.containsBytes;
import static ru.secon.ExchangeEmulator.Order.SELL;
import static ru.secon.ExchangeEmulator.Order.order;
import static ru.secon.ExchangeEmulator.Symbol.symbol;

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
		// should cause previous order execution and no new order placed message
		// emulator.placeOrder(order.symbol(symbol).side(BUY).price(34.5).qty(50));

	}

	private ByteBuffer wrap(String string) {
		return ByteBuffer.wrap(string.getBytes());
	}

}
