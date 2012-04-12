package ru.secon;

import static org.easymock.EasyMock.getCurrentArguments;

import java.nio.ByteBuffer;

import org.easymock.IAnswer;

import ru.secon.fast.MessageReader;

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
		ByteBuffer buf = (ByteBuffer) args[buffersArgIndex];
		int offset = ((Integer) args[offsetArgIndex]).intValue();

		value = new String(buf.array(), offset, MessageReader.SYMBOL_LENGTH);

		return null;
	}

	public String getValue() {
		return value;
	}

}