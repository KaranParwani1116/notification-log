package org.hcilab.projects.nlogx.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import org.hcilab.projects.nlogx.R;


//implemented shared preference listener to change summary
public class DarkMode extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.darkmode);

        SharedPreferences sharedPreferences=getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen=getPreferenceScreen();

        int count=preferenceScreen.getPreferenceCount();//total preference on screen

        for(int i=0;i<count;i++)
        {
            Preference p=preferenceScreen.getPreference(i);

            if(!(p instanceof CheckBoxPreference))
            {
                String value=sharedPreferences.getString(p.getKey(),"");
                setpreferencesummary(p,value);
            }
        }
    }


     //setting the summary of list preference
    private void setpreferencesummary(Preference p, String value) {
       if(p instanceof ListPreference)
       {
           ListPreference listPreference=(ListPreference)p;
           int prefindex=listPreference.findIndexOfValue(value);
           if(prefindex>=0)
           {
               listPreference.setSummary(listPreference.getEntries()[prefindex]);
           }
       }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference=findPreference(key);
        if(preference!=null)
        {
            if(!(preference instanceof CheckBoxPreference))
            {
                String value=sharedPreferences.getString(preference.getKey(),"");
                setpreferencesummary(preference,value);
            }
        }

    }


    //registering and deregistering shared preferences
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
