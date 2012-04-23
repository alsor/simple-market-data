package ru.secon.plain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TopOfBook {


	private final UpdateListener updateListener;

	private static class Top {
		public int sellPrice;
		public int sellQty;
		public int buyPrice;
		public int buyQty;
	}

	public TopOfBook(UpdateListener updateListener) {
		this.updateListener = updateListener;
	}

	Map<String, Top> tops = new HashMap<String, Top>();

	Map<Integer, Order> orders = new HashMap<Integer, Order>();

	private Map<String, NavigableMap<Integer, Integer>> symbol2sell = new HashMap<String, NavigableMap<Integer, Integer>>();
	private Map<String, NavigableMap<Integer, Integer>> symbol2buy = new HashMap<String, NavigableMap<Integer, Integer>>();

	public void addOrder(Order order) {
		orders.put(order.id, order);

		if (Side.SELL.equals(order.side)) {
			NavigableMap<Integer, Integer> prices = symbol2sell.get(order.symbol);
			if (prices == null) {
				prices = new TreeMap<Integer, Integer>();
				prices.put(order.price, order.qty);
				symbol2sell.put(order.symbol, prices);
			} else {
				Integer qty = prices.get(order.price);
				if (qty == null) {
					prices.put(order.price, order.qty);
				} else {
					prices.put(order.price, order.qty + qty.intValue());
				}
			}
		} else {
			NavigableMap<Integer, Integer> prices = symbol2buy.get(order.symbol);
			if (prices == null) {
				prices = new TreeMap<Integer, Integer>();
				prices.put(order.price, order.qty);
				symbol2buy.put(order.symbol, prices);
			} else {
				Integer qty = prices.get(order.price);
				if (qty == null) {
					prices.put(order.price, order.qty);
				} else {
					prices.put(order.price, order.qty + qty.intValue());
				}
			}
		}

		boolean updated = false;
		Top top = tops.get(order.symbol);
		if (top == null) {
			top = new Top();

			if (Side.SELL.equals(order.side)) {
				top.sellPrice = order.price;
				top.sellQty = order.qty;
			} else {
				top.buyPrice = order.price;
				top.buyQty = order.qty;
			}

			tops.put(order.symbol, top);
			updated = true;
		} else {
			if (Side.SELL.equals(order.side)) {
				if (top.sellPrice == order.price) {
					top.sellQty += order.qty;
					updated = true;
				} else if (top.sellPrice == 0 || order.price < top.sellPrice) {
					top.sellPrice = order.price;
					top.sellQty = order.qty;
					updated = true;
				}
			} else {
				if (top.buyPrice == order.price) {
					top.buyQty += order.qty;
					updated = true;
				} else if (top.buyPrice == 0 || order.price > top.buyPrice) {
					top.buyPrice = order.price;
					top.buyQty = order.qty;
					updated = true;
				}
			}
		}

		if (updated) {
			Update update = new Update();
			update.symbol = order.symbol;
			update.sellPrice = top.sellPrice;
			update.sellQty = top.sellQty;
			update.buyPrice = top.buyPrice;
			update.buyQty = top.buyQty;
			updateListener.onUpdate(update);
		}

	}


	public void executeOrder(int orderId) {
		Order order = orders.remove(orderId);
		
		Top top = tops.get(order.symbol);
		
		if (Side.SELL.equals(order.side)) {
			
			NavigableMap<Integer, Integer> prices = symbol2sell.get(order.symbol);
			int qty = prices.get(order.price);
			if (qty > order.qty) {
				int newQty = qty - order.qty;
				prices.put(order.price, newQty);
				top.sellQty = newQty;
			} else {
				prices.remove(order.price);
				Entry<Integer, Integer> newTop = prices.firstEntry();
				if (newTop != null) {
					top.sellPrice = newTop.getKey();
					top.sellQty = newTop.getValue();
				} else {
					top.sellPrice = 0;
					top.sellQty = 0;
				}
			}
		} else {
			NavigableMap<Integer, Integer> prices = symbol2buy.get(order.symbol);
			int qty = prices.get(order.price);
			if (qty > order.qty) {
				int newQty = qty - order.qty;
				prices.put(order.price, newQty);
				top.buyQty = newQty;
			} else {
				prices.remove(order.price);
				Entry<Integer, Integer> newTop = prices.lastEntry();
				if (newTop != null) {
					top.buyPrice = newTop.getKey();
					top.buyQty = newTop.getValue();
				} else {
					top.buyPrice = 0;
					top.buyQty = 0;
				}
			}
		}

		Update update = new Update();
		update.symbol = order.symbol;
		update.sellPrice = top.sellPrice;
		update.sellQty = top.sellQty;
		update.buyPrice = top.buyPrice;
		update.buyQty = top.buyQty;
		updateListener.onUpdate(update);
	}

}
