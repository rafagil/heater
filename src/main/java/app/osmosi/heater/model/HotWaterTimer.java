package app.osmosi.heater.model;

import java.util.Objects;

public class HotWaterTimer {
  private final int hours;
  private final int minutes;
  private final int timeout;

  public HotWaterTimer(int hours, int minutes, int timeout) {
    this.hours = hours;
    this.minutes = minutes;
    this.timeout = timeout;
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
