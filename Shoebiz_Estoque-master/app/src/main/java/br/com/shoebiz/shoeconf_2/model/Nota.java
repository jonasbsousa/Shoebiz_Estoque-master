package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Nota implements Parcelable {

    @SerializedName("LOJADEST")
    public String lojaDest;

    @SerializedName("CHAVE")
    public String chave;

    @SerializedName("EMISSAO")
    public Date emissao;

    @SerializedName("NOTANUMERO")
    public String notaNumero;

    @SerializedName("NOTASERIE")
    public String notaSerie;

    @SerializedName("FORNECEDORCOD")
    public String fornecedorCod;

    @SerializedName("FORNECEDORLOJA")
    public String fornecedorLoja;

    @SerializedName("FORNECEDORCNPJ")
    public String fornecedorCnpj;

    @SerializedName("FORNECEDORDESC")
    public String fornecedorDesc;

    @SerializedName("RECEBIMENTODATA")
    public Date recebimentoData;

    @SerializedName("RECEBIMENTOHORA")
    public String recebimentoHora;

    public String liberacaoMensagem;

    @SerializedName("LIBERACAOUSER")
    public String liberacaoUser;

    @SerializedName("LIBERACAODATA")
    public Date liberacaoData;

    @SerializedName("LIBERACAOHORA")
    public String liberacaoHora;

    @SerializedName("ENTRADAEFETUADA")
    public String notaProtheus;

    @SerializedName("STATUS")
    public String status;

    @SerializedName("STATUSPROC")
    public String statusProc;

    @SerializedName("SHOE_DANFEPEDIDO")
    public List<NotaPedido> notaPedidos;

    public List<ProdutoFilho> produtosFilho;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(lojaDest);
        parcel.writeString(chave);
        parcel.writeSerializable(emissao);
        parcel.writeString(notaNumero);
        parcel.writeString(notaSerie);
        parcel.writeString(fornecedorCod);
        parcel.writeString(fornecedorLoja);
        parcel.writeString(fornecedorCnpj);
        parcel.writeString(fornecedorDesc);
        parcel.writeSerializable(recebimentoData);
        parcel.writeString(recebimentoHora);
        parcel.writeString(liberacaoMensagem);
        parcel.writeString(liberacaoUser);
        parcel.writeSerializable(liberacaoData);
        parcel.writeString(liberacaoHora);
        parcel.writeString(notaProtheus);
        parcel.writeString(status);
        parcel.writeString(statusProc);
        parcel.writeTypedList(notaPedidos);
        parcel.writeTypedList(produtosFilho);
    }

    public void readFromParcel(Parcel parcel) {
        this.lojaDest = parcel.readString();
        this.chave = parcel.readString();
        this.emissao = (Date) parcel.readSerializable();
        this.notaNumero = parcel.readString();
        this.notaSerie = parcel.readString();
        this.fornecedorCod = parcel.readString();
        this.fornecedorLoja = parcel.readString();
        this.fornecedorCnpj = parcel.readString();
        this.fornecedorDesc = parcel.readString();
        this.recebimentoData = (Date) parcel.readSerializable();
        this.recebimentoHora = parcel.readString();
        this.liberacaoMensagem = parcel.readString();
        this.liberacaoUser = parcel.readString();
        this.liberacaoData = (Date) parcel.readSerializable();
        this.notaProtheus = parcel.readString();
        this.liberacaoHora = parcel.readString();
        this.status = parcel.readString();
        this.statusProc = parcel.readString();

        if (notaPedidos == null) {
            notaPedidos = new ArrayList<>();
        }

        parcel.readTypedList(notaPedidos, NotaPedido.CREATOR);

        if (produtosFilho == null) {
            produtosFilho = new ArrayList<>();
        }

        parcel.readTypedList(produtosFilho, ProdutoFilho.CREATOR);
    }

    public static final Creator<Nota> CREATOR = new Creator<Nota>() {
        @Override
        public Nota createFromParcel(Parcel parcel) {
            Nota nota = new Nota();
            nota.readFromParcel(parcel);
            return nota;
        }

        @Override
        public Nota[] newArray(int size) {
            return new Nota[size];
        }
    };
}