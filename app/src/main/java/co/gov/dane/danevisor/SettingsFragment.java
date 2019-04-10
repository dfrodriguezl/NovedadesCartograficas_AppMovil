package co.gov.dane.danevisor;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;



public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.pref_general, s);
    }


}
