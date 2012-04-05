package ru.secon;

import java.nio.ByteBuffer;

public class AsciiByteUtils {

	public static void putInt(ByteBuffer dest, int num, int length) {
		int offset = dest.position();
		int i = offset + length;

		boolean isNegative;

		if (num < 0) {
			isNegative = true;
			num = -num;
		} else {
			isNegative = false;
		}

		while (i > offset && num > 0) {
			i--;

			int x = num / 10;
			dest.put(i, (byte) ((byte) '0' + (num - x * 10)));
			num = x;
		}

		if (i > offset && isNegative) {
			i--;
			dest.put(i, (byte) '-');
		}

        while (i > offset) {
			i--;
			dest.put(i, (byte) '0');
		}
		dest.position(offset + length);
	}

}
