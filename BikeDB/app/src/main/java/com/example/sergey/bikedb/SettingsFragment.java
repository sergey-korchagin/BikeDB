package com.example.sergey.bikedb;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.sergey.bikedb.manager.SharedManager;

/**
 * Created by serge_000 on 09/09/2015.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    ImageView mBack;
    RadioButton mSalelite;
    RadioButton mMap;
    TextView mRestoreButton;
    TextView mRestoreTimeButton;
    SharedManager sharedManager;
    TextView mContactUs;
    TextView mAboutUs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.settigs_fragment, container, false);
        sharedManager = SharedManager.getInstance();

        mBack = (ImageView) root.findViewById(R.id.settingsBackBtn);
        mBack.setOnClickListener(this);

        mSalelite = (RadioButton) root.findViewById(R.id.sateliteRB);
        mSalelite.setOnClickListener(this);

        mMap = (RadioButton) root.findViewById(R.id.mapRB);
        mMap.setOnClickListener(this);

        mRestoreButton = (TextView) root.findViewById(R.id.restoreDistanceButton);
        mRestoreButton.setOnClickListener(this);

        mContactUs = (TextView) root.findViewById(R.id.contactUs);
        mContactUs.setOnClickListener(this);

        mRestoreTimeButton = (TextView) root.findViewById(R.id.restoreTimeButton);
        mRestoreTimeButton.setOnClickListener(this);

        mAboutUs = (TextView) root.findViewById(R.id.aboutUs);
        mAboutUs.setOnClickListener(this);

        initRadioButtons();
        return root;


    }

    private void initRadioButtons() {

        if (sharedManager.getInt(Constants.MAP_VIEW_KEY) == 0 || sharedManager.getInt(Constants.MAP_VIEW_KEY) == Constants.MAP_NORMAL) {
            mMap.setChecked(true);
            mSalelite.setChecked(false);
            sharedManager.put(Constants.MAP_VIEW_KEY, Constants.MAP_NORMAL);
        } else if (sharedManager.getInt(Constants.MAP_VIEW_KEY) == Constants.MAP_SATELLITE) {
            mSalelite.setChecked(true);
            mMap.setChecked(false);
            // sharedManager.put(Constants.MAP_VIEW_KEY, Constants.MAP_SATELLITE);
        }

    }


    @Override
    public void onClick(View v) {
        if (mBack.getId() == v.getId()) {
            DashboardFragment dashboardFragment = new DashboardFragment();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, dashboardFragment, true);
        } else if (mSalelite.getId() == v.getId()) {
            sharedManager.put(Constants.MAP_VIEW_KEY, Constants.MAP_SATELLITE);
            mSalelite.setChecked(true);
            mMap.setChecked(false);
        } else if (mMap.getId() == v.getId()) {
            sharedManager.put(Constants.MAP_VIEW_KEY, Constants.MAP_NORMAL);
            mSalelite.setChecked(false);
            mMap.setChecked(true);
        } else if (mRestoreButton.getId() == v.getId()) {
            Utils.restoreDistance(getActivity());
        }else if(mContactUs.getId() == v.getId()){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "korch.se@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from bike app");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "User text: ");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }else if(mRestoreTimeButton.getId()==v.getId()){
            Utils.restoreTime(getActivity());
        }else if(mAboutUs.getId() == v.getId()){
            Utils.aboutDialog(getActivity());
        }

    }
}
