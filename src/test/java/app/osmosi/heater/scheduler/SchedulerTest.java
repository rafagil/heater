package app.osmosi.heater.scheduler;

import static org.junit.Assert.assertEquals;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import app.osmosi.heater.model.Device;
import app.osmosi.heater.model.ScheduleItem;

public class SchedulerTest {
	@Test
	public void getsTheCorrectScheduleItem() {
		var allWeek = Set.of(DayOfWeek.values());
		var allDevices = Set.of(Device.values());
		List<ScheduleItem> schedule = List.of(new ScheduleItem("Cima", 7, 0, 19, allWeek, allDevices),
				new ScheduleItem("Cima", 18, 20, 21, allWeek, allDevices),
				new ScheduleItem("Cima", 23, 0, 20.3, allWeek, allDevices),
				new ScheduleItem("Baixo", 7, 0, 21, allWeek, allDevices),
				new ScheduleItem("Baixo", 22, 0, 20, allWeek, allDevices),
				new ScheduleItem("Baixo", 23, 0, 18, allWeek, allDevices));
		Scheduler s = new Scheduler();
		assertEquals(19, s.findScheduleItem(7 * 60, DayOfWeek.MONDAY, schedule, "Cima").get().getDesiredTemp(),
				0); // 7:00
		assertEquals(21, s.findScheduleItem(7 * 60, DayOfWeek.MONDAY, schedule, "Baixo").get().getDesiredTemp(),
				0); // 7:00

		assertEquals(19, s.findScheduleItem(12 * 60, DayOfWeek.MONDAY, schedule, "Cima").get().getDesiredTemp(),
				0); // 12:00
		assertEquals(21, s.findScheduleItem(12 * 60, DayOfWeek.MONDAY, schedule, "Baixo").get()
				.getDesiredTemp(), 0); // 12:00

		assertEquals(21, s.findScheduleItem((22 * 60) + 59, DayOfWeek.MONDAY, schedule, "Cima").get()
				.getDesiredTemp(), 0); // 22:59
		assertEquals(20, s.findScheduleItem((22 * 60) + 59, DayOfWeek.MONDAY, schedule, "Baixo").get()
				.getDesiredTemp(), 0); // 22:59

		assertEquals(20.3, s.findScheduleItem(0, DayOfWeek.MONDAY, schedule, "Cima").get().getDesiredTemp(), 0); // 0:00
		assertEquals(18, s.findScheduleItem(0, DayOfWeek.MONDAY, schedule, "Baixo").get().getDesiredTemp(), 0); // 0:00

		assertEquals(20.3,
				s.findScheduleItem(3 * 60, DayOfWeek.MONDAY, schedule, "Cima").get().getDesiredTemp(),
				0); // 3:00
		assertEquals(18, s.findScheduleItem(3 * 60, DayOfWeek.MONDAY, schedule, "Baixo").get().getDesiredTemp(),
				0); // 3:00
	}

	@Test
	public void getsTheCorrectItemOnDifferentWeekDays() {
		var allDevices = Set.of(Device.values());
		List<ScheduleItem> schedule = List.of(
				new ScheduleItem("Cima", 7, 0, 21, Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
						allDevices),
				new ScheduleItem("Cima", 7, 0, 17, Set.of(DayOfWeek.SUNDAY), allDevices),
				new ScheduleItem("Cima", 17, 0, 22, Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
						allDevices));

		Scheduler s = new Scheduler();
		assertEquals(21, s.findScheduleItem(7 * 60, DayOfWeek.MONDAY, schedule, "Cima").get().getDesiredTemp(),
				0);
		assertEquals(21, s.findScheduleItem(7 * 60, DayOfWeek.TUESDAY, schedule, "Cima").get().getDesiredTemp(),
				0);
		assertEquals(21, s.findScheduleItem(7 * 60, DayOfWeek.WEDNESDAY, schedule, "Cima").get()
				.getDesiredTemp(), 0);

		assertEquals(22, s.findScheduleItem(17 * 60, DayOfWeek.SATURDAY, schedule, "Cima").get()
				.getDesiredTemp(), 0);
		assertEquals(22, s.findScheduleItem(6 * 60, DayOfWeek.MONDAY, schedule, "Cima").get().getDesiredTemp(),
				0);

		assertEquals(17, s.findScheduleItem(7 * 60, DayOfWeek.SUNDAY, schedule, "Cima").get().getDesiredTemp(),
				0);
	}
}
