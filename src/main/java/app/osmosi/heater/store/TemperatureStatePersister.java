package app.osmosi.heater.store;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.FileUtils;
import app.osmosi.heater.utils.Logger;
import app.osmosi.heater.utils.OrgTableParser;

public class TemperatureStatePersister {
	private static final File file = new File(Env.DB_PATH + "/temps.org");

	public static void persist(AppState state) {
		try {
			StringBuilder out = new StringBuilder("|Zone | Temperature|\n");
			state.getFloors()
					.forEach(zone -> {
						out.append("|").append(zone.getName())
								.append("|").append(zone.getActualTemp())
								.append("|\n");
					});
			FileUtils.write(file, out.toString());
		} catch (IOException e) {
			Logger.error("Failed to persist temperatures.");
			e.printStackTrace();
		}
	}

	public static Map<String, Double> load() {
		OrgTableParser parser = new OrgTableParser();
		Map<String, Double> out = new HashMap<>();
		try {
			parser.parseFile(file).forEach(row -> {
				out.put(row.get("zone"), Double.parseDouble(row.get("temperature")));
			});
		} catch (IOException e) {
			Logger.info("Could not find persisted temperatures file. Using default temps.");
		}
		return out;
	}
}
