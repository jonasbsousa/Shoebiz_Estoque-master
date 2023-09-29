package br.com.shoebiz.shoeconf_2.service.api;

import java.util.List;

import br.com.shoebiz.shoeconf_2.model.AlteracaoPreco;
import br.com.shoebiz.shoeconf_2.model.HistoricoPreco;
import br.com.shoebiz.shoeconf_2.model.Produto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AlteracaoPrecoAPI {

    @GET("consulta/{dataDe}/{dataAte}")
    Call<List<Produto>> consultaAlteracao(@Path("dataDe") String dataDe, @Path("dataAte") String dataAte);

    @GET("v2/consulta/{dataDe}/{dataAte}")
    Call<AlteracaoPreco> consultaAlteracaoV2(@Path("dataDe") String dataDe, @Path("dataAte") String dataAte);

    @GET("historicoProduto/{codigo}")
    Call<List<HistoricoPreco>> consultaHistorico(@Path("codigo") String codigo);
}