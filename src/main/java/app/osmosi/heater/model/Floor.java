package app.osmosi.heater.model;

public class Floor {
    private String name;
    private double desiredTemp;
    private double setBackTemp;
    private double actualTemp;
    private Switch heaterState;
    private String mqtt;
    private int sonoffChannel;
    private float lastUpdate;

    public Floor(String name, double desiredTemp, double setBackTemp, double actualTemp, Switch heaterState, String mqtt, int sonoffChannel, float lastUpdate) {
        this.name = name;
        this.desiredTemp = desiredTemp;
        this.setBackTemp = setBackTemp;
        this.actualTemp = actualTemp;
        this.heaterState = heaterState;
        this.mqtt = mqtt;
        this.sonoffChannel = sonoffChannel;
        this.lastUpdate = lastUpdate;
    }

    public Floor setActualTemp(double actualTemp) {
        Floor f = from(this);
        f.actualTemp = actualTemp;
        return f;
    }

    public Floor setDesiredTemp(double desiredTemp) {
        Floor f = from(this);
        f.desiredTemp = desiredTemp;
        return f;
    }

    public Floor setSetBackTemp(double setBackTemp) {
        Floor f = from(this);
        f.setBackTemp = setBackTemp;
        return f;
    }

    public Floor setHeaterState(Switch heaterState) {
        Floor f = from(this);
        f.heaterState = heaterState;
        return f;
    }

    private Floor from(Floor f) {
        return new Floor(f.getName(), f.getDesiredTemp(), f.getSetBackTemp(), f.getActualTemp(), f.getHeaterState(),
                f.getMqtt(), f.getSonoffChannel(), f.getLastUpdate());
    }

    public float getLastUpdate() {
        return lastUpdate;
    }

    public int getSonoffChannel() {
        return sonoffChannel;
    }

    public String getMqtt() {
        return mqtt;
    }

    public Switch getHeaterState() {
        return heaterState;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getActualTemp() {
        return actualTemp;
    }

    public double getSetBackTemp() {
        return setBackTemp;
    }

    public double getDesiredTemp() {
        return setBackTemp > 0 ? setBackTemp : desiredTemp;
    }

    public String getName() {
        return name;
    }
}
