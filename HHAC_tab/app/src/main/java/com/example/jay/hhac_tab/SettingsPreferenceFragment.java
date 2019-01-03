package com.example.jay.hhac_tab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

public class SettingsPreferenceFragment extends PreferenceFragment {

    SharedPreferences prefs;
    ListPreference backgroundPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

        backgroundPref = (ListPreference) findPreference("key_dialog_backgroundcolor");
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (!prefs.getString("key_dialog_backgroundcolor", "").equals("")) {
            backgroundPref.setSummary(prefs.getString("key_dialog_backgroundcolor", "false"));
        }
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

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
