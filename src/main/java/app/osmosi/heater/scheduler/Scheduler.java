package app.osmosi.heater.scheduler;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.ScheduleItem;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.IntervalThread;

public class Scheduler {
	private static final String SCHEDULE_PATH = Env.CONFIG_PATH + "/schedule.csv";
	private IntervalThread intervalThread;

	private int getNowMinutes() {
		LocalTime now = LocalTime.now();
		return (now.getHour() * 60) + now.getMinute();
	}

	private static Comparator<ScheduleItem> byTotalMinutesReversed = Comparator
			.comparing(ScheduleItem::getTotalMinutes)
			.reversed();

	private DayOfWeek theDayBefore(DayOfWeek dayOfWeek) {
		int day = dayOfWeek.getValue() == 1 ? 7 : dayOfWeek.getValue() - 1;
		return DayOfWeek.of(day);
	}

	private Optional<ScheduleItem> findLastSchedule(List<ScheduleItem> items, DayOfWeek dayOfWeek) {
		var found = items.stream().filter(byDayOfWeek(dayOfWeek)).findAny();

		if (found.isPresent()) {
			return found;
		} else {
			return findLastSchedule(items, theDayBefore(dayOfWeek));
		}
	}

	public Optional<ScheduleItem> findScheduleItem(int nowMinutes, DayOfWeek dayOfWeek, List<ScheduleItem> schedule,
			String floorName) {
		List<ScheduleItem> sorted = schedule.stream()
				.filter(i -> i.getFloorName().equals(floorName))
				.sorted(byTotalMinutesReversed).toList();

		Optional<ScheduleItem> optionalItem = sorted.stream()
				.filter(i -> nowMinutes >= i.getTotalMinutes())
				.filter(byDayOfWeek(dayOfWeek))
				.findAny();

		if (optionalItem.isPresent()) {
			return optionalItem;
		} else if (sorted.size() > 0) {
			return findLastSchedule(sorted, theDayBefore(dayOfWeek));// Optional.of(sorted.get(0));
		}

		return Optional.empty();
	}

	public void syncInitialDesiredTemp(List<ScheduleItem> schedule, DayOfWeek dayOfWeek, int nowMinutes) {
		Api.getCurrentState().getFloors().forEach(floor -> {
			Optional<ScheduleItem> item = findScheduleItem(nowMinutes, dayOfWeek, schedule,
					floor.getName());

			if (item.isPresent()) {
				Api.updateFloor(floor.withDesiredTemp(item.get().getDesiredTemp()));
			}
		});
	}

	public void updateFloor(ScheduleItem item) {
		Floor floor = Api.getCurrentState().getFloorByName(item.getFloorName());
		Api.updateFloor(floor.withDesiredTemp(item.getDesiredTemp())
				.withActiveDevices(item.getDevices()));
	}

	public Predicate<ScheduleItem> byDayOfWeek(DayOfWeek dayOfWeek) {
		return scheduleItem -> scheduleItem.getDaysOfWeek().contains(dayOfWeek);
	}

	public void start() throws IOException {
		List<ScheduleItem> currentSchedule = ScheduleParser.parse(new File(SCHEDULE_PATH));
		// Starts with the correct schedule on the first time:
		syncInitialDesiredTemp(currentSchedule, LocalDateTime.now().getDayOfWeek(), getNowMinutes());

		Map<Integer, List<ScheduleItem>> scheduleMap = currentSchedule.stream()
				.collect(Collectors.groupingBy(ScheduleItem::getTotalMinutes));

		intervalThread = new IntervalThread(() -> {
			List<ScheduleItem> items = scheduleMap.get(getNowMinutes());
			if (items != null) {
				items.stream()
						.filter(byDayOfWeek(LocalDateTime.now().getDayOfWeek()))
						.forEach(this::updateFloor);
			}
		}, 60000);
		new Thread(intervalThread).start();
	}

	public void stop() {
		if (intervalThread != null) {
			intervalThread.stop();
		}
	}
}
