package app.osmosi.heater.model;

public class HotWater {
    private Switch state;
    private final int sonoffChannel = 4;
    private static long instanceId = 0l;

    public HotWater(Switch state) {
        this.state = state;
        instanceId = instanceId + 1;
        if (instanceId > 1000) {
            instanceId = 0;
        }
    }

    public Switch getState() {
        return state;
    }

    public int getSonoffChannel() {
        return sonoffChannel;
    }

    public long getInstanceId() {
        return instanceId;
    }
}
