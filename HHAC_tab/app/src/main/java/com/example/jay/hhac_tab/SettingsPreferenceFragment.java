package com.example.jay.hhac_tab;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;


public class SettingsPreferenceFragment extends PreferenceFragment {

    SharedPreferences prefs;
    ListPreference backgroundPref;
    Preference InfoPref;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

        backgroundPref = (ListPreference) findPreference("key_dialog_backgroundcolor");
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        InfoPref = (Preference) findPreference("key_dialog_Info");

        if (!prefs.getString("key_dialog_backgroundcolor", "").equals("")) {
            backgroundPref.setSummary(prefs.getString("key_dialog_backgroundcolor", "false"));
        }
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
        InfoPref.setOnPreferenceClickListener(btnListener);
    }

    Preference.OnPreferenceClickListener btnListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            final String key = preference.getKey();

            if (key.equals("key_dialog_Info")) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("개발자 정보")
                        .setMessage("준민 : 010 - 0520 - 0170\n태회 : 010 - 4014 - 3422\n아람 : 010 - 5540 - 0099")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();
                return true; // we handled the click
            }
            return false; // we didn't handle the click
        }
    };


    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("key_dialog_backgroundcolor")) {
                backgroundPref.setSummary(prefs.getString("key_dialog_backgroundcolor", "honeydew"));
            }
            String bgColor = String.valueOf(backgroundPref.getSummary());
            if (bgColor != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("bgcolor", bgColor);
                Log.i("배경색-----------------", bgColor);
                startActivity(intent);
            }
        }

    };

    @Override
    public void onResume() {
        super.onResume();

    }
}