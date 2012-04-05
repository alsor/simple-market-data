package ru.secon;

import static org.junit.Assert.assertThat;
import static ru.secon.CustomMatchers.containsBytes;
import static ru.secon.ExchangeEmulator.Order.SELL;
import static ru.secon.ExchangeEmulator.Order.order;
import static ru.secon.ExchangeEmulator.Symbol.symbol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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
		assertThat(msg, containsBytes("A??????????"));
		// should cause previous order execution and no new order placed message
		// emulator.placeOrder(order.symbol(symbol).side(BUY).price(34.5).qty(50));

	}

	private ByteBuffer wrap(String string) {
		return ByteBuffer.wrap(string.getBytes());
	}

	public static void main(String[] args) throws IOException {
		File file = new File("data.txt");
		ByteBuffer buffer = ByteBuffer.wrap("123abc\n".getBytes());
		FileChannel channel = new FileOutputStream(file).getChannel();
		channel.write(buffer);
	}

}
