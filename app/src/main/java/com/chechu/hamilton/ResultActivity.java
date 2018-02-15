package com.chechu.hamilton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import java.util.ArrayList;
import Jama.Matrix;

public class ResultActivity extends AppCompatActivity {
    private ArrayList<ItemMatrix> bundleMatrix;
    private ArrayList<ItemMatrix> finalMatrix;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean isBasic = false;
    private String exception;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_result);

        final Bundle bundle = getIntent().getExtras();
        final double scalar = bundle.getDouble("scalar");
        bundleMatrix = bundle.getParcelableArrayList("bundleMatrix");
        final ItemOperation itemOperation = bundle.getParcelable("itemOperation");

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(itemOperation.getTitle());

        showTutorial();

        final String matrixLetter = getString(R.string.display_matrix_letter);
        final String titleFormat = getString(R.string.placeholder_matrix_name);
        final String matrixName = getString(R.string.display_matrix_name);
        final ArrayList<Matrix> auxArray = new ArrayList<>();
        finalMatrix = new ArrayList<>();

        //ui init
        viewPager = findViewById(R.id.activity_result_viewpager);
        tabLayout = findViewById(R.id.activity_result_tablayout);

        //ad banner
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2850448736568170~8639461090");
        ((AdView) findViewById(R.id.adView)).loadAd(new AdRequest.Builder().build());

        //do operations
        try {
            switch (itemOperation.getId()) {
                //basic data
                case 0:
                    isBasic = true;
                    break;
                //system of equations
                case 1:
                    auxArray.add(bundleMatrix.get(0).solve());
                    break;
                //gauss elimination
                case 2:
                    auxArray.add(bundleMatrix.get(0).getU());
                    break;
                //inverse
                case 3:
                    auxArray.add(bundleMatrix.get(0).inverse());
                    break;
                //cofactor
                case 4:
                    auxArray.add(bundleMatrix.get(0).cofactor());
                    break;
                //adjugate
                case 5:
                    auxArray.add(bundleMatrix.get(0).cofactor().transpose());
                    break;
                //add
                case 6:
                    auxArray.add(bundleMatrix.get(0).plus(bundleMatrix.get(1)));
                    break;
                //subtract
                case 7:
                    auxArray.add(bundleMatrix.get(0).minus(bundleMatrix.get(1)));
                    break;
                //multiply
                case 8:
                    auxArray.add(bundleMatrix.get(0).times(bundleMatrix.get(1)));
                    break;
                //LU decomposition
                case 9:
                    auxArray.add(bundleMatrix.get(0).getL());
                    auxArray.add(bundleMatrix.get(0).getU());
                    break;
                //scalar product
                case 10:
                    auxArray.add(bundleMatrix.get(0).times(scalar));
                    break;
                //matrix to the power of a scalar
                case 11:
                    auxArray.add(bundleMatrix.get(0).scalarPower(scalar));
                    break;
                //diagonal matrix
                case 12:
                    auxArray.addAll(bundleMatrix.get(0).diagonalMatrix());
                    break;
            }
        } catch(Exception e) {
            exception = e.getMessage();
        }

        for (int i = 0; i < auxArray.size(); ++i)
            finalMatrix.add(new ItemMatrix(String.format(titleFormat, matrixName, matrixLetter.charAt(i + bundleMatrix.size())), auxArray.get(i)));

        initViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViewPager() {
        final String[] titleArray = getResources().getStringArray(R.array.display_result_title);
        final Fragment[] fragments = {new FragmentResult(), new FragmentResult(), new FragmentContinue()};
        final AdapterTabLayout adapter = new AdapterTabLayout(getSupportFragmentManager());
        final Bundle[] bundles = {new Bundle(), new Bundle(), new Bundle()};

        //add bundle to fragment 1
        bundles[0].putParcelableArrayList("bundleMatrix", bundleMatrix);

        //add bundle to fragment 2
        if (!isBasic) {
            if (exception == null)
                bundles[1].putParcelableArrayList("bundleMatrix", finalMatrix);
            else
                bundles[1].putString("exception", exception);
        }

        //add bundle to fragment 3
        final ArrayList<ItemMatrix> arrayList = new ArrayList<>();
        arrayList.addAll(bundleMatrix);
        arrayList.addAll(finalMatrix);
        bundles[2].putParcelableArrayList("bundleMatrix", arrayList);

        for (int i = 0; i < 3; ++i) {
            if (i != 1 || !isBasic) {
                fragments[i].setArguments(bundles[i]);
                adapter.addFragment(fragments[i], titleArray[i]);
            }
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem((isBasic) ? 0 : 1);
    }

    private void showTutorial() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(getString(R.string.key_tutorial_result), false)) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.tutorial_result_head)
                .setMessage(R.string.tutorial_result_body)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sharedPreferences.edit().putBoolean(getString(R.string.key_tutorial_result), true).apply();
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