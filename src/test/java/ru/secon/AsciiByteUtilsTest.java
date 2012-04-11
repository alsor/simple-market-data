package ru.secon;

import static org.junit.Assert.assertEquals;
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

	@Test
	public void parseInt() throws Exception {
		ByteBuffer src = ByteBuffer.wrap("XXX0012345YYY".getBytes());
		int offset = 3;
		int length = 7;
		assertEquals(12345, AsciiByteUtils.parseInt(src, offset, length));
	}

	@Test
	public void parsePrice() throws Exception {
		ByteBuffer src = ByteBuffer.wrap("XXXX000374.7810ZZZ".getBytes());
		int offset = 4;
		int length = 11;
		assertEquals(3747810, AsciiByteUtils.parsePrice(src, offset, length));
	}
}
