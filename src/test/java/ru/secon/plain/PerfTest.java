package ru.secon.plain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import ru.secon.MessageCounter;

public class PerfTest {

	private static AtomicInteger count = new AtomicInteger();
	private static AtomicLong second = new AtomicLong();

	public static void main(String[] args) throws IOException {
		File file;
		if (args.length == 0) {
			file = new File("data.txt");
		} else {
			file = new File(args[0]);
		}

		FileChannel channel = new FileInputStream(file).getChannel();

		ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 800);

		UpdateListener updateListener = new UpdateListener();
		TopOfBook tob = new TopOfBook(updateListener);
		MessageCounter counter = new MessageCounter() {

			public void processed() {
				count.incrementAndGet();
			}
		};

		MessageReader messageReader = new MessageReader(tob, counter);

		System.out.print("Ready to start. Press ENTER...");
		System.in.read();

		new Timer(true).schedule(new TimerTask() {

			@Override
			public void run() {
				long current = System.currentTimeMillis() / 1000;
				long prev = second.getAndSet(current);

				long secs = current - prev;
				System.out.println(count.getAndSet(0) / secs + "\t per second (time lag: "
						+ (secs - 1) + ")");

			}

		}, 1000, 1000);

		while (channel.read(buffer) != -1) {
			buffer.flip();
			System.out.println("read");

			messageReader.processBuffer(buffer);
		}
	}

}
