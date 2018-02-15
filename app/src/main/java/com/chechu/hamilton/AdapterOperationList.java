package com.chechu.hamilton;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdapterOperationList extends ArrayAdapter<ItemOperation> implements Filterable {
    private ArrayList<ItemOperation> auxItemList;
    private int[] colorArray;
    private Context context;

    AdapterOperationList(Context context) {
        super(context, R.layout.item_operation, initItemOperationList(context));
        this.auxItemList = getArrayListItems();
        this.context = context;
        colorArray = getColors();
    }

    //view holder cache
    private static class ViewHolder {
        ImageView iconView;
        TextView titleView;
        TextView descriptionView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ItemOperation itemOperation = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_operation, parent, false);
            viewHolder = new ViewHolder();

            //get item id
            viewHolder.iconView = convertView.findViewById(R.id.item_operation_icon);
            viewHolder.titleView = convertView.findViewById(R.id.item_operation_title);
            viewHolder.descriptionView = convertView.findViewById(R.id.item_operation_description);

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        //assign objects to viewholder
        viewHolder.iconView.setImageDrawable(
                TextDrawable.builder().beginConfig().textColor(colorArray[0]).endConfig().buildRound(itemOperation.getIcon(), colorArray[1])
        );
        viewHolder.titleView.setText(itemOperation.getTitle());
        viewHolder.descriptionView.setText(Html.fromHtml(itemOperation.getDescription()));

        return convertView;
    }

    private static ArrayList<ItemOperation> initItemOperationList(Context context) {
        final ArrayList<List<String>> stringArray = new ArrayList<>();
        final ArrayList<ItemOperation> aux = new ArrayList<>();

        //gets the data from xml file
        stringArray.add(Arrays.asList(context.getResources().getStringArray(R.array.operation_title)));
        stringArray.add(Arrays.asList(context.getResources().getStringArray(R.array.operation_description)));
        stringArray.add(Arrays.asList(context.getResources().getStringArray(R.array.operation_icon)));
        stringArray.add(Arrays.asList(context.getResources().getStringArray(R.array.operation_number)));

        for (int i = 0; i < stringArray.get(0).size(); ++i)
            aux.add(new ItemOperation(
                    i, stringArray.get(0).get(i), stringArray.get(1).get(i), stringArray.get(2).get(i), Integer.parseInt(stringArray.get(3).get(i)))
            );

        return aux;
    }

    private ArrayList<ItemOperation> getArrayListItems() {
        final ArrayList<ItemOperation> arrayList = new ArrayList<>();

        for (int i = 0; i < getCount(); ++i)
            arrayList.add(getItem(i));

        return arrayList;
    }

    private int[] getColors() {
        final boolean mode = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_darkmode), false);
        final int[] colors = new int[2];

        //get colors depending of the theme
        colors[0] = context.getResources().getColor(mode ? android.R.color.black : android.R.color.white);
        colors[1] = context.getResources().getColor(mode ? R.color.colorAccentInverse : R.color.colorAccent);

        return colors;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        //filters the array adapter when search is activated
        return new Filter() {
            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                AdapterOperationList.super.clear();
                AdapterOperationList.super.addAll((ArrayList<ItemOperation>)results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final ArrayList<ItemOperation> filteredArrayList = new ArrayList<>();
                final String string = constraint.toString().toLowerCase();
                final FilterResults results = new FilterResults();

                //checks if search textbox is empty
                if (string.isEmpty()) {
                    results.count = auxItemList.size();
                    results.values = auxItemList;
                } else {
                    for (ItemOperation item : auxItemList) {
                        if ((item.getTitle().toLowerCase().contains(string)) || (item.getDescription().toLowerCase().contains(string)))
                            filteredArrayList.add(item);
                    }
                    results.count = filteredArrayList.size();
                    results.values = filteredArrayList;
                }
                return results;
            }
        };
    }
}