package app.osmosi.http;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import app.osmosi.http.exceptions.RequestParseException;

public class RequestParser {
	private static String indexOrEmpty(int index, String[] arr) {
		if (index >= arr.length) {
			return "";
		}
		return arr[index];
	}

	public static Request parse(String firstLine, BufferedReader bfIn) throws RequestParseException {
		String[] parts = firstLine.split(" ");
		if (parts.length != 3) {
			throw new RequestParseException();
		}
		String method = parts[0];
		String[] fullPath = parts[1].split("\\?");
		String path = fullPath[0];
		Map<String, String> queryParams;

		if (fullPath.length > 1) {
			String query = fullPath[1];
			queryParams = Arrays.stream(query.split("&"))
					.map(s -> s.split("="))
					.collect(Collectors.toMap(a -> a[0], s -> indexOrEmpty(1, s)));
		} else {
			queryParams = new HashMap<>();
		}

		return new Request(path, method, queryParams);
	}
}
