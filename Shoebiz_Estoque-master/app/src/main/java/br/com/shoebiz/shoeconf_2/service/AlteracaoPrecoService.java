package br.com.shoebiz.shoeconf_2.service;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.shoebiz.shoeconf_2.model.AlteracaoPreco;
import br.com.shoebiz.shoeconf_2.model.HistoricoPreco;
import br.com.shoebiz.shoeconf_2.service.api.AlteracaoPrecoAPI;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlteracaoPrecoService {
    private final Context context;
    private final Retrofit retrofit;

    public interface HistoricoAlteracaoCallback {
        void onSuccess(List<HistoricoPreco> alteracoesPreco);
    }

    public interface ConsultaAlteracaoPrecoCallback {
        void onSuccess(AlteracaoPreco alteracaoPreco);
    }

    public AlteracaoPrecoService(final Context context) {
        this.context = context;

        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("t", new PrefsUtils(context).isLibToken())
                    .addHeader("filial", new PrefsUtils(context).isCodLoja())
                    .build();
            return chain.proceed(request);
        }).readTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Utils.getUrlRestEsc_3() + "alteracaoPreco/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void consultaHistoricoPrecoAPI(String codigo, final HistoricoAlteracaoCallback callback) {
        Utils.showProgress(context,"Atenção!", "Consultando historico de alterações");

        AlteracaoPrecoAPI alteracaoPrecoAPI = retrofit.create(AlteracaoPrecoAPI.class);
        Call<List<HistoricoPreco>> historicoAlteracaoCall = alteracaoPrecoAPI.consultaHistorico(codigo);
        historicoAlteracaoCall.enqueue(new Callback<List<HistoricoPreco>>() {

            @Override
            public void onResponse(@NonNull Call<List<HistoricoPreco>> call, @NonNull Response<List<HistoricoPreco>> response) {
                List<HistoricoPreco> alteracoesPreco = response.body();

                Utils.closeProgress();

                if (String.valueOf(response.code()).trim().startsWith("4") || response.code() == 500) {
                    try {
                        Utils.toast(context, Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        Utils.toast(context, e.getMessage());
                    }
                }

                callback.onSuccess(alteracoesPreco);
            }

            @Override
            public void onFailure(@NonNull Call<List<HistoricoPreco>> call, @NonNull Throwable t) {
                Utils.closeProgress();
                Utils.toast(context, t.getMessage());

                callback.onSuccess(null);
            }
        });
    }

    public void consultaAlteracaoPrecoAPI(String dataDe, String dateAte, final ConsultaAlteracaoPrecoCallback callback) {
        Utils.showProgress(context,"Atenção!", "Consultando Produto");

        AlteracaoPrecoAPI alteracaoPrecoAPI = retrofit.create(AlteracaoPrecoAPI.class);
        Call<AlteracaoPreco> alteracaoPrecoCall = alteracaoPrecoAPI.consultaAlteracaoV2(dataDe, dateAte);
        alteracaoPrecoCall.enqueue(new Callback<AlteracaoPreco>() {

            @Override
            public void onResponse(@NonNull Call<AlteracaoPreco> call, @NonNull Response<AlteracaoPreco> response) {
                AlteracaoPreco alteracaoPreco = response.body();

                Utils.closeProgress();

                if (String.valueOf(response.code()).trim().startsWith("4") || response.code() == 500) {
                    try {
                        Utils.toast(context, Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        Utils.toast(context, e.getMessage());
                    }
                }

                callback.onSuccess(alteracaoPreco);
            }

            @Override
            public void onFailure(@NonNull Call<AlteracaoPreco> call, @NonNull Throwable t) {
                Utils.closeProgress();
                Utils.toast(context, t.getMessage());

                callback.onSuccess(null);
            }
        });
    }
}