package com.example.allenarango.umbrella;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Umbrella extends ActionBarActivity {

    private class WebServiceTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView textview = (TextView) findViewById(R.id.hello);
            textview.setText("Should I take an umbrella today? "+s);
        }

        @Override
        protected String doInBackground(String... params) {
            String useUmbrellaStr = "Don't know, sorry about that.";
            HttpURLConnection urlConnection = null;

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
                //read and parse InputStream
                return "Yes!"; //temporary
            }
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
            //JSON needs to be parsed here
            Log.i("Returned data", stringBuilder.toString());
        } catch (Exception e) {
            Log.e("MainActivity", "Error", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return "Don't know, sorry about that.";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umbrella);
        WebServiceTask webserviceTask = new WebServiceTask();
        webserviceTask.execute("Allen");
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
}
