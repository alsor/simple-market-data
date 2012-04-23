package ru.secon;

import org.junit.Test;

import ru.secon.plain.Order;
import ru.secon.plain.Side;
import ru.secon.plain.Update;

public abstract class TopOfBookTestBase {

	abstract protected void invoke_add_order(Order order);

	abstract protected void invoke_execute_order(int orderId);

	abstract protected void configure_expect_on_update(Update update);

	abstract protected void reset_mocks();

	abstract protected void assert_after_add_order(String symbol);

	@Test
	public void generate_updates() throws Exception {

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

			configure_expect_on_update(update);

			invoke_add_order(order);

			assert_after_add_order(symbol);
		}

		reset_mocks();

		/*
		 *            | 
		 * -----------|-----------
		 *  100@25.50 |
		 *   70@25.55 |
		 */

		{
			Order order = new Order();
			order.symbol = symbol;
			order.id = 23231;
			order.side = Side.SELL;
			order.price = 255500;
			order.qty = 70;

			invoke_add_order(order);
		}

		reset_mocks();

		/*
		 *            | 50@25.45 
		 * -----------|-----------
		 *  100@25.50 |
		 *   70@25.55 |
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

			configure_expect_on_update(update);

			invoke_add_order(order);

			assert_after_add_order(symbol);
		}

		reset_mocks();
		
		/*
		 *            | 50@25.45 
		 * -----------|-----------
		 *  130@25.50 |
		 *   70@25.55 |
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
			configure_expect_on_update(update);

			invoke_add_order(order);

			assert_after_add_order(symbol);
		}

		reset_mocks();
		
		/*
		 *            | 50@25.45 
		 * -----------|-----------
		 *   80@25.46 |
		 *  130@25.50 |
		 *   70@25.55 |
		 */
		{
			Order order = new Order();
			order.symbol = symbol;
			order.id = 3834;
			order.side = Side.SELL;
			order.price = 254600;
			order.qty = 80;

			Update update = new Update();
			update.symbol = symbol;
			update.sellPrice = 254600;
			update.sellQty = 80;
			update.buyPrice = 254500;
			update.buyQty = 50;
			configure_expect_on_update(update);

			invoke_add_order(order);

			assert_after_add_order(symbol);
		}

		reset_mocks();

		// execute some orders
		/*
		 *            | 50@25.45 
		 * -----------|-----------
		 *  130@25.50 |
		 *   70@25.55 |
		 */

		{
			Update update = new Update();
			update.symbol = symbol;
			update.sellPrice = 255000;
			update.sellQty = 130;
			update.buyPrice = 254500;
			update.buyQty = 50;
			configure_expect_on_update(update);

			invoke_execute_order(3834);
		}

		reset_mocks();

		// execute some orders
		/*
		 *            | 50@25.45 
		 * -----------|-----------
		 *  100@25.50 |
		 *   70@25.55 |
		 */

		{
			Update update = new Update();
			update.symbol = symbol;
			update.sellPrice = 255000;
			update.sellQty = 100;
			update.buyPrice = 254500;
			update.buyQty = 50;
			configure_expect_on_update(update);

			invoke_execute_order(3478);
		}

		reset_mocks();

		// execute some orders
		/*
		 *            | 50@25.45 
		 * -----------|-----------
		 *   70@25.55 |
		 */

		{
			Update update = new Update();
			update.symbol = symbol;
			update.sellPrice = 255500;
			update.sellQty = 70;
			update.buyPrice = 254500;
			update.buyQty = 50;
			configure_expect_on_update(update);

			invoke_execute_order(789);
		}

		reset_mocks();

		// execute some orders
		/*
		 *            | 0@0 
		 * -----------|-----------
		 *   70@25.55 |
		 */

		{
			Update update = new Update();
			update.symbol = symbol;
			update.sellPrice = 255500;
			update.sellQty = 70;
			update.buyPrice = 0;
			update.buyQty = 0;
			configure_expect_on_update(update);

			invoke_execute_order(893);
		}

	}


}
