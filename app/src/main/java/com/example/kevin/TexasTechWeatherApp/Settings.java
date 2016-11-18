

/**
 * Created by Stephen on 11/13/2016.
 */

package com.example.kevin.TexasTechWeatherApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kevin.TexasTechWeatherApp.fragments.SettingsFragment;

public class Settings extends PreferenceActivity {
    private AppCompatDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }
    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }
    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }
    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }
    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }
    private AppCompatDelegate getDelegate() {
        if (delegate == null) {
            delegate = AppCompatDelegate.create(this, null);
            setTitle("Settings");
        }
        return delegate;
    }

    //if back button pressed go back to main page automatically
    @Override
    public void onBackPressed()
    {
        SharedPreferences pref=getSharedPreferences(getString(R.string.Location_Number),0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("location_number",0);//reassign page location to 0
        editor.apply();
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);
    }


}

