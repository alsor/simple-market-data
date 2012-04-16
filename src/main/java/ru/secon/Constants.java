package ru.secon;

public class Constants {

	public static byte SELL = 'S';
	public static byte BUY = 'B';

	public static final int ID_LENGTH = 10;
	public static final int SYMBOL_LENGTH = 6;
	public static final int SIDE_LENGTH = 1;
	public static final int PRICE_LENGTH = 11;
	public static final int QTY_LENGTH = 6;
	public static final int ADD_ORDER_LENGTH = ID_LENGTH + SYMBOL_LENGTH + SIDE_LENGTH + PRICE_LENGTH
			+ QTY_LENGTH + 1;

	public static final byte ADD_ORDER = 'A';
}
