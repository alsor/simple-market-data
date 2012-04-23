package ru.secon.fast;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static ru.secon.Constants.BUY;
import static ru.secon.Constants.SELL;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import ru.secon.TopOfBookTestBase;
import ru.secon.plain.Order;
import ru.secon.plain.Side;
import ru.secon.plain.Update;
import ru.secon.testutils.CaptureSymbol;


public class TopOfBookTest extends TopOfBookTestBase {

	private TopOfBook tob;
	private UpdateListener updateListener;
	private CaptureSymbol capturedSymbol;

	@Before
	public void setUp() {
		updateListener = createMock(UpdateListener.class);
		tob = new TopOfBook(updateListener);
	}

	@Test
	public void generate_updates232() throws Exception {

		String symbol = "123ABC";
		int orderId = 789;
		byte side = SELL;
		int price = 255000;
		int qty = 100;


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
		updateListener.onUpdate((byte[]) anyObject(), anyInt(), eq(sellPrice), eq(sellQty), eq(buyPrice),
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

	@Override
	protected void invoke_add_order(Order order) {
		byte s = Side.SELL.equals(order.side) ? SELL : BUY;
		addOrder(order.symbol, order.id, s, order.price, order.qty);
	}

	@Override
	protected void invoke_execute_order(int orderId) {
		replay(updateListener);
		tob.executeOrder(orderId);
		verify(updateListener);
	}

	@Override
	protected void configure_expect_on_update(Update update) {
		capturedSymbol = expectUpdate(update.sellPrice, update.sellQty, update.buyPrice, update.buyQty);
	}

	@Override
	protected void reset_mocks() {
		reset(updateListener);
	}

	@Override
	protected void assert_after_add_order(String symbol) {
		assertEquals(symbol, capturedSymbol.getValue());
	}

}
