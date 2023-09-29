package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ProdutoFilho implements Parcelable {

    public long id;

    @SerializedName("TAMANHO")
    public String codigo;

    @SerializedName("GTIN")
    public String codigoGtin;

    @SerializedName("QUANTIDADE")
    public int quantidade;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(codigo);
        parcel.writeString(codigoGtin);
        parcel.writeInt(quantidade);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.codigo = parcel.readString();
        this.codigoGtin = parcel.readString();
        this.quantidade = parcel.readInt();
    }

    public static final Creator<ProdutoFilho> CREATOR = new Creator<ProdutoFilho>() {
        @Override
        public ProdutoFilho createFromParcel(Parcel parcel) {
            ProdutoFilho produtoFilho = new ProdutoFilho();
            produtoFilho.readFromParcel(parcel);
            return produtoFilho;
        }

        @Override
        public ProdutoFilho[] newArray(int size) {
            return new ProdutoFilho[size];
        }
    };
}