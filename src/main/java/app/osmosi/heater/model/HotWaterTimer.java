package app.osmosi.heater.model;

import java.util.Objects;

public class HotWaterTimer {
  private final int hours;
  private final int minutes;
  private final int timeout;
  private final int dayTriggered;

  public HotWaterTimer(int hours, int minutes, int timeout, int dayTriggered) {
    this.hours = hours;
    this.minutes = minutes;
    this.timeout = timeout;
    this.dayTriggered = dayTriggered;
  }

  public HotWaterTimer withDayTriggered(int dayTriggered) {
    return new HotWaterTimer(this.hours, this.minutes, this.timeout, dayTriggered);
  }

  public int getHours() {
    return hours;
  }

  public int getMinutes() {
    return minutes;
  }

  public int getTimeout() {
    return timeout;
  }

  public int getDayTriggered() {
    return dayTriggered;
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
    HotWaterTimer that = (HotWaterTimer) o;
    return hours == that.hours && minutes == that.minutes && timeout == that.timeout;
  }

  @Override
  public int hashCode() {
    return Objects.hash(hours, minutes, timeout);
  }
}
