package com.rodrigo.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    DownloadTask task;
    ArrayList<JSONObject> a;
    //EditText city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hides the key board
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(findViewById(R.id.city).getWindowToken(),0);
    }

    public void checkCityWeather(View view){

        EditText city = (EditText) findViewById(R.id.city);

        if (city.getText().toString().matches(".*\\d+.*") || city.getText().toString().isEmpty()){
            Toast.makeText(this, "Invalid city name!", Toast.LENGTH_SHORT).show();
            ((TextView) findViewById(R.id.weather)).setText("");
        }else{
            String result = null;
            a = new ArrayList<>();
            //a.clear();
            task = new DownloadTask();
            try {
                // URLEncoder.encode(city.getText().toString(), "UTF-8");
                result = task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(city.getText().toString(), "UTF-8") + "&APPID=64ea46746393a4f8f7214272f41a8ccf").get();
                JSONObject jsonObject = new JSONObject(result);
                Log.i("url: ", jsonObject.getString("weather"));
                JSONArray jsonArray = new JSONArray(jsonObject.getString("weather"));

                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonComponent = jsonArray.getJSONObject(i);
                    a.add(jsonComponent);
                }
                ((TextView) findViewById(R.id.weather)).setText(a.get(0).getString("main") + ": " + a.get(0).getString("description"));

            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "weather not found", Toast.LENGTH_SHORT).show();
            } catch (ExecutionException e) {
                e.printStackTrace();
                Toast.makeText(this, "weather not found", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "weather not found", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(this, "weather not found", Toast.LENGTH_SHORT).show();
            }
        }
        //((TextView) findViewById(R.id.weather)).setText("This works!");
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            StringBuffer stringBuffer = new StringBuffer();
            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line;
                while ((line = in.readLine()) != null){
                    stringBuffer.append(line);
                    stringBuffer.append('\n');
                }

                return stringBuffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Connection Failed!";
        }
    }
}
