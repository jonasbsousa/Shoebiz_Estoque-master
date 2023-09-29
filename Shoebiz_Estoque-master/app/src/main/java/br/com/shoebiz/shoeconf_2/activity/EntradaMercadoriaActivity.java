package br.com.shoebiz.shoeconf_2.activity;

import static br.com.shoebiz.shoeconf_2.utils.Utils.toast;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.ShoebizApplication;
import br.com.shoebiz.shoeconf_2.adapter.ProdutoEntradaAdapter;
import br.com.shoebiz.shoeconf_2.fragment.dialog.QuantidadeDialog;
import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Cor;
import br.com.shoebiz.shoeconf_2.model.Nota;
import br.com.shoebiz.shoeconf_2.model.NotaPedido;
import br.com.shoebiz.shoeconf_2.model.ProdutoEntrada;
import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;
import br.com.shoebiz.shoeconf_2.service.EntradaMercadoriaService;
import br.com.shoebiz.shoeconf_2.utils.FTPUtils;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class EntradaMercadoriaActivity extends BaseActivity {

    private final String FORMA_COLETA = "formaColeta";
    private final String NOTA = "nota";
    private final String PRODUTO_ENTRADA = "produtoEntrada";
    private final String QUANTIDADE_PRODUTOS = "quantidadeProduto";

    private Nota nota;
    private List<ProdutoEntrada> produtosEntrada = new ArrayList<>();
    private String formaColeta;

    private RecyclerView.Adapter adapterPtrodutosEntrada;
    private RecyclerView recyclerView;

    private EditText etCodigoProduto;
    private TextView tvSemProduto;
    private TextView tvQntProdutos;

    private int quantidadeProdutos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alteraTema();
        alteraStatusBarColor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada_mercadoria);
        setUpToolBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_incluir_danfe);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        nota = getIntent().getParcelableExtra("nota");

        if (nota != null) {
            recyclerView = findViewById(R.id.rvNota);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            updateListProd();

            tvSemProduto = findViewById(R.id.tvSemProduto);

            TextView tvNumNota = findViewById(R.id.tvNumNota);
            TextView tvFornecedor = findViewById(R.id.tvFornecedor);

            tvQntProdutos = findViewById(R.id.tvQntProdutos);
            tvQntProdutos.setText(String.valueOf(quantidadeProdutos));

            if (nota != null) {
                tvNumNota.setText(getString(R.string.frag_vd_desc, nota.notaNumero, nota.notaSerie));
                tvFornecedor.setText(nota.fornecedorDesc);
            }

            etCodigoProduto = findViewById(R.id.etCodigoProduto);
            etCodigoProduto.requestFocus();
            etCodigoProduto.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String codDigitado = etCodigoProduto.getText().toString().trim();

                    if (codDigitado.length() == 8 || codDigitado.length() == 12 || codDigitado.length() == 13 || codDigitado.length() == 14) {
                        try {
                            etCodigoProduto.setText("");
                            incluiProdutoList(codDigitado.toUpperCase().trim());
                        } catch (Exception e) {
                            toast(getContext(), e.getMessage());
                        }
                    } else {
                        etCodigoProduto.setText("");
                    }
                }

                return false;
            });

            ImageButton ibLimpaLista = findViewById(R.id.ibLimpaLista);
            ibLimpaLista.setOnClickListener(v -> {
                if (produtosEntrada.size() > 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Atenção!!!")
                            .setMessage("Deseja excluir a lista de produtos?")
                            .setNegativeButton("Não", null)
                            .setPositiveButton("Sim", (dialog, which) -> limpaLista()).show();
                } else {
                    Utils.toast(getContext(), "Sem produtos para excluir!");
                }
            });

            if (savedInstanceState != null) {
                formaColeta = savedInstanceState.getString(FORMA_COLETA, getString(R.string.forma_coleta_cod_shoebiz));
                quantidadeProdutos = savedInstanceState.getInt(QUANTIDADE_PRODUTOS, 0);
                nota = savedInstanceState.getParcelable(NOTA);
                produtosEntrada = savedInstanceState.getParcelableArrayList(PRODUTO_ENTRADA);

                tvQntProdutos.setText(String.valueOf(quantidadeProdutos));

                if (produtosEntrada.size() > 0) {
                    tvSemProduto.setVisibility(View.GONE);
                }

                updateListProd();
            } else {
                consultaFormaColeta();
            }
        } else {
            Utils.toast(getContext(), "Problema ao abrir a nota! Tente novamente.");
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(FORMA_COLETA, formaColeta);
        outState.putParcelable(NOTA, nota);
        outState.putParcelableArrayList(PRODUTO_ENTRADA, (ArrayList<? extends Parcelable>) produtosEntrada);
        outState.putInt(QUANTIDADE_PRODUTOS, quantidadeProdutos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nota, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancelarNota) {
            ShoebizApplication.getInstance().getBus().post("refreshEntradaMercadoria");
            finish();
            return true;
        } else if (item.getItemId() == R.id.confirmaNota) {
            if (produtosEntrada.size() > 0) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Atenção!!!")
                        .setMessage("Confirma entrada dessa nota?")
                        .setNegativeButton("Não", null)
                        .setPositiveButton("Sim", (dialog, which) -> validaProdutosNotas()).show();
            } else {
                Utils.toast(getContext(), "Favor incluir os produtos!");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void consultaFormaColeta() {
        Utils.showProgress(getContext(), "Aguarde...", "Consultado forma de coleta!");

        new EntradaMercadoriaService(getContext(), "").consultaFormaEntrada(new EntradaMercadoriaService.ConsultaFormaEntradaCallback() {

            @Override
            public void onSuccess(APIMensagem apiMensagem) {
                Utils.closeProgress();

                incluiFormaColeta(apiMensagem);
            }

            @Override
            public void onFailure(String erro) {
                Utils.closeProgress();
                Utils.toast(getContext(), erro);

                finish();
            }
        });
    }

    private void incluiFormaColeta(APIMensagem apiMensagem) {
        if (apiMensagem != null && Utils.getFormaColeta(getContext(), apiMensagem.mensagem)) {
            this.formaColeta = apiMensagem.mensagem;
        } else {
            Utils.toast(getContext(), "Problema ao consultar a forma de coleta! Tente novamente.");
            finish();
        }
    }

    private void incluiProdutoList(String codigoProduto) {
        ProdutoEntrada produtoEntrada = null;
        boolean inclui = true;

        quantidadeProdutos++;
        tvQntProdutos.setText(String.valueOf(quantidadeProdutos));

        for (ProdutoEntrada produtoEntradaAux : this.produtosEntrada) {
            if (produtoEntradaAux.codigo.toUpperCase().trim().equals(codigoProduto)) {
                produtoEntradaAux.quantidade++;

                inclui = false;
            }
        }

        if (inclui) {
            produtoEntrada = new ProdutoEntrada();
            produtoEntrada.codigo = codigoProduto;
            produtoEntrada.quantidade = 1;
            produtoEntrada.descricao = "";
            produtoEntrada.tamanho = "";
            produtoEntrada.status = "V";

            this.produtosEntrada.add(produtoEntrada);
        }

        if (produtosEntrada.size() > 0) {
            tvSemProduto.setVisibility(View.GONE);
        }

        updateListProd();

        if (produtoEntrada != null) {
            validaProduto(produtoEntrada);
        }
    }

    private void limpaLista() {
        produtosEntrada.clear();

        adapterPtrodutosEntrada = new ProdutoEntradaAdapter(getContext(), produtosEntrada, onClickProdutosEntrada());
        recyclerView.setAdapter(adapterPtrodutosEntrada);

        quantidadeProdutos = 0;
        tvQntProdutos.setText(String.valueOf(quantidadeProdutos));

        tvSemProduto.setVisibility(View.VISIBLE);
    }

    private void recalculaQntTotal() {
        quantidadeProdutos = 0;

        for (ProdutoEntrada produtoEntrada : this.produtosEntrada) {
            quantidadeProdutos += produtoEntrada.quantidade;
        }

        tvQntProdutos.setText(String.valueOf(quantidadeProdutos));

        updateListProd();
    }

    private void validaProdutosNotas() {
        StringBuilder produtoPaiErro = new StringBuilder();
        StringBuilder corErro = new StringBuilder();

        boolean produtoPaiOk = false;
        boolean corOk = false;
        boolean produtosOk = true;

        String codigoPai = "";
        String cor = "";

        for (ProdutoEntrada produtoEntrada : produtosEntrada) {
            if (formaColeta.equals(getString(R.string.forma_coleta_cod_shoebiz)) || formaColeta.equals(getString(R.string.forma_coleta_hibrido))) {
                switch (produtoEntrada.codigo.length()) {
                    case 12:
                        codigoPai = "0" + produtoEntrada.codigo.substring(0,8);
                        cor = produtoEntrada.codigo.substring(8,10);

                        break;
                    case 13:
                        codigoPai = produtoEntrada.codigo.substring(0,9);
                        cor = produtoEntrada.codigo.substring(9,11);

                        break;
                    case 14:
                        codigoPai = produtoEntrada.codigo.substring(1,10);
                        cor = produtoEntrada.codigo.substring(10,12);

                        break;
                }

                for (NotaPedido danfePedido : nota.notaPedidos) {
                    if (danfePedido.codigoPai.equals(codigoPai)) {
                        produtoPaiOk = true;

                        for (Cor corAux : danfePedido.cores) {
                            if (corAux.codigo.equals(cor)) {
                                corOk = true;
                                break;
                            }
                        }

                        if (corOk) {
                            break;
                        }
                    }
                }
            }

            if (!produtoPaiOk) {
                if (formaColeta.equals(getString(R.string.forma_coleta_gtin)) || formaColeta.equals(getString(R.string.forma_coleta_hibrido))) {
                    for (NotaPedido pedidoAux : nota.notaPedidos) {
                        for (Cor corAux : pedidoAux.cores) {
                            for (ProdutoFilho filhoAux : corAux.produtoFilho) {
                                if (filhoAux.codigoGtin.equals(produtoEntrada.codigo)) {
                                    produtoPaiOk = true;

                                    break;
                                }
                            }
                        }
                    }
                }

                if (!produtoPaiOk) {
                    produtoPaiErro.append(produtoEntrada.codigo).append(", ");
                }
            } else {
                if (!corOk) {
                    corErro.append(produtoEntrada.codigo).append(", ");
                } else {
                    corOk = false;
                }
            }

            produtoPaiOk = false;
        }

        if (!produtoPaiErro.toString().isEmpty()) {
            produtosOk = false;

            new AlertDialog.Builder(this)
                    .setTitle("Atenção!")
                    .setMessage("Código não localizado no(s) Pedido(s)! Código(s): " + produtoPaiErro.substring(0, produtoPaiErro.length() - 2))
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss()).show();
        }

        if (!corErro.toString().isEmpty()) {
            produtosOk = false;

            new AlertDialog.Builder(this)
                    .setTitle("Atenção!")
                    .setMessage("Cor não localizado em Pedido! Código(s): " + corErro)
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss()).show();
        }

        if (produtosOk) {
            new FTPUtils(getContext()).enviaArquivoEntMercadoria(this.produtosEntrada, this.nota.chave + ".json", "Enviando arquivo FTP!",
                    new FTPUtils.RetornoCallback() {

                @Override
                public void onSuccess() {
                    enviaNota();
                }

                @Override
                public void onFailure(String mensagem) {
                    Utils.toast(getContext(), mensagem);
                }
            });
        }
    }

    private void enviaNota() {
        Utils.showProgress(getContext(), "Aguarde...", "Enviando nota!");

        new EntradaMercadoriaService(getContext(), "").enviaNota(nota.chave, new EntradaMercadoriaService.EnviaCallback() {

            @Override
            @SuppressLint("InvalidAnalyticsName")
            public void onSuccess(String mensagem) {
                Utils.closeProgress();
                Utils.toast(getContext(), mensagem);

                Bundle bundle = new Bundle();
                bundle.putString("Loja", new PrefsUtils(getContext()).isCodLoja());
                mFirebaseAnalytics.logEvent("Entrada Mercadoria", bundle);

                ShoebizApplication.getInstance().getBus().post("refreshEntradaMercadoria");
                finish();
            }

            @Override
            public void onFailure(String erro) {
                Utils.closeProgress();
                Utils.toast(getContext(), erro);
            }
        });
    }

    private void validaProduto(final ProdutoEntrada produtoEntrada) {
        new Handler().postDelayed(() -> {
            String status = "N";
            String codigoVld = "";
            String tamanhoVld = "";
            String codPaiAux;
            String codAux;
            String descricao = null;
            String cor = null;
            String tamanho = null;

            if (formaColeta.equals(getString(R.string.forma_coleta_cod_shoebiz)) || formaColeta.equals(getString(R.string.forma_coleta_hibrido))) {
                switch (produtoEntrada.codigo.length()) {
                    case 12:
                        codigoVld = "0" + produtoEntrada.codigo.substring(0,10);
                        tamanhoVld = produtoEntrada.codigo.substring(10,12);

                        break;
                    case 13:
                        codigoVld = produtoEntrada.codigo.substring(0,11);
                        tamanhoVld = produtoEntrada.codigo.substring(11,13);

                        break;
                    case 14:
                        codigoVld = produtoEntrada.codigo.substring(1,12);
                        tamanhoVld = produtoEntrada.codigo.substring(12,14);

                        break;
                }

                MAINFOR: for (NotaPedido danfePedido : nota.notaPedidos) {
                    codPaiAux = danfePedido.codigoPai;
                    descricao = danfePedido.descricao;

                    for (Cor corAux : danfePedido.cores) {
                        codAux = codPaiAux + corAux.codigo;

                        if (codAux.equals(codigoVld)) {
                            cor = corAux.descricaoFornecedor;

                            for (ProdutoFilho produtoFilho : corAux.produtoFilho) {
                                if (produtoFilho.codigo.equals(tamanhoVld)) {
                                    tamanho = produtoFilho.codigo;
                                    status = "S";

                                    break MAINFOR;
                                }
                            }
                        }
                    }
                }
            }

            if (formaColeta.equals(getString(R.string.forma_coleta_gtin)) || (formaColeta.equals(getString(R.string.forma_coleta_hibrido)) && status.equals("N")) ) {
                MAINFOR: for (NotaPedido danfePedido : nota.notaPedidos) {
                    descricao = danfePedido.descricao;

                    for (Cor corAux : danfePedido.cores) {
                        for (ProdutoFilho produtoFilho : corAux.produtoFilho) {
                            cor = corAux.descricaoFornecedor;

                            if (produtoFilho.codigoGtin.equals(produtoEntrada.codigo)) {
                                tamanho = produtoFilho.codigo;
                                status = "S";

                                break MAINFOR;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < produtosEntrada.size(); i++) {
                if (produtosEntrada.get(i).codigo.equals(produtoEntrada.codigo)) {
                    produtosEntrada.get(i).status = status;
                    produtoEntrada.descricao = status.equals("S") ? descricao : "Produto não localizado no pedido!";
                    produtoEntrada.cor = cor;
                    produtoEntrada.tamanho = tamanho;

                    break;
                }
            }

            updateListProd();

        }, 500);
    }

    private void updateListProd() {
        adapterPtrodutosEntrada = new ProdutoEntradaAdapter(getContext(), produtosEntrada, onClickProdutosEntrada());
        recyclerView.setAdapter(adapterPtrodutosEntrada);
    }

    @SuppressLint("NonConstantResourceId")
    private ProdutoEntradaAdapter.ProdutoEntradaOnClickListener onClickProdutosEntrada() {
        return (view, idx) -> {
            final ProdutoEntrada produtoEntrada = produtosEntrada.get(idx);

            if (!produtoEntrada.status.equals("V")) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                Menu menu = popupMenu.getMenu();
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_ent, menu);

                popupMenu.setOnMenuItemClickListener(menuItem -> false);

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_ent_excluir:
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Atenção!!!")
                                    .setMessage("Excluir o produto '"+ produtoEntrada.codigo +"' ?")
                                    .setNegativeButton("Não", null)
                                    .setPositiveButton("Sim", (dialog, which) -> {
                                        produtosEntrada.remove(produtoEntrada);
                                        recalculaQntTotal();
                                    }).show();

                            break;
                        case R.id.menu_ent_quantidade:
                            QuantidadeDialog.show(getSupportFragmentManager(), produtoEntrada.quantidade, quantidade -> {
                                produtoEntrada.quantidade = quantidade;

                                recalculaQntTotal();
                            });

                            break;
                    }

                    return false;
                });

                popupMenu.show();
            }
        };
    }
}