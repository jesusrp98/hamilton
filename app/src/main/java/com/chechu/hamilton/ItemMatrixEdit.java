package com.chechu.hamilton;

public class ItemMatrixEdit extends ItemMatrix {
    private String titleFormat;
    private int[] pointer;

    ItemMatrixEdit(ItemMatrix itemMatrix, String titleFormat) {
        super(itemMatrix);
        this.titleFormat = titleFormat;
        pointer = new int[2];
    }

    public String getTitle() {
        return String.format(titleFormat, getName(), getRow(), getColumn());
    }

    int[] getPointer() {
        return pointer;
    }

    void setPointer(int[] pointer) {
        this.pointer = pointer;
    }

    boolean isFinalCell() {
        return ((getRow() - 1) == pointer[0]) && ((getColumn() - 1) == pointer[1]);
    }

    int isPointerOutOfRange() {
        //indicated whether pointer is out of matrix boundaries
        if (pointer[0]  > (getRow() - 1) && pointer[1] > (getColumn() - 1))
            return 2;
        if (pointer[0] > (getRow() - 1))
            return 0;
        if (pointer[1] > (getColumn() - 1))
            return 1;
        return -1;
    }

    void setItemMatrix(ItemMatrix itemMatrix) {
        setMatrix(itemMatrix.getMatrix());
        setName(itemMatrix.getName());
    }
}
