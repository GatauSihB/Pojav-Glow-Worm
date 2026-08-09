package net.kdt.pojavlaunch.firefly.prefs.screens;

import android.os.Bundle;

import androidx.preference.Preference;

import com.firefly.feature.UpdateLauncher;

import net.kdt.pojavlaunch.firefly.R;
import net.kdt.pojavlaunch.firefly.prefs.CustomSeekBarPreference;

public class LauncherPreferenceLauncherFragment extends LauncherPreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_launcher);

        Preference updatePreference = requirePreference("update_launcher");
        updatePreference.setOnPreferenceClickListener(preference -> {
            UpdateLauncher updateLauncher = new UpdateLauncher(getContext());
            updateLauncher.checkForUpdates(false);
            return true;
        });

        CustomSeekBarPreference settingsButtonYOffset =
                requirePreference("settingsButtonYOffset", CustomSeekBarPreference.class);
        settingsButtonYOffset.setRange(0, 200);

        CustomSeekBarPreference settingsButtonScale =
                requirePreference("settingsButtonScale", CustomSeekBarPreference.class);
        settingsButtonScale.setRange(50, 150);
    }

}
