package com.example.sergey.bikedb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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


    public static void noGpsAlert(final Context context){
        final AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        final String message = "No gps location is available, the application will be reloaded!";

        builder.setMessage(message)
                .setPositiveButton("Reload",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.dismiss();
                                Intent i = context.getPackageManager()
                                        .getLaunchIntentForPackage(context.getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
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

    public static void restoreDistance(final Context context){
        final AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        final String message = "Are you sure want to restore distance?";

        builder.setMessage(message)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                SharedManager sharedManager = SharedManager.getInstance();
                                sharedManager.put(Constants.DISTANCE,0f);
                                d.dismiss();
                            }
                        })
                .setNegativeButton("No",
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
