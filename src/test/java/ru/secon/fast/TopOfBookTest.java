package ru.secon.fast;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static ru.secon.Constants.SELL;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import ru.secon.testutils.CaptureSymbol;

public class TopOfBookTest {

	private TopOfBook tob;
	private UpdateListener updateListener;

	@Before
	public void setUp() {
		updateListener = createMock(UpdateListener.class);
		tob = new TopOfBook(updateListener);
	}

	@Test
	public void generate_updates() throws Exception {

		String symbol = "123ABC";
		int orderId = 789;
		byte side = SELL;
		int price = 255000;
		int qty = 100;

		CaptureSymbol capturedSymbol = expectUpdate(255000, 100, 0, 0);
		addOrder(symbol, orderId, side, price, qty);
		assertEquals("123ABC", capturedSymbol.getValue());

		// added order - new TOB
		// added on lower price - nothing
		// added on same price - new TOB with increased qty
		// removed on same price - new TOB with decreased qty
		// added with better price - new TOB with new price and qty

		// added mathed order - new TOB with execution taken into account
	}

	@SuppressWarnings("unchecked")
	private CaptureSymbol expectUpdate(int sellPrice, int sellQty, int buyPrice, int buyQty) {
		CaptureSymbol capturedSymbol = new CaptureSymbol(0, 1);
		updateListener.onUpdate((ByteBuffer) anyObject(), anyInt(), eq(sellPrice), eq(sellQty), eq(buyPrice),
				eq(buyQty));
		expectLastCall().andAnswer(capturedSymbol);
		return capturedSymbol;
	}

	private void addOrder(String symbol, int orderId, byte side, int price, int qty) {
		replay(updateListener);
		tob.onAddOrder(wrap(symbol), orderId, 0, side, price, qty);
		verify(updateListener);
	}

	private static ByteBuffer wrap(String string) {
		return ByteBuffer.wrap(string.getBytes());
	}

}
