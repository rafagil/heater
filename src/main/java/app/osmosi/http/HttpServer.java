package app.osmosi.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import app.osmosi.heater.utils.Logger;
import app.osmosi.http.exceptions.RequestParseException;

public class HttpServer {
  private boolean running = false;
  private static final int MAX_THREADS = 8;

  private void processRequest(Socket connection, Function<Request, Response> onRequestFn) {
    PrintStream printStream = null;
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      OutputStream out = new BufferedOutputStream(connection.getOutputStream());
      printStream = new PrintStream(out);
      String request = in.readLine();
      if (request != null) {
        try {
          Request req = RequestParser.parse(request, in);
          Response resp = onRequestFn.apply(req);
          printStream.println("HTTP/1.1 " + resp.getCode().code + " " + resp.getCode().description);
          printStream.println("Content-type:" + resp.getContentType());
          printStream.println();
          printStream.println(resp.getBody());
          printStream.println();
        } catch (RequestParseException e) {
          printStream.println("HTTP/1.1 400 Bad Request");
          printStream.println();
        }
      }
    } catch (Throwable t) {
      Logger.error("Error Processing the request");
      if (printStream != null) {
        printStream.println("HTTP/1.1 500 Server Error");
        printStream.println();
      }
    } finally {
      if (printStream != null) {
        printStream.close();
      }
    }
  }

  public void start(Function<Request, Response> onRequestFn) {
    start(8080, onRequestFn);
  }

  public void start(int port, Function<Request, Response> onRequestFn) {
    running = true;
    try {
      Logger.info("Starting HTTP Server on port " + port);
      ServerSocket socket = new ServerSocket(port);
      ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
      while (running) { // TODO: Improve this to close the socket immediately on "stop()"
        Socket connection = socket.accept();
        pool.execute(() -> processRequest(connection, onRequestFn));
      }
      socket.close();
    } catch (IOException e) {
      Logger.error("Error Creating Socket");
    }
  }

  public void stop() {
    running = false;
  }
}
