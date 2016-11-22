/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yoel Nunez <dev@nunez.guru>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package com.example.kevin.TexasTechWeatherApp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;

import com.example.kevin.TexasTechWeatherApp.MainNonGPS;
import com.example.kevin.TexasTechWeatherApp.R;
import com.example.kevin.TexasTechWeatherApp.MainActivity;

import static android.content.Context.LOCATION_SERVICE;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preferences;

    private SwitchPreference geolocationEnabledPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference button = (Preference)findPreference(getString(R.string.cache));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearLocations();
                //get if gps is enabled by user and go to the corresponding main page
                SharedPreferences gps_pref= getActivity().getApplicationContext().getSharedPreferences(getString(R.string.GPS_Enabled),0);
                Boolean gps_enabled= gps_pref.getBoolean("GPS_Enabled",false);
                //if gps is enabled during non gps pages go to corresponding gps pages
                LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent= new Intent(getActivity().getApplicationContext(),MainNonGPS.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        geolocationEnabledPreference = (SwitchPreference) findPreference(getString(R.string.pref_geolocation_enabled));

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_temperature_unit)));

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);

        onSharedPreferenceChanged(null, null);

        setHasOptionsMenu(true);
    }

    //clear cache funtion
    public void clearLocations(){
        SharedPreferences loc_number = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.Location_Number),0);
        SharedPreferences loc_name= getActivity().getApplicationContext().getSharedPreferences(getString(R.string.PREF_NAME),0);
        SharedPreferences gps_loc= getActivity().getApplicationContext().getSharedPreferences("GPS_Location",0);

        //clear location number
        SharedPreferences.Editor editor = loc_number.edit();
        editor.clear();
        editor.apply();
        //clear all saved pages
        SharedPreferences.Editor editor1 = loc_name.edit();
        editor1.clear();
        editor1.apply();
        //clear gps location
        SharedPreferences.Editor editor2 = gps_loc.edit();
        editor2.clear();
        editor2.apply();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.Location_Number),0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("location_number",0);//reassign page location to 0
            editor.apply();
            //if gps is enabled during non gps pages go to corresponding gps pages
            LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else{
                Intent intent= new Intent(getActivity().getApplicationContext(),MainNonGPS.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (geolocationEnabledPreference.isChecked()) {

        } else {
        }
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, preferences.getString(preference.getKey(), null));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

        }

        return true;
    }
}
