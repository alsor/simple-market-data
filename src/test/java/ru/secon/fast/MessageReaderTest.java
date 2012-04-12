package ru.secon.fast;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static ru.secon.CustomMatchers.containsBytesInTheBeginning;
import static ru.secon.fast.TopOfBook.BUY;
import static ru.secon.fast.TopOfBook.SELL;

import java.nio.ByteBuffer;

import org.junit.Test;

import ru.secon.CaptureSymbol;
import ru.secon.fast.MessageReader;

public class MessageReaderTest {

	@SuppressWarnings("unchecked")
	@Test
	public void invoke_add_order() throws Exception {
		TopOfBook tob = createMock(TopOfBook.class);
		MessageReader reader = new MessageReader(tob);
		
		ByteBuffer buffer = wrap("A0000000888123ABCS000034.5000000100\n"
				+ "A0000000999456QWEB000974.2500007894\nA0000");

		CaptureSymbol capturedSymbol1 = new CaptureSymbol(0, 2);
		{
			int orderId = 888;
			byte side = SELL;
			int price = 345000;
			int qty = 100;
			tob.onAddOrder((ByteBuffer) anyObject(), eq(orderId), anyInt(), eq(side), eq(price), eq(qty));
			expectLastCall().andAnswer(capturedSymbol1);
		}

		CaptureSymbol capturedSymbol2 = new CaptureSymbol(0, 2);
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

}
