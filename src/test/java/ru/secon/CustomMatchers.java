package ru.secon;

import java.nio.ByteBuffer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomMatchers {

	public static Matcher<ByteBuffer> containsBytes(final String expectedStr) {
		return new BaseMatcher<ByteBuffer>() {
	
			public boolean matches(Object item) {
				if (item instanceof ByteBuffer) {
					ByteBuffer bb = (ByteBuffer) item;
					byte[] expected = expectedStr.getBytes();
	
					if (bb.remaining() != expected.length) return false;
	
					for (int i = bb.position(), j = 0; i < bb.limit(); i++, j++) {
						if (expected[j] != bb.get(i)) return false;
					}
					return true;
				}
				return false;
			}
	
			public void describeTo(Description description) {
				// TODO Auto-generated method stub
	
			}
		};
	
	}

}
