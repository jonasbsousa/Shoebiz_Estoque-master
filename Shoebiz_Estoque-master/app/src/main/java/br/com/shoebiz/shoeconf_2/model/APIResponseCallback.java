package br.com.shoebiz.shoeconf_2.model;

public interface APIResponseCallback<T> {

    void onSuccess(T t);
    void onFailure(String erro);
}