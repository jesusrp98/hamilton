package com.chechu.hamilton;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemOperation implements Parcelable {
    private int id;
    private String title;
    private String description;
    private String icon;
    private int matrixNumber;

    ItemOperation(int id, String title, String description, String icon, int matrixNumber) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.matrixNumber = matrixNumber;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    int getMatrixNumber() {
        return matrixNumber;
    }

    //parcel stuff

    private ItemOperation(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        icon = in.readString();
        matrixNumber = in.readInt();
    }

    public static final Creator<ItemOperation> CREATOR = new Creator<ItemOperation>() {
        @Override
        public ItemOperation createFromParcel(Parcel in) {
            return new ItemOperation(in);
        }

        @Override
        public ItemOperation[] newArray(int size) {
            return new ItemOperation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(icon);
        parcel.writeInt(matrixNumber);
    }
}