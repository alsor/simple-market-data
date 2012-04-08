package ru.secon;

import org.junit.Test;

public class TopOfBookPlainImplTest {

	@Test
	public void generate_updates() throws Exception {
		TopOfBook tob = new TopOfBookPlainImpl();
		tob.onMessage("A0000010000345000000100");

		// added order - new TOB
		// added on lower price - nothing
		// added on same price - new TOB with increased qty
		// removed on same price - new TOB with decreased qty
		// added with better price - new TOB with new price and qty

		// added mathed order - new TOB with execution taken into account
	}

}
