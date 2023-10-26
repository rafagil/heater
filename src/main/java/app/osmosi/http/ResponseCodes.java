package app.osmosi.http;

public enum ResponseCodes {
	OK(200, "OK"),
	BAD_REQUEST(400, "Bad Request"),
	NOT_FOUND(404, "Not Found"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	SERVER_ERROR(500, "Server Error");

	public final String description;
	public final int code;

	private ResponseCodes(int code, String description) {
		this.description = description;
		this.code = code;
	}
}
