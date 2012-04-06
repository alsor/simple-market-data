package ru.secon;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void floatAsInt() throws Exception {
		assertEquals(1233210, Utils.doubleAsInt(123.321));
		assertEquals(1233216, Utils.doubleAsInt(123.321678));
	}

}
