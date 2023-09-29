package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransfNota implements Parcelable {

    public long id;

    @SerializedName("CHAVE")
    public String chave;

    @SerializedName("LJCODDEST")
    public String codLojaDestino;

    @SerializedName("LJCODORIG")
    public String codLojaOrigem;

    @SerializedName("LJDESDEST")
    public String descLojaDestino;

    @SerializedName("LJDESORIG")
    public String descLojaOrigem;

    @SerializedName("DATAEMISSAO")
    public Date emissao;

    @SerializedName("NOTANUMERO")
    public String numero;

    @SerializedName("NOTASERIE")
    public String serie;

    @SerializedName("OTNUMERO")
    public String otNumero;

    @SerializedName("PEDIDONUMERO")
    public String pedidoNumero;

    @SerializedName("STATUS")
    public String status;

    @SerializedName("STATUSPROC")
    public String statusProc;

    @SerializedName("VALORNOTA")
    public Double valorNota;

    @SerializedName("SHOE_TRANSF_OT_TIPO")
    public List<TransfTipo> transfTipos;

    @SerializedName("SHOE_TRANSF_OT_PROD")
    public List<TransfProduto> transfProdutos;

    @SerializedName("SHOE_TRANSF_GTIN_COR")
    public List<TransfGtin> transfGtin;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(chave);
        parcel.writeString(codLojaDestino);
        parcel.writeString(codLojaOrigem);
        parcel.writeString(descLojaDestino);
        parcel.writeString(descLojaOrigem);
        parcel.writeSerializable(emissao);
        parcel.writeString(numero);
        parcel.writeString(serie);
        parcel.writeString(otNumero);
        parcel.writeString(pedidoNumero);
        parcel.writeString(status);
        parcel.writeString(statusProc);
        parcel.writeDouble(valorNota);
        parcel.writeTypedList(transfTipos);
        parcel.writeTypedList(transfProdutos);
        parcel.writeTypedList(transfGtin);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.chave = parcel.readString();
        this.codLojaDestino = parcel.readString();
        this.codLojaOrigem = parcel.readString();
        this.descLojaDestino = parcel.readString();
        this.descLojaOrigem = parcel.readString();
        this.emissao = (Date) parcel.readSerializable();
        this.numero = parcel.readString();
        this.serie = parcel.readString();
        this.otNumero = parcel.readString();
        this.pedidoNumero = parcel.readString();
        this.status = parcel.readString();
        this.statusProc = parcel.readString();
        this.valorNota = parcel.readDouble();

        if (this.transfTipos == null) {
            this.transfTipos = new ArrayList<>();
        }

        parcel.readTypedList(this.transfTipos, TransfTipo.CREATOR);

        if (this.transfProdutos == null) {
            this.transfProdutos = new ArrayList<>();
        }

        parcel.readTypedList(this.transfProdutos, TransfProduto.CREATOR);

        if (this.transfGtin == null) {
            this.transfGtin = new ArrayList<>();
        }

        parcel.readTypedList(this.transfGtin, TransfGtin.CREATOR);
    }

    public static final Creator<TransfNota> CREATOR = new Creator<TransfNota>() {

        @Override
        public TransfNota createFromParcel(Parcel parcel) {
            TransfNota transfNota = new TransfNota();
            transfNota.readFromParcel(parcel);
            return transfNota;
        }

        @Override
        public TransfNota[] newArray(int size) {
            return new TransfNota[size];
        }
    };
}