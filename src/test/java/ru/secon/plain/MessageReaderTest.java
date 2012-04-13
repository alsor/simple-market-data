package ru.secon.plain;

import org.junit.Test;

public class MessageReaderTest {
	@Test
	public void create_order() throws Exception {
		TopOfBook tob = createMock(TopOfBook.class);
		MessageReader reader = new MessageReader(tob);

		reader.processBuffer(buffer);

		Order order = new Order();
		order.id = 739;

	}
}
