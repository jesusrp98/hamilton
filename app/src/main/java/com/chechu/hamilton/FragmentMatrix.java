package com.chechu.hamilton;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.text.DecimalFormat;
import Jama.Matrix;

public class FragmentMatrix extends Fragment {
    private TextView cardTitle;
    private ItemMatrixEdit matrix;
    private TextView[][] textViews;
    private TableLayout tableLayout;
    private DecimalFormat decimalFormat;
    private TableRow.LayoutParams layoutParams;
    private View.OnClickListener onClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_matrix, container, false);

        //card ui init
        decimalFormat = new DecimalFormat("#.###");
        cardTitle = view.findViewById(R.id.matrix_input_title);
        tableLayout = view.findViewById(R.id.matrix_input_layout);
        matrix = new ItemMatrixEdit((ItemMatrix) this.getArguments().getParcelable("matrix"), getString(R.string.placeholder_matrix_title));
        layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPointer((int[]) view.getTag());
            }
        };

        updateCardView();
        return view;
    }

    private void drawTextViews() {
        //reinit textview array and cleans tableview
        textViews = new TextView[matrix.getRow()][matrix.getColumn()];
        tableLayout.removeAllViews();

        for (int i = 0; i < matrix.getRow(); ++i) {
            //create new row to populate
            final TableRow row = new TableRow(getContext());

            for (int j = 0; j < matrix.getColumn(); ++j) {
                //format textview
                final TextView textView = (TextView) View.inflate(getContext(), R.layout.item_matrix_text, null);

                textView.setText(decimalFormat.format(matrix.getCell(i, j)));
                textView.setOnClickListener(onClickListener);
                textView.setTag(new int[]{i, j});
                textViews[i][j] = textView;
                row.addView(textView);
            }
            tableLayout.addView(row, layoutParams);
        }
    }

    public boolean stepPointer() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int counterMode;
        int[] pointer;
        int mode;

        //move pointer one unit if allowed
        if (!matrix.isFinalCell()) {
            mode = Integer.parseInt(sharedPreferences.getString(getString(R.string.key_cell_flow), "0"));
            pointer = getPointer();
            counterMode = (mode == 0) ? 1 : 0;

            if (pointer[counterMode] + 1 != matrix.getDimensionArray()[counterMode])
                setPointer(new int[]{pointer[0] + mode, pointer[1] + counterMode});
            else
                setPointer(new int[]{(pointer[0] + 1) * counterMode, (pointer[1] + 1) * mode});
        } else {
            if (sharedPreferences.getBoolean(getString(R.string.key_avoid_nextpage), false))
                setPointer(new int[]{0, 0});
            else
                return false;
        }
        return true;
    }

    private void updatePointer() {
        int pointerOutOfRange = matrix.isPointerOutOfRange();
        int[] pointer = getPointer();

        //updates pointer when matrix changes
        if (pointerOutOfRange != -1)
            setPointer(
                    new int[]{(pointerOutOfRange == 0 || pointerOutOfRange == 2) ? (matrix.getRow() - 1) : pointer[0],
                            (pointerOutOfRange == 1 || pointerOutOfRange == 2) ? (matrix.getColumn() - 1) : pointer[1]
                    }
            );
        else
            setPointer(getPointer());
    }

    public void setCell(double number) {
        int[] pointer = getPointer();

        //set cell & updates textview background
        matrix.getMatrix().set(pointer[0], pointer[1], number);
        textViews[pointer[0]][pointer[1]].setText(String.valueOf(decimalFormat.format(number)));
    }

    public void transposeMatrix() {
        matrix.transpose();
        updateCardView();
    }

    public void oppositeMatrix() {
        matrix.opposite();
        updateCardView();
    }

    public void updateCardViewTitle() {
        cardTitle.setText(matrix.getTitle());
    }

    private void updateCardView() {
        drawTextViews();
        updateCardViewTitle();
        updatePointer();
    }

    public void updateMatrixDimension(int dimension, int progress) {
        matrix.setMatrix(getUpdatedMatrix(dimension, progress + 1));
        updateCardView();
    }

    private Matrix getUpdatedMatrix(int dimension, int progress) {
        Matrix auxMatrix = new Matrix((dimension == 0) ? progress : matrix.getRow(), (dimension == 1) ? progress : matrix.getColumn());

        //creates new matrix with new dimension, but same cells
        for (int i = 0; i < auxMatrix.getRowDimension(); ++i) {
            for (int j = 0; j < auxMatrix.getColumnDimension(); ++j) {
                if ((i < matrix.getRow()) && (j < matrix.getColumn()))
                    auxMatrix.set(i, j, matrix.getCell(i, j));
                else
                    auxMatrix.set(i, j, 0);
            }
        }
        return auxMatrix;
    }

    public ItemMatrix getItemMatrix() {
        return matrix;
    }

    public int[] getPointer() {
        return matrix.getPointer();
    }

    public void setMatrixName(String name) {
        matrix.setName(name);
        updateCardViewTitle();
    }

    public void setMatrix(ItemMatrix itemMatrix) {
        matrix.setItemMatrix(itemMatrix);
        updateCardView();
    }

    public void setPointer(int[] pointer) {
        int[] oldPointer = getPointer();

        if (matrix.isPointerOutOfRange() == -1)
            textViews[oldPointer[0]][oldPointer[1]].setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.matrix_cell));
        matrix.setPointer(pointer);
        textViews[pointer[0]][pointer[1]].setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.matrix_cell_selected));
    }
}