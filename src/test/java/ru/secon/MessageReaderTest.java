package ru.secon;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;

import org.junit.Test;

public class MessageReaderTest {
	@Test
	public void invoke_add_order() throws Exception {
		TopOfBook tob = createMock(TopOfBook.class);
		MessageReader reader = new MessageReader(tob);
		
		int orderId = 999;
		int symbolOffset = TEST_OFFSET + SYMBOL_OFFSET;
		int symbolLength = TEST_OFFSET + SYMBOL_OFFSET + SYMBOL_LENGTH;
		byte side = SELL;
		int price = 345000;
		int qty = 100;
		tob.onAddOrder(eq(999), eq());
		reader.processBuffer(wrap("A0000000999123ABCS000034.5000000100"));
	}

	public static void main(String[] args) {

	}
}
