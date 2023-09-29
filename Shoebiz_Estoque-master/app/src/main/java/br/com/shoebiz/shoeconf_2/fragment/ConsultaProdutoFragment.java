package br.com.shoebiz.shoeconf_2.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.activity.CustomScannerActivity;
import br.com.shoebiz.shoeconf_2.adapter.ProdutoCorAdapter;
import br.com.shoebiz.shoeconf_2.fragment.dialog.AboutDialog;
import br.com.shoebiz.shoeconf_2.model.Cor;
import br.com.shoebiz.shoeconf_2.model.HistoricoPreco;
import br.com.shoebiz.shoeconf_2.model.Produto;
import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;
import br.com.shoebiz.shoeconf_2.service.AlteracaoPrecoService;
import br.com.shoebiz.shoeconf_2.service.ProdutoService;
import br.com.shoebiz.shoeconf_2.utils.BloqAppException;
import br.com.shoebiz.shoeconf_2.utils.MaskUtils;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class ConsultaProdutoFragment extends BaseFragment {
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;
    private final String PRODUTO_KEY = "produto";

    private Produto produto;
    private boolean isAlteracao = false;

    private TableLayout tlHistoricoAlteracoes;
    private SearchView svCodigoProduto;
    private NestedScrollView nscInfoProduto;
    private RecyclerView rvCores;
    private ImageView ivFotoProduto;
    private TextView tvCodigoPai;
    private TextView tvSemProduto;
    private TextView tvDescricao;
    private TextView tvGrupo;
    private TextView tvMarca;
    private TextView tvCodResumido;
    private TextView tvTamanhos;
    private TextView tvQntPares;
    private TextView tvProdutoSemEstoque;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_consulta_produto, container, false);

        requireActivity().setTitle(R.string.nav_consulta_produto);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            produto = getArguments().getParcelable(PRODUTO_KEY);

            if (produto != null) {
                isAlteracao = true;
            }
        }

        LinearLayout llPesquisaCodigo = view.findViewById(R.id.llPesquisaCodigo);
        llPesquisaCodigo.setVisibility(isAlteracao ? View.GONE : View.VISIBLE);

        CardView cvHistoricoAlt = view.findViewById(R.id.cvHistoricoAlt);
        cvHistoricoAlt.setVisibility(isAlteracao ? View.VISIBLE : View.GONE);

        tlHistoricoAlteracoes = view.findViewById(R.id.tlHistoricoAlteracoes);

        tvCodigoPai = view.findViewById(R.id.tvCodigoPai);
        tvSemProduto = view.findViewById(R.id.tvSemProduto);
        tvDescricao = view.findViewById(R.id.tvDescricao);
        tvGrupo = view.findViewById(R.id.tvGrupo);
        tvMarca = view.findViewById(R.id.tvMarca);
        tvCodResumido = view.findViewById(R.id.tvCodResumido);
        tvTamanhos = view.findViewById(R.id.tvTamanhos);
        tvQntPares = view.findViewById(R.id.tvQntPares);
        tvProdutoSemEstoque = view.findViewById(R.id.tvProdutoSemEstoque);

        ivFotoProduto = view.findViewById(R.id.ivFotoProduto);

        nscInfoProduto = view.findViewById(R.id.nscInfoProduto);

        svCodigoProduto = view.findViewById(R.id.svCodigoProduto);
        svCodigoProduto.requestFocusFromTouch();
        svCodigoProduto.setFocusable(!isAlteracao);
        svCodigoProduto.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                String codigoProduto = query.toUpperCase().trim();

                if ((codigoProduto.length() == 4) || (codigoProduto.length() == 9) || (codigoProduto.length() == 12) ||
                        (codigoProduto.length() == 13) || (codigoProduto.length() == 14)) {

                    Utils.showProgress(getContext(),"Atenção!", "Consultando Produto");

                    new ProdutoService(getContext(), "", "").consultaProdutoV2(codigoProduto, new ProdutoService.ConsultaProdutoCallback() {

                        @Override
                        public void onSuccess(Produto produto) {
                            Utils.closeProgress();

                            showInfoProduto(produto);
                        }

                        @Override
                        public void onFailure(String erro) {
                            Utils.closeProgress();
                            Utils.toast(getContext(), erro);
                        }
                    });
                } else {
                    Utils.toast(getContext(), "Código incompleto!");

                    return true;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        ImageButton ibPesquisaCamera = view.findViewById(R.id.ibPesquisaCamera);
        ibPesquisaCamera.setOnClickListener(v ->
                IntentIntegrator
                        .forSupportFragment(ConsultaProdutoFragment.this)
                        .setDesiredBarcodeFormats(IntentIntegrator.CODE_128)
                        .setOrientationLocked(false)
                        .setCaptureActivity(CustomScannerActivity.class)
                        .setBeepEnabled(false)
                        .initiateScan()
        );

        rvCores = view.findViewById(R.id.rvCores);
        rvCores.setHasFixedSize(true);
        rvCores.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCores.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (this.produto != null) {
            showInfoProduto(this.produto);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            AboutDialog.showAbout(getChildFragmentManager());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(PRODUTO_KEY, this.produto);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            this.produto = savedInstanceState.getParcelable(PRODUTO_KEY);

            if (produto != null) {
                showInfoProduto(produto);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != CUSTOMIZED_REQUEST_CODE && requestCode != IntentIntegrator.REQUEST_CODE) {
            return;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);

        if(result.getContents() == null) {
            Intent originalIntent = result.getOriginalIntent();

            if (originalIntent == null) {
                Utils.toast(getContext(), "Problema ao consultar produto!");
            } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Utils.toast(getContext(), "Cancelled due to missing camera permission");
            }
        } else {
            svCodigoProduto.setQuery(result.getContents(), true);
        }
    }

    private void showInfoProduto(Produto produto) {
        Activity activity = getActivity();

        if(activity != null && isAdded()) {
            if (produto != null) {
                this.produto = produto;

                tvCodigoPai.setText(produto.codigoPai);
                tvDescricao.setText(produto.descricao);
                tvGrupo.setText(getString(R.string.frag_cp_descGrupo, produto.grupo.codigo, produto.grupo.descricao));
                tvMarca.setText(produto.marca);
                tvCodResumido.setText(produto.codigoResumido);
                tvTamanhos.setText(getString(R.string.frag_cp_descTamanho, produto.tamanhoMenor, produto.tamanhoMaior));

                Glide.with(requireContext())
                        .load(Utils.getUrlFotoProduto() + produto.codigoPai.substring(1) + ".JPG")
                        .apply(new RequestOptions()
                                .error(R.drawable.sem_foto)
                                .placeholder(R.drawable.sem_foto)
                                .fitCenter()
                                .override(800, 400))
                        .into(ivFotoProduto);

                tvSemProduto.setVisibility(View.GONE);
                nscInfoProduto.setVisibility(View.VISIBLE);

                if (isAlteracao) {
                    new AlteracaoPrecoService(getContext()).consultaHistoricoPrecoAPI(produto.codigoPai,
                            this::atualizaHistorico);
                }

                Bundle bundle = new Bundle();
                bundle.putString("Loja", new PrefsUtils(getContext()).isCodLoja());
                bundle.putString("Consulta", "Ok");
                mFirebaseAnalytics.logEvent("ConsultaProduto", bundle);

                new ConsultaProdutoLojaTask().execute(produto.codigoPai);
            } else {
                this.produto = null;

                nscInfoProduto.setVisibility(View.GONE);
                tvSemProduto.setVisibility(View.VISIBLE);
            }
        }
    }

    private void montaCardGrade(List<ProdutoFilho> produtosFilho){
        int quantPares = 0;

        if (this.produto != null) {
            if (produtosFilho != null && produtosFilho.size() > 0) {
                for (ProdutoFilho produtoFilhoAux : produtosFilho) {
                    for (Cor cor : this.produto.cores) {
                        if (cor.codigo.equals(produtoFilhoAux.codigo.trim().substring(9,11))) {
                            for (ProdutoFilho produtoFilho : cor.produtoFilho) {
                                if (produtoFilho.codigo.equals(produtoFilhoAux.codigo.trim().substring(11,13))) {

                                    produtoFilho.quantidade += produtoFilhoAux.quantidade;
                                }
                            }
                        }
                    }
                }
            }

            if (this.produto.cores != null && this.produto.cores.size() > 0) {
                for (Cor cor : this.produto.cores) {
                    for (ProdutoFilho produtoFilho : cor.produtoFilho) {
                        quantPares += produtoFilho.quantidade;
                    }
                }
            }
        }

        tvQntPares.setText(String.valueOf(quantPares));

        if (quantPares > 0) {
            for (Cor cor : this.produto.cores) {
                for (Iterator<ProdutoFilho> iterator = cor.produtoFilho.iterator(); iterator.hasNext(); ) {
                    ProdutoFilho produtoFilho = iterator.next();

                    if (produtoFilho.quantidade == 0) {
                        iterator.remove();
                    }
                }
            }

            for (Iterator<Cor> iterator = produto.cores.iterator(); iterator.hasNext(); ) {
                Cor cor = iterator.next();
                int quant = 0;

                for (ProdutoFilho produtoFilho : cor.produtoFilho) {
                    quant += produtoFilho.quantidade;
                }

                if (quant == 0) {
                    iterator.remove();
                }
            }

            rvCores.setAdapter(new ProdutoCorAdapter(getContext(), produto.cores, null));

            rvCores.setVisibility(View.VISIBLE);
            tvProdutoSemEstoque.setVisibility(View.GONE);
        } else {
            rvCores.setVisibility(View.GONE);
            tvProdutoSemEstoque.setVisibility(View.VISIBLE);
        }

        svCodigoProduto.clearFocus();
    }

    @SuppressLint({"SimpleDateFormat", "RtlHardcoded"})
    private void atualizaHistorico(List<HistoricoPreco> alteracoesPreco) {
        int index = 1;
        float textSize = getResources().getDimension(R.dimen.font_size_table);

        TableRow.LayoutParams cabecTableRowParams = new TableRow.LayoutParams();
        cabecTableRowParams.weight = 1;

        TableRow.LayoutParams tableRowParams_1 = new TableRow.LayoutParams();
        tableRowParams_1.weight = 1;

        TableRow.LayoutParams tableRowParams_2 = new TableRow.LayoutParams();
        tableRowParams_2.weight = 1;

        TableRow cabecTableRow = new TableRow(getContext());
        cabecTableRow.setGravity(Gravity.CENTER);

        TextView cabecRevisao = new TextView(getContext());
        TextView cabecData = new TextView(getContext());
        TextView cabecPrecoDe = new TextView(getContext());
        TextView cabecPrecoPor = new TextView(getContext());

        cabecRevisao.setText(getString(R.string.table_revisao));
        cabecRevisao.setTypeface(null, Typeface.BOLD);
        cabecRevisao.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        cabecData.setText(getString(R.string.table_date));
        cabecData.setTypeface(null, Typeface.BOLD);
        cabecData.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        cabecPrecoDe.setText(getString(R.string.table_preco_de));
        cabecPrecoDe.setTypeface(null, Typeface.BOLD);
        cabecPrecoDe.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        cabecPrecoDe.setGravity(Gravity.RIGHT);

        cabecPrecoPor.setText(getString(R.string.table_preco_ate));
        cabecPrecoPor.setTypeface(null, Typeface.BOLD);
        cabecPrecoPor.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        cabecPrecoPor.setGravity(Gravity.RIGHT);

        cabecTableRow.addView(cabecRevisao, cabecTableRowParams);
        cabecTableRow.addView(cabecData, cabecTableRowParams);
        cabecTableRow.addView(cabecPrecoDe, cabecTableRowParams);
        cabecTableRow.addView(cabecPrecoPor, cabecTableRowParams);
        tlHistoricoAlteracoes.addView(cabecTableRow, 0);

        for (HistoricoPreco historicoPreco : alteracoesPreco) {
            TableRow tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            tableRow.setGravity(Gravity.CENTER);

            TextView revisao = new TextView(getContext());
            TextView data = new TextView(getContext());
            TextView precoDe = new TextView(getContext());
            TextView precoPor = new TextView(getContext());

            revisao.setText(historicoPreco.revisao);
            revisao.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            data.setText(new SimpleDateFormat(Utils.formatoData()).format(historicoPreco.dataAlteracao));
            data.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            precoDe.setText(MaskUtils.addMaskMoney(historicoPreco.precoAnterior));
            precoDe.setGravity(Gravity.END);
            precoDe.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            precoPor.setText(MaskUtils.addMaskMoney(historicoPreco.precoAtual));
            precoPor.setGravity(Gravity.END);
            precoPor.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            tableRow.addView(revisao, tableRowParams_1);
            tableRow.addView(data, tableRowParams_1);
            tableRow.addView(precoDe, tableRowParams_2);
            tableRow.addView(precoPor, tableRowParams_2);
            tlHistoricoAlteracoes.addView(tableRow, index);

            index++;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ConsultaProdutoLojaTask extends AsyncTask<String, Void, List<ProdutoFilho>> {
        private boolean appOk = true;
        private String erro;

        @Override
        protected void onPreExecute() {
            Utils.showProgress(getContext(), "Aguarde...", "Atualizando saldo!");
        }

        @Override
        protected List<ProdutoFilho> doInBackground(String... strings) {
            try {
                Thread.sleep(500);

                return ProdutoService.getFromWsLoja(getContext(), strings[0]);
            } catch (SocketTimeoutException | UnknownHostException | ConnectException e) {
                erro = "Falha ao acessar o servidor da Loja!";
            } catch (BloqAppException e) {
                appOk = false;
                erro = e.getMessage();
            } catch (Exception e) {
                erro = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<ProdutoFilho> produtosFilho) {
            Utils.closeProgress();

            if (erro != null) {
                Utils.toast(getContext(), erro);

                if (!appOk){
                    bloqueiaApp();
                }
            }

            montaCardGrade(produtosFilho);
        }
    }
}