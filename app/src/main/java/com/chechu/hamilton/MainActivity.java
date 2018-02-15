package com.chechu.hamilton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.gson.Gson;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MenuItem searchItem;
    private SearchView searchView;
    private FloatingActionButton fab;
    private AdapterOperationList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialBoot();
        setTheme();
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        final ListView listView = findViewById(R.id.activity_main_list);
        fab = findViewById(R.id.fab);

        showTutorial();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //transfer operation to next activity
                Intent intent = new Intent(MainActivity.this, MatrixActivity.class);
                intent.putExtra("itemOperation", (ItemOperation) parent.getItemAtPosition(position));
                startActivity(intent);
            }
        });

        adapter = new AdapterOperationList(getBaseContext());
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //search ui init
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        //look for text box changes
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                adapter.getFilter().filter(text);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                changeSearchView(false);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified())
            searchView.setIconified(true);
        else
            super.onBackPressed();
    }

    public void fabClickListener(View view) {
        changeSearchView(!searchItem.isVisible());
    }

    private void changeSearchView(boolean state) {
        //if search view is open
        if (state) {
            searchItem.setVisible(true);
            searchView.setIconified(false);
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close));
        } else {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_search));
            searchItem.setVisible(false);
            searchView.onActionViewCollapsed();
        }
    }

    private void initialBoot() {
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_initial_boot), false)) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString(getString(R.string.key_list_matrix), new Gson().toJson(new ArrayList<String>()));
            editor.putString(getString(R.string.key_default_matrix_dimension), new Gson().toJson(getResources().getIntArray(R.array.dimension_default)));
            editor.putString(getString(R.string.key_random_range_dimension), new Gson().toJson(getResources().getIntArray(R.array.dimension_random)));
            editor.putBoolean(getString(R.string.key_initial_boot), true).apply();
        }
    }

    private void showTutorial() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(getString(R.string.key_tutorial_home), false)) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.tutorial_main_head)
                .setMessage(R.string.tutorial_main_body)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sharedPreferences.edit().putBoolean(getString(R.string.key_tutorial_home), true).apply();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create().show();
        }
    }

    private void setTheme() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_darkmode), false))
            setTheme(R.style.ThemeDark);
        else
            setTheme(R.style.ThemeLight);
    }
}