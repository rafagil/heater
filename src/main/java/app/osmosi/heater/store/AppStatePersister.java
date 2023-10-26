package app.osmosi.heater.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.Logger;

public class AppStatePersister {
	private static final File file = new File(Env.DB_PATH + "/store.data");

	public static void persist(AppState state) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(state);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			Logger.error("Failed to persist the AppState on disk");
			e.printStackTrace();
		}
	}

	public static AppState loadState() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		AppState state = (AppState) ois.readObject();
		ois.close();
		return state;
	}
}
