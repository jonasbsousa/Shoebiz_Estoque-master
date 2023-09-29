package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProdutoEntrada implements Parcelable {
    public String codigo;
    public int quantidade;
    public String descricao;
    public String cor;
    public String tamanho;
    public String status;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(codigo);
        parcel.writeInt(quantidade);
        parcel.writeString(descricao);
        parcel.writeString(cor);
        parcel.writeString(tamanho);
        parcel.writeString(status);
    }

    public void readFromParcel(Parcel parcel) {
        codigo = parcel.readString();
        quantidade = parcel.readInt();
        descricao = parcel.readString();
        cor = parcel.readString();
        tamanho = parcel.readString();
        status = parcel.readString();
    }

    public static final Creator<ProdutoEntrada> CREATOR = new Creator<ProdutoEntrada>() {
        @Override
        public ProdutoEntrada createFromParcel(Parcel parcel) {
            ProdutoEntrada notaEntrada = new ProdutoEntrada();
            notaEntrada.readFromParcel(parcel);
            return notaEntrada;
        }

        @Override
        public ProdutoEntrada[] newArray(int size) {
            return new ProdutoEntrada[size];
        }
    };
}