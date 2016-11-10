package com.erpdevelopment.vbvm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.erpdevelopment.vbvm.MainActivity;
import com.erpdevelopment.vbvm.R;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchAutoComplete = (Switch) findViewById(R.id.switch_auto);
        boolean checked = MainActivity.settings.getBoolean("switchAuto", true);
        switchAutoComplete.setChecked(checked);
        switchAutoComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.settings.edit().putBoolean("switchAuto", true).commit();
                } else {
                    MainActivity.settings.edit().putBoolean("switchAuto", false).commit();
                }
            }
        });

    }

}
