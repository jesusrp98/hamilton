package com.chechu.hamilton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Objects;

public class SettingsDialog extends DialogPreference {
    private final Context context;

    public SettingsDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        //dialog can be reset app content || delete custom matrices list
        //decides what to do when click ok of settings dialog
        if (positiveResult) {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            final String dialogTitle = getDialogTitle().toString();

            if (Objects.equals(context.getString(R.string.settings_restore_all_head), dialogTitle)) {
                final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editor.clear().apply();
                context.startActivity(intent);
            } else if (Objects.equals(context.getString(R.string.settings_restore_matrix_head), dialogTitle)) {
                final ArrayList<String> stringList = new Gson().fromJson(
                        sharedPreferences.getString(context.getString(R.string.key_list_matrix), null),
                        new TypeToken<ArrayList<String>>(){}.getType()
                );

                for (String string : stringList)
                    editor.remove(context.getString(R.string.key_matrix) + string);
                editor.putString(getContext().getString(R.string.key_list_matrix), new Gson().toJson(new ArrayList<String>())).apply();
                Toast.makeText(context, context.getString(R.string.settings_restore_matrix_message), Toast.LENGTH_SHORT).show();
            }
        }
    }
}