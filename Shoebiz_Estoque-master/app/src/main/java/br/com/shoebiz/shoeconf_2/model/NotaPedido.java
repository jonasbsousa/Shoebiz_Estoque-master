package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NotaPedido implements Parcelable{

    public long id;

    @SerializedName("NUMEROPEDIDO")
    public String codigo;

    @SerializedName("CODIGOPAI")
    public String codigoPai;

    @SerializedName("CODIGORESUMIDO")
    public String codigoResumido;

    @SerializedName("DESCRICAOPRODUTO")
    public String descricao;

    @SerializedName("PEDIDOOK")
    public String pedidoOk;

    @SerializedName("SHOE_DANFECOR")
    public List<Cor> cores;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(codigo);
        parcel.writeString(codigoPai);
        parcel.writeString(codigoResumido);
        parcel.writeString(descricao);
        parcel.writeString(pedidoOk);
        parcel.writeTypedList(cores);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();
        this.codigo = parcel.readString();
        this.codigoPai = parcel.readString();
        this.codigoResumido = parcel.readString();
        this.descricao = parcel.readString();
        this.pedidoOk = parcel.readString();

        if (cores == null) {
            cores = new ArrayList<>();
        }

        parcel.readTypedList(cores, Cor.CREATOR);
    }

    public static final Creator<NotaPedido> CREATOR = new Creator<NotaPedido>() {
        @Override
        public NotaPedido createFromParcel(Parcel parcel) {
            NotaPedido danfePedido = new NotaPedido();
            danfePedido.readFromParcel(parcel);
            return danfePedido;
        }

        @Override
        public NotaPedido[] newArray(int size) {
            return new NotaPedido[size];
        }
    };
}