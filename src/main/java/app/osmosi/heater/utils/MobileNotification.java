package app.osmosi.heater.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MobileNotification {
  private static final String urlString = "https://maker.ifttt.com/trigger/notify_mobile/with/key/c650OOZruDLHqJH6jPONFv/?value1=";

  public static void sendNotification(String text) {
    if (Env.DEBUG) {
      Logger.debug("Sending a notification with: " + text);
    } else {

      try {
        URL url = new URL(urlString + URLEncoder.encode(text, StandardCharsets.UTF_8));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        Logger.debug("Notification Response Code: " + con.getResponseCode());

        con.disconnect();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
