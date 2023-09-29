package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TransfGtin implements Parcelable {

    @SerializedName("CODIGOPRODUTO")
    public String produtoCodigo;

    @SerializedName("CODIGOGTIN")
    public String produtoGtin;

    @SerializedName("CODIGO")
    public String codigo;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(produtoCodigo);
        parcel.writeString(produtoGtin);
        parcel.writeString(codigo);
    }

    public void readFromParcel(Parcel parcel) {
        this.produtoCodigo = parcel.readString();
        this.produtoGtin = parcel.readString();
        this.codigo = parcel.readString();
    }

    public static final Creator<TransfGtin> CREATOR = new Creator<TransfGtin>() {
        @Override
        public TransfGtin createFromParcel(Parcel parcel) {
            TransfGtin transfGtin = new TransfGtin();
            transfGtin.readFromParcel(parcel);
            return transfGtin;
        }

        @Override
        public TransfGtin[] newArray(int size) {
            return new TransfGtin[size];
        }
    };
}