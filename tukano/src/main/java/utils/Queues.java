package utils;

import java.util.concurrent.BlockingQueue;

/**
 * A collection of convenience methods for dealing with threads.
 *
 * @author smduarte (smd@fct.unl.pt)
 */
public final class Queues {

	private Queues() {
	}

	public static <T> T takeFrom(BlockingQueue<T> queue) {
		while (true)
			try {
				return queue.take();
			} catch (InterruptedException e) {
			}
	}

	public static <T> void putInto(BlockingQueue<T> queue, T val) {
		while (true)
			try {
				queue.put(val);
				return;
			} catch (InterruptedException e) {
			}
	}
}
