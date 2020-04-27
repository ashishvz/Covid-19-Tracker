package com.example.covid_19tracker;

import android.os.AsyncTask;

import com.example.covid_19tracker.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchData extends AsyncTask<Void,Void,Void> {
    String data="";
    String newConfirmed,totalConfirmed,newDeaths,totalDeaths,newRecovered,totalRecovered;
    @Override
    protected Void doInBackground(Void... voids) {
        try{
            URL url = new URL(" https://api.covid19api.com/summary");
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while (line!=null)
            {
                line = bufferedReader.readLine();
                data=data+line;
            }
            JSONObject json = (JSONObject) new JSONTokener(data).nextValue();
            JSONObject json2 = json.getJSONObject("Global");
            newConfirmed= String.valueOf(json2.get("NewConfirmed"));
            totalConfirmed=String.valueOf(json2.get("TotalConfirmed"));
            newDeaths=String.valueOf(json2.get("NewDeaths"));
            totalDeaths=String.valueOf(json2.get("TotalDeaths"));
            newRecovered=String.valueOf(json2.get("NewRecovered"));
            totalRecovered=String.valueOf(json2.get("TotalRecovered"));


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.newConfirmed.setText(newConfirmed);
        MainActivity.totalConfirmed.setText(totalConfirmed);
        MainActivity.newDeaths.setText(newDeaths);
        MainActivity.totalDeaths.setText(totalDeaths);
        MainActivity.newRecovered.setText(newRecovered);
        MainActivity.totalRecovered.setText(totalRecovered);
    }
}
