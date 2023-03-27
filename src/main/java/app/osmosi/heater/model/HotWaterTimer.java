package app.osmosi.heater.model;

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

    // public String asJSON() {
    //     Sblurb.object()
    //         .number("dayTriggered", this.getDayTriggered())
    //         .number("minutes", this.getMinutes())
    //         .serialize();

    //     Sblurb.object(Sblurb.number("key", 13), S)
    // All functions return string. An object is just a list of strings joined with ","
    // }
}
