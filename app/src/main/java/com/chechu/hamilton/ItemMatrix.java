package com.chechu.hamilton;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import Jama.Matrix;

public class ItemMatrix implements Parcelable {
    protected String name;
    protected Matrix matrix;

    ItemMatrix(ItemMatrix itemMatrix) {
        name = itemMatrix.name;
        matrix = itemMatrix.matrix;
    }

    ItemMatrix(String name, Matrix matrix) {
        this.name = name;
        this.matrix = matrix;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    int getRow() {
        return matrix.getRowDimension();
    }

    int getColumn() {
        return matrix.getColumnDimension();
    }

    double getCell(int i, int j) {
        return matrix.get(i, j);
    }

    private void setCell(int i, int j, double number) {
        matrix.set(i, j, number);
    }

    int[] getDimensionArray() {
        return new int[]{getRow(), getColumn()};
    }

    ItemMatrix copy(){
        return new ItemMatrix(name, matrix);
    }

    //preset matrices

    ItemMatrix randomMatrix(int[] range) {
        for (int i = 0; i < getRow(); ++i)
            for (int j = 0; j < getColumn(); ++j)
                setCell(i, j, (int) (Math.random() * ((range[1] - range[0]) + 1)) + range[0]);
        return this;
    }

    ItemMatrix identityMatrix() {
        for (int i = 0; i < getRow(); ++i)
            for (int j = 0; j < getColumn(); ++j)
                setCell(i, j, (i == j) ? 1 : 0);
        return this;
    }

    ItemMatrix nullMatrix() {
        for (int i = 0; i < getRow(); ++i)
            for (int j = 0; j < getColumn(); ++j)
                setCell(i, j, 0);
        return this;
    }

    //interface operations

    void transpose() {
        this.matrix = this.matrix.transpose();
    }

    void opposite() {
        this.matrix = this.matrix.uminus();
    }

    double determinant() {
        try {
            return matrix.det();
        } catch(Exception e) {
            return Double.NaN;
        }
    }

    int rank() {
        return this.matrix.rank();
    }

    double trace() {
        return this.matrix.trace();
    }

    Matrix solve() {
        if (getRow() != getColumn() - 1 || getRow() == 1)
            throw new IllegalArgumentException("Matrix dimensions are illegal.");
        final Matrix a = getMatrix().getMatrix(0, getRow() - 1, 0, getColumn() - 2);
        final Matrix b = getMatrix().getMatrix(0, getRow() - 1, getColumn() - 1, getColumn() - 1);
        return a.solve(b).transpose();
    }

    Matrix inverse() {
        return getMatrix().inverse();
    }

    Matrix getU() {
        return getMatrix().lu().getU();
    }

    Matrix getL() {
        return getMatrix().lu().getL();
    }

    Matrix times(double s) {
        return getMatrix().times(s);
    }

    Matrix times(ItemMatrix itemMatrix) {
        return getMatrix().times(itemMatrix.getMatrix());
    }

    Matrix plus(ItemMatrix itemMatrix) {
        return getMatrix().plus(itemMatrix.getMatrix());
    }

    Matrix minus(ItemMatrix itemMatrix) {
        return getMatrix().minus(itemMatrix.getMatrix());
    }

    ArrayList<Matrix> diagonalMatrix() {
        final ArrayList<Matrix> auxArray = new ArrayList<>();

        auxArray.add(getMatrix().eig().getD());
        auxArray.add(getMatrix().eig().getV());
        return auxArray;
    }

    Matrix cofactor() {
        final Matrix aux = new Matrix(getRow(), getColumn());
        int k = 0;

        for (int i = 0; i < getRow(); ++i){
            for (int j = 0; j < getColumn(); ++j) {
                aux.set(i, j, (k % 2 == 0 ? 1 : -1) * new Matrix(minor(matrix.getArray(), i, j)).det());
                ++k;
            }
        }
        return aux;
    }

    private static double[][] minor(double[][] matrix, int row, int column) {
        final double[][] minor = new double[matrix.length - 1][matrix.length - 1];

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; i != row && j < matrix[i].length; j++)
                if (j != column)
                    minor[i < row ? i : i - 1][j < column ? j : j - 1] = matrix[i][j];
        return minor;
    }


    Matrix scalarPower(double scalar) {
        final Matrix readMatrix = getMatrix();
        Matrix auxMatrix = getMatrix();

        for (int i = 1; i < scalar; ++i)
            auxMatrix = auxMatrix.times(readMatrix);
        return auxMatrix;
    }

    //parcel stuff

    private ItemMatrix(Parcel in) {
        name = in.readString();
        matrix = (Matrix) in.readValue(Matrix.class.getClassLoader());
    }

    public static final Parcelable.Creator<ItemMatrix> CREATOR = new Parcelable.Creator<ItemMatrix>() {
        @Override
        public ItemMatrix createFromParcel(Parcel in) {
            return new ItemMatrix(in);
        }

        @Override
        public ItemMatrix[] newArray(int size) {
            return new ItemMatrix[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeValue(matrix);
    }
}