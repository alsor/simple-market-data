package ru.secon;

import org.junit.Test;

public class TopOfBookPlainImplTest {

	@Test
	public void generate_updates() throws Exception {
		TopOfBook tob = new TopOfBookPlainImpl();
		tob.onMessage("A0000010000345000000100");
	}

}
