package app.osmosi.heater.model;

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
}
