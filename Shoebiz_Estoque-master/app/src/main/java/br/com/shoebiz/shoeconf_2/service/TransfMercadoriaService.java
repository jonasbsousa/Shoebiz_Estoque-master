package br.com.shoebiz.shoeconf_2.service;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.TransfNota;
import br.com.shoebiz.shoeconf_2.model.TransfProduto;
import br.com.shoebiz.shoeconf_2.model.TransfQuant;
import br.com.shoebiz.shoeconf_2.model.TransfStatus;
import br.com.shoebiz.shoeconf_2.service.api.TransfMercadoriaAPI;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransfMercadoriaService {
    private Context context;
    private Retrofit retrofit;

    public interface ConsultaQuantCallback {
        void onSuccess(TransfQuant transfQuant);
        void onFailure(String erro);
    }

    public interface ConsultaAutoEntCallback {
        void onSuccess(APIMensagem apiMensagem);
        void onFailure(String erro);
    }

    public interface ConsultaNotasCallback {
        void onSuccess(List<TransfNota> transfNotas);
        void onFailure(String erro);
    }

    public interface ConsultaNotaCallback {
        void onSuccess(TransfNota transfNota);
        void onFailure(String erro);
    }

    public interface ConsultaStatusCallback {
        void onSuccess(TransfStatus transfStatus);
        void onFailure(String erro);
    }

    public interface EnviaCallBack {
        void onSuccess(String mensagem);
        void onFailure(String erro);
    }

    public TransfMercadoriaService(final Context context, final String filialOrigem, final String quantVolumes) {
        this.context = context;

        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("t", new PrefsUtils(context).isLibToken())
                    .addHeader("filial", new PrefsUtils(context).isCodLoja())
                    .addHeader("ljOrigem", filialOrigem)
                    .addHeader("quantVolume", quantVolumes)
                    .build();
            return chain.proceed(request);
        }).readTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Utils.getUrlRestEsc_2() + "transferenciaMercadoria/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void consultaQntProdCol(final ConsultaQuantCallback callback) {
        TransfMercadoriaAPI transfMercadoriaAPI = retrofit.create(TransfMercadoriaAPI.class);
        Call<TransfQuant> apiMensagemCall = transfMercadoriaAPI.qntProdColet();
        apiMensagemCall.enqueue(new Callback<TransfQuant>() {

            @Override
            public void onResponse(@NonNull Call<TransfQuant> call, @NonNull Response<TransfQuant> response) {
                TransfQuant transfQuant = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(transfQuant);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransfQuant> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void ConsultaAutoEnt(final ConsultaAutoEntCallback callback) {
        TransfMercadoriaAPI transfMercadoriaAPI = retrofit.create(TransfMercadoriaAPI.class);
        Call<APIMensagem> apiMensagemCall = transfMercadoriaAPI.autoEntrada();
        apiMensagemCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull Response<APIMensagem> response) {
                APIMensagem apiMensagem = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(apiMensagem);
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIMensagem> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void consultaStatus(final ConsultaStatusCallback callback) {
        TransfMercadoriaAPI transfMercadoriaAPI = retrofit.create(TransfMercadoriaAPI.class);
        Call<TransfStatus> transfStatusCall = transfMercadoriaAPI.consultaStatus();
        transfStatusCall.enqueue(new Callback<TransfStatus>() {

            @Override
            public void onResponse(@NonNull Call<TransfStatus> call, @NonNull Response<TransfStatus> response) {
                TransfStatus transfStatus = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(transfStatus);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransfStatus> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void consultaOtsAPI(String status, final ConsultaNotasCallback callback) {
        TransfMercadoriaAPI transfMercadoriaAPI = retrofit.create(TransfMercadoriaAPI.class);
        Call<List<TransfNota>> transfMercadoriaCall = transfMercadoriaAPI.consultaOts(status);
        transfMercadoriaCall.enqueue(new Callback<List<TransfNota>>() {

            @Override
            public void onResponse(@NonNull Call<List<TransfNota>> call, @NonNull Response<List<TransfNota>> response) {
                List<TransfNota> transfNotas = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(transfNotas);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TransfNota>> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void consultaOtAPI(String codigo, final ConsultaNotaCallback callback) {
        TransfMercadoriaAPI transfMercadoriaAPI = retrofit.create(TransfMercadoriaAPI.class);
        Call<TransfNota> transfMercadoriaCall = transfMercadoriaAPI.consultaOt(codigo);
        transfMercadoriaCall.enqueue(new Callback<TransfNota>() {

            @Override
            public void onResponse(@NonNull Call<TransfNota> call, @NonNull Response<TransfNota> response) {
                TransfNota transfNota = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(transfNota);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransfNota> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void consultaNotasAPI(String status, final ConsultaNotasCallback callback) {
        TransfMercadoriaAPI transfMercadoriaAPI = retrofit.create(TransfMercadoriaAPI.class);
        Call<List<TransfNota>> transfMercadoriaCall = transfMercadoriaAPI.consultaNotas(status);
        transfMercadoriaCall.enqueue(new Callback<List<TransfNota>>() {

            @Override
            public void onResponse(@NonNull Call<List<TransfNota>> call, @NonNull Response<List<TransfNota>> response) {
                List<TransfNota> transfNotas = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(transfNotas);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TransfNota>> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void consultaNotaAPI(String chave, final ConsultaNotaCallback callback) {
        TransfMercadoriaAPI transfMercadoriaAPI = retrofit.create(TransfMercadoriaAPI.class);
        Call<TransfNota> transfMercadoriaCall = transfMercadoriaAPI.consultaNota(chave);
        transfMercadoriaCall.enqueue(new Callback<TransfNota>() {

            @Override
            public void onResponse(@NonNull Call<TransfNota> call, @NonNull Response<TransfNota> response) {
                TransfNota transfNota = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(transfNota);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransfNota> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void enviaNotaAPI(TransfNota transfNota, final EnviaCallBack callback) {
        Utils.showProgress(context,"Atenção!", "Enviando nota");

        TransfNota transfNotaAux = new TransfNota();
        transfNotaAux.chave = transfNota.chave;
        transfNotaAux.codLojaOrigem = transfNota.codLojaOrigem;

        TransfMercadoriaAPI transfMercadoriaAPI = retrofit.create(TransfMercadoriaAPI.class);
        Call<APIMensagem> enviaNotaCall = transfMercadoriaAPI.recebeNota(transfNotaAux);
        enviaNotaCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull Response<APIMensagem> response) {
                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(Objects.requireNonNull(response.body()).mensagem);
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIMensagem> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void enviaOtAPI(String codigoOt, List<TransfProduto> transfProdutos, final EnviaCallBack callBack) {
        TransfMercadoriaAPI transfMercadoriaAPI = retrofit.create(TransfMercadoriaAPI.class);
        Call<APIMensagem> enviaOtCall = transfMercadoriaAPI.recebeOt(transfProdutos, codigoOt);
        enviaOtCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull Response<APIMensagem> response) {
                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callBack.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callBack.onFailure(e.getMessage());
                    }
                } else {
                    callBack.onSuccess(Objects.requireNonNull(response.body()).mensagem);
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIMensagem> call, @NonNull Throwable t) {
                callBack.onFailure(t.getMessage());
            }
        });
    }
}