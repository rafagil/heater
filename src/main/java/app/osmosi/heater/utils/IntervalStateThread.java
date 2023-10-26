package app.osmosi.heater.utils;

import java.util.function.Function;

public class IntervalStateThread<T> implements Worker {

	private boolean running = false;
	private final int timeout;
	private final Function<T, T> fn;
	private T state;

	public IntervalStateThread(Function<T, T> fn, int timeout) {
		this.fn = fn;
		this.timeout = timeout;
	}

	public IntervalStateThread(Function<T, T> fn, int timeout, T initialState) {
		this.fn = fn;
		this.timeout = timeout;
		this.state = initialState;
	}

	@Override
	public void start() {
		running = true;
		new Thread(() -> {
			while (running) {
				this.state = fn.apply(this.state);
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					running = false;
				}
			}
		}).start();
	}

	@Override
	public void stop() {
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}
}
