package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TransfProduto implements Parcelable {

    private long id;

    public String coletado;

    @SerializedName("CODIGOPRODUTO")
    public String codigoProduto;

    @SerializedName("DESCRICAO")
    public String descricao;

    @SerializedName("DESCCATEG")
    public String descricaoGrupo;

    @SerializedName("UNICACONT")
    public String unicaCount;

    @SerializedName("QUANTIDADE")
    public int quantidade;

    @SerializedName("SHOE_TRANSF_GTIN")
    public List<TransfGtin> transfGtins;

    public int divergencia;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(coletado);
        parcel.writeString(codigoProduto);
        parcel.writeString(descricao);
        parcel.writeString(descricaoGrupo);
        parcel.writeString(unicaCount);
        parcel.writeInt(quantidade);
        parcel.writeInt(divergencia);
        parcel.writeTypedList(transfGtins);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.coletado = parcel.readString();
        this.codigoProduto = parcel.readString();
        this.descricao = parcel.readString();
        this.descricaoGrupo = parcel.readString();
        this.unicaCount = parcel.readString();
        this.quantidade = parcel.readInt();
        this.divergencia = parcel.readInt();

        if (this.transfGtins == null) {
            this.transfGtins = new ArrayList<>();
        }

        parcel.readTypedList(this.transfGtins, TransfGtin.CREATOR);
    }

    public static final Creator<TransfProduto> CREATOR = new Creator<TransfProduto>() {
        @Override
        public TransfProduto createFromParcel(Parcel parcel) {
            TransfProduto transfProduto = new TransfProduto();
            transfProduto.readFromParcel(parcel);
            return transfProduto;
        }

        @Override
        public TransfProduto[] newArray(int size) {
            return new TransfProduto[size];
        }
    };

    public boolean isTotal() {
        return codigoProduto != null && codigoProduto.length() == 9;
    }

    public boolean isUnicaContagem() {
        return unicaCount.equals("S");
    }
}