package ru.secon.plain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

public class TopOfBook {


	private final UpdateListener updateListener;

	// private static class Book {
	// public NavigableMap<Integer, Integer> sell = new TreeMap<Integer, Integer>();
	// public NavigableMap<Integer, Integer> buy = new TreeMap<Integer, Integer>();
	//
	// public Update currentTop = new Update();
	//
	// public Book(String symbol) {
	// currentTop.symbol = symbol;
	// }
	// }

	private static class Top {
		public int sellPrice;
		public int sellQty;
		public int buyPrice;
		public int buyQty;
	}

	public TopOfBook(UpdateListener updateListener) {
		this.updateListener = updateListener;
	}

	// Map<String, Book> books = new HashMap<String, Book>();
	Map<String, Top> tops = new HashMap<String, Top>();

	public void addOrder(Order order) {
		// Book book = books.get(order.symbol);
		// if (book == null) {
		// book = new Book(order.symbol);
		// books.put(order.symbol, book);
		// }

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

//		NavigableMap<Integer, Integer> side = Side.SELL.equals(order.side) ? book.sell : book.buy;
//		
//		Integer price = side.get(order.price);
//		int newQty;
//		if (price != null) {
//			newQty = price.intValue() + order.qty;
//		} else {
//			newQty = order.qty;
//		}
//		side.put(order.price, newQty);
//
//		{
//			Entry<Integer, Integer> sellEntry = book.sell.firstEntry();
//			if (sellEntry != null) {
//				int sellPrice = sellEntry.getKey();
//				int sellQty = sellEntry.getValue();
//				if (sellPrice != book.currentTop.sellPrice || sellQty != book.currentTop.sellQty) {
//					book.currentTop.sellPrice = sellPrice;
//					book.currentTop.sellQty = sellQty;
//					updateListener.onUpdate(book.currentTop);
//				}
//			}
//		}
//
//		{
//			Entry<Integer, Integer> buyEntry = book.buy.lastEntry();
//			if (buyEntry != null) {
//				int buyPrice = buyEntry.getKey();
//				int buyQty = buyEntry.getValue();
//				if (buyPrice != book.currentTop.buyPrice || buyQty != book.currentTop.buyQty) {
//					book.currentTop.buyPrice = buyPrice;
//					book.currentTop.buyQty = buyQty;
//					updateListener.onUpdate(book.currentTop);
//				}
//			}
//		}
	}

}
