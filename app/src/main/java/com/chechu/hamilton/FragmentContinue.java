package com.chechu.hamilton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class FragmentContinue extends Fragment {
    private ArrayList<ItemMatrix> bundleMatrix;
    private boolean[] checkedItems;
    int i;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_continue, container, false);

        final Bundle bundle = this.getArguments();
        bundleMatrix = bundle.getParcelableArrayList("bundleMatrix");

        final ListView listView = view.findViewById(R.id.result_listview);
        listView.setAdapter(new AdapterOperationList(getActivity()));

        //creates string array
        final ArrayList<String> stringArrayList = new ArrayList<>();
        for (i = 0; i < bundleMatrix.size(); ++i)
            stringArrayList.add(bundleMatrix.get(i).getName());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final ItemOperation itemOperation = (ItemOperation) parent.getItemAtPosition(position);
                checkedItems = new boolean[stringArrayList.size()];

                new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.display_select_matrix))
                    .setMultiChoiceItems(stringArrayList.toArray(new String[stringArrayList.size()]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {}
                    })
                    .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //if selected matrices is not higher than operation matrices number
                            if (getCheckedMatrix(checkedItems) <= itemOperation.getMatrixNumber()) {
                                final Intent intent = new Intent(getActivity(), MatrixActivity.class);
                                final ArrayList<ItemMatrix> auxArray = new ArrayList<>();

                                //saves selected matrices
                                for (i = 0; i < bundleMatrix.size(); ++i)
                                    if (checkedItems[i])
                                        auxArray.add(bundleMatrix.get(i));

                                intent.putExtra("itemOperation", itemOperation);
                                intent.putExtra("bundleMatrix", auxArray);
                                startActivity(intent);
                            } else
                                Toast.makeText(getContext(), String.format(getResources().getString(R.string.error_select_matrix), itemOperation.getMatrixNumber()), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .create().show();
            }
        });
        return view;
    }

    private int getCheckedMatrix(boolean[] array) {
        int aux = 0;
        for (boolean b : array)
            aux += b ? 1 : 0;
        return aux;
    }
}