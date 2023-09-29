package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Cor implements Parcelable {

    public long id;

    @SerializedName("CODIGOCOR")
    public String codigo;

    @SerializedName("DESCRIERP")
    public String descricaoErp;

    @SerializedName("DESCRIFAB")
    public String descricaoFornecedor;

    @SerializedName("PRECOATU")
    public double precoAtual;

    @SerializedName("PRECOANT")
    public double precoAnterior;

    @SerializedName("PRECOSHOE")
    public double precoShoebizCard;

    @SerializedName("SHOE_PRODUTO_FILHO")
    public List<ProdutoFilho> produtoFilho;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(codigo);
        parcel.writeString(descricaoErp);
        parcel.writeString(descricaoFornecedor);
        parcel.writeDouble(precoAtual);
        parcel.writeDouble(precoAnterior);
        parcel.writeDouble(precoShoebizCard);
        parcel.writeTypedList(produtoFilho);
    }

    public void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.codigo = parcel.readString();
        this.descricaoErp = parcel.readString();
        this.descricaoFornecedor = parcel.readString();
        this.precoAtual = parcel.readDouble();
        this.precoAnterior = parcel.readDouble();
        this.precoShoebizCard = parcel.readDouble();

        if (produtoFilho == null) {
            produtoFilho = new ArrayList<>();
        }

        parcel.readTypedList(produtoFilho, ProdutoFilho.CREATOR);
    }

    public static final Creator<Cor> CREATOR = new Creator<Cor>() {
        @Override
        public Cor createFromParcel(Parcel parcel) {
            Cor cor = new Cor();
            cor.readFromParcel(parcel);
            return cor;
        }

        @Override
        public Cor[] newArray(int size) {
            return new Cor[size];
        }
    };
}