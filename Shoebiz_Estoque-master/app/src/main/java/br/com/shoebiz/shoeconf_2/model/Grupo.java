package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Grupo implements Parcelable {

    public long id;

    @SerializedName("CODIGO")
    public String codigo;

    @SerializedName("DESCRICAO")
    public String descricao;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(codigo);
        parcel.writeString(descricao);
    }

    public void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.codigo = parcel.readString();
        this.descricao = parcel.readString();
    }

    public static final Creator<Grupo> CREATOR = new Creator<Grupo>() {
        @Override
        public Grupo createFromParcel(Parcel parcel) {
            Grupo grupo = new Grupo();
            grupo.readFromParcel(parcel);
            return grupo;
        }

        @Override
        public Grupo[] newArray(int size) {
            return new Grupo[size];
        }
    };
}