package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TipoOcorrencia implements Parcelable {

    @SerializedName("CODIGO")
    public String codigo;

    @SerializedName("DESCRICAO")
    public String descricao;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(codigo);
        parcel.writeString(descricao);
    }

    public void readFromParcel(Parcel parcel) {
        this.codigo = parcel.readString();
        this.descricao = parcel.readString();
    }

    public static final Parcelable.Creator<Ocorrencia> CREATOR = new Creator<Ocorrencia>() {
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