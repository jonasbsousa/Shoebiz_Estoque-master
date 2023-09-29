package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ColetorProduto implements Parcelable {
    public String codigo;
    public int quantidade;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(codigo);
        parcel.writeInt(quantidade);
    }

    public void readFromParcel(Parcel parcel) {
        codigo = parcel.readString();
        quantidade = parcel.readInt();
    }

    public static final Creator<ColetorProduto> CREATOR = new Creator<ColetorProduto>() {
        @Override
        public ColetorProduto createFromParcel(Parcel parcel) {
            ColetorProduto coletorProduto = new ColetorProduto();
            coletorProduto.readFromParcel(parcel);
            return coletorProduto;
        }

        @Override
        public ColetorProduto[] newArray(int size) {
            return new ColetorProduto[size];
        }
    };
}