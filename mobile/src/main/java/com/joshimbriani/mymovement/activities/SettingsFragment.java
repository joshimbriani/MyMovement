package com.joshimbriani.mymovement.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.preference.PreferenceFragmentCompat;

import com.joshimbriani.mymovement.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_pref);
    }
}
