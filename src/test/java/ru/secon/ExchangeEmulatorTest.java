package ru.secon;

import static ru.secon.ExchangeEmulator.Order.BUY;
import static ru.secon.ExchangeEmulator.Order.SELL;
import static ru.secon.ExchangeEmulator.Order.order;
import static ru.secon.ExchangeEmulator.Symbol.symbol;

import org.junit.Test;

public class ExchangeEmulatorTest {

	@Test
	public void execute_matching_orders() throws Exception {
		ExchangeEmulator emulator = new ExchangeEmulator();

		// should cause place order message
		byte[] msg = emulator.placeOrder(order.symbol(symbol.fromString("123ABC")).side(SELL).price(34.5).qty(100));
		assertAddOrderEvent(msg).

		// should cause previous order execution and no new order placed message
		emulator.placeOrder(orderFor(symbol).side(BUY).price(34.5).qty(50));

	}

}
