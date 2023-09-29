package br.com.shoebiz.shoeconf_2.service;

import android.content.Context;

import androidx.annotation.NonNull;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Produto;
import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;
import br.com.shoebiz.shoeconf_2.service.api.ProdutoAPI;
import br.com.shoebiz.shoeconf_2.utils.BloqAppException;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.SoapEnvelope;
import br.com.shoebiz.shoeconf_2.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProdutoService {
    private static final String WSURLPROTHEUSLOJ = Utils.URLWSSRVLOJ;
    private static final String WSACTIONPROTHEUSLOJ = Utils.DIRWSSRVLOJ + "/SINCMOBILEPROTHEUS.apw";

    private final Retrofit retrofit;

    public interface EnviaCallBack {
        void onSuccess(String mensagem);
        void onFailure(String erro);
    }

    public interface ConsultaProdutoCallback {
        void onSuccess(Produto produto);
        void onFailure(String erro);
    }

    public ProdutoService(final Context context, final String codProduto, final String codGtin) {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("t", new PrefsUtils(context).isLibToken())
                    .addHeader("filial", new PrefsUtils(context).isCodLoja())
                    .addHeader("codProduto", codProduto)
                    .addHeader("codGtin", codGtin)
                    .build();
            return chain.proceed(request);
        }).readTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Utils.getUrlRestEsc_3() + "produto/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void consultaProduto(String codigoProduto,  final ConsultaProdutoCallback callback){
        ProdutoAPI produtoAPI = retrofit.create(ProdutoAPI.class);
        Call<Produto> produtoCall = produtoAPI.consultaProduto(codigoProduto);
        produtoCall.enqueue(new Callback<Produto>() {

            @Override
            public void onResponse(@NonNull Call<Produto> call, @NonNull Response<Produto> response) {
                Produto produto = response.body();

                if (String.valueOf(response.code()).trim().startsWith("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                }

                callback.onSuccess(produto);
            }

            @Override
            public void onFailure(@NonNull Call<Produto> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void consultaProdutoV2(String codigoProduto,  final ConsultaProdutoCallback callback){
        ProdutoAPI produtoAPI = retrofit.create(ProdutoAPI.class);
        Call<Produto> produtoCall = produtoAPI.consultaProdutoV2(codigoProduto);
        produtoCall.enqueue(new Callback<Produto>() {

            @Override
            public void onResponse(@NonNull Call<Produto> call, @NonNull Response<Produto> response) {
                Produto produto = response.body();

                if (String.valueOf(response.code()).trim().startsWith("4") || response.code() == 500) {
                    try {
                        callback.onFailure(Utils.apiErroMessage(response));
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                }

                callback.onSuccess(produto);
            }

            @Override
            public void onFailure(@NonNull Call<Produto> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void gravaCodigoGtin(final EnviaCallBack callBack) {
        ProdutoAPI produtoAPI = retrofit.create(ProdutoAPI.class);
        Call<APIMensagem> gravaOtCall = produtoAPI.gravaGtin();
        gravaOtCall.enqueue(new Callback<APIMensagem>() {

            @Override
            public void onResponse(@NonNull Call<APIMensagem> call, @NonNull Response<APIMensagem> response) {
                if (String.valueOf(response.code()).trim().startsWith("4") || response.code() == 500) {
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

    public static List<ProdutoFilho> getFromWsLoja(Context context, String codigo) throws Exception {
        List<ProdutoFilho> produtosFilho = new ArrayList<>();

        String wsMethodProtheus = "ESTOQUEPRODUTO";

        SoapEnvelope soapEnvelope = new SoapEnvelope(SoapEnvelope.VER11);
        SoapObject soapReq = new SoapObject(WSURLPROTHEUSLOJ, wsMethodProtheus);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        soapEnvelope.setAddAdornments(false);
        soapReq.addProperty("TOKEN", new PrefsUtils(context).isLibToken());
        soapReq.addProperty("CODIGOPRODUTO", codigo);
        soapReq.addProperty("DATACONSULTA", Utils.getDataAtual());
        soapEnvelope.setOutputSoapObject(soapReq);

        HttpTransportSE httpTransport = new HttpTransportSE(WSURLPROTHEUSLOJ + WSACTIONPROTHEUSLOJ, Utils.TIME_OUT_WS);
        httpTransport.call(WSURLPROTHEUSLOJ + "/" + wsMethodProtheus, soapEnvelope);

        Object retObject = soapEnvelope.bodyIn;

        if (retObject instanceof SoapFault) {
            SoapFault soapFault = (SoapFault) retObject;
            throw new Exception(soapFault.faultstring);
        } else {
            SoapObject soapObject = (SoapObject) soapEnvelope.getResponse();

            if (soapObject.getPropertyCount() > 0) {
                if (soapObject.getProperty(1).toString().equals("ok")) {
                    SoapObject objectProdutos = (SoapObject) soapObject.getProperty(2);

                    for (int n = 0; n < objectProdutos.getPropertyCount(); n++) {
                        SoapObject objectProduto = (SoapObject) objectProdutos.getProperty(n);

                        ProdutoFilho produtoFilho = new ProdutoFilho();
                        produtoFilho.codigo = objectProduto.getProperty(0).toString();
                        produtoFilho.quantidade = Integer.parseInt(objectProduto.getProperty(1).toString());

                        produtosFilho.add(produtoFilho);
                    }
                } else if (soapObject.getProperty(1).toString().equals("sok")) {
                    return null;
                } else if (soapObject.getProperty(1).toString().equals("bok")) {
                    throw new BloqAppException(soapObject.getProperty(0).toString());
                } else if (soapObject.getProperty(1).toString().equals("nok")) {
                    throw new Exception(soapObject.getProperty(0).toString());
                }
            }
        }

        return produtosFilho;
    }
}