package com.example.sergey.bikedb;


import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

/**
 * Created by serge_000 on 09/09/2015.
 */
public class DashboardFragment extends Fragment implements LocationListener, View.OnClickListener, OnMapReadyCallback {

    TextView mSpeedText;


    TextView mCity;
    TextView mTemperature;
    TextView mDescription;
    Handler handler;

    String city;
    double mLatitude;
    double mLongitude;
    String provider;

    ImageView mSettingsButton;
    LinearLayout mWeatherAlert;

    Location location;

    LatLng myLocation;


    public DashboardFragment() {
        handler = new Handler();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.dashboard_fragment, container, false);
        mWeatherAlert = (LinearLayout) root.findViewById(R.id.errorWeather);

        mSpeedText = (TextView) root.findViewById(R.id.speedView);
        mCity = (TextView) root.findViewById(R.id.city);
        mTemperature = (TextView) root.findViewById(R.id.temperature);
        mSettingsButton = (ImageView) root.findViewById(R.id.settingsButon);
        mDescription = (TextView) root.findViewById(R.id.description);
        mSettingsButton.setOnClickListener(this);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (locationManager.isProviderEnabled(provider)) {

            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
            this.onLocationChanged(null);


            location = locationManager.getLastKnownLocation(provider);

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            city = Utils.getLocationName(mLatitude, mLongitude, getActivity());
            updateWeatherData(city);


        } else {
            Utils.enableGps(getActivity());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return root;
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            mSpeedText.setText("-.-");
        } else {
            float currentSpeed = location.getSpeed();
            String formattedString = String.format("%.02f", currentSpeed);
            mSpeedText.setText(formattedString);
        }
    }


    private void updateWeatherData(final String city) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            mWeatherAlert.setVisibility(View.VISIBLE);

                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    public void renderWeather(JSONObject jsonObject) {
        try {
            String shortCity = jsonObject.getString("name");
            if (shortCity.length() > 9) {
                mCity.setTextSize(15);
            }
            mCity.setText(shortCity);
            JSONObject main = jsonObject.getJSONObject("main");
            JSONObject wind = jsonObject.getJSONObject("wind");
            JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);

            mTemperature.setText(main.getString("temp") + "℃");
            mDescription.setText(details.getString("description"));
            if (shortCity.length() > 9) {
                mDescription.setTextSize(15);
            }
        } catch (Exception e) {
            e.toString();

        }
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onClick(View view) {
        if (mSettingsButton.getId() == view.getId()) {
            SettingsFragment settingsFragment = new SettingsFragment();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, settingsFragment, true);
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        myLocation = new LatLng(mLatitude, mLongitude);
//TODO need to change to custom
        googleMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(17)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

}
