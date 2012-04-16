package ru.secon.testutils;

import java.nio.ByteBuffer;

public class Utils {

	public static ByteBuffer wrap(String string) {
		return ByteBuffer.wrap(string.getBytes());
	}

}
