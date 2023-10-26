package app.osmosi.http;

import java.util.Map;

public class Request {
	private final String path;
	private final String method;
	private final Map<String, String> queryParams;

	public Request(String path, String method, Map<String, String> queryParams) {
		this.path = path;
		this.method = method;
		this.queryParams = queryParams;
	}

	public String getPath() {
		return path;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

}
