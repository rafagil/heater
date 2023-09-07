package app.osmosi.heater.model;

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.Set;

public class ScheduleItem {
  private final String floorName;
  private final int hours;
  private final int minutes;
  private final double desiredTemp;
  private final Set<DayOfWeek> daysOfWeek;
  private final Set<Device> devices;

  public ScheduleItem(String floorName, int hours, int minutes, double desiredTemp, Set<DayOfWeek> daysOfWeek, Set<Device> devices) {
    this.floorName = floorName;
    this.hours = hours;
    this.minutes = minutes;
    this.desiredTemp = desiredTemp;
    this.daysOfWeek = daysOfWeek;
    this.devices = devices;
  }

  public double getDesiredTemp() {
    return desiredTemp;
  }

  public int getMinutes() {
    return minutes;
  }

  public int getHours() {
    return hours;
  }

  public String getFloorName() {
    return floorName;
  }

  public Set<Device> getDevices() {
    return devices;
  }

  public Set<DayOfWeek> getDaysOfWeek() {
    return daysOfWeek;
  }

  public int getTotalMinutes() {
    return (this.getHours() * 60) + this.getMinutes();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ScheduleItem that = (ScheduleItem) o;
    return hours == that.hours && minutes == that.minutes && Double.compare(that.desiredTemp, desiredTemp) == 0
        && Objects.equals(floorName, that.floorName) && Objects.equals(daysOfWeek, that.daysOfWeek)
        && Objects.equals(devices, that.devices);
  }

  @Override
  public int hashCode() {
      return Objects.hash(floorName, hours, minutes, desiredTemp, daysOfWeek, devices);
  }
}
