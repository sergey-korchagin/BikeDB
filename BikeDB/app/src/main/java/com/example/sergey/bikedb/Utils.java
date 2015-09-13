package com.example.sergey.bikedb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by serge_000 on 09/09/2015.
 */
public class Utils {

    public static String getLocationName(double lattitude, double longitude, Context context) {

        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(context, Locale.ENGLISH);
        try {

            List<Address> addresses = gcd.getFromLocation(lattitude, longitude,
                    10);

            for (Address adrs : addresses) {
                if (adrs != null) {

                    String city = adrs.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                        return  cityName;
                    } else {

                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;

    }

    public static void enableGps(final Context context){
        final AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "For use application please enable GPS";

        builder.setMessage(message)
                .setPositiveButton("Open Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                context.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public static void replaceFragment(FragmentManager fragmentManager, int container, Fragment fragment, boolean AddToBackStack) {

        //Enter Animations Later
        if(fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (AddToBackStack)
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(container, fragment).commitAllowingStateLoss();

        }
    }




}