package abc.crypshot;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    TextView bitcoinText;
    TextView ethereumText;
    TextView rippleText;
    TextView liteText;

    public String currencyCode="CAD";
    String currency;

    Spinner spinner;
    ArrayAdapter<CharSequence>adapter;

    public boolean hasWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this, MyService.class);
        ///startService(intent);

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        bitcoinText = (TextView) findViewById(R.id.bitcoinTextView);
        ethereumText = (TextView) findViewById(R.id.ethereumTextView);
        rippleText = (TextView) findViewById(R.id.rippleTextView);
        liteText = (TextView) findViewById(R.id.liteCoinTextView);
        hasWifi = isInternetAvailable(this);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currency = (String) parent.getSelectedItem();

                if (currency.equals("Canadian Dollar")) {
                    currencyCode = "cad";
                } else if (currency.equals("US Dollar")) {
                    currencyCode = "usd";
                } else if (currency.equals("British Pound Sterling")) {
                    currencyCode = "gbp";
                } else if (currency.equals("Japanese Yen")) {
                    currencyCode = "jpy";
                } else if (currency.equals("Chinese Yuan")) {
                    currencyCode = "cny";
                } else if (currency.equals("Euro")) {
                    currencyCode = "eur";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button queryButton = (Button) findViewById(R.id.mainButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveBitcoinPrice().execute();
                new RetrieveEthereumPrice().execute();
                new RetrieveRipplePrice().execute();
                new RetrieveLiteCoinPrice().execute();
            }
        });
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } else {
            return false;
        }
        return false;
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
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(android.R.drawable.stat_notify_error)
                                .setContentTitle("Price Alert")
                                .setContentText("A trigger has been reached.");
                        mBuilder.setDefaults(Notification.DEFAULT_ALL);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                        notificationManager.notify(1, mBuilder.build());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class RetrieveLiteCoinPrice extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            liteText.setText("");
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

            if (response.equals("wifi not available")){
                liteText.setText("No Internet Connection");
            }

            if (!response.equals("wifi not available") && !response.equals("THERE WAS AN ERROR")){
                try {
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    String requestID = object.getString("ticker");

                    JSONObject object2 = (JSONObject) new JSONTokener(requestID).nextValue();
                    String requestID2 = object2.getString("price");

                    if (currencyCode.equals("jpy")){
                        liteText.setText("LTC - "+requestID2.substring(0,7));
                    }
                    else if (currencyCode.equals("cny")){
                        liteText.setText("LTC - "+requestID2.substring(0,6));
                    }
                    else{
                        liteText.setText("LTC - "+requestID2.substring(0,5));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class RetrieveRipplePrice extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            rippleText.setText("");
        }

        protected String doInBackground(Void... urls) {
            if (hasWifi){
                try {
                    URL url = new URL("https://api.cryptonator.com/api/ticker/xrp-"+currencyCode);

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
            return null;
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            if (!response.equals("wifi not available")  && !response.equals("THERE WAS AN ERROR")) {
                try {
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    String requestID = object.getString("ticker");

                    JSONObject object2 = (JSONObject) new JSONTokener(requestID).nextValue();
                    String requestID2 = object2.getString("price");

                    rippleText.setText("XRP - " + requestID2.substring(0, 6));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class RetrieveEthereumPrice extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            ethereumText.setText("");
        }

        protected String doInBackground(Void... urls) {
            if (hasWifi){
                try {
                    URL url = new URL("https://api.cryptonator.com/api/ticker/eth-"+currencyCode);

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
            return null;
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

                    if (currencyCode.equals("jpy")){
                        ethereumText.setText("ETH - "+requestID2.substring(0,8));
                    }
                    else if (currencyCode.equals("cny")){
                        ethereumText.setText("ETH - "+requestID2.substring(0,7));
                    }
                    else{
                        ethereumText.setText("ETH - "+requestID2.substring(0,6));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class RetrieveBitcoinPrice extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            bitcoinText.setText("");
        }

        protected String doInBackground(Void... urls) {
            if (hasWifi){
                try {
                    URL url = new URL("https://api.cryptonator.com/api/ticker/btc-"+currencyCode);

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
            return null;
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

                    if (currencyCode.equals("jpy")){
                        bitcoinText.setText("BTC - "+requestID2.substring(0,9));
                    }
                    else if (currencyCode.equals("cny")){
                        bitcoinText.setText("BTC - "+requestID2.substring(0,8));
                    }
                    else{
                        bitcoinText.setText("BTC - "+requestID2.substring(0,7));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
