package net.whirvis.mc.discraft.bot.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A cached value.
 * <p>
 * This container should be used in order to contain a value that must first be
 * fetched from another source. This value may be updated later on, and must be
 * able to be stored somewhere else not immediately.
 * 
 * @param <T>
 *            the value type.
 */
public class Cached<T> {

	private final Supplier<T> fetch;
	private final Consumer<T> flush;
	private T value;
	private boolean fetched;

	public Cached(Supplier<T> fetch, Consumer<T> flush) {
		this.fetch = fetch;
		this.flush = flush;
	}

	public Cached(T value, Supplier<T> fetch, Consumer<T> flush) {
		this(fetch, flush);
		this.set(value);
	}

	public T get() {
		if (!fetched) {
			this.value = fetch.get();
			this.fetched = true;
		}
		return this.value;
	}

	public void set(T value) {
		this.value = value;
		this.fetched = true;
	}

	public void flush() {
		flush.accept(value);
	}

}
