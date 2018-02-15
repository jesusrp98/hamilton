package com.chechu.hamilton;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterResult extends RecyclerView.Adapter<AdapterResult.ViewHolder> {
    private ArrayList<ItemMatrixResult> itemResultList;
    private static TableRow.LayoutParams layoutParams;
    private static DecimalFormat decimalFormat;
    private static String titleFormat;
    private static String valueFormat;
    private Context context;

    AdapterResult(ArrayList<ItemMatrixResult> itemList, Context context) {
        layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        decimalFormat = new DecimalFormat("#.###");
        titleFormat = context.getString(R.string.placeholder_matrix_title);
        valueFormat = context.getString(R.string.placeholder_matrix_prop);
        this.itemResultList = itemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemMatrixResult item = itemResultList.get(position);

        holder.title.setText(String.format(titleFormat, item.getName(), item.getRow(), item.getColumn()));
        drawTextViews(item, holder.layout);
        holder.determinant.setText(String.format(valueFormat, context.getString(R.string.display_determinant), decimalFormat.format(item.getDeterminant())));
        holder.rank.setText(String.format(valueFormat, context.getString(R.string.display_rank), decimalFormat.format(item.getRank())));
        holder.trace.setText(String.format(valueFormat, context.getString(R.string.display_trace), decimalFormat.format(item.getTrace())));
    }

    private void drawTextViews(ItemMatrixResult matrix, TableLayout tableLayout) {
        for (int i = 0; i < matrix.getRow(); ++i) {
            //create new row to populate
            final TableRow tableRow = new TableRow(context);

            for (int j = 0; j < matrix.getColumn(); ++j) {
                //format textview
                final TextView textView = (TextView) View.inflate(context, R.layout.item_matrix_text, null);

                textView.setText(decimalFormat.format(matrix.getCell(i, j)));
                textView.setTag(new int[]{i, j});
                tableRow.addView(textView);
            }
            tableLayout.addView(tableRow, layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return itemResultList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TableLayout layout;
        private final TextView determinant;
        private final TextView rank;
        private final TextView trace;

        private ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.matrix_result_title);
            layout = itemView.findViewById(R.id.matrix_result_layout);
            determinant = itemView.findViewById(R.id.matrix_result_det);
            rank = itemView.findViewById(R.id.matrix_result_range);
            trace = itemView.findViewById(R.id.matrix_result_trace);
        }
    }
}