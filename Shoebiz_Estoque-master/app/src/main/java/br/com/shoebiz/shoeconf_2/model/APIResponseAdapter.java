package br.com.shoebiz.shoeconf_2.model;

import java.io.IOException;

import br.com.shoebiz.shoeconf_2.utils.Utils;
import retrofit2.Response;

public class APIResponseAdapter<T> implements APIResponseCallback<Response<T>> {

    private APIResponseCallback<T> callback;

    public APIResponseAdapter(APIResponseCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onSuccess(Response<T> response) {
        if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() >= 500) {
            try {
                callback.onFailure(Utils.apiErroMessage(response));
            } catch (IOException e) {
                callback.onFailure(e.getMessage());
            }
        } else {
            callback.onSuccess(response.body());
        }
    }

    @Override
    public void onFailure(String message) {
        callback.onFailure(message);
    }
}