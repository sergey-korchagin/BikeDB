package com.example.sergey.bikedb.manager;

import com.example.sergey.bikedb.Data.WeatherData;

/**
 * Created by User on 24/09/2015.
 */
public class DataManager {
    private static DataManager ourInstance = new DataManager();
    String mResponceText;

    public WeatherData getWeatherData() {
        return mWeatherData;
    }

    public void setWeatherData(WeatherData mWeatherData) {
        this.mWeatherData = mWeatherData;
    }

    WeatherData mWeatherData;
    public static DataManager getInstance() {
        return ourInstance;
    }

    public String setResponseText(String responseText){
        mResponceText = responseText;
        return mResponceText;
    }
    public String getResponceText(){
        return mResponceText;
    }


}
