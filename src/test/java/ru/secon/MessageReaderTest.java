package ru.secon;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static ru.secon.CustomMatchers.containsBytesInTheBeginning;
import static ru.secon.TopOfBook.BUY;
import static ru.secon.TopOfBook.SELL;

import java.nio.ByteBuffer;

import org.easymock.IAnswer;
import org.junit.Test;

public class MessageReaderTest {

	@SuppressWarnings("unchecked")
	@Test
	public void invoke_add_order() throws Exception {
		TopOfBook tob = createMock(TopOfBook.class);
		MessageReader reader = new MessageReader(tob);
		
		ByteBuffer buffer = wrap("A0000000888123ABCS000034.5000000100\n"
				+ "A0000000999456QWEB000974.2500007894\nA0000");

		CaptureSymbol capturedSymbol1 = new CaptureSymbol();
		{
			int orderId = 888;
			byte side = SELL;
			int price = 345000;
			int qty = 100;
			tob.onAddOrder((ByteBuffer) anyObject(), eq(orderId), anyInt(), eq(side), eq(price), eq(qty));
			expectLastCall().andAnswer(capturedSymbol1);
		}

		CaptureSymbol capturedSymbol2 = new CaptureSymbol();
		{
			int orderId = 999;
			byte side = BUY;
			int price = 9742500;
			int qty = 7894;
			tob.onAddOrder((ByteBuffer) anyObject(), eq(orderId), anyInt(), eq(side), eq(price), eq(qty));
			expectLastCall().andAnswer(capturedSymbol2);
		}

		replay(tob);
		reader.processBuffer(buffer);
		verify(tob);
		
		assertThat(buffer, containsBytesInTheBeginning("A0000"));
		assertEquals("123ABC", capturedSymbol1.getValue());
		assertEquals("456QWE", capturedSymbol2.getValue());
	}

	private ByteBuffer wrap(String string) {
		return ByteBuffer.wrap(string.getBytes());
	}

	@SuppressWarnings("rawtypes")
	private static class CaptureSymbol implements IAnswer {

		private String value;

		public Object answer() throws Throwable {
			Object[] args = getCurrentArguments();
			ByteBuffer buf = (ByteBuffer) args[0];
			int offset = ((Integer) args[2]).intValue();

			value = new String(buf.array(), offset, MessageReader.SYMBOL_LENGTH);

			return null;
		}

		public String getValue() {
			return value;
		}

	}

}
