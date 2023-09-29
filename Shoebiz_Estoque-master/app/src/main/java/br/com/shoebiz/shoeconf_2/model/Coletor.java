package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Coletor implements Parcelable {

    @SerializedName("depto")
    public String departamento;

    @SerializedName("obs")
    public String observacao;

    @SerializedName("produtos")
    public List<ColetorProduto> coletorProdutos;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(departamento);
        parcel.writeString(observacao);
        parcel.writeTypedList(coletorProdutos);
    }

    public void readFromParcel(Parcel parcel) {
        departamento = parcel.readString();
        observacao = parcel.readString();

        if (coletorProdutos == null) {
            coletorProdutos = new ArrayList<>();
        }

        parcel.readTypedList(coletorProdutos, ColetorProduto.CREATOR);
    }

    public static final Creator<Coletor> CREATOR = new Creator<Coletor>() {
        @Override
        public Coletor createFromParcel(Parcel parcel) {
            Coletor coletor = new Coletor();
            coletor.readFromParcel(parcel);
            return coletor;
        }

        @Override
        public Coletor[] newArray(int size) {
            return new Coletor[size];
        }
    };
}