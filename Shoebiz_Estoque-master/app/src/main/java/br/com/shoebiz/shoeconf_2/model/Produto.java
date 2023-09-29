package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Produto implements Parcelable {
    public long id;

    @SerializedName("CODIGOPAI")
    public String codigoPai;

    @SerializedName("DESCRICAO")
    public String descricao;

    @SerializedName("MARCA")
    public String marca;

    @SerializedName("CODRESUMIDO")
    public String codigoResumido;

    @SerializedName("TAMMAIOR")
    public String tamanhoMaior;

    @SerializedName("TAMMENOR")
    public String tamanhoMenor;

    @SerializedName("TIPOALTERACAO")
    public String tipoAlteracao;

    @SerializedName("DATAALTERACAO")
    public Date dataAlteracao;

    @SerializedName("QNTESTOQUE")
    public int quantidadeEstoque;

    @SerializedName("PRECOATU")
    public double precoAtual;

    @SerializedName("PRECOANT")
    public double precoAnterior;

    @SerializedName("GRUPO")
    public Grupo grupo;

    @SerializedName("SHOE_COR")
    public List<Cor> cores;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(codigoPai);
        parcel.writeString(descricao);
        parcel.writeString(marca);
        parcel.writeString(codigoResumido);
        parcel.writeString(tamanhoMaior);
        parcel.writeString(tamanhoMenor);
        parcel.writeString(tipoAlteracao);
        parcel.writeSerializable(dataAlteracao);
        parcel.writeInt(quantidadeEstoque);
        parcel.writeDouble(precoAtual);
        parcel.writeDouble(precoAnterior);
        parcel.writeParcelable(grupo, flags);
        parcel.writeTypedList(cores);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.codigoPai = parcel.readString();
        this.descricao = parcel.readString();
        this.marca = parcel.readString();
        this.codigoResumido = parcel.readString();
        this.tamanhoMaior = parcel.readString();
        this.tamanhoMenor = parcel.readString();
        this.tipoAlteracao = parcel.readString();
        this.dataAlteracao = (Date) parcel.readSerializable();
        this.quantidadeEstoque = parcel.readInt();
        this.precoAtual = parcel.readDouble();
        this.precoAnterior = parcel.readDouble();

        this.grupo = parcel.readParcelable(Grupo.class.getClassLoader());

        if (cores == null) {
            cores = new ArrayList<>();
        }

        parcel.readTypedList(cores, Cor.CREATOR);
    }

    public static final Creator<Produto> CREATOR = new Creator<Produto>() {
        @Override
        public Produto createFromParcel(Parcel parcel) {
            Produto produto = new Produto();
            produto.readFromParcel(parcel);
            return produto;
        }

        @Override
        public Produto[] newArray(int size) {
            return new Produto[size];
        }
    };
}