package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Liberacao implements Parcelable {

    @SerializedName("CodFilial")
    public String codigoLoja;

    @SerializedName("DescEmpresa")
    public String descricaoEmpresa;

    @SerializedName("DescFilial")
    public String descricaoFilial;

    @SerializedName("tkSolic")
    public String solicitacaoToken;

    @SerializedName("codigo")
    public String liberacaoCodigo;

    @SerializedName("tkLiberacao")
    public String liberacaoToken;

    public String status;

    @SerializedName("mensagem")
    public String mensagem;

    @SerializedName("tpAcesso")
    public String perfilAcesso;

    public String ultimoAcesso;

    @SerializedName("idAndroid")
    public String idAndroid;

    @SerializedName("macDispositivo")
    public String mac;

    @SerializedName("appNome")
    public String appNome;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(codigoLoja);
        parcel.writeString(descricaoEmpresa);
        parcel.writeString(descricaoFilial);
        parcel.writeString(solicitacaoToken);
        parcel.writeString(liberacaoCodigo);
        parcel.writeString(liberacaoToken);
        parcel.writeString(status);
        parcel.writeString(mensagem);
        parcel.writeString(perfilAcesso);
        parcel.writeString(ultimoAcesso);
        parcel.writeString(idAndroid);
        parcel.writeString(mac);
        parcel.writeString(appNome);
    }

    private void readFromParcel(Parcel parcel) {
        codigoLoja = parcel.readString();
        descricaoEmpresa = parcel.readString();
        descricaoFilial = parcel.readString();
        solicitacaoToken = parcel.readString();
        liberacaoCodigo = parcel.readString();
        liberacaoToken = parcel.readString();
        status = parcel.readString();
        mensagem = parcel.readString();
        perfilAcesso = parcel.readString();
        ultimoAcesso = parcel.readString();
        idAndroid = parcel.readString();
        mac = parcel.readString();
        appNome = parcel.readString();
    }

    public static final Creator<Liberacao> CREATOR = new Creator<Liberacao>() {
        @Override
        public Liberacao createFromParcel(Parcel parcel) {
            Liberacao liberacao = new Liberacao();
            liberacao.readFromParcel(parcel);
            return liberacao;
        }

        @Override
        public Liberacao[] newArray(int size) {
            return new Liberacao[size];
        }
    };
}