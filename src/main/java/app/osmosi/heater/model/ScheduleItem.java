package app.osmosi.heater.model;

import java.util.Objects;

public class ScheduleItem {
  private final String floorName;
  private final int hours;
  private final int minutes;
  private final double desiredTemp;

  public ScheduleItem(String floorName, int hours, int minutes, double desiredTemp) {
    this.floorName = floorName;
    this.hours = hours;
    this.minutes = minutes;
    this.desiredTemp = desiredTemp;
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
        && Objects.equals(floorName, that.floorName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(floorName, hours, minutes, desiredTemp);
  }
}
