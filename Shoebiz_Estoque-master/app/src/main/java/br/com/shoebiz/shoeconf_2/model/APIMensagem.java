package br.com.shoebiz.shoeconf_2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class APIMensagem implements Parcelable {

    public String mensagem;

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel parcel) {
        mensagem = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mensagem);
    }

    public static final Creator<APIMensagem> CREATOR = new Creator<APIMensagem>() {
        @Override
        public APIMensagem createFromParcel(Parcel parcel) {
            APIMensagem apiMensagem = new APIMensagem();
            apiMensagem.readFromParcel(parcel);
            return apiMensagem;
        }

        @Override
        public APIMensagem[] newArray(int size) {
            return new APIMensagem[size];
        }
    };
}