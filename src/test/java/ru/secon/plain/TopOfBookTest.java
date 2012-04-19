package ru.secon.plain;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import org.junit.Test;


public class TopOfBookTest {

	@Test
	public void generate_updates() throws Exception {
		UpdateListener updateListener = createMock(UpdateListener.class);
		TopOfBook tob = new TopOfBook(updateListener);

		/*
		 *            | 
		 * -----------|-----------
		 *  100@25.50 |
		 */
		
		String symbol = "123ABC";
		{
			Order order = new Order();
			order.symbol = symbol;
			order.id = 789;
			order.side = Side.SELL;
			order.price = 255000;
			order.qty = 100;

			Update update = new Update();
			update.symbol = symbol;
			update.sellPrice = 255000;
			update.sellQty = 100;
			update.buyPrice = 0;
			update.buyQty = 0;
			updateListener.onUpdate(update);

			replay(updateListener);
			tob.addOrder(order);
			verify(updateListener);
		}

		reset(updateListener);
		
		/*
		 *            | 
		 * -----------|-----------
		 *  100@25.50 |
		 */
		
		{
			Order order = new Order();
			order.symbol = symbol;
			order.id = 23231;
			order.side = Side.SELL;
			order.price = 255500;
			order.qty = 70;

			replay(updateListener);
			tob.addOrder(order);
			verify(updateListener);
		}

		reset(updateListener);

		/*
		 *            | 50@25.45 
		 * -----------|-----------
		 *  100@25.50 |
		 */
		{
			Order order = new Order();
			order.symbol = symbol;
			order.id = 893;
			order.side = Side.BUY;
			order.price = 254500;
			order.qty = 50;

			Update update = new Update();
			update.symbol = symbol;
			update.sellPrice = 255000;
			update.sellQty = 100;
			update.buyPrice = 254500;
			update.buyQty = 50;
			updateListener.onUpdate(update);
			
			replay(updateListener);
			tob.addOrder(order);
			verify(updateListener);
		}
		
		reset(updateListener);
		/*
		 *            | 50@25.45 
		 * -----------|-----------
		 *  130@25.50 |
		 */
		{
			Order order = new Order();
			order.symbol = symbol;
			order.id = 3478;
			order.side = Side.SELL;
			order.price = 255000;
			order.qty = 30;

			Update update = new Update();
			update.symbol = symbol;
			update.sellPrice = 255000;
			update.sellQty = 130;
			update.buyPrice = 254500;
			update.buyQty = 50;
			updateListener.onUpdate(update);
			
			replay(updateListener);
			tob.addOrder(order);
			verify(updateListener);
		}
		
		reset(updateListener);
		/*
		 *            | 50@25.45 
		 * -----------|-----------
		 *   80@25.46 |
		 */
		{
			Order order = new Order();
			order.symbol = symbol;
			order.id = 3478;
			order.side = Side.SELL;
			order.price = 254600;
			order.qty = 80;

			Update update = new Update();
			update.symbol = symbol;
			update.sellPrice = 254600;
			update.sellQty = 80;
			update.buyPrice = 254500;
			update.buyQty = 50;
			updateListener.onUpdate(update);
			
			replay(updateListener);
			tob.addOrder(order);
			verify(updateListener);
		}
	}

}
