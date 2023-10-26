package app.osmosi.heater.utils;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonObjectBuilder {

	private static String nonText(String key, String data) {
		return String.format("\"%s\": %s", key, data);
	}

	public static String number(String key, Number number) {
		return nonText(key, number.toString());
	}

	public static String text(String text) {
		return "\"" + text + "\"";
	}

	public static String text(String key, String text) {
		return String.format("\"%s\": \"%s\"", key, text);
	}

	public static String object(String... fields) {
		return "{" + Arrays.stream(fields).collect(Collectors.joining(",")) + "}";
	}

	public static String key(String key, String value) {
		return nonText(key, value);
	}

	public static String array(String... items) {
		return array(Arrays.stream(items));
	}

	public static String array(Stream<String> items) {
		return "[" + items.collect(Collectors.joining(",")) + "]";
	}
}
