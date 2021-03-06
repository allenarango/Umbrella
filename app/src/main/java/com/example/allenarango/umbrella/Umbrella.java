package com.example.allenarango.umbrella;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Umbrella extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    protected String latitude;
    protected String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umbrella);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Button alarmButton = (Button) findViewById(R.id.button);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });


        Button cancelAlarmButton = (Button) findViewById(R.id.button2);
        cancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });
    }

    protected void setAlarm() {
        Alarm alarm = new Alarm();
        alarm.setAlarm(this,latitude,longitude);
    }

    protected void cancelAlarm() {
        Alarm alarm = new Alarm();
        alarm.cancelAlarm(this,latitude,longitude);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_umbrella, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i("info","onConnected");
        WebServiceTask webserviceTask = new WebServiceTask();
        webserviceTask.execute(String.valueOf(mCurrentLocation.getLatitude()),String.valueOf(mCurrentLocation.getLongitude()));
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("error","onConnectedFailed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private class WebServiceTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String useUmbrellaStr = "Don't know, sorry about that.";
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=json&units=metric&cnt=1");
                urlConnection = (HttpURLConnection) url.openConnection();
                useUmbrellaStr = useUmbrella(urlConnection.getInputStream());
            } catch (IOException e) {
                Log.e("MainActivity", "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return useUmbrellaStr;
        }
        protected String useUmbrella(InputStream in) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                Log.i("JSON Response", stringBuilder.toString());
                //Parse JSON
                JSONObject forecastJson = new JSONObject(stringBuilder.toString());
                JSONArray weatherArray = forecastJson.getJSONArray("list");
                JSONObject todayForecast = weatherArray.getJSONObject(0);

                if (todayForecast.has("rain") || todayForecast.has("snow")) {
                    return ("Yes");
                }
                else {
                    return ("No");
                }
            }
            catch (Exception e) {
                Log.e("MainActivity", "Error", e);
            }
            finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    }
                    catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return "Don't know, sorry about that.";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView textview = (TextView) findViewById(R.id.hello);
            textview.setText("Should I take an umbrella today? "+s);
        }
    }

}
