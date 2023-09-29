package br.com.shoebiz.shoeconf_2.service;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Nota;
import br.com.shoebiz.shoeconf_2.model.Ocorrencia;
import br.com.shoebiz.shoeconf_2.model.TipoOcorrencia;
import br.com.shoebiz.shoeconf_2.service.api.EntradaMercadoriaAPI;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntradaMercadoriaService {
    private Retrofit retrofit;

    public interface ValidaDanfeCallback {
        void onSuccess(APIMensagem apiMensagem);
        void onFailure(String erro);
    }

    public interface ConsultaNotaCallback {
        void onSuccess(Nota nota);
        void onFailure(String erro);
    }

    public interface ConsultaNotasCallback {
        void onSuccess(List<Nota> notas);
        void onFailure(String erro);
        void onFinish(List<Nota> notas);
    }

    public interface ConsultaFormaEntradaCallback {
        void onSuccess(APIMensagem apiMensagem);
        void onFailure(String erro);
    }

    public interface ConsultaTipoOcorrenciaCallback {
        void onSuccess(List<TipoOcorrencia> ocorrencias);
        void onFailure(String erro);
    }

    public interface EnviaCallback {
        void onSuccess(String mensagem);
        void onFailure(String erro);
    }

    public EntradaMercadoriaService(final Context context, final String operacao) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("t", new PrefsUtils(context).isLibToken())
                    .addHeader("filial", new PrefsUtils(context).isCodLoja())
                    .addHeader("oper", operacao)
                    .build();

            return chain.proceed(request);
        })
                .connectTimeout(!operacao.equals("V") ? Utils.TIME_OUT_RETROFIT_CONNECT : 360, TimeUnit.SECONDS)
                .writeTimeout(!operacao.equals("V") ? Utils.TIME_OUT_RETROFIT_CONNECT : 360, TimeUnit.SECONDS)
                .readTimeout(!operacao.equals("V") ? Utils.TIME_OUT_RETROFIT_READ : 360, TimeUnit.SECONDS)
                .build();

        this.retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Utils.getUrlRestEsc_1() + "entradaMercadoria/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void validaDanfe(String chave, final ValidaDanfeCallback callback) {
        EntradaMercadoriaAPI entradaMercadoriaAPI = this.retrofit.create(EntradaMercadoriaAPI.class);
        Call<APIMensagem> validaDanfeCall = entradaMercadoriaAPI.validaDanfe(chave);
        validaDanfeCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull retrofit2.Response<APIMensagem> response) {
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

    public void consultaNota(String chave, final ConsultaNotaCallback callback) {
        EntradaMercadoriaAPI entradaMercadoriaAPI = this.retrofit.create(EntradaMercadoriaAPI.class);
        Call<Nota> consultaNotaCall = entradaMercadoriaAPI.consultaNota(chave);
        consultaNotaCall.enqueue(new Callback<Nota>() {

            @Override
            public void onResponse(@NonNull Call<Nota> call, @NonNull retrofit2.Response<Nota> response) {
                Nota nota = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(nota);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Nota> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void consultaNotas(String status, final ConsultaNotasCallback callback) {
        EntradaMercadoriaAPI entradaMercadoriaAPI = this.retrofit.create(EntradaMercadoriaAPI.class);
        Call<List<Nota>> consultaNotasCall = entradaMercadoriaAPI.consultaNotas(status);
        consultaNotasCall.enqueue(new Callback<List<Nota>>() {

            @Override
            public void onResponse(@NonNull Call<List<Nota>> call, @NonNull retrofit2.Response<List<Nota>> response) {
                List<Nota> notas = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(notas);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Nota>> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void consutaTipoOcorrencia(final ConsultaTipoOcorrenciaCallback callback) {
        EntradaMercadoriaAPI entradaMercadoriaAPI = this.retrofit.create(EntradaMercadoriaAPI.class);
        Call<List<TipoOcorrencia>> consultaTipoOcorrenciaCall = entradaMercadoriaAPI.consultaTipoOcorrencia();
        consultaTipoOcorrenciaCall.enqueue(new Callback<List<TipoOcorrencia>>() {

            @Override
            public void onResponse(@NonNull Call<List<TipoOcorrencia>> call, @NonNull retrofit2.Response<List<TipoOcorrencia>> response) {
                List<TipoOcorrencia> tiposOcorrencia = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onSuccess(tiposOcorrencia);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TipoOcorrencia>> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void consultaFormaEntrada(final ConsultaFormaEntradaCallback callback) {
        EntradaMercadoriaAPI entradaMercadoriaAPI = this.retrofit.create(EntradaMercadoriaAPI.class);
        Call<APIMensagem> consultaFormaEntradaCall = entradaMercadoriaAPI.consultaFormaColeta();
        consultaFormaEntradaCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull retrofit2.Response<APIMensagem> response) {
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

    public void enviaOcorrencia(Ocorrencia ocorrencia, final EnviaCallback callback) {
        EntradaMercadoriaAPI entradaMercadoriaAPI = this.retrofit.create(EntradaMercadoriaAPI.class);
        Call<APIMensagem> enviaOcorrenciaCall = entradaMercadoriaAPI.enviaOcorrencia(ocorrencia);
        enviaOcorrenciaCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull retrofit2.Response<APIMensagem> response) {
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

    public void enviaNota(String chave, final EnviaCallback callback) {
        EntradaMercadoriaAPI entradaMercadoriaAPI = this.retrofit.create(EntradaMercadoriaAPI.class);
        Call<APIMensagem> enviaNotaCall = entradaMercadoriaAPI.enviaNota(chave);
        enviaNotaCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull retrofit2.Response<APIMensagem> response) {
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
}