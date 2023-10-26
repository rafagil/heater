package app.osmosi.heater.utils;

public interface Worker {
	public void start();

	public void stop();

	public boolean isRunning();
}
