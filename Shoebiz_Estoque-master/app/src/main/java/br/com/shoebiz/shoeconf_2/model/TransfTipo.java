package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TransfTipo implements Parcelable {

    private long id;

    @SerializedName("CODTIPO")
    public String codTipo;

    @SerializedName("SHOE_TRANSF_OT_PROD")
    public List<TransfProduto> transfProdutos;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(codTipo);
        parcel.writeTypedList(transfProdutos);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.codTipo = parcel.readString();

        if (this.transfProdutos == null) {
            this.transfProdutos = new ArrayList<>();
        }

        parcel.readTypedList(this.transfProdutos, TransfProduto.CREATOR);
    }

    public static final Creator<TransfTipo> CREATOR = new Creator<TransfTipo>() {
        @Override
        public TransfTipo createFromParcel(Parcel parcel) {
            TransfTipo transfTipo = new TransfTipo();
            transfTipo.readFromParcel(parcel);
            return transfTipo;
        }

        @Override
        public TransfTipo[] newArray(int size) {
            return new TransfTipo[size];
        }
    };
}