package app.osmosi.heater.utils;

public enum LogLevel {
	ERROR(1),
	INFO(2),
	DEBUG(4);

	public final int code;

	private LogLevel(int code) {
		this.code = code;
	}
}
