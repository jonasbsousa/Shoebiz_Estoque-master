package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OtProduto implements Parcelable {

    public String coletado;
    public String codigo;
    public String gtin;
    public String descricao;
    public int quantidade;
    public boolean codigoCaixa;
    public boolean codigoProduto;
    public boolean unicaContagem;
    public boolean ot;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(coletado);
        parcel.writeString(codigo);
        parcel.writeString(gtin);
        parcel.writeString(descricao);
        parcel.writeInt(quantidade);
        parcel.writeByte((byte) (codigoCaixa ? 1 : 0));
        parcel.writeByte((byte) (codigoProduto ? 1 : 0));
        parcel.writeByte((byte) (unicaContagem ? 1 : 0));
        parcel.writeByte((byte) (ot ? 1 : 0));
    }

    private void readFromParcel(Parcel parcel) {
        this.coletado = parcel.readString();
        this.codigo = parcel.readString();
        this.gtin = parcel.readString();
        this.descricao = parcel.readString();
        this.quantidade = parcel.readInt();
        this.codigoCaixa = parcel.readByte() != 0;
        this.codigoProduto = parcel.readByte() != 0;
        this.unicaContagem = parcel.readByte() != 0;
        this.ot = parcel.readByte() != 0;
    }

    public static final Creator<OtProduto> CREATOR = new Creator<OtProduto>() {
        @Override
        public OtProduto createFromParcel(Parcel parcel) {
            OtProduto otProduto = new OtProduto();
            otProduto.readFromParcel(parcel);
            return otProduto;
        }

        @Override
        public OtProduto[] newArray(int size) {
            return new OtProduto[size];
        }
    };

    public boolean achouNaOt() {
        return ot;
    }
}