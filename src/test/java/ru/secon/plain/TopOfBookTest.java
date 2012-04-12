package ru.secon.plain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TopOfBookTest {

	@Test
	public void generate_updates() throws Exception {
		TopOfBook tob = new TopOfBook();
		
		Order order = new Order();
		order.symbol = "123ABC";
		order.id = 789;
		order.side = Side.SELL;
		order.price = 2550000;
		order.qty = 100;

		Update update = tob.addOrder(order);
		assertEquals("123ABC", update.symbol);
		assertEquals(2550000, update.sellPrice);
		assertEquals(100, update.sellQty);
		assertEquals(0, update.buyPrice);
		assertEquals(0, update.buyQty);
	}

}
