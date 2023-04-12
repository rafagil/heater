package app.osmosi.heater.model;

import static app.osmosi.heater.utils.JsonObjectBuilder.*;

import java.io.Serializable;
import java.util.Objects;

public class Floor implements JsonObject, Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private double desiredTemp;
  private double setBackTemp;
  private double actualTemp;
  private Switch heaterState;
  private long lastUpdate;

  public Floor(String name, double desiredTemp, double setBackTemp, double actualTemp, Switch heaterState,
      long lastUpdate) {
    this.name = name;
    this.desiredTemp = desiredTemp;
    this.setBackTemp = setBackTemp;
    this.actualTemp = actualTemp;
    this.heaterState = heaterState;
    this.lastUpdate = lastUpdate;
  }

  public Floor withActualTemp(double actualTemp) {
    Floor f = from(this);
    f.actualTemp = actualTemp;
    return f;
  }

  public Floor withDesiredTemp(double desiredTemp) {
    Floor f = from(this);
    f.desiredTemp = desiredTemp;
    return f;
  }

  public Floor withSetBackTemp(double setBackTemp) {
    Floor f = from(this);
    f.setBackTemp = setBackTemp;
    return f;
  }

  public Floor withHeaterState(Switch heaterState) {
    Floor f = from(this);
    f.heaterState = heaterState;
    return f;
  }

  public Floor withLastUpdate(long lastUpdate) {
    Floor f = from(this);
    f.lastUpdate = lastUpdate;
    return f;
  }

  private Floor from(Floor f) {
    return new Floor(f.name, f.desiredTemp, f.setBackTemp, f.actualTemp, f.heaterState, f.lastUpdate);
  }

  public long getLastUpdate() {
    return lastUpdate;
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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Floor that = (Floor) o;
    return name == that.name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  public String asJson() {
    return object(
        text("name", getName()),
        number("desiredTemp", getDesiredTemp()),
        number("setBackTemp", getSetBackTemp()),
        number("actualTemp", getActualTemp()),
        text("heaterState", getHeaterState().toString()),
        number("lastUpdate", getLastUpdate()));
  }
}
