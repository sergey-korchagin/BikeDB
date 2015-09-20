package com.example.sergey.bikedb;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.google.android.gms.maps.GoogleMap;

import java.util.concurrent.RecursiveAction;

/**
 * Created by serge_000 on 09/09/2015.
 */
public class SettingsFragment  extends Fragment implements View.OnClickListener{

    ImageView mBack;
    RadioButton mSalelite;
    RadioButton mMap;
    SharedManager sharedManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.settigs_fragment, container, false);
         sharedManager = SharedManager.getInstance();

        mBack = (ImageView)root.findViewById(R.id.settingsBackBtn);
        mBack.setOnClickListener(this);

        mSalelite = (RadioButton)root.findViewById(R.id.sateliteRB);
        mSalelite.setOnClickListener(this);

        mMap = (RadioButton)root.findViewById(R.id.mapRB);
        mMap.setOnClickListener(this);

        initRadioButtons();
        return root;


    }

    private void initRadioButtons(){

        if (sharedManager.getInt(Constants.MAP_VIEW_KEY) == 0 || sharedManager.getInt(Constants.MAP_VIEW_KEY) == Constants.MAP_NORMAL) {
            mMap.setChecked(true);
            mSalelite.setChecked(false);
            sharedManager.put(Constants.MAP_VIEW_KEY, Constants.MAP_NORMAL);
        } else if(sharedManager.getInt(Constants.MAP_VIEW_KEY) == Constants.MAP_SATELLITE){
            mSalelite.setChecked(true);
            mMap.setChecked(false);
           // sharedManager.put(Constants.MAP_VIEW_KEY, Constants.MAP_SATELLITE);
        }

    }


    @Override
    public void onClick(View v) {
        if(mBack.getId() == v.getId()){
            DashboardFragment dashboardFragment  = new DashboardFragment();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, dashboardFragment, true);
        }
        else if(mSalelite.getId() == v.getId()){
            sharedManager.put(Constants.MAP_VIEW_KEY,Constants.MAP_SATELLITE);
            mSalelite.setChecked(true);
            mMap.setChecked(false);
        }
        else if(mMap.getId() == v.getId()){
            sharedManager.put(Constants.MAP_VIEW_KEY,Constants.MAP_NORMAL);
            mSalelite.setChecked(false);
            mMap.setChecked(true);
        }
    }
}
