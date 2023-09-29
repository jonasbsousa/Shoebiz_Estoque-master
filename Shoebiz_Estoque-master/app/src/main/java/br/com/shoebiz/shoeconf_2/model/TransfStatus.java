package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TransfStatus implements Parcelable {

    @SerializedName("otAberta")
    public int quantOtAberta;

    @SerializedName("otPendente")
    public int quantOtPendente;

    @SerializedName("notaPendente")
    public int quantNotaPendente;

    @SerializedName("notaRecebida")
    public int quantNotaRecebida;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(quantOtAberta);
        parcel.writeInt(quantOtPendente);
        parcel.writeInt(quantNotaPendente);
        parcel.writeInt(quantNotaRecebida);
    }

    private void readFromParcel(Parcel parcel) {
        this.quantOtAberta = parcel.readInt();
        this.quantOtPendente = parcel.readInt();
        this.quantNotaPendente = parcel.readInt();
        this.quantNotaRecebida = parcel.readInt();
    }

    public static final Creator<TransfStatus> CREATOR = new Creator<TransfStatus>() {
        @Override
        public TransfStatus createFromParcel(Parcel parcel) {
            TransfStatus transfProduto = new TransfStatus();
            transfProduto.readFromParcel(parcel);
            return transfProduto;
        }

        @Override
        public TransfStatus[] newArray(int size) {
            return new TransfStatus[size];
        }
    };
}