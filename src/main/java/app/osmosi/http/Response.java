package app.osmosi.http;

import app.osmosi.heater.model.JsonObject;

public class Response {
	private final ResponseCodes code;
	private final String body;
	private final String contentType;

	public Response(ResponseCodes code, String body, String contentType) {
		this.code = code;
		this.body = body;
		this.contentType = contentType;
	}

	public Response(ResponseCodes code, String body) {
		this(code, body, "text/html");
	}

	public Response(String body) {
		this(ResponseCodes.OK, body);
	}

	public Response(ResponseCodes code, JsonObject obj) {
		this.code = code;
		this.body = obj.asJson();
		this.contentType = "application/json";
	}

	public Response(JsonObject obj) {
		this(ResponseCodes.OK, obj);
	}

	public String getBody() {
		return body;
	}

	public ResponseCodes getCode() {
		return code;
	}

	public String getContentType() {
		return contentType;
	}
}
