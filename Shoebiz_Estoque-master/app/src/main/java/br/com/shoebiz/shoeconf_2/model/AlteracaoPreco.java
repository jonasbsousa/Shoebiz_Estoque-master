package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlteracaoPreco implements Parcelable {
    public long id;

    @SerializedName("PRODUTOPAI")
    public List<Produto> produtosPai;

    @SerializedName("PRODUTOCOR")
    public List<Produto> produtosCor;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeTypedList(produtosPai);
        parcel.writeTypedList(produtosCor);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readLong();

        if (produtosPai == null) {
            produtosPai = new ArrayList<>();
        }

        parcel.readTypedList(produtosPai, Produto.CREATOR);

        if (produtosCor == null) {
            produtosCor = new ArrayList<>();
        }

        parcel.readTypedList(produtosCor, Produto.CREATOR);
    }

    public static final Creator<AlteracaoPreco> CREATOR = new Creator<AlteracaoPreco>() {
        @Override
        public AlteracaoPreco createFromParcel(Parcel parcel) {
            AlteracaoPreco alteracaoPreco = new AlteracaoPreco();
            alteracaoPreco.readFromParcel(parcel);
            return alteracaoPreco;
        }

        @Override
        public AlteracaoPreco[] newArray(int size) {
            return new AlteracaoPreco[size];
        }
    };
}