package br.com.shoebiz.shoeconf_2.service;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.APIResponseAdapter;
import br.com.shoebiz.shoeconf_2.model.Coletor;
import br.com.shoebiz.shoeconf_2.model.ColetorProduto;
import br.com.shoebiz.shoeconf_2.service.api.ColetorAPI;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ColetorService {
    private static final String ENDPOINT = "coletor/";

    private ColetorAPI coletorAPI;

    public ColetorService(Context context) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("t", new PrefsUtils(context).isLibToken())
                    .addHeader("filial", new PrefsUtils(context).isCodLoja())
                    .build();
            return chain.proceed(request);
        }).readTimeout(Utils.TIMEOUT, TimeUnit.SECONDS).connectTimeout(Utils.TIMEOUT, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Utils.getUrlRestEsc_3() + ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        coletorAPI = retrofit.create(ColetorAPI.class);
    }

    public void validaProdutos(List<ColetorProduto> coletorProdutos, APIResponseAdapter<APIMensagem> callback) {
        Call<APIMensagem> validaProdutosCall = coletorAPI.validaProdutos(coletorProdutos);
        validaProdutosCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull Response<APIMensagem> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(@NonNull Call<APIMensagem> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void contagemProdutos(Coletor coletor, String nomeArquivo, APIResponseAdapter<APIMensagem> callback) {
        Call<APIMensagem> contagemProdutosCall = coletorAPI.contagemProdutos(coletor, nomeArquivo);
        contagemProdutosCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull Response<APIMensagem> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(@NonNull Call<APIMensagem> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
}