package app.osmosi.heater.timer;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.IntervalThread;

public class Timer {
	private static final String FILE_PATH = Env.CONFIG_PATH + "/hw-timers.csv";
	private IntervalThread intervalThread;

	private int getNowMinutes() {
		LocalTime now = LocalTime.now();
		return (now.getHour() * 60) + now.getMinute();
	}

	public void reloadTimers() throws IOException {
		Set<HotWaterTimer> fileTimers = HotWaterTimerParser.parse(new File(FILE_PATH));
		Api.updateTimers(fileTimers);
	}

	public void start() {
		Set<HotWaterTimer> timers = Api.getCurrentState().getTimers();

		Map<Integer, HotWaterTimer> timerMap = timers.stream()
				.collect(Collectors.toMap(HotWaterTimer::getTotalMinutes, Function.identity()));

		intervalThread = new IntervalThread(() -> {
			HotWaterTimer timer = timerMap.get(getNowMinutes());
			if (timer != null) {
				Api.turnOnHotWater(timer.getTimeout());
			}
		}, 60000);

		new Thread(intervalThread).start();
	}

	public void stop() {
		if (intervalThread != null) {
			intervalThread.stop();
		}
	}

	public boolean isRunning() {
		if (intervalThread == null) {
			return false;
		}
		return intervalThread.isRunning();
	}
}
