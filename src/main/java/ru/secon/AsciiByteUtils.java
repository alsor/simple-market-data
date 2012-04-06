package ru.secon;

import java.nio.ByteBuffer;

public class AsciiByteUtils {

	private static byte[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

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

	public static void putIntAsFloat(ByteBuffer dest, int num) {
		int c;

		if (num < 0) {
			dest.put((byte) '-');
			num = -num;
		}

		if (num < 10000) {
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '.');
		}

		if (num < 10) {
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put(digits[num]);
		} else if (num < 100) {
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put(digits[num / 10]);
			dest.put(digits[num % 10]);
		} else if (num < 1000) {
			dest.put((byte) '0');
			dest.put(digits[num / 100]);
			dest.put(digits[(c = num % 100) / 10]);
			dest.put(digits[c % 10]);
		} else if (num < 10000) {
			dest.put(digits[num / 1000]);
			dest.put(digits[(c = num % 1000) / 100]);
			dest.put(digits[(c = num % 100) / 10]);
			dest.put(digits[c % 10]);
		} else if (num < 100000) {
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put(digits[num / 10000]);
			dest.put((byte) '.');
			dest.put(digits[(c = num % 10000) / 1000]);
			dest.put(digits[(c = num % 1000) / 100]);
			dest.put(digits[(c = num % 100) / 10]);
			dest.put(digits[c % 10]);
		} else if (num < 1000000) {
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put(digits[num / 100000]);
			dest.put(digits[(c = num % 100000) / 10000]);
			dest.put((byte) '.');
			dest.put(digits[(c = num % 10000) / 1000]);
			dest.put(digits[(c = num % 1000) / 100]);
			dest.put(digits[(c = num % 100) / 10]);
			dest.put(digits[c % 10]);
		} else if (num < 10000000) {
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put(digits[num / 1000000]);
			dest.put(digits[(c = num % 1000000) / 100000]);
			dest.put(digits[(c = num % 100000) / 10000]);
			dest.put((byte) '.');
			dest.put(digits[(c = num % 10000) / 1000]);
			dest.put(digits[(c = num % 1000) / 100]);
			dest.put(digits[(c = num % 100) / 10]);
			dest.put(digits[c % 10]);
		} else if (num < 100000000) {
			dest.put((byte) '0');
			dest.put((byte) '0');
			dest.put(digits[num / 10000000]);
			dest.put(digits[(c = num % 10000000) / 1000000]);
			dest.put(digits[(c = num % 1000000) / 100000]);
			dest.put(digits[(c = num % 100000) / 10000]);
			dest.put((byte) '.');
			dest.put(digits[(c = num % 10000) / 1000]);
			dest.put(digits[(c = num % 1000) / 100]);
			dest.put(digits[(c = num % 100) / 10]);
			dest.put(digits[c % 10]);
		} else if (num < 1000000000) {
			dest.put((byte) '0');
			dest.put(digits[num / 100000000]);
			dest.put(digits[(c = num % 100000000) / 10000000]);
			dest.put(digits[(c = num % 10000000) / 1000000]);
			dest.put(digits[(c = num % 1000000) / 100000]);
			dest.put(digits[(c = num % 100000) / 10000]);
			dest.put((byte) '.');
			dest.put(digits[(c = num % 10000) / 1000]);
			dest.put(digits[(c = num % 1000) / 100]);
			dest.put(digits[(c = num % 100) / 10]);
			dest.put(digits[c % 10]);
		} else if (num >= 1000000000) {
			dest.put(digits[num / 1000000000]);
			dest.put(digits[(c = num % 1000000000) / 100000000]);
			dest.put(digits[(c = num % 100000000) / 10000000]);
			dest.put(digits[(c = num % 10000000) / 1000000]);
			dest.put(digits[(c = num % 1000000) / 100000]);
			dest.put(digits[(c = num % 100000) / 10000]);
			dest.put((byte) '.');
			dest.put(digits[(c = num % 10000) / 1000]);
			dest.put(digits[(c = num % 1000) / 100]);
			dest.put(digits[(c = num % 100) / 10]);
			dest.put(digits[c % 10]);
		}
	}

}
