package app.osmosi.heater.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class OrgTableParser {

	private boolean isDivider(String line) {
		return line.startsWith("|-");
	}

	private String[] parseLine(String line) {
		String[] split = line.split("\\|");
		String[] output = new String[split.length - 1];
		for (int i = 1; i < split.length; i++) {
			output[i - 1] = split[i].trim();
		}
		return output;
	}

	private List<Map<String, String>> parseTable(BufferedReader lines) throws IOException {
		List<Map<String, String>> list = new ArrayList<>();
		String line = lines.readLine();
		while (line != null && (isDivider(line) || line.trim().isEmpty())) {
			line = lines.readLine();
		}
		String[] headers = Arrays.stream(parseLine(line))
				.map(String::toLowerCase)
				.toArray(String[]::new);

		line = lines.readLine();
		while (line != null) {
			if (!isDivider(line) && !line.trim().isEmpty()) {
				Map<String, String> map = new HashMap<>();
				String[] row = parseLine(line);
				for (int i = 0; i < row.length; i++) {
					map.put(headers[i], row[i]);
				}
				list.add(map);
			}
			line = lines.readLine();
		}
		return list;
	}

	public List<Map<String, String>> parseFile(File file) throws IOException {
		return parseTable(new BufferedReader(new FileReader(file)));
	}
}
