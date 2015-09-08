package org.kde.kdeconnect.UserInterface;

import android.graphics.Color;
import android.preference.CheckBoxPreference;
import android.view.View;
import android.widget.TextView;

import org.kde.kdeconnect.Device;
import org.kde.kdeconnect.Plugins.Plugin;
import org.kde.kdeconnect.Plugins.PluginFactory;
import org.kde.kdeconnect.UserInterface.SettingsActivity;
import org.kde.kdeconnect_tp.R;

public class PluginPreference extends CheckBoxPreference {

    final Device device;
    final String pluginKey;
    final View.OnClickListener listener;

    public PluginPreference(final SettingsActivity activity, final String pluginKey, final Device device) {
        super(activity);

        setLayoutResource(R.layout.preference_with_button);

        this.device = device;
        this.pluginKey = pluginKey;

        PluginFactory.PluginInfo info = PluginFactory.getPluginInfo(activity, pluginKey);
        setTitle(info.getDisplayName());
        setSummary(info.getDescription());
        setChecked(device.isPluginEnabled(pluginKey));

        Plugin plugin = device.getPlugin(pluginKey, true);
        if (info.hasSettings() && plugin != null) {
            this.listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Plugin plugin = device.getPlugin(pluginKey, true);
                    if (plugin != null) {
                        plugin.startPreferencesActivity(activity);
                    } else { //Could happen if the device is not connected anymore
                        activity.finish(); //End this activity so we go to the "device not reachable" screen
                    }
                }
            };
        } else {
            this.listener = null;
        }

    }

    @Override
    protected void onBindView(View root) {
        super.onBindView(root);
        final View button = root.findViewById(R.id.settingsButton);

        if (device.getUnsupportedPlugins().contains(pluginKey)) {
            ((TextView)root.findViewById(android.R.id.title)).setTextColor(Color.GRAY);
            ((TextView)root.findViewById(android.R.id.summary)).setText(R.string.plugin_not_supported);
            button.setVisibility(View.GONE);
        } else if (listener == null) {
            button.setVisibility(View.GONE);
        } else {
            button.setEnabled(isChecked());
            button.setOnClickListener(listener);
        }

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newState = !device.isPluginEnabled(pluginKey);
                setChecked(newState);
                button.setEnabled(newState);
                device.setPluginEnabled(pluginKey, newState);
            }
        });
    }

}
