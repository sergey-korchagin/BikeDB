package com.example.sergey.bikedb;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by serge_000 on 09/09/2015.
 */
public class SettingsFragment  extends Fragment implements View.OnClickListener{

ImageView mBack;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.settigs_fragment, container, false);

        mBack = (ImageView)root.findViewById(R.id.settingsBackBtn);
        mBack.setOnClickListener(this);

        return root;

    }

    @Override
    public void onClick(View v) {
        if(mBack.getId() == v.getId()){
            DashboardFragment dashboardFragment  = new DashboardFragment();
            Utils.replaceFragment(getFragmentManager(), android.R.id.content, dashboardFragment, true);
        }
    }
}
