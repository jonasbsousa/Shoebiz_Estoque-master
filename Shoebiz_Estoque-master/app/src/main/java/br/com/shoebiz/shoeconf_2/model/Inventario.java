package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Inventario implements Parcelable {
    public long id;

    @SerializedName("codInventario")
    public String codigo;

    @SerializedName("area")
    public String area;

    public String integrado;

    @SerializedName("codAparelho")
    public String aparelhoCodigo;

    @SerializedName("codOperador")
    public String operadorCodigo;

    @SerializedName("nomeOperador")
    public String operadorNome;

    @SerializedName("dataArquivo")
    public String dataArqSB1;

    @SerializedName("horaArquivo")
    public String horaArqSB1;

    @SerializedName("produtos")
    public List<ProdutoFilho> produtosFilho;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(codigo);
        parcel.writeString(area);
        parcel.writeString(integrado);
        parcel.writeString(aparelhoCodigo);
        parcel.writeString(operadorCodigo);
        parcel.writeString(operadorNome);
        parcel.writeString(dataArqSB1);
        parcel.writeString(horaArqSB1);
        parcel.writeTypedList(produtosFilho);
    }

    public void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.codigo = parcel.readString();
        this.area = parcel.readString();
        this.integrado = parcel.readString();
        this.aparelhoCodigo = parcel.readString();
        this.operadorCodigo = parcel.readString();
        this.operadorNome = parcel.readString();
        this.dataArqSB1 = parcel.readString();
        this.horaArqSB1 = parcel.readString();

        if (produtosFilho == null) {
            produtosFilho = new ArrayList<>();
        }

        parcel.readTypedList(produtosFilho, ProdutoFilho.CREATOR);
    }

    public static final Creator<Inventario> CREATOR = new Creator<Inventario>() {
        @Override
        public Inventario createFromParcel(Parcel parcel) {
            Inventario inventario = new Inventario();
            inventario.readFromParcel(parcel);
            return inventario;
        }

        @Override
        public Inventario[] newArray(int size) {
            return new Inventario[size];
        }
    };
}