package com.erpdevelopment.vbvm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.utils.FilesManager;
import com.erpdevelopment.vbvm.utils.Utilities;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchAutoComplete;
    private RadioButton radioBibleBook;
    private RadioButton radioReleaseDate;
    private RadioGroup radioGroupSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchAutoComplete = (Switch) findViewById(R.id.switch_auto);
        radioBibleBook = (RadioButton) findViewById(R.id.radio_bible_book);
        radioReleaseDate = (RadioButton) findViewById(R.id.radio_release_date);
        radioGroupSort = (RadioGroup) findViewById(R.id.radio_group_sort);

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

        int selectedSort = MainActivity.settings.getInt("selectedSort", -1);
        if (selectedSort == R.id.radio_bible_book)
            radioBibleBook.setChecked(true);
        else
            radioReleaseDate.setChecked(true);
        radioGroupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_bible_book) {
                    MainActivity.settings.edit().putInt("selectedSort", R.id.radio_bible_book).commit();
                } else {
                    MainActivity.settings.edit().putInt("selectedSort", R.id.radio_release_date).commit();
                }
                FilesManager.listStudiesNew = null;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_close:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
