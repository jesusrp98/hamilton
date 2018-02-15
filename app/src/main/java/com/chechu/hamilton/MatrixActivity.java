package com.chechu.hamilton;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager.LayoutParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import Jama.Matrix;
import me.relex.circleindicator.CircleIndicator;

public class MatrixActivity extends AppCompatActivity {
    private TextView[] textViewsDimension = new TextView[2];
    private SeekBar[] seekBarsDimension = new SeekBar[2];
    private ItemOperation itemOperation;
    private AlertDialog editTextDialog;
    private String[] stringsDimension;
    private AdapterTabLayout adapter;
    private Button confirmButton;
    private ViewPager viewPager;
    private EditText editText;
    private double scalar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_matrix);

        final Bundle bundle = getIntent().getExtras();
        itemOperation = bundle.getParcelable("itemOperation");

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(itemOperation.getTitle());

        showTutorial();

        //ui init
        editText = findViewById(R.id.matrix_edittext);
        viewPager = findViewById(R.id.matrix_viewpager);
        confirmButton = findViewById(R.id.matrix_button);
        seekBarsDimension[0] = findViewById(R.id.matrix_seekbar_row);
        seekBarsDimension[1] = findViewById(R.id.matrix_seekbar_column);
        textViewsDimension[0] =  findViewById(R.id.matrix_textview_row);
        textViewsDimension[1] =  findViewById(R.id.matrix_textview_column);
        stringsDimension = getResources().getStringArray(R.array.display_dimension);

        initViewPager(bundle.<ItemMatrix>getParcelableArrayList("bundleMatrix"));

        final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                switch (seekBar.getId()) {
                    case R.id.matrix_seekbar_row:
                        seekBarChangeListener(0, progress);
                        break;

                    case R.id.matrix_seekbar_column:
                        seekBarChangeListener(1, progress);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
        seekBarsDimension[0].setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarsDimension[1].setOnSeekBarChangeListener(onSeekBarChangeListener);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //if press enter
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    tryEnterText();
                    if (!getFragment().stepPointer())
                        nextPage();
                    return true;
                }
                return false;
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryEnterText();
                nextPage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_matrix, menu);
        //hide options if single matrix
        menu.findItem(R.id.action_swap).setVisible(itemOperation.getMatrixNumber() == 2);
        menu.findItem(R.id.action_clone).setVisible(itemOperation.getMatrixNumber() == 2);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                editTextDialogView(1);
                break;
            case R.id.action_transpose:
                getFragment().transposeMatrix();
                break;
            case R.id.action_sample:
                sampleMatrixDialog();
                break;
            case R.id.action_opposite:
                getFragment().oppositeMatrix();
                break;
            case R.id.action_swap:
                final ItemMatrix aux = getMatrix(0).copy();
                getFragment(0).setMatrix(getMatrix(1));
                getFragment(1).setMatrix(aux);
                break;
            case R.id.action_clone:
                getFragment((viewPager.getCurrentItem() == 0) ? 1 : 0).setMatrix(getMatrix());
                break;
            case R.id.action_reset:
                finish();
                startActivity(getIntent());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViewPager(ArrayList<ItemMatrix> bundleArrayList) {
        final ArrayList<ItemMatrix> arrayList = (bundleArrayList == null) ? new ArrayList<ItemMatrix>() : bundleArrayList;
        adapter = new AdapterTabLayout(getSupportFragmentManager());
        final String titleFormat = getString(R.string.placeholder_matrix_name);
        final String matrixName = getString(R.string.display_matrix_name);
        final String matrixLetter = getString(R.string.display_matrix_letter);
        int i;

        //check if has matrix in bundle
        if (arrayList.size() != itemOperation.getMatrixNumber()) {
            int[] dimensionDefault = new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString(getString(R.string.key_default_matrix_dimension), null), int[].class);

            //add default matrices
            for (i = arrayList.size(); i < itemOperation.getMatrixNumber(); ++i)
                arrayList.add(new ItemMatrix(String.format(titleFormat, matrixName, matrixLetter.charAt(i)),
                        new Matrix(dimensionDefault[0], dimensionDefault[1])));
        }

        //create fragments
        for (i = 0; i < itemOperation.getMatrixNumber(); ++i) {
            FragmentMatrix fragmentMatrix = new FragmentMatrix();
            Bundle bundle = new Bundle();

            bundle.putParcelable("matrix", arrayList.get(i));
            fragmentMatrix.setArguments(bundle);
            adapter.addFragment(fragmentMatrix, "");
        }

        updateSeekBars(arrayList.get(0));
        viewPager.setAdapter(adapter);

        if (adapter.getCount() > 1)
            ((CircleIndicator) findViewById(R.id.matrix_circleindicator)).setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateSeekBars(null);
            }
        });
    }

    public void migrateData() {
        final Intent intent = new Intent(this, ResultActivity.class);

        intent.putExtra("bundleMatrix", getMatrixArray());
        intent.putExtra("itemOperation", itemOperation);
        intent.putExtra("scalar", scalar);
        startActivity(intent);
    }

    public void seekBarChangeListener(int dimension, int progress) {
        getFragment().updateMatrixDimension(dimension, progress);
        textViewsDimension[dimension].setText(String.format(getString(R.string.placeholder_seekbar), stringsDimension[dimension], getMatrix().getDimensionArray()[dimension]));
    }

    @SuppressWarnings("unchecked")
    private void sampleMatrixDialog() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //get saved & sample matrices
        final ArrayList<String> matrixList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.display_input_sample)));
        matrixList.addAll((Collection<? extends String>) new Gson().fromJson(sharedPreferences.getString(getString(R.string.key_list_matrix), null), new TypeToken<ArrayList<String>>(){}.getType()));

        new AlertDialog.Builder(this)
            .setTitle(R.string.action_input_matrix)
            .setSingleChoiceItems(matrixList.toArray(new String[matrixList.size()]), 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            })
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    switch (((AlertDialog) dialog).getListView().getCheckedItemPosition()) {
                        //identity matrix
                        case 0:
                            getFragment().setMatrix(getMatrix().identityMatrix());
                            break;
                        //null matrix
                        case 1:
                            getFragment().setMatrix(getMatrix().nullMatrix());
                            break;
                        //random matrix
                        case 2:
                            getFragment().setMatrix(getMatrix().randomMatrix(
                                    new Gson().fromJson(sharedPreferences.getString(getString(R.string.key_random_range_dimension), null), int[].class)));
                            break;
                        //saved matrix
                        default:
                            getFragment().setMatrix(
                                    new Gson().fromJson(sharedPreferences.getString(getString(R.string.key_matrix) + matrixList.get(((AlertDialog) dialog).getListView().getCheckedItemPosition()), null), ItemMatrix.class));
                            updateSeekBars(null);
                            break;
                    }
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .show();
    }

    @SuppressLint("InflateParams")
    private void editTextDialogView(final int type) {
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_text, null);

        final EditText dialogEditText = dialogView.findViewById(R.id.text_input);
        dialogEditText.setHint(getString((type == 0) ? R.string.display_hint_number : R.string.display_hint_text));
        dialogEditText.setInputType((type == 0) ? (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED) : (InputType.TYPE_CLASS_TEXT));
        dialogEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //if pressed enter
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    enterEditTextDialog(type, dialogEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((type == 0) ? R.string.display_input_scalar : R.string.action_save)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    enterEditTextDialog(type, dialogEditText.getText().toString());
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        editTextDialog = builder.create();
        editTextDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        editTextDialog.show();
    }

    private void enterEditTextDialog(int type, String string) {
        try {
            if (type == 0) {
                scalar = Float.parseFloat(string);
                migrateData();
            } else
                saveMatrix(string);
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        editTextDialog.dismiss();
    }

    private void saveMatrix(String string) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final ArrayList<String> stringList = new Gson().fromJson(preferences.getString(getString(R.string.key_list_matrix), null), new TypeToken<ArrayList<String>>(){}.getType());

        if (!string.equals("")) {
            if (!stringList.contains(string)) {
                //finally saves matrix to storage
                SharedPreferences.Editor editor = preferences.edit();
                stringList.add(string);
                getFragment().setMatrixName(string);
                editor.putString(getString(R.string.key_matrix) + string, new Gson().toJson(getMatrix()));
                editor.putString(getString(R.string.key_list_matrix), new Gson().toJson(stringList)).apply();
                Toast.makeText(getApplicationContext(), R.string.message_saved_matrix, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), R.string.error_existing_matrix, Toast.LENGTH_SHORT).show();
        }
    }

    private void tryEnterText() {
        String text = editText.getText().toString();

        if (!text.matches("")) {
            getFragment().setCell(Double.parseDouble(text));
            editText.setText("");
        }
    }

    public void updateSeekBars(ItemMatrix itemMatrix){
        int[] newDimension = ((itemMatrix == null) ? getMatrix().getDimensionArray() : itemMatrix.getDimensionArray());

        for (int i = 0; i < 2; ++i) {
            seekBarsDimension[i].setProgress(newDimension[i] - 1);
            textViewsDimension[i].setText(String.format(getString(R.string.placeholder_seekbar), stringsDimension[i], newDimension[i]));
        }

        //change enter button
        if (itemOperation.getMatrixNumber() > 1) {
            if (viewPager.getCurrentItem() == 0)
                confirmButton.setText(getString(R.string.display_button_next));
            else
                confirmButton.setText(getString(R.string.display_button_solve));
        }
    }

    private void nextPage() {
        //if need scalar
        if (itemOperation.getId() == 10 || itemOperation.getId() == 11)
            editTextDialogView(0);
        else {
            //if final page
            if (itemOperation.getMatrixNumber() == 1 || viewPager.getCurrentItem() == 1)
                migrateData();
            else
                viewPager.setCurrentItem(1);
        }
    }

    public ArrayList<ItemMatrix> getMatrixArray() {
        ArrayList<ItemMatrix> auxArray = new ArrayList<>();

        for (int i = 0; i < itemOperation.getMatrixNumber(); ++i)
            auxArray.add(getMatrix(i));
        return auxArray;
    }

    private ItemMatrix getMatrix() {
        return getFragment().getItemMatrix();
    }

    private ItemMatrix getMatrix(int matrix) {
        return getFragment(matrix).getItemMatrix();
    }

    private FragmentMatrix getFragment() {
        return getFragment(viewPager.getCurrentItem());
    }

    private FragmentMatrix getFragment(int fragment) {
        return ((FragmentMatrix) adapter.getItem(fragment));
    }

    private void showTutorial() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(getString(R.string.key_tutorial_matrix), false)) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.tutorial_matrix_head)
                .setMessage(R.string.tutorial_matrix_body)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sharedPreferences.edit().putBoolean(getString(R.string.key_tutorial_matrix), true).apply();
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
}