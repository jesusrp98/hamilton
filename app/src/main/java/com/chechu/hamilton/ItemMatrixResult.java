package com.chechu.hamilton;

class ItemMatrixResult extends ItemMatrix {
    private double determinant;
    private double trace;
    private int rank;

    ItemMatrixResult(ItemMatrix itemMatrix) {
        super(itemMatrix);
        this.determinant = determinant();
        this.trace = trace();
        this.rank = rank();
    }

    double getDeterminant() {
        return determinant;
    }

    double getTrace() {
        return trace;
    }

    int getRank() {
        return rank;
    }
}
