package com.example.covid_19tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static TextView newConfirmed,totalConfirmed,newDeaths,totalDeaths,newRecovered,totalRecovered,
            indianewConfirmed,indiatotalConfirmed,indianewDeaths,indiatotalDeaths,indianewRecovered,indiatotalRecovered,current_loc,
            active_cases,confirmed_cases,death_cases,recovered_cases,st_active_cases,st_confirmed_cases,st_death_cases,st_recovered_cases;
    private RequestQueue requestQueue;
    String City,State;
    Toolbar toolbar;
    String add;
    ArrayList<String> loc_arr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loc_arr=getIntent().getStringArrayListExtra("location_array");
        current_loc=findViewById(R.id.cur_loc);
        newConfirmed=findViewById(R.id.new_confirmed);
        totalConfirmed=findViewById(R.id.total_confirmed);
        newDeaths=findViewById(R.id.new_deaths);
        totalDeaths=findViewById(R.id.total_deaths);
        newRecovered=findViewById(R.id.new_recovered);
        totalRecovered=findViewById(R.id.total_recovered);
        indianewConfirmed=findViewById(R.id.india_new_confirmed);
        indiatotalConfirmed=findViewById(R.id.india_total_confirmed);
        indianewDeaths=findViewById(R.id.india_new_deaths);
        indiatotalDeaths=findViewById(R.id.india_total_deaths);
        indianewRecovered=findViewById(R.id.india_new_recovered);
        indiatotalRecovered=findViewById(R.id.india_total_recovered);
        active_cases=findViewById(R.id.active_cases);
        confirmed_cases=findViewById(R.id.confirmed_cases);
        death_cases=findViewById(R.id.death_cases);
        recovered_cases=findViewById(R.id.recovered_cases);
        st_active_cases=findViewById(R.id.st_active_cases);
        st_confirmed_cases=findViewById(R.id.st_confirmed_cases);
        st_death_cases=findViewById(R.id.st_death_cases);
        st_recovered_cases=findViewById(R.id.st_recovered_cases);
        City=loc_arr.get(0);
        State=loc_arr.get(1);
        if(City.equals("Bagalkot"))
        {
            City="Bagalkote";
        }
        current_loc.append(City+","+State);


        FetchData pro = new FetchData();
        pro.execute();
        requestQueue= Volley.newRequestQueue(this);
        jsonParse();
        getCurrentData();
        getStateData();
    }


    public void getCurrentData()
    {
        String url="https://api.covid19india.org/state_district_wise.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String str =response.getString(State);
                        JSONObject ka_obj = new JSONObject(str);
                        String str1 = ka_obj.getString("districtData");
                        JSONObject dis_obj = new JSONObject(str1);
                        String dis = dis_obj.getString(City);
                        if(!dis.isEmpty()) {
                            JSONObject ob = new JSONObject(dis);
                            active_cases.setText(String.valueOf(ob.getInt("active")));
                            confirmed_cases.setText(String.valueOf(ob.getInt("confirmed")));
                            death_cases.setText(String.valueOf(ob.getInt("deceased")));
                            recovered_cases.setText(String.valueOf(ob.getInt("recovered")));
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"No Data Found for Current Location"+add,Toast.LENGTH_SHORT).show();
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    public void getStateData()
    {
        String url="https://api.rootnet.in/covid19-in/stats/latest";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String str =response.getString("data");
                    JSONObject obj = new JSONObject(str);
                    String str1=obj.getString("regional");
                    JSONArray arr = new JSONArray(str1);
                    for(int i=0;i<arr.length();i++)
                    {
                        JSONObject o = arr.getJSONObject(i);
                        String loc = o.getString("loc");
                        if(loc.equals(State))
                        {
                            st_active_cases.setText(String.valueOf((o.getInt("confirmedCasesIndian"))-(o.getInt("discharged"))-(o.getInt("deaths"))));
                            st_recovered_cases.setText(String.valueOf(o.getInt("discharged")));
                            st_confirmed_cases.setText(String.valueOf(o.getInt("totalConfirmed")));
                            st_death_cases.setText(String.valueOf(o.getInt("deaths")));
                            break;
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    private void jsonParse() {

        String url="https://api.covid19api.com/summary";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("Countries");
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject country = jsonArray.getJSONObject(i);
                        if (country.getString("Country").equals("India")) {
                            indianewConfirmed.setText(String.valueOf(country.getInt("NewConfirmed")));
                            indiatotalConfirmed.setText(String.valueOf(country.getInt("TotalConfirmed")));
                            indianewDeaths.setText(String.valueOf(country.getInt("NewDeaths")));
                            indiatotalDeaths.setText(String.valueOf(country.getInt("TotalDeaths")));
                            indianewRecovered.setText(String.valueOf(country.getInt("NewRecovered")));
                            indiatotalRecovered.setText(String.valueOf(country.getInt("TotalRecovered")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }


}
