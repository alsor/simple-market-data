package ru.secon;

import static org.junit.Assert.assertThat;
import static ru.secon.CustomMatchers.containsBytes;

import java.nio.ByteBuffer;

import org.junit.Test;

public class AsciiByteUtilsTest {

	@Test
	public void putInt() throws Exception {
		ByteBuffer dest = ByteBuffer.allocate(10);
		int num = 123;
		int length = 10;

		AsciiByteUtils.putInt(dest, num, length);
		dest.flip();

		assertThat(dest, containsBytes("0000000123"));
	}

	@Test
	public void putIntAsFloat() throws Exception {
		ByteBuffer dest = ByteBuffer.allocate(11);
		int num = 1233210;

		AsciiByteUtils.putIntAsFloat(dest, num);
		dest.flip();

		assertThat(dest, containsBytes("000123.3210"));
	}

}
