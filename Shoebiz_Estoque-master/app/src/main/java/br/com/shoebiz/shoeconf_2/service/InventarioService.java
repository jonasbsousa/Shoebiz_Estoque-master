package br.com.shoebiz.shoeconf_2.service;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Inventario;
import br.com.shoebiz.shoeconf_2.service.api.InventarioAPI;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InventarioService {

    private Context context;
    private Retrofit retrofit;

    public interface inventarioAbertoCallback {
        void onSuccess(Inventario inventario);
    }

    public interface statusAparelhoCallback {
        void onSuccess(Inventario inventario);
    }

    public interface arquivoSB1Callback {
        void onSuccess(Inventario inventario);
    }

    public interface gravaAreaCallback {
        void onSuccess(Inventario inventario);
    }

    public InventarioService(final Context context) {
        this.context = context;

        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("t", new PrefsUtils(context).isLibToken())
                    .addHeader("filial", new PrefsUtils(context).isCodLoja())
                    .build();
            return chain.proceed(request);
        }).readTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).build();

        this.retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Utils.getUrlRestEsc_2() + "inventario/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void consultaInventarioAbertoAPI(final inventarioAbertoCallback callback) {
        Utils.showProgress(context,"Atenção!", "Consultando inventário aberto");

        InventarioAPI inventarioAPI = retrofit.create(InventarioAPI.class);
        Call<Inventario> inventarioAbertoCall = inventarioAPI.consultaInventarioAberto();
        inventarioAbertoCall.enqueue(new Callback<Inventario>() {

            @Override
            public void onResponse(@NonNull Call<Inventario> call, @NonNull Response<Inventario> response) {
                Inventario inventario = response.body();

                Utils.closeProgress();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        Utils.toast(context, Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        Utils.toast(context, e.getMessage());
                    }
                }

                callback.onSuccess(inventario);
            }

            @Override
            public void onFailure(@NonNull Call<Inventario> call, @NonNull Throwable t) {
                Utils.closeProgress();
                Utils.toast(context, t.getMessage());

                callback.onSuccess(null);
            }
        });
    }

    public void consultaStatusAparelhoAPI(final statusAparelhoCallback callback) {
        Utils.showProgress(context,"Atenção!", "Consultando status do aparelho");

        InventarioAPI inventarioAPI = retrofit.create(InventarioAPI.class);
        Call<Inventario> statusAparelhoCall = inventarioAPI.consultaStatusAparelho(new PrefsUtils(context).isLibCodigo());
        statusAparelhoCall.enqueue(new Callback<Inventario>() {

            @Override
            public void onResponse(@NonNull Call<Inventario> call, @NonNull Response<Inventario> response) {
                Inventario inventario = response.body();

                Utils.closeProgress();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        Utils.toast(context, Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        Utils.toast(context, e.getMessage());
                    }
                }

                callback.onSuccess(inventario);
            }

            @Override
            public void onFailure(@NonNull Call<Inventario> call, @NonNull Throwable t) {
                Utils.closeProgress();
                Utils.toast(context, t.getMessage());

                callback.onSuccess(null);
            }
        });
    }

    public void consultaArquivoSB1API(final arquivoSB1Callback callback ) {
        Utils.showProgress(context,"Atenção!", "Consultando cadastro de produtos");

        InventarioAPI inventarioAPI = retrofit.create(InventarioAPI.class);
        Call<Inventario> arquivoSb1Call = inventarioAPI.consultaArquivoSb1();
        arquivoSb1Call.enqueue(new Callback<Inventario>() {

            @Override
            public void onResponse(@NonNull Call<Inventario> call, @NonNull Response<Inventario> response) {
                Inventario inventario = response.body();

                Utils.closeProgress();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        Utils.toast(context, Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        Utils.toast(context, e.getMessage());
                    }
                }

                callback.onSuccess(inventario);
            }

            @Override
            public void onFailure(@NonNull Call<Inventario> call, @NonNull Throwable t) {
                Utils.closeProgress();
                Utils.toast(context, t.getMessage());

                callback.onSuccess(null);
            }
        });
    }

    public void gravarAreaAPI(final Inventario inventario, final gravaAreaCallback callback) {
        InventarioAPI inventarioAPI = retrofit.create(InventarioAPI.class);

        Call<APIMensagem> gravaInventarioCall = inventarioAPI.gravarArea(inventario);
        gravaInventarioCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull Response<APIMensagem> response) {
                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        Utils.toast(context, Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        Utils.toast(context, e.getMessage());
                    }

                    inventario.integrado = "N";
                } else {
                    inventario.integrado = "S";
                }

                callback.onSuccess(inventario);
            }

            @Override
            public void onFailure(@NonNull Call<APIMensagem> call, @NonNull Throwable t) {
                Utils.toast(context, t.getMessage());

                inventario.integrado = "N";

                callback.onSuccess(inventario);
            }
        });
    }
}