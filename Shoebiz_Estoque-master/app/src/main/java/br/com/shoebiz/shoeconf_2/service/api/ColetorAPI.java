package br.com.shoebiz.shoeconf_2.service.api;

import java.util.List;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Coletor;
import br.com.shoebiz.shoeconf_2.model.ColetorProduto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ColetorAPI {

    @POST("validaProdutos/")
    Call<APIMensagem> validaProdutos(@Body List<ColetorProduto> coletorProdutos);

    @POST("contagemProdutos/{nomeArquivo}")
    Call<APIMensagem> contagemProdutos(@Body Coletor coletor, @Path("nomeArquivo") String nomeArquivo);
}