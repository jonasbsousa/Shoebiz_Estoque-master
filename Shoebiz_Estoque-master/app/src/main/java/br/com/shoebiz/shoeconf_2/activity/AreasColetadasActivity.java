package br.com.shoebiz.shoeconf_2.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.adapter.AreasColetadasAdapter;
import br.com.shoebiz.shoeconf_2.dao.InventarioDao;
import br.com.shoebiz.shoeconf_2.dao.InventarioProdutoDao;
import br.com.shoebiz.shoeconf_2.model.Inventario;
import br.com.shoebiz.shoeconf_2.service.InventarioService;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class AreasColetadasActivity extends BaseActivity {

    private List<Inventario> inventarios = new ArrayList<>();

    private RecyclerView rvAreasColetadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alteraTema();
        alteraStatusBarColor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areas_coletadas);
        setUpToolBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_areas_coletadas);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvAreasColetadas = findViewById(R.id.rvAreasColetadas);
        rvAreasColetadas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAreasColetadas.setItemAnimator(new DefaultItemAnimator());
        rvAreasColetadas.setHasFixedSize(true);

        buscaAreas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_areas_coletadas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sincronizar) {
            if (new PrefsUtils(getContext()).isModoOnline().equals("S")) {
                sincronizaAreas();
            } else {
                Utils.toast(getContext(), "Modo OnLine inativo, favor ativar!");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void sincronizaAreas() {
        List<Inventario> inventariosSinc = new ArrayList<>();

        for (Inventario inventario : this.inventarios) {
            if (inventario.integrado.equals("N")) {
                inventariosSinc.add(inventario);
            }
        }

        if (inventariosSinc.size() > 0) {
            new SincAreasTask(inventariosSinc).execute();
        } else {
            Utils.toast(getContext(), "Todas as áreas estão sincronizadas!");
        }
    }

    private void buscaAreas() {
        new BuscaAreasTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class BuscaAreasTask extends AsyncTask<Void, Void, List<Inventario>> {

        @Override
        protected void onPreExecute() {
            Utils.showProgress(getContext(), "Atenção...", "Buscando área(s) coletads(s)");
        }

        @Override
        protected List<Inventario> doInBackground(Void... voids) {
            return new InventarioDao(getContext()).buscaTodos();
        }

        @Override
        protected void onPostExecute(List<Inventario> inventarios) {
            Utils.closeProgress();

            if (inventarios != null && inventarios.size() > 0) {
                AreasColetadasActivity.this.inventarios = inventarios;

                AreasColetadasAdapter adapter = new AreasColetadasAdapter(getContext(), inventarios);
                rvAreasColetadas.setAdapter(adapter);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SincAreasTask extends AsyncTask<Void, Void, Void> {
        private List<Inventario> inventarios;
        private StringBuilder areas_erro = new StringBuilder();
        private String erro = null;

        SincAreasTask(List<Inventario> inventarios) {
            this.inventarios = inventarios;
        }

        @Override
        protected void onPreExecute() {
            Utils.showProgressBar(getContext(), "Atenção..", "Sincronizando com o Protheus!", inventarios.size());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int count = 1;

            PrefsUtils prefsUtils = new PrefsUtils(getContext());

            for (final Inventario inventario : this.inventarios) {
                try {
                    inventario.codigo = prefsUtils.isInventCod();
                    inventario.operadorCodigo = prefsUtils.isInventOperCod();
                    inventario.operadorNome = prefsUtils.isInventOperNome();
                    inventario.produtosFilho = new InventarioProdutoDao(getContext()).buscaPorCodigo(inventario.area);
                    inventario.aparelhoCodigo = prefsUtils.isLibCodigo();

                    new InventarioService(getContext()).gravarAreaAPI(inventario, inventario1 -> {
                        if (inventario1.integrado.equals("S")) {
                            new InventarioDao(getContext()).salvar(inventario1);
                        } else {
                            areas_erro.append(inventario1.area).append(", ");
                        }
                    });

                    Thread.sleep(1000);
                } catch (Exception e) {
                    areas_erro.append(inventario.area).append(", ");
                    erro = e.getMessage();
                }

                Utils.updateProgressBar(count);
                count++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utils.closeProgress();

            buscaAreas();

            if (erro != null) {
                Utils.toast(getContext(), "Problema ao sincronizar a(s) área(s): " + areas_erro + ". Erro: " + erro);
            } else {
                Utils.toast(getContext(), "Área(s) sincronizada(s)!");
            }
        }
    }
}