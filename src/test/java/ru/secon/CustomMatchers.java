package ru.secon;

import java.nio.ByteBuffer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomMatchers {

	protected static final byte Q = '?';

	public static Matcher<ByteBuffer> containsBytes(final String expectedStr) {
		return new BaseMatcher<ByteBuffer>() {
	
			public boolean matches(Object item) {
				if (item instanceof ByteBuffer) {
					ByteBuffer bb = (ByteBuffer) item;
					byte[] expected = expectedStr.getBytes();
	
					if (bb.remaining() != expected.length) return false;
	
					for (int i = bb.position(), j = 0; i < bb.limit(); i++, j++) {
						if (expected[j] == Q) continue;
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

	public static Matcher<ByteBuffer> containsBytesAt(final String expectedStr, final int offset) {
		return new BaseMatcher<ByteBuffer>() {

			public boolean matches(Object item) {
				if (item instanceof ByteBuffer) {
					ByteBuffer bb = (ByteBuffer) item;
					byte[] expected = expectedStr.getBytes();
	
					if (bb.remaining() != expected.length) return false;

					for (int i = offset, j = 0; i < bb.limit(); i++, j++) {
						if (expected[j] == Q) continue;
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
	
	public static Matcher<ByteBuffer> containsBytesInTheBeginning(final String expectedStr) {
		return new BaseMatcher<ByteBuffer>() {

			public boolean matches(Object item) {
				if (item instanceof ByteBuffer) {
					ByteBuffer bb = (ByteBuffer) item;
					byte[] expected = expectedStr.getBytes();
					
					int bytesInTheBeginningCnt = bb.position();
					if (bytesInTheBeginningCnt != expected.length) return false;
					
					for (int i = 0; i < expected.length; i++) {
						if (expected[i] == Q) continue;
						if (expected[i] != bb.get(i)) return false;
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
