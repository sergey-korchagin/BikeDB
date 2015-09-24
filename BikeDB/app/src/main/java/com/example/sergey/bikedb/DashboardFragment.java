package com.example.sergey.bikedb;


import android.content.Context;
import android.graphics.Typeface;
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

import com.android.volley.toolbox.NetworkImageView;
import com.example.sergey.bikedb.interfaces.ErrorResponseListener;
import com.example.sergey.bikedb.interfaces.ResponseListener;
import com.example.sergey.bikedb.manager.DataManager;
import com.example.sergey.bikedb.manager.SharedManager;
import com.example.sergey.bikedb.volley.VolleyWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by serge_000 on 09/09/2015.
 */
public class DashboardFragment extends Fragment implements LocationListener, View.OnClickListener, OnMapReadyCallback, ResponseListener,ErrorResponseListener {

    TextView mSpeedText;


    TextView mCity;
    TextView mTemperature;
    TextView mDescription;
    TextView distanceView;
    TextView mTime;
    TextView mTripTime;
    TextView mCountry;
    NetworkImageView mIcon;
    Handler handler;

    //debug
    TextView tmpDist;

    //end debug


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
    Calendar cal;

    VolleyWrapper volleyWrapper;
    DataManager dataManager = DataManager.getInstance();

    public DashboardFragment() {
        handler = new Handler();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.dashboard_fragment, container, false);
        //Debug
        tmpDist = (TextView)root.findViewById(R.id.tmpDist);
        tmpDist.setText("No moving now");



        //end debug



        cal = Calendar.getInstance(Locale.getDefault());

        volleyWrapper = new VolleyWrapper(getActivity());

        sharedManager = SharedManager.getInstance();
        mWeatherAlert = (LinearLayout) root.findViewById(R.id.errorWeather);
        startedMoving = false;
        movingTime = sharedManager.getFloat(Constants.TRIP_TIME);
        mTime = (TextView) root.findViewById(R.id.timeView);
        showTime();

        mIcon = (NetworkImageView)root.findViewById(R.id.iconView);



        mSpeedText = (TextView) root.findViewById(R.id.speedView);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/radioland.ttf");
        mSpeedText.setTypeface(font);
        mCity = (TextView) root.findViewById(R.id.city);
        mTemperature = (TextView) root.findViewById(R.id.temperature);
        mSettingsButton = (ImageView) root.findViewById(R.id.settingsButon);
        mDescription = (TextView) root.findViewById(R.id.description);
        mTripTime = (TextView) root.findViewById(R.id.tripTimeView);
        String formattedStringTime = String.format("%.02f", sharedManager.getFloat(Constants.TRIP_TIME));
        mTripTime.setText(formattedStringTime);
        mCountry = (TextView) root.findViewById(R.id.countryView);

        distanceView = (TextView) root.findViewById(R.id.distanseText);
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
                updateWeatherData(city, this,this);


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
            float currentSpeed = location.getSpeed() * 3.6f;
            String formattedString = String.format("%.1f", currentSpeed);
            mSpeedText.setText(formattedString);

            if (currentSpeed > 0.1 && !startedMoving) {
                mStartTime = System.currentTimeMillis();
                startedMoving = true;
            } else if (currentSpeed >= 0 && startedMoving) {
                mEndTime = System.currentTimeMillis();
            }

            if (mEndTime > 0) {
                movingTime = ((mEndTime - mStartTime) / (60 * 60 * 1000));
                if (currentSpeed == 0) {
                    currentSpeed = 0.0001f;
                }
                distance = currentSpeed * movingTime;
                tmpDist.setText("Dist " + Float.toString(distance)+" Time " + Float.toString(movingTime));
                float tmp = distance + prevDistance;
                tmp = tmp/10;
                String formattedString2 = String.format("%.02f", tmp);
                distanceView.setText(formattedString2);
                String formattedString3 = String.format("%.02f", movingTime);
                mTripTime.setText(formattedString3);
                sharedManager.put(Constants.DISTANCE, tmp);
            }


            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            if (mGoogleMap != null) {
                mGoogleMap.clear();
                initMap(mGoogleMap);
            }

        }
    }

    private void showTime() {
        String currTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
        mTime.setText(currTime);
    }


    private void updateWeatherData(String city, ResponseListener responseListener,ErrorResponseListener errorResponseListener) {

        city = city.replace(" ", "%20");
        volleyWrapper.request(city, responseListener,errorResponseListener);

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
        prevDistance = sharedManager.getFloat(Constants.DISTANCE);
        movingTime = sharedManager.getFloat(Constants.TRIP_TIME);

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

    private void initMap(GoogleMap googleMap) {
        myLocation = new LatLng(mLatitude, mLongitude);
        googleMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle_marker)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(17)
                .build();

    }


    @Override
    public void onResponseListener() {

        if(dataManager.getWeatherData().getCod().equals("200")) {
            String shortCity = dataManager.getWeatherData().getName();
            if (shortCity.length() > 9) {
                mCity.setTextSize(15);
            }
            mCity.setText(shortCity);
            mTemperature.setText(dataManager.getWeatherData().getMain().getTemp() + "℃");
            mDescription.setText(dataManager.getWeatherData().getWeather().get(0).getDescription());
            if (shortCity.length() > 9) {
                mDescription.setTextSize(15);
            }

           // mCountry.setText(dataManager.getWeatherData().getSys().getCountry());
            mCountry.setText((Utils.getCountry(mLatitude,mLongitude,getActivity())));

            mIcon.setImageUrl("http://openweathermap.org/img/w/" + dataManager.getWeatherData().getWeather().get(0).getIcon() + ".png", volleyWrapper.getImageLoader());
            mWeatherAlert.setVisibility(View.GONE);
        }
        else{
            onResponseListener();
        }
    }

    @Override
    public void OnErrorResponseListener() {
        mWeatherAlert.setVisibility(View.VISIBLE);
    }


    @Override
    public void onPause() {
        super.onPause();
        sharedManager.put(Constants.TRIP_TIME, movingTime);
    }

    @Override
    public void onStop() {
        super.onStop();
        sharedManager.put(Constants.TRIP_TIME, 0f);

    }
}
