package br.com.shoebiz.shoeconf_2.service.api;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Inventario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InventarioAPI {

    @GET("buscaInventarioAberto/")
    Call<Inventario> consultaInventarioAberto();

    @GET("consultaArquivoSb1/")
    Call<Inventario> consultaArquivoSb1();

    @GET("statusAparelho/{codigo}")
    Call<Inventario> consultaStatusAparelho(@Path("codigo") String codigo);

    @POST("gravaArea/")
    Call<APIMensagem> gravarArea(@Body Inventario inventario);
}