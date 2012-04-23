package ru.secon.testutils;

import static org.easymock.EasyMock.getCurrentArguments;
import static ru.secon.Constants.SYMBOL_LENGTH;

import org.easymock.IAnswer;

@SuppressWarnings("rawtypes")
public class CaptureSymbol implements IAnswer {

	private String value;
	private final int buffersArgIndex;
	private final int offsetArgIndex;

	public CaptureSymbol(int bufferArgIndex, int offsetArgIndex) {
		buffersArgIndex = bufferArgIndex;
		this.offsetArgIndex = offsetArgIndex;
	}

	public Object answer() throws Throwable {
		Object[] args = getCurrentArguments();
		byte[] buf = (byte[]) args[buffersArgIndex];
		int offset = ((Integer) args[offsetArgIndex]).intValue();

		value = new String(buf, offset, SYMBOL_LENGTH);

		return null;
	}

	public String getValue() {
		return value;
	}

}