package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Ocorrencia implements Parcelable {
    public long id;

    @SerializedName("chave")
    public String chave;

    @SerializedName("tipoOcorrencia")
    public String codigo;

    @SerializedName("detalhes")
    public String descricao;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(chave);
        parcel.writeString(codigo);
        parcel.writeString(descricao);
    }

    public void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.chave = parcel.readString();
        this.codigo = parcel.readString();
        this.descricao = parcel.readString();
    }

    public static final Creator<Ocorrencia> CREATOR = new Creator<Ocorrencia>() {
        @Override
        public Ocorrencia createFromParcel(Parcel parcel) {
            Ocorrencia ocorrencia = new Ocorrencia();
            ocorrencia.readFromParcel(parcel);
            return ocorrencia;
        }

        @Override
        public Ocorrencia[] newArray(int size) {
            return new Ocorrencia[size];
        }
    };

    @Override
    public String toString() {
        return codigo + " - " + descricao.substring(0,1).toUpperCase() + descricao.substring(1).toLowerCase();
    }
}