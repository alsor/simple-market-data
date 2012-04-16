package ru.secon.plain;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Test;


public class TopOfBookTest {

	@Test
	public void generate_updates() throws Exception {
		UpdateListener updateListener = createMock(UpdateListener.class);
		TopOfBook tob = new TopOfBook(updateListener);
		
		Order order = new Order();
		order.symbol = "123ABC";
		order.id = 789;
		order.side = Side.SELL;
		order.price = 255000;
		order.qty = 100;

		Update update = new Update();
		update.symbol = "123ABC";
		update.sellPrice = 255000;
		update.sellQty = 100;
		update.buyPrice = 0;
		update.buyQty = 0;
		updateListener.onUpdate(update);

		replay(updateListener);
		tob.addOrder(order);
		verify(updateListener);
	}

}
