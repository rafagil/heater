package app.osmosi.heater.adapters.http;

import java.util.Optional;
import java.util.function.Predicate;

public class RequestConfig {
	private final String identifier;
	private final String onURL;
	private final String offURL;
	private final String method;
	private final Optional<String> onPayload;
	private final Optional<String> offPayload;

	public RequestConfig(String identifier, String onURL, String offURL, String method, Optional<String> onPayload,
			Optional<String> offPayload) {
		this.identifier = identifier;
		this.onURL = onURL;
		this.offURL = offURL;
		this.method = method;
		this.onPayload = onPayload;
		this.offPayload = offPayload;
	}

	public RequestConfig(String identifier, String onURL, String offURL, String method, String onPayload,
			String offPayload) {
		this(identifier,
				onURL,
				offURL,
				method,
				Optional.ofNullable(onPayload).filter(Predicate.not(String::isBlank)),
				Optional.ofNullable(offPayload).filter(Predicate.not(String::isBlank)));
	}

	public RequestConfig(String identifier, String onURL, String offURL, String method) {
		this.identifier = identifier;
		this.onURL = onURL;
		this.offURL = offURL;
		this.method = method;
		this.onPayload = Optional.empty();
		this.offPayload = Optional.empty();
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getOnURL() {
		return onURL;
	}

	public String getOffURL() {
		return offURL;
	}

	public String getMethod() {
		return method;
	}

	public Optional<String> getOnPayload() {
		return onPayload;
	}

	public Optional<String> getOffPayload() {
		return offPayload;
	}
}
