package app.osmosi.heater.store;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.osmosi.heater.model.AppState;

public class AppStatePersister {
    private static final File file = new File("/projects/heater/db/store.json");

    public static void persist(AppState state) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, state);
        } catch (IOException e) {
            System.out.println("Failed to persist the AppState on disk");
        }
    }

    public static AppState loadState() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, AppState.class);
    }
}
