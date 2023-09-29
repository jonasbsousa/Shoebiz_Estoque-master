package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class HistoricoPreco implements Parcelable {
    public long id;

    @SerializedName("REVISAO")
    public String revisao;

    @SerializedName("PRECOPOR")
    public double precoAtual;

    @SerializedName("PRECODE")
    public double precoAnterior;

    @SerializedName("DATAALTERACAO")
    public Date dataAlteracao;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(revisao);
        parcel.writeDouble(precoAtual);
        parcel.writeDouble(precoAnterior);
        parcel.writeSerializable(dataAlteracao);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.revisao = parcel.readString();
        this.precoAtual = parcel.readDouble();
        this.precoAnterior = parcel.readDouble();
        this.dataAlteracao = (Date) parcel.readSerializable();
    }

    public static final Creator<HistoricoPreco> CREATOR = new Creator<HistoricoPreco>() {
        @Override
        public HistoricoPreco createFromParcel(Parcel parcel) {
            HistoricoPreco historicoPreco = new HistoricoPreco();
            historicoPreco.readFromParcel(parcel);
            return historicoPreco;
        }

        @Override
        public HistoricoPreco[] newArray(int size) {
            return new HistoricoPreco[size];
        }
    };
}