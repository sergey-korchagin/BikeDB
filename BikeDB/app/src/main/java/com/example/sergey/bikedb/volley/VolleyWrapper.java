package com.example.sergey.bikedb.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sergey.bikedb.Data.WeatherData;
import com.example.sergey.bikedb.interfaces.ErrorResponseListener;
import com.example.sergey.bikedb.interfaces.ResponseListener;
import com.example.sergey.bikedb.manager.DataManager;
import com.google.gson.Gson;

/**
 * Created by User on 24/09/2015.
 */
public class VolleyWrapper {
    RequestQueue requestQueue;
    DataManager dataManager = DataManager.getInstance();

    public VolleyWrapper(Context context){
        requestQueue = Volley.newRequestQueue(context);

    }

    public void request(String city ,final ResponseListener responseListener, final ErrorResponseListener errorResponseListener) {

        String url = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Gson gson = new Gson();
                        WeatherData weatherData = gson.fromJson(response,WeatherData.class);

                        dataManager.setWeatherData(weatherData);
                        if(responseListener!=null){
                            responseListener.onResponseListener();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorResponseListener != null) {
                    errorResponseListener.OnErrorResponseListener();
                }
            }
        });
// Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }



}
