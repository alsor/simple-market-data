package ru.secon;

import java.nio.ByteBuffer;

public class Utils {

	public static int doubleAsInt(double d) {
		return (int) (d * 10000);
	}

	public static void inc(ByteBuffer buffer, int length) {
		buffer.position(buffer.position() + length);
	}
}
