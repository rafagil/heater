package app.osmosi.heater;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.http.HttpServer;
import app.osmosi.http.Request;
import app.osmosi.http.Response;
import app.osmosi.http.ResponseCodes;

/**
 * Main Class
 *
 */
public class App {
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
        Api.updateFloor(floor.setDesiredTemp(newTemp));
        return new Response(Api.getCurrentState());
    }

    private static Response warmer(Request req) {
        return step(req, (a, b) -> a + b);
    }

    private static Response cooler(Request req) {
        return step(req, (a, b) -> a - b);
    }

    public static void main( String[] args ) {
        Api.init();

        AppController app = new AppController();
        app.get("/status", req -> new Response(Api.getCurrentState()));
        app.get("/set-actual-temp", req -> updateFloor(req, (f, t) -> Api.updateFloor(f.setActualTemp(t))), "name", "temp");
        app.get("/set-desired-temp", req -> updateFloor(req, (f, t) -> Api.updateFloor(f.setDesiredTemp(t))), "name", "temp");
        app.get("/warmer", App::warmer, "name");
        app.get("/cooler", App::cooler, "name");

        HttpServer server = new HttpServer();
        server.start(8080, app.allRoutes());
    }
}
