package ru.secon.plain;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import org.junit.Before;

import ru.secon.TopOfBookTestBase;

public class TopOfBookTest extends TopOfBookTestBase {

	private UpdateListener updateListener;
	private TopOfBook tob;

	@Before
	public void setUp() {
		updateListener = createMock(UpdateListener.class);
		tob = new TopOfBook(updateListener);
	}

	protected void invoke_add_order(Order order) {
		replay(updateListener);
		tob.addOrder(order);
		verify(updateListener);
	}

	protected void invoke_execute_order(int orderId) {
		replay(updateListener);
		tob.executeOrder(orderId);
		verify(updateListener);
	}

	@Override
	protected void configure_expect_on_update(Update update) {
		updateListener.onUpdate(update);
	}

	@Override
	protected void reset_mocks() {
		reset(updateListener);
	}

	@Override
	protected void assert_after_add_order(String symbol) {
	}

}
