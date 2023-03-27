package app.osmosi.heater.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import app.osmosi.heater.model.ScheduleItem;

public class SchedulerTest {
    @Test
    public void getsTheCorrectScheduleItem() {
        List<ScheduleItem> schedule = List.of(new ScheduleItem("Cima", 7, 0, 19),
                                              new ScheduleItem("Cima", 18, 20, 21),
                                              new ScheduleItem("Cima", 23, 0, 20.3),
                                              new ScheduleItem("Baixo", 7, 0, 21),
                                              new ScheduleItem("Baixo", 22, 0, 20),
                                              new ScheduleItem("Baixo", 23, 0, 18));
        Scheduler s = new Scheduler();
        assertEquals(19, s.findScheduleItem(7 * 60, schedule, "Cima").getDesiredTemp(), 0); // 7:00
        assertEquals(21, s.findScheduleItem(7 * 60, schedule, "Baixo").getDesiredTemp(), 0); // 7:00

        assertEquals(19, s.findScheduleItem(12 * 60, schedule, "Cima").getDesiredTemp(), 0); // 12:00
        assertEquals(21, s.findScheduleItem(12 * 60, schedule, "Baixo").getDesiredTemp(), 0); // 12:00

        assertEquals(21, s.findScheduleItem((22 * 60) + 59, schedule, "Cima").getDesiredTemp(), 0); // 22:59
        assertEquals(20, s.findScheduleItem((22 * 60) + 59, schedule, "Baixo").getDesiredTemp(), 0); // 22:59

        assertEquals(20.3, s.findScheduleItem(0, schedule, "Cima").getDesiredTemp(), 0); // 0:00
        assertEquals(18, s.findScheduleItem(0, schedule, "Baixo").getDesiredTemp(), 0); // 0:00

        assertEquals(20.3, s.findScheduleItem(3 * 60, schedule, "Cima").getDesiredTemp(), 0); // 3:00
        assertEquals(18, s.findScheduleItem(3 * 60, schedule, "Baixo").getDesiredTemp(), 0); // 3:00
    }
}
