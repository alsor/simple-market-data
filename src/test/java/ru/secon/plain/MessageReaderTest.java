package ru.secon.plain;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertThat;
import static ru.secon.plain.Side.BUY;
import static ru.secon.plain.Side.SELL;
import static ru.secon.testutils.CustomMatchers.containsBytesInTheBeginning;
import static ru.secon.testutils.Utils.wrap;

import java.nio.ByteBuffer;

import org.junit.Test;

import ru.secon.MessageCounter;

public class MessageReaderTest {
	@Test
	public void create_order() throws Exception {
		TopOfBook tob = createStrictMock(TopOfBook.class);
		MessageCounter counter = createMock(MessageCounter.class);
		MessageReader reader = new MessageReader(tob, counter);

		ByteBuffer buffer = wrap("A0000000888123ABCS000034.5000000100\n"
				+ "A0000000999456QWEB000974.2500007894\nA0000");

		{
			Order order = new Order();
			order.symbol = "123ABC";
			order.id = 888;
			order.side = SELL;
			order.price = 345000;
			order.qty = 100;

			tob.addOrder(order);
		}

		{
			Order order = new Order();
			order.symbol = "456QWE";
			order.id = 999;
			order.side = BUY;
			order.price = 9742500;
			order.qty = 7894;

			tob.addOrder(order);
		}

		counter.processed();
		counter.processed();

		replay(tob, counter);
		reader.processBuffer(buffer);
		verify(tob, counter);

		assertThat(buffer, containsBytesInTheBeginning("A0000"));
	}

	@Test
	public void execute_order() throws Exception {
		TopOfBook tob = createStrictMock(TopOfBook.class);
		MessageCounter counter = createMock(MessageCounter.class);
		MessageReader reader = new MessageReader(tob, counter);

		ByteBuffer buffer = wrap("E0000000888\nE0000000999\nA0000");

		tob.executeOrder(888);
		counter.processed();
		tob.executeOrder(999);
		counter.processed();

		replay(tob, counter);
		reader.processBuffer(buffer);
		verify(tob, counter);

		assertThat(buffer, containsBytesInTheBeginning("A0000"));
	}
}
