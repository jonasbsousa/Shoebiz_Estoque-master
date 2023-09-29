package br.com.shoebiz.shoeconf_2.service.api;

import br.com.shoebiz.shoeconf_2.model.Liberacao;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AcessoAPI {

    @GET("isAlive/")
    Call<Liberacao> verificaLocal();

    @GET("consultaAcesso/")
    Call<Liberacao> consultaAcesso();

    @GET("consultaLiberacao/")
    Call<Liberacao> consultaLiberacao();

    @GET("consultaTipoAcesso/")
    Call<Liberacao> consultaTipoAcesso();

    @POST("solicitaLiberacao/")
    Call<Liberacao> solicitaLiberacao(@Body Liberacao liberacao);
}