package com.chechu.hamilton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_about);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        showTutorial();

        //set version label
        try {
            ((TextView) findViewById(R.id.versionTextView)).setText(
                    String.format(getResources().getString(R.string.app_version), getPackageManager().getPackageInfo(getPackageName(), 0).versionName)
            );
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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

    public void aboutButtonClickListener(View view) {
        String string = "";

        //link to follow when item clicks
        switch (view.getId()) {
            //email
            case R.id.authorView:
                string = "mailto:hamilton.matrix.app@gmail.com?subject=Related to Hamilton";
                break;
            //play store
            case R.id.opinionView:
                string = "market://details?id=" + getPackageName();
                break;
            //donation
            case R.id.donationView:
                string = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=LRH6Z3L44WXLY";
                break;
            //wikipedia
            case R.id.wikipediaView:
                string = "https://en.wikipedia.org/wiki/William_Rowan_Hamilton";
                break;
            //github
            case R.id.githubView:
                string = "https://github.com/jesusrp98/hamilton";
                break;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(string)));
    }

    private void showTutorial() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean(getString(R.string.key_tutorial_about), false)) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.tutorial_about_head)
                .setMessage(R.string.tutorial_about_body)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        preferences.edit().putBoolean(getString(R.string.key_tutorial_about), true).apply();
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
    }
}