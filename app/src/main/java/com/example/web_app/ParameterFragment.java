package com.example.web_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class ParameterFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_param, rootKey);

        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .registerOnSharedPreferenceChangeListener(this);
    }



    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("notifications")) {
            boolean notificationsEnabled = sharedPreferences.getBoolean(key, false);
            if (notificationsEnabled) {
                requestNotificationPermission();
            }
        } else if (key.equals("cam")) {
            boolean camEnabled = sharedPreferences.getBoolean(key, false);
            if (camEnabled) {
                requestCameraPermission();
            }
        }
    }

    private void requestNotificationPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Autoriser les notifications");
        builder.setMessage("Pour activer les notifications, veuillez autoriser les notifications dans les paramètres de votre appareil.");
        builder.setPositiveButton("Paramètres", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void requestCameraPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Autoriser l'accès à la caméra");
        builder.setMessage("Pour activer l'accès à la caméra, veuillez autoriser l'accès à la caméra dans les paramètres de votre appareil.");
        builder.setPositiveButton("Paramètres", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }



}
