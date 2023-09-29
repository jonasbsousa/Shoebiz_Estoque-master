package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TransfQuant implements Parcelable {

    @SerializedName("produtos")
    public int quantProdutos;

    @SerializedName("linhas")
    public int quantLinhas;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(quantProdutos);
        parcel.writeInt(quantLinhas);
    }

    private void readFromParcel(Parcel parcel) {
        quantProdutos = parcel.readInt();
        quantLinhas = parcel.readInt();
    }

    public static final Creator<TransfQuant> CREATOR = new Creator<TransfQuant>() {
        @Override
        public TransfQuant createFromParcel(Parcel parcel) {
            TransfQuant transfQuant = new TransfQuant();
            transfQuant.readFromParcel(parcel);
            return transfQuant;
        }

        @Override
        public TransfQuant[] newArray(int size) {
            return new TransfQuant[size];
        }
    };
}