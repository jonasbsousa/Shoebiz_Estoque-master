package br.com.shoebiz.shoeconf_2.service.api;

import java.util.List;

import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Nota;
import br.com.shoebiz.shoeconf_2.model.Ocorrencia;
import br.com.shoebiz.shoeconf_2.model.TipoOcorrencia;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EntradaMercadoriaAPI {

    @GET("listaNotasEntrada/{status}")
    Call<List<Nota>> consultaNotas(@Path("status") String status);

    @GET("informacoesDanfe/{chave}")
    Call<Nota> consultaNota(@Path("chave") String chave);

    @GET("validaDanfe/{chave}")
    Call<APIMensagem> validaDanfe(@Path("chave") String chave);

    @GET("listaTipoOcorrencia/")
    Call<List<TipoOcorrencia>> consultaTipoOcorrencia();

    @GET("formaColetaProdutos/")
    Call<APIMensagem> consultaFormaColeta();

    @POST("gravaOcorrencia/")
    Call<APIMensagem> enviaOcorrencia(@Body Ocorrencia ocorrencia);

    @POST("entradaDanfe/{chave}")
    Call<APIMensagem> enviaNota(@Path("chave") String chave);
}