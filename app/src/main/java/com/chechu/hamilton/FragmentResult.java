package com.chechu.hamilton;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class FragmentResult extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view;
        final Bundle bundle = this.getArguments();
        final String exception = bundle.getString("exception");

        if (exception == null) {
            view = inflater.inflate(R.layout.fragment_result, container, false);

            final RecyclerView recyclerView = view.findViewById(R.id.solutionRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);

            final ArrayList<ItemMatrix> bundleMatrix = bundle.getParcelableArrayList("bundleMatrix");
            final ArrayList<ItemMatrixResult> listItems = new ArrayList<>();

            //add matrix card to list
            for (int i = 0; i < bundleMatrix.size(); ++i)
                listItems.add(new ItemMatrixResult(bundleMatrix.get(i)));

            recyclerView.setAdapter(new AdapterResult(listItems, getActivity()));
        } else {
            view = inflater.inflate(R.layout.fragment_exception, container, false);
            ((TextView) view.findViewById(R.id.exceptionTextView)).setText(exception);
        }
        return view;
    }
}