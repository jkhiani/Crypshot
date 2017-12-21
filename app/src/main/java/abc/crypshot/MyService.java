package abc.crypshot;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;
import java.util.Timer;

public class MyService extends Service {

    Timer timer = new Timer();
    public boolean hasWifi;
    public String currencyCode = "cad";

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e("Timer", "PRE");
            new RetrieveLiteCoinPriceBackground().execute();
            Log.e("Timer", "POST");
        }
    };

    public MyService() {
    }

    public void onCreate() {
        timer.schedule(timerTask, 1*1000, 5*1000);
    }

    public void onStartCommand(){

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class RetrieveLiteCoinPriceBackground extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            if (hasWifi) {
                try {
                    URL url = new URL("https://api.cryptonator.com/api/ticker/ltc-" + currencyCode);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    try {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();

                        return stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                }
            }

            return "wifi not available";
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }

            if (!response.equals("wifi not available") && !response.equals("THERE WAS AN ERROR")){
                try {
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    String requestID = object.getString("ticker");

                    JSONObject object2 = (JSONObject) new JSONTokener(requestID).nextValue();
                    String requestID2 = object2.getString("price");

                    if (Double.parseDouble(requestID2) < 77){

                        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(android.R.drawable.stat_notify_error)
                                .setContentTitle("Price Alert")
                                .setContentText("A trigger has been reached.");
                        mBuilder.setDefaults(Notification.DEFAULT_ALL);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                        notificationManager.notify(1, mBuilder.build());*/
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
