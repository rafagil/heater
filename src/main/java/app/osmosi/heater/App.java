package app.osmosi.heater;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.scheduler.Scheduler;
import app.osmosi.heater.timer.Timer;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.FileUtils;
import app.osmosi.http.HttpServer;
import app.osmosi.http.Request;
import app.osmosi.http.Response;
import app.osmosi.http.ResponseCodes;

/**
 * Main Class
 *
 */
public class App {
  private static final Timer hwTimer = new Timer();
  private static final Scheduler scheduler = new Scheduler();

  private static Floor getFloor(Request req, AppState state) {
    String name = req.getQueryParams().get("name");
    return state.getFloorByName(name);
  }

  private static Response updateFloor(Request req, BiConsumer<Floor, Double> fn) {
    AppState state = Api.getCurrentState();
    Double temp = Double.valueOf(req.getQueryParams().get("temp"));
    Floor floor = getFloor(req, state);
    if (floor == null) {
      return new Response(ResponseCodes.NOT_FOUND, "Floor not found");
    }
    fn.accept(floor, temp);

    return new Response(Api.getCurrentState());
  }

  private static Response step(Request req, BiFunction<Double, Double, Double> fn) {
    AppState state = Api.getCurrentState();
    Floor floor = getFloor(req, state);
    if (floor == null) {
      return new Response(ResponseCodes.NOT_FOUND, "Floor not found");
    }
    double newTemp = fn.apply(floor.getDesiredTemp(), 0.5);
    Api.updateFloor(floor.withDesiredTemp(newTemp));
    return new Response(Api.getCurrentState());
  }

  private static Response warmer(Request req) {
    return step(req, (temp, delta) -> temp + delta);
  }

  private static Response cooler(Request req) {
    return step(req, (temp, delta) -> temp - delta);
  }

  private static Response reloadTimers(Request req) {
    try {
      hwTimer.reloadTimers();
      return new Response("OK");
    } catch (IOException e) {
      return new Response(ResponseCodes.SERVER_ERROR, "Could not Reload the timers");
    }
  }

  private static Response turnOnHotWater(Request req) {
    Api.turnOnHotWater(Integer.valueOf(req.getQueryParams().get("timeout")) * 60 * 1000);
    return new Response(Api.getCurrentState().getHotWater());
  }

  private static Response setBack(double temp) {
    AppState state = Api.getCurrentState();
    Api.updateFloor(state.getCima().withSetBackTemp(temp));
    Api.updateFloor(state.getBaixo().withSetBackTemp(temp));
    return new Response(Api.getCurrentState());
  }

  private static Response travelMode(Request req) {
    hwTimer.stop();
    return setBack(15);
  }

  private static Response backHome(Request req) {
    if (!hwTimer.isRunning()) {
      hwTimer.start();
    }
    return setBack(0);
  }

  private static Response balance(Request req) {
    try (Stream<String> lines = FileUtils.read(Monitor.BALANCE_FILE_PATH)) {
      return new Response(lines.findFirst().orElse("-1"));
    } catch (IOException e) {
      return new Response(ResponseCodes.SERVER_ERROR, "Problem loading balance.");
    }
  }

  private static Response updateBalance(Request req) {
    try {
      String value = req.getQueryParams().get("value");
      Double.valueOf(value); // Just to throw the exeption
      FileUtils.write(Monitor.BALANCE_FILE_PATH, value);
      return new Response(value);
    } catch (IOException e) {
      return new Response(ResponseCodes.SERVER_ERROR, "Could not update the balance");
    } catch (NumberFormatException e) {
      return new Response(ResponseCodes.BAD_REQUEST, "value must be a number");
    }
  }

  public static void main(String[] args) throws IOException {
    Api.init();
    Api.syncAdapters();

    AppController app = new AppController();
    // General:
    app.get("/status", req -> new Response(Api.getCurrentState()));
    app.get("/sync-adapters", req -> {
      Api.syncAdapters();
      return new Response(Api.getCurrentState());
    });

    // Heater:
    app.get("/set-actual-temp", req -> updateFloor(req, (f, t) -> Api.updateFloor(f.withActualTemp(t)
        .withLastUpdate(System.currentTimeMillis()))), "name", "temp");
    app.get("/set-desired-temp", req -> updateFloor(req, (f, t) -> Api.updateFloor(f.withDesiredTemp(t))), "name",
        "temp");
    app.get("/warmer", App::warmer, "name");
    app.get("/cooler", App::cooler, "name");
    // app.get("/reload-schedule") -> should be equal to hotwater schedule
    // (persisted on AppState)

    // Hot Water
    app.get("/reload-timers", App::reloadTimers);
    app.get("/turn-on-hw", App::turnOnHotWater, "timeout");

    // Scheduler:
    app.get("/out-of-home", r -> setBack(19));
    app.get("/back-home", App::backHome);
    app.get("/travel-mode", App::travelMode);

    // Monitoring:
    app.get("/balance", App::balance);
    app.get("/update-balance", App::updateBalance, "value");

    hwTimer.reloadTimers();
    hwTimer.start();
    scheduler.start();
    Monitor.start();

    if (Env.DEBUG) {
      System.out.println("Running in DEBUG mode");
    }

    int port = 8080;
    if (args.length > 0) {
      port = Integer.valueOf(args[0]);
    }
    HttpServer server = new HttpServer();
    server.start(port, app.allRoutes());
  }
}
