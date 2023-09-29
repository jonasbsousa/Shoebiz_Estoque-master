package br.com.shoebiz.shoeconf_2.service.api;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Produto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProdutoAPI {

    @GET("consulta/{codigo}")
    Call<Produto> consultaProduto(@Path("codigo") String codigo);

    @GET("v2/consulta/{codigo}")
    Call<Produto> consultaProdutoV2(@Path("codigo") String codigo);

    @POST("gravaGtin/")
    Call<APIMensagem> gravaGtin();
}
