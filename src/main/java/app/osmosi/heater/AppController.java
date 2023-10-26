package app.osmosi.heater;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import app.osmosi.http.Request;
import app.osmosi.http.Response;
import app.osmosi.http.ResponseCodes;

public class AppController {
	private final Map<String, Function<Request, Response>> routes = new HashMap<>();

	public void get(String path, Function<Request, Response> handler, String... requiredParams) {
		routes.put(path, req -> {
			if (!req.getMethod().equals("GET")) {
				return new Response(ResponseCodes.METHOD_NOT_ALLOWED,
						req.getMethod() + " method is not allowed for " + req.getPath());
			}
			if (requiredParams.length > 0) {
				String missing = Arrays.stream(requiredParams)
						.filter(p -> req.getQueryParams().get(p) == null)
						.collect(Collectors.joining(","));
				if (missing.length() > 0) {
					String errorMsg = "Missing mandatory parameters: [" + missing + "]";
					return new Response(ResponseCodes.BAD_REQUEST, errorMsg);
				}
			}
			return handler.apply(req);
		});
	}

	public Function<Request, Response> allRoutes() {
		return req -> {
			Function<Request, Response> handler = routes.get(req.getPath());
			if (handler == null) {
				return new Response(ResponseCodes.NOT_FOUND, "Not Found");
			}
			return handler.apply(req);
		};
	}
}
