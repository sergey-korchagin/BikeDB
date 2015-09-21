package com.example.sergey.bikedb;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by serge_000 on 09/09/2015.
 */
public class DashboardFragment extends Fragment implements LocationListener, View.OnClickListener, OnMapReadyCallback {

    TextView mSpeedText;


    TextView mCity;
    TextView mTemperature;
    TextView mDescription;
    TextView distanceView;
    TextView mTime;
    TextView mTripTime;
    Handler handler;

    String city;
    double mLatitude;
    double mLongitude;
    String provider;

    ImageView mSettingsButton;
    LinearLayout mWeatherAlert;

    Location location;

    LatLng myLocation;

    float mStartTime;
    float mEndTime;
    float movingTime;
    boolean startedMoving;
    float distance;
    float prevDistance;
    SharedManager sharedManager;

    GoogleMap mGoogleMap;

    public DashboardFragment() {
        handler = new Handler();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.dashboard_fragment, container, false);

        sharedManager = SharedManager.getInstance();
        mWeatherAlert = (LinearLayout) root.findViewById(R.id.errorWeather);
        startedMoving =false;
        movingTime = 0;
        mTime = (TextView)root.findViewById(R.id.timeView);
        showTime();


        mSpeedText = (TextView) root.findViewById(R.id.speedView);
        mCity = (TextView) root.findViewById(R.id.city);
        mTemperature = (TextView) root.findViewById(R.id.temperature);
        mSettingsButton = (ImageView) root.findViewById(R.id.settingsButon);
        mDescription = (TextView) root.findViewById(R.id.description);
        mTripTime=(TextView) root.findViewById(R.id.tripTimeView);


        distanceView =(TextView) root.findViewById(R.id.distanseText);
        prevDistance = sharedManager.getFloat(Constants.DISTANCE);
        String formattedStringDistance = String.format("%.02f", prevDistance);
        distanceView.setText(formattedStringDistance);

        mSettingsButton.setOnClickListener(this);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (locationManager.isProviderEnabled(provider)) {

            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);

          //  this.onLocationChanged(null);

            //TODO need to change
            location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                Utils.noGpsAlert(getActivity());
             //  locationManager.requestLocationUpdates(locationManager.PASSIVE_PROVIDER, 0, 0, this);
            } else {

                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();

                city = Utils.getLocationName(mLatitude, mLongitude, getActivity());
                updateWeatherData(city);


            }
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
            float currentSpeed = location.getSpeed() *3.6f;
            String formattedString = String.format("%.02f", currentSpeed);
            mSpeedText.setText(formattedString);

            if(currentSpeed>0.1 && !startedMoving){
                mStartTime = System.currentTimeMillis();
                startedMoving = true;
            }else if(currentSpeed >=0 && startedMoving){
                mEndTime = System.currentTimeMillis();
            }

            if(mEndTime>0) {
                movingTime = ((mEndTime - mStartTime) / (60 * 60 * 1000));
                distance = currentSpeed * movingTime;
                String formattedString2 = String.format("%.02f", distance + prevDistance);
                distanceView.setText(formattedString2);
                String formattedString3 = String.format("%.02f", movingTime);
                mTripTime.setText(formattedString3);
                sharedManager.put(Constants.DISTANCE, distance + prevDistance);
            }


            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            if(mGoogleMap != null){
                mGoogleMap.clear();
                initMap(mGoogleMap);
            }

        }
    }

    private void showTime(){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        String currTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
        mTime.setText(currTime);
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
        Utils.enableGps(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
        startedMoving = false;
        movingTime = 0;
        prevDistance = sharedManager.getFloat(Constants.DISTANCE);
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
        mGoogleMap = googleMap;
        SharedManager sharedManager = SharedManager.getInstance();
        if (sharedManager.getInt(Constants.MAP_VIEW_KEY) == 0) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else {
            googleMap.setMapType(sharedManager.getInt(Constants.MAP_VIEW_KEY));
        }
        googleMap.addMarker(new MarkerOptions()
                .position(myLocation)
                        //    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle_marker)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(17)
                .bearing(90)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void initMap(GoogleMap googleMap){
        myLocation = new LatLng(mLatitude, mLongitude);
        googleMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle_marker)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(17)
                .build();

    }



}
