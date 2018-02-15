package com.chechu.hamilton;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.google.gson.Gson;
import com.shawnlin.numberpicker.NumberPicker;
import android.widget.Toast;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_settings);

        showTutorial();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //builds settings fragment
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTutorial() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(getString(R.string.key_tutorial_settings), false)) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.tutorial_settings_head)
                .setMessage(R.string.tutorial_settings_body)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sharedPreferences.edit().putBoolean(getString(R.string.key_tutorial_settings), true).apply();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create().show();
        }
    }

    private void setTheme() {
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_darkmode), false))
            setTheme(R.style.ThemeDark);
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.layout_settings);

            //gets dimension & range arrays
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final int[] dimensionDefault = new Gson().fromJson(sharedPreferences.getString(getString(R.string.key_default_matrix_dimension), null), int[].class);
            final int[] dimensionRandom = new Gson().fromJson(sharedPreferences.getString(getString(R.string.key_random_range_dimension), null), int[].class);

            final Preference darkModePreference = findPreference(getString(R.string.key_darkmode));
            final Preference defaultPreference = findPreference(getString(R.string.key_default_matrix));
            final Preference randomPreference = findPreference(getString(R.string.key_random_range));
            final Preference restoreTutorials = findPreference(getString(R.string.key_restore_tutorials));

            restoreTutorials.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(getString(R.string.key_tutorial_home));
                    editor.remove(getString(R.string.key_tutorial_matrix));
                    editor.remove(getString(R.string.key_tutorial_result));
                    editor.remove(getString(R.string.key_tutorial_about));
                    editor.remove(getString(R.string.key_tutorial_settings)).apply();
                    Toast.makeText(getActivity(), getString(R.string.settings_restore_tutorial_message), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            //set summary for dimensions dialogs
            defaultPreference.setSummary(String.format(getString(R.string.placeholder_matrix_dimensions), dimensionDefault[0], dimensionDefault[1]));
            randomPreference.setSummary(String.format(getString(R.string.placeholder_random_range), dimensionRandom[0], dimensionRandom[1]));

            //default matrix dimensions & random range
            Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    @SuppressLint("InflateParams") final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_dimension, null);
                    final boolean isDialogDefault = Objects.equals(preference.getKey(), getString(R.string.key_default_matrix));

                    final NumberPicker rowNumberPicker = dialogView.findViewById(R.id.rowNumberPicker);
                    final NumberPicker columnNumberPicker = dialogView.findViewById(R.id.columnNumberPicker);
                    final int[] range = getResources().getIntArray(isDialogDefault ? R.array.range_default : R.array.range_random);
                    final int[] dimension  = isDialogDefault ? dimensionDefault : dimensionRandom;

                    //init number picker
                    rowNumberPicker.setMinValue(range[0]);
                    rowNumberPicker.setMaxValue(range[1]);
                    rowNumberPicker.setValue(dimension[0]);
                    columnNumberPicker.setMinValue(range[0]);
                    columnNumberPicker.setMaxValue(range[1]);
                    columnNumberPicker.setValue(dimension[1]);

                    new AlertDialog.Builder(getActivity())
                        .setView(dialogView)
                        .setTitle(preference.getTitle())
                        .setMessage(preference.getSummary())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                int[] newDimensions = {rowNumberPicker.getValue(), columnNumberPicker.getValue()};

                                if(!isDialogDefault && newDimensions[0] >= newDimensions[1]){
                                    Toast.makeText(getActivity(), getString(R.string.settings_matrix_random_error), Toast.LENGTH_SHORT).show();
                                }else
                                    editor.putString(isDialogDefault ? getString(R.string.key_default_matrix_dimension) : getString(R.string.key_random_range_dimension),
                                            new Gson().toJson(newDimensions)).apply();
                                dialog.dismiss();
                                getActivity().recreate();
                            }
                        })
                        .setNeutralButton(getString(R.string.action_reset), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                sharedPreferences.edit().putString(isDialogDefault ? getString(R.string.key_default_matrix_dimension) : getString(R.string.key_random_range_dimension),
                                        new Gson().toJson(getResources().getIntArray(R.array.dimension_default))).apply();
                                Toast.makeText(getActivity(), getString(R.string.settings_matrix_default_message), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                getActivity().recreate();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                    return true;
                }
            };
            defaultPreference.setOnPreferenceClickListener(onPreferenceClickListener);
            randomPreference.setOnPreferenceClickListener(onPreferenceClickListener);

            //dark mode switch
            darkModePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object o) {
                     if (sharedPreferences.getBoolean(getString(R.string.settings_darkmode_head), false) != (Boolean) o){
                        new AlertDialog.Builder(getActivity())
                            .setTitle(preference.getTitle())
                            .setMessage(R.string.settings_darkmode_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sharedPreferences.edit().putBoolean(getString(R.string.settings_darkmode_head), (Boolean) o).apply();
                                    Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ((SwitchPreference) preference).setChecked(!(Boolean) o);
                                    sharedPreferences.edit().putBoolean(getString(R.string.key_darkmode), !(Boolean) o).apply();
                                    dialogInterface.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .create().show();
                    }
                    return true;
                }
            });
        }
    }
}