package br.com.shoebiz.shoeconf_2.service;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.shoebiz.shoeconf_2.model.Liberacao;
import br.com.shoebiz.shoeconf_2.service.api.AcessoAPI;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LiberacaoService {
    private Retrofit retrofit;

    public interface GenericCallBack {
        void onSuccess(Liberacao liberacao);
        void onFailure(String erro);
    }

    public interface ConsultaLiberacaoCallBack {
        void onSuccess(Liberacao liberacao);
        void onForbidden(String erro);
        void onUnauthorized(String erro);
        void onFailure(String erro);
    }

    public interface SolicitaLiberacaoCallBack {
        void onSuccess(Liberacao liberacao);
        void onUnauthorized(String erro);
        void onFailure(String erro);
    }

    public LiberacaoService(final Context context, final String codigo, final String ip, final String codigoIdent, final String idAndroid, final String tSolicitacao, final String tF) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("t", new PrefsUtils(context).isLibToken())
                    .addHeader("filial", new PrefsUtils(context).isCodLoja())
                    .addHeader("c", codigo)
                    .addHeader("idDispositivo", ip)
                    .addHeader("codigoIdent", codigoIdent)
                    .addHeader("idAndroid", idAndroid)
                    .addHeader("tSolicitacao", tSolicitacao)
                    .addHeader("tF", tF)
                    .build();
            return chain.proceed(request);
        }).readTimeout(Utils.TIME_OUT_RETROFIT_READ, TimeUnit.SECONDS).connectTimeout(Utils.TIME_OUT_RETROFIT_CONNECT, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Utils.getUrlRestEsc_3() + "acessoApp/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void verificaLocal(final GenericCallBack callBack) {
        AcessoAPI acessoAPI = retrofit.create(AcessoAPI.class);
        Call<Liberacao> acessoCall = acessoAPI.verificaLocal();
        acessoCall.enqueue(new Callback<Liberacao>() {

            @Override
            public void onResponse(@NonNull Call<Liberacao> call, @NonNull Response<Liberacao> response) {
                Liberacao liberacao = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4")) {
                    try {
                        callBack.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callBack.onFailure(e.getMessage());
                    }
                } else {
                    callBack.onSuccess(liberacao);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Liberacao> call, @NonNull Throwable t) {
                callBack.onFailure("Falha ao acessar o servidor!");
            }
        });
    }

    public void consultaAcesso(final GenericCallBack callBack) {
        AcessoAPI acessoAPI = retrofit.create(AcessoAPI.class);
        Call<Liberacao> acessoCall = acessoAPI.consultaAcesso();
        acessoCall.enqueue(new Callback<Liberacao>() {

            @Override
            public void onResponse(@NonNull Call<Liberacao> call, @NonNull Response<Liberacao> response) {
                Liberacao liberacao = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callBack.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callBack.onFailure(e.getMessage());
                    }
                } else {
                    callBack.onSuccess(liberacao);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Liberacao> call, @NonNull Throwable t) {
                callBack.onFailure(t.getMessage());
            }
        });
    }

    public void consultaLiberacao(final ConsultaLiberacaoCallBack callBack) {
        AcessoAPI acessoAPI = retrofit.create(AcessoAPI.class);
        Call<Liberacao> acessoCall = acessoAPI.consultaLiberacao();
        acessoCall.enqueue(new Callback<Liberacao>() {

            @Override
            public void onResponse(@NonNull Call<Liberacao> call, @NonNull Response<Liberacao> response) {
                Liberacao liberacao = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        if (response.code() == 401) {
                            callBack.onUnauthorized(Utils.apiErroMessage(response));
                        } else if (response.code() == 403) {
                            callBack.onForbidden(Utils.apiErroMessage(response));
                        } else {
                            callBack.onFailure(Utils.apiErroMessage(response));
                        }
                    } catch (IOException e) {
                        callBack.onFailure(e.getMessage());
                    }
                } else {
                    callBack.onSuccess(liberacao);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Liberacao> call, @NonNull Throwable t) {
                callBack.onFailure(t.getMessage());
            }
        });
    }

    public void consultaTipoAcesso(final GenericCallBack callBack) {
        AcessoAPI acessoAPI = retrofit.create(AcessoAPI.class);
        Call<Liberacao> acessoCall = acessoAPI.consultaTipoAcesso();
        acessoCall.enqueue(new Callback<Liberacao>() {

            @Override
            public void onResponse(@NonNull Call<Liberacao> call, @NonNull Response<Liberacao> response) {
                Liberacao liberacao = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        callBack.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callBack.onFailure(e.getMessage());
                    }
                } else {
                    callBack.onSuccess(liberacao);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Liberacao> call, @NonNull Throwable t) {
                callBack.onFailure(t.getMessage());
            }
        });
    }

    public void solicitaLiberacao(Liberacao liberacao, final SolicitaLiberacaoCallBack callBack) {
        AcessoAPI acessoAPI = retrofit.create(AcessoAPI.class);
        Call<Liberacao> acessoCall = acessoAPI.solicitaLiberacao(liberacao);
        acessoCall.enqueue(new Callback<Liberacao>() {

            @Override
            public void onResponse(@NonNull Call<Liberacao> call, @NonNull Response<Liberacao> response) {
                Liberacao liberacao = response.body();

                if (String.valueOf(response.code()).trim().substring(0,1).equals("4") || response.code() == 500) {
                    try {
                        if (response.code() == 401) {
                            callBack.onUnauthorized(Utils.apiErroMessage(response));
                        } else {
                            callBack.onFailure(Utils.apiErroMessage(response));
                        }
                    } catch (IOException e) {
                        callBack.onFailure(e.getMessage());
                    }
                } else {
                    callBack.onSuccess(liberacao);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Liberacao> call, @NonNull Throwable t) {
                callBack.onFailure(t.getMessage());
            }
        });
    }
}