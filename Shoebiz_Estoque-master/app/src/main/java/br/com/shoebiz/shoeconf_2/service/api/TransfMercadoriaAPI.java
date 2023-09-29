package br.com.shoebiz.shoeconf_2.service.api;

import java.util.List;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.TransfNota;
import br.com.shoebiz.shoeconf_2.model.TransfProduto;
import br.com.shoebiz.shoeconf_2.model.TransfQuant;
import br.com.shoebiz.shoeconf_2.model.TransfStatus;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TransfMercadoriaAPI {

    @GET("v2/qntProdColet/")
    Call<TransfQuant> qntProdColet();

    @GET("autoEntrada/")
    Call<APIMensagem> autoEntrada();

    @GET("consultaStatus/")
    Call<TransfStatus> consultaStatus();

    @GET("consultaOts/{status}")
    Call<List<TransfNota>> consultaOts(@Path("status") String status);

    @GET("v2/consultaOt/{codigo}")
    Call<TransfNota> consultaOt(@Path("codigo") String codigo);

    @GET("consultaNotas/{status}")
    Call<List<TransfNota>> consultaNotas(@Path("status") String status);

    @GET("consultaNota/{chave}")
    Call<TransfNota> consultaNota(@Path("chave") String chave);

    @POST("recebeNota/")
    Call<APIMensagem> recebeNota(@Body TransfNota transfNota);

    @POST("recebeOt/{codigo}")
    Call<APIMensagem> recebeOt(@Body List<TransfProduto> transfProduto, @Path("codigo") String codigo);
}