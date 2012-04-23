package ru.secon.testutils;

import java.nio.ByteBuffer;

public class Utils {

	public static ByteBuffer wrap(String string) {
		return ByteBuffer.wrap(string.getBytes());
	}

	public static boolean contentEqual(byte[] a1, int o1, byte[] a2, int o2, int length) {
		for (int i1 = o1, i2 = o2; i1 < length; i1++, i2++) {
			if (a1[i1] != a2[i2]) return false;
		}
		return true;
	}

}
