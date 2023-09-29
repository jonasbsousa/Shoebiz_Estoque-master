package br.com.shoebiz.shoeconf_2.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.ShoebizApplication;
import br.com.shoebiz.shoeconf_2.adapter.OtTransfAdapter;
import br.com.shoebiz.shoeconf_2.adapter.SliderAdapter;
import br.com.shoebiz.shoeconf_2.adapter.TransfOtProdAdapter;
import br.com.shoebiz.shoeconf_2.fragment.dialog.QuantVolumeDialog;
import br.com.shoebiz.shoeconf_2.fragment.dialog.QuantidadeDialog;
import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.OtProduto;
import br.com.shoebiz.shoeconf_2.model.TransfGtin;
import br.com.shoebiz.shoeconf_2.model.TransfNota;
import br.com.shoebiz.shoeconf_2.model.TransfProduto;
import br.com.shoebiz.shoeconf_2.model.TransfQuant;
import br.com.shoebiz.shoeconf_2.model.TransfTipo;
import br.com.shoebiz.shoeconf_2.service.EntradaMercadoriaService;
import br.com.shoebiz.shoeconf_2.service.TransfMercadoriaService;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class ColetaOtActivity extends BaseActivity {

    private final String FORMA_COLETA = "formaColeta";
    private final String TRANSF_NOTA = "transfNota";
    private final String TRANSF_QUANT = "transfQuant";
    private final String PRODUTO_DIV = "produtoDiv";
    private final String PRODUTO_OK = "produtoOK";
    private final String QUANT_DIV = "quantDiv";
    private final String QUANT_PARES = "quantPares";
    private final String QUANT_LINHAS = "quantLinhas";

    private List<OtProduto> produtosDiv = new ArrayList<>();
    private List<OtProduto> produtosOk = new ArrayList<>();
    private TransfNota transfNota;
    private TransfQuant transfQuant;
    private String formaColeta;

    private RecyclerView rvProdutosOt;
    private RecyclerView rvProdDiv;
    private RecyclerView rvProdOk;
    private RecyclerView.Adapter adapterProdDiv;
    private RecyclerView.Adapter adapterProdOk;

    private RelativeLayout rlPasso1;
    private RelativeLayout rlPasso2;
    private RelativeLayout rlPasso3;

    private Button bVoltar;
    private Button bProximo;

    private LinearLayout llPontos;

    private ViewPager vpPassos;

    private EditText etCodProduto;

    private TextView tvSemProdDiv;
    private TextView tvSemProdOk;
    private TextView tvNumeroOt;
    private TextView tvLojaOrigem;
    private TextView tvLojaDestino;
    private TextView tvEmissao;
    private TextView tvSemProdutos;
    private TextView tvQntDiv;
    private TextView tvQntPares;
    private TextView tvQntLinha;

    private NestedScrollView nsvProdutos;

    private TextView[] pontos;
    private int paginaAtual;
    private int passo;
    private int quantDivergencias;
    private int quantPares;
    private int quantLinhas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alteraTema();
        alteraStatusBarColor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleta_ot);
        setUpToolBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_coleta_ot);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        transfNota = getIntent().getParcelableExtra("ot");

        if (transfNota != null) {
            llPontos = findViewById(R.id.llPontos);
            rlPasso1 = findViewById(R.id.rlPasso1);
            rlPasso2 = findViewById(R.id.rlPasso2);
            rlPasso3 = findViewById(R.id.rlPasso3);

            bVoltar = findViewById(R.id.bVoltar);
            bProximo = findViewById(R.id.bProximo);

            tvSemProdDiv = findViewById(R.id.tvSemProdDiv);
            tvSemProdOk = findViewById(R.id.tvSemProdOk);
            tvNumeroOt = findViewById(R.id.tvNumeroOt);
            tvLojaOrigem = findViewById(R.id.tvLojaOrigem);
            tvLojaDestino = findViewById(R.id.tvLojaDestino);
            tvEmissao = findViewById(R.id.tvEmissao);
            tvSemProdutos = findViewById(R.id.tvSemProdutos);
            tvQntDiv = findViewById(R.id.tvQntDiv);
            tvQntPares = findViewById(R.id.tvQntPares);
            tvQntLinha = findViewById(R.id.tvQntLinha);

            nsvProdutos = findViewById(R.id.nsvProdutos);

            rvProdutosOt = findViewById(R.id.rvProdutos);
            rvProdutosOt.setHasFixedSize(true);
            rvProdutosOt.setLayoutManager(new LinearLayoutManager(getContext()));
            rvProdutosOt.setItemAnimator(new DefaultItemAnimator());

            rvProdDiv = findViewById(R.id.rvProdDiv);
            rvProdDiv.setHasFixedSize(true);
            rvProdDiv.setLayoutManager(new LinearLayoutManager(getContext()));
            rvProdDiv.setItemAnimator(new DefaultItemAnimator());

            rvProdOk = findViewById(R.id.rvProdOk);
            rvProdOk.setHasFixedSize(true);
            rvProdOk.setLayoutManager(new LinearLayoutManager(getContext()));
            rvProdOk.setItemAnimator(new DefaultItemAnimator());

            vpPassos = findViewById(R.id.vpPassos);
            vpPassos.setAdapter(new SliderAdapter(getContext(),
                    new int[] { R.string.acti_co_passo1, R.string.acti_co_passo2, R.string.acti_co_passo3 },
                    new int[] { R.string.acti_co_passo1_desc, R.string.acti_co_passo2_desc, R.string.acti_co_passo3_desc })
            );

            vpPassos.addOnPageChangeListener(viewPager);

            indicadorPontos(0);

            bVoltar.setOnClickListener(v -> {
                vpPassos.setCurrentItem(paginaAtual - 1);
                passo = vpPassos.getCurrentItem();
            });

            bProximo.setOnClickListener(v -> {
                vpPassos.setCurrentItem(paginaAtual + 1);
                passo += passo < 3 ? 1 : 0;

                if (passo == 3 && bProximo.getText().equals(getString(R.string.acti_et_finalizar))) {
                    validaEnvioOt();
                }
            });

            etCodProduto = findViewById(R.id.etCodProdCaixa);
            etCodProduto.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String codDigitado = etCodProduto.getText().toString().toUpperCase().trim();

                    if (codDigitado.length() == 12 || codDigitado.length() == 14 || codDigitado.length() == 13) {
                        incluiProduto(codDigitado);
                    } else {
                        Utils.toast(getContext(), "Código coletado inválido! Coletado: '"+ codDigitado +"'");
                    }

                    etCodProduto.setText("");
                    etCodProduto.requestFocus();
                }

                return false;
            });

            ImageButton ibLimpaListaProd = findViewById(R.id.ibLimpaListaProd);
            ibLimpaListaProd.setOnClickListener(v -> {
                if (produtosDiv.size() > 0 || produtosOk.size() > 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Atenção!!!")
                            .setMessage("Deseja excluir a lista de produtos?")
                            .setNegativeButton("Não", null)
                            .setPositiveButton("Sim", (dialog, which) -> {
                                produtosDiv.clear();
                                produtosOk.clear();

                                quantDivergencias = 0;
                                quantPares = 0;
                                quantLinhas = 0;

                                tvQntDiv.setText((String.valueOf(quantDivergencias)));
                                tvQntPares.setText(String.valueOf(quantPares));
                                tvQntLinha.setText(String.valueOf(quantLinhas));

                                adapterProdOk = new TransfOtProdAdapter(getContext(), produtosOk, false, formaColeta, onClickOtProduto());
                                rvProdOk.setAdapter(adapterProdOk);

                                adapterProdDiv = new TransfOtProdAdapter(getContext(), produtosDiv, true, formaColeta, onClickOtProduto());
                                rvProdDiv.setAdapter(adapterProdDiv);

                                tvSemProdDiv.setVisibility(View.VISIBLE);
                                tvSemProdOk.setVisibility(View.VISIBLE);
                            }).show();
                } else {
                    Utils.toast(getContext(), "Sem produtos para excluir!");
                }
            });

            if (savedInstanceState != null) {
                formaColeta = savedInstanceState.getString(FORMA_COLETA, getString(R.string.forma_coleta_cod_shoebiz));
                transfNota = savedInstanceState.getParcelable(TRANSF_NOTA);
                transfQuant = savedInstanceState.getParcelable(TRANSF_QUANT);
                produtosDiv = savedInstanceState.getParcelableArrayList(PRODUTO_DIV);
                produtosOk = savedInstanceState.getParcelableArrayList(PRODUTO_OK);
                quantDivergencias = savedInstanceState.getInt(QUANT_DIV);
                quantPares = savedInstanceState.getInt(QUANT_PARES);
                quantLinhas = savedInstanceState.getInt(QUANT_LINHAS);

                recalculaQuantProd(true);
                recalculaQuantProd(false);

                infoNota();
            } else {
                consultaFormaColeta();
                consultaQntProdOt();
            }
        } else {
            Utils.toast(getContext(), "Problema ao abrir a ot! Tente novamente.");
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(FORMA_COLETA, formaColeta);
        outState.putParcelable(TRANSF_NOTA, transfNota);
        outState.putParcelable(TRANSF_QUANT, transfQuant);
        outState.putParcelableArrayList(PRODUTO_DIV, (ArrayList<? extends Parcelable>) produtosDiv);
        outState.putParcelableArrayList(PRODUTO_OK, (ArrayList<? extends Parcelable>) produtosOk);
        outState.putInt(QUANT_DIV, quantDivergencias);
        outState.putInt(QUANT_PARES, quantPares);
        outState.putInt(QUANT_LINHAS, quantLinhas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fechar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.fechar) {
            ShoebizApplication.getInstance().getBus().post("refresh");

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void consultaFormaColeta() {
        Utils.showProgress(getContext(), "Aguarde...", "Consultado dados da Ot!");

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

            adapterProdDiv = new TransfOtProdAdapter(getContext(), produtosDiv, true, formaColeta, onClickOtProduto());
            rvProdDiv.setAdapter(adapterProdDiv);

            adapterProdOk = new TransfOtProdAdapter(getContext(), produtosOk, false, formaColeta, onClickOtProduto());
            rvProdOk.setAdapter(adapterProdOk);

            infoNota();
        } else {
            Utils.toast(getContext(), "Problema ao consultar a forma de coleta! Tente novamente.");
            finish();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void infoNota() {
        List<TransfProduto> transfProdutos = new ArrayList<>();

        if (this.transfNota != null && this.formaColeta != null) {
            tvNumeroOt.setText(transfNota.otNumero);
            tvLojaOrigem.setText(getString(R.string.acti_co_dois_texto, transfNota.codLojaOrigem, transfNota.descLojaOrigem));
            tvLojaDestino.setText(getString(R.string.acti_co_dois_texto, transfNota.codLojaDestino, transfNota.descLojaDestino));
            tvEmissao.setText(new SimpleDateFormat(Utils.formatoData()).format(transfNota.emissao));

            for (TransfTipo transfTipo : transfNota.transfTipos) {
                transfProdutos.addAll(transfTipo.transfProdutos);
            }

            if (transfProdutos.size() > 0) {
                rvProdutosOt.setAdapter(new OtTransfAdapter(getContext(), transfProdutos, this.formaColeta));

                nsvProdutos.setVisibility(View.VISIBLE);
                tvSemProdutos.setVisibility(View.GONE);
            } else {
                nsvProdutos.setVisibility(View.GONE);
                tvSemProdutos.setVisibility(View.VISIBLE);
            }
        } else {
            Utils.toast(getContext(), "Problema com as infomrações da nota ou forma de coleta! Tente novamente.");
            finish();
        }
    }

    private void indicadorPontos(int position) {
        if (position < 3) {
            pontos = new TextView[3];
            llPontos.removeAllViews();

            for (int i = 0; i < pontos.length; i++) {
                pontos[i] = new TextView(getContext());
                pontos[i].setText(Html.fromHtml("&#8226"));
                pontos[i].setTextSize(35);
                pontos[i].setTextColor(getResources().getColor(R.color.colorTranparentDot));

                llPontos.addView(pontos[i]);
            }

            if (pontos.length > 0) {
                pontos[position].setTextColor(getResources().getColor(R.color.colorDot));
            }
        }
    }

    private void consultaQntProdOt() {
        new TransfMercadoriaService(getContext(), "", "").consultaQntProdCol(new TransfMercadoriaService.ConsultaQuantCallback() {

            @Override
            public void onSuccess(TransfQuant transfQuant) {
                if (transfQuant != null) {
                    gravaQntProdCol(transfQuant);
                } else {
                    Utils.toast(getContext(), "Problema ao consultar os parametros de quantidade!");
                    finish();
                }
            }

            @Override
            public void onFailure(String erro) {
                Utils.toast(getContext(), erro);

                finish();
            }
        });
    }

    private void gravaQntProdCol(TransfQuant transfQuant) {
        this.transfQuant = transfQuant;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void incluiProduto(String codDigitado) {
        StringBuilder textoGtin = new StringBuilder();
        String descricaoProduto = "";

        boolean isCodShoebiz = formaColeta.equals(getString(R.string.forma_coleta_cod_shoebiz)) || formaColeta.equals(getString(R.string.forma_coleta_hibrido));
        boolean isCodGtin = formaColeta.equals(getString(R.string.forma_coleta_gtin)) || formaColeta.equals(getString(R.string.forma_coleta_hibrido));
        boolean achouPaiCor = false;
        boolean unicaContagem = false;
        boolean incluiProdutoOt = true;
        boolean produtoDiv = true;
        boolean incluiu = false;

        OtProduto otProdutoRm = new OtProduto();

        OtProduto otProduto = new OtProduto();
        otProduto.coletado = codDigitado;
        otProduto.quantidade = 1;
        otProduto.unicaContagem = false;
        otProduto.ot = false;

        if (isCodShoebiz) {
            if (codDigitado.length() == 12) {
                otProduto.codigo = "0" + otProduto.coletado;
                otProduto.codigoCaixa = true;
                otProduto.codigoProduto = false;
            } else if (codDigitado.length() == 14) {
                otProduto.codigo = otProduto.coletado.substring(1);
                otProduto.codigoCaixa = true;
                otProduto.codigoProduto = false;
            } else if (codDigitado.length() == 13) {
                otProduto.codigo = otProduto.coletado;
                otProduto.codigoCaixa = false;
                otProduto.codigoProduto = true;
            } else {
                Utils.toast(getContext(), "Código coletado inválido! Coletado: '"+ codDigitado +"'");

                return;
            }

            MAINFOR: for (TransfTipo transfTipo : this.transfNota.transfTipos) {
                for (TransfProduto transfProduto : transfTipo.transfProdutos) {
                    if (transfTipo.codTipo.equals("P")) {
                        if (transfProduto.codigoProduto.substring(0,11).equals(otProduto.codigo.substring(0,11))) {
                            otProduto.ot = true;
                            otProduto.unicaContagem = transfProduto.isUnicaContagem();
                            otProduto.descricao = transfProduto.descricao;

                            break MAINFOR;
                        }
                    } else if (transfTipo.codTipo.equals("T")) {
                        if (transfProduto.codigoProduto.equals(otProduto.codigo.substring(0,9))) {
                            otProduto.ot = true;
                            otProduto.unicaContagem = transfProduto.isUnicaContagem();
                            otProduto.descricao = transfProduto.descricao;

                            break MAINFOR;
                        }
                    }
                }
            }
        }

        if (isCodGtin && !otProduto.achouNaOt()) {
            MAINFOR: for (TransfTipo transfTipo : this.transfNota.transfTipos) {
                for (TransfProduto transfProduto : transfTipo.transfProdutos) {
                    if (transfTipo.codTipo.equals("P")) {
                        for (TransfGtin transfGtin : transfProduto.transfGtins) {
                            if (transfGtin.codigo.equals(otProduto.coletado)) {
                                otProduto.codigo = transfProduto.codigoProduto;
                                otProduto.gtin = transfGtin.codigo;
                                otProduto.descricao = transfProduto.descricao;
                                otProduto.ot = true;

                                break;
                            }
                        }
                    } else if (transfTipo.codTipo.equals("T")) {
                        for (TransfGtin transfGtin : this.transfNota.transfGtin) {
                            if (transfGtin.produtoGtin.equals(otProduto.coletado)) {
                                otProduto.codigo = transfGtin.produtoCodigo;
                                otProduto.gtin = transfGtin.produtoGtin;
                                otProduto.ot = true;

                                break;
                            }
                        }
                    }

                    if (otProduto.ot) {
                        otProduto.unicaContagem = transfProduto.isUnicaContagem();
                        otProduto.descricao = transfProduto.descricao;
                        otProduto.codigoCaixa = true;
                        otProduto.codigoProduto = false;

                        break MAINFOR;
                    }
                }
            }

            if (!otProduto.achouNaOt() && otProduto.coletado.length() == 13) {
                MAINFOR: for (TransfTipo transfTipo : this.transfNota.transfTipos) {
                    for (TransfProduto transfProduto : transfTipo.transfProdutos) {
                        if (transfTipo.codTipo.equals("P")) {
                            if (transfProduto.codigoProduto.substring(0,11).equals(otProduto.coletado.substring(0,11))) {
                                if (transfProduto.codigoProduto.equals(otProduto.coletado)) {
                                    otProduto.ot = true;
                                } else {
                                    descricaoProduto = transfProduto.descricao;
                                    unicaContagem = transfProduto.isUnicaContagem();
                                    achouPaiCor = true;
                                }
                            }
                        } else if (transfTipo.codTipo.equals("T")) {
                            if (transfProduto.codigoProduto.equals(otProduto.coletado.substring(0,9))) {
                                descricaoProduto = transfProduto.descricao;
                                unicaContagem = transfProduto.isUnicaContagem();
                                achouPaiCor = true;

                                break MAINFOR;
                            }
                        }

                        if (otProduto.ot) {
                            for (int n = 0; n < transfProduto.transfGtins.size(); n++){
                                textoGtin.append(transfProduto.transfGtins.get(n).codigo).append(n < (transfProduto.transfGtins.size() - 1) ? ", " : "");
                            }

                            otProduto.codigo = transfProduto.codigoProduto;
                            otProduto.gtin = textoGtin.toString();
                            otProduto.descricao = transfProduto.descricao;
                            otProduto.unicaContagem = transfProduto.isUnicaContagem();
                            otProduto.codigoCaixa = false;
                            otProduto.codigoProduto = true;

                            break MAINFOR;
                        }
                    }
                }

                if (!otProduto.achouNaOt() && achouPaiCor) {
                    for (TransfGtin transfGtin : this.transfNota.transfGtin) {
                        if (transfGtin.produtoCodigo.equals(otProduto.coletado)) {
                            otProduto.codigo = transfGtin.produtoCodigo;
                            otProduto.gtin = transfGtin.produtoGtin;
                            otProduto.unicaContagem = unicaContagem;
                            otProduto.descricao = descricaoProduto;
                            otProduto.codigoCaixa = false;
                            otProduto.codigoProduto = true;
                            otProduto.ot = true;

                            break;
                        }
                    }
                }
            }
        }

        if (otProduto.achouNaOt()) {
            if (!otProduto.unicaContagem) {
                for (OtProduto otProdutoAux : produtosDiv) {
                    if (otProdutoAux.codigo.equals(otProduto.codigo)) {
                        if ((otProduto.codigoCaixa && !otProdutoAux.codigoCaixa) || (otProduto.codigoProduto && !otProdutoAux.codigoProduto)) {
                            if (isCodGtin && otProduto.codigoProduto) {
                                otProduto.gtin = otProdutoAux.gtin;
                            }

                            otProdutoRm = otProdutoAux;
                            produtoDiv = false;

                            break;
                        }
                    }
                }
            } else {
                produtoDiv = false;
            }

            if (!produtoDiv) {
                for (OtProduto otProdutoAux : produtosOk) {
                    if (otProdutoAux.codigo.toUpperCase().equals(otProduto.codigo) && validaQuantProdutos()) {
                        otProdutoAux.quantidade++;
                        incluiProdutoOt = false;
                        incluiu = true;

                        break;
                    }
                }

                if (incluiProdutoOt && validaQuantLinhas() && validaQuantProdutos()) {
                    if (!otProduto.codigoCaixa) {
                        otProduto.codigoCaixa = true;
                    } else if (!otProduto.codigoProduto) {
                        otProduto.codigoProduto = true;
                    }

                    incluiu = true;
                    produtosOk.add(otProduto);
                    quantLinhas++;
                }

                if (incluiu) {
                    produtosDiv.remove(otProdutoRm);

                    quantDivergencias =  otProduto.unicaContagem ? quantDivergencias : (quantDivergencias - 1);
                    quantPares++;

                    if (otProduto.unicaContagem) {
                        Utils.toast(getContext(), "Produto: " + otProduto.codigo + " com contagem única!");
                    }
                }

                tvQntPares.setText(String.valueOf(quantPares));
                tvQntLinha.setText(String.valueOf(quantLinhas));
                tvQntDiv.setText(String.valueOf(quantDivergencias));
            } else {
                produtosDiv.add(otProduto);
                quantDivergencias++;

                tvQntDiv.setText(String.valueOf(quantDivergencias));
            }

            tvSemProdDiv.setVisibility(produtosDiv.size() == 0 ? View.VISIBLE : View.GONE);
            tvSemProdOk.setVisibility(produtosOk.size() == 0 ? View.VISIBLE : View.GONE);

            adapterProdDiv.notifyDataSetChanged();
            adapterProdOk.notifyDataSetChanged();
        } else {
            Utils.toast(getContext(), "Produto não consta na Ot. Produto: " + otProduto.coletado);
        }
    }

    private boolean validaQuantProdutos() {
        if (quantPares >= transfQuant.quantProdutos) {
            Utils.toast(getContext(), "A ot deve conter até " + transfQuant.quantProdutos + " produtos.");

            return false;
        }

        return true;
    }

    private boolean validaQuantLinhas() {
        if (quantLinhas >= transfQuant.quantLinhas) {
            Utils.toast(getContext(), "A ot deve conter até " + transfQuant.quantLinhas + " linhas.");

            return false;
        }

        return true;
    }

    private void validaEnvioOt() {
        if (produtosOk.size() > 0) {
            if (produtosDiv.size() > 0) {
                Utils.toast(getContext(), "Existe produto(s) divergente. Favor corrigir!");
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Atenção!")
                        .setMessage("Confirma a coleta da Ot?")
                        .setNegativeButton("Não", null)
                        .setPositiveButton("Sim", (dialog, which) -> QuantVolumeDialog.show(getSupportFragmentManager(), this::enviaOt)).show();
            }
        } else {
            Utils.toast(getContext(), "Produto(s) não coletado!");
        }
    }

    private void enviaOt(int quantVolumes) {
        List<TransfProduto> transfProdutos = new ArrayList<>();

        for (OtProduto otProdutoAux : produtosOk) {
            TransfProduto transfProduto = new TransfProduto();

            if (formaColeta.equals(getString(R.string.forma_coleta_gtin))) {
                transfProduto.codigoProduto = otProdutoAux.gtin;
            } else {
                transfProduto.codigoProduto = otProdutoAux.codigo;
            }

            transfProduto.quantidade = otProdutoAux.quantidade;
            transfProdutos.add(transfProduto);
        }

        Utils.showProgress(getContext(), "Atenção", "Enviando Ot!");

        new TransfMercadoriaService(getContext(), "", String.valueOf(quantVolumes))
                .enviaOtAPI(transfNota.otNumero, transfProdutos, new TransfMercadoriaService.EnviaCallBack() {

            @Override
            @SuppressLint("InvalidAnalyticsName")
            public void onSuccess(String mensagem) {
                Utils.closeProgress();
                Utils.toast(getContext(), mensagem);

                Bundle bundle = new Bundle();
                bundle.putString("Loja", new PrefsUtils(getContext()).isCodLoja());
                bundle.putString("Operação", "Coletado");
                mFirebaseAnalytics.logEvent("Transferencia Mercadoria", bundle);

                ShoebizApplication.getInstance().getBus().post("refresh");
                finish();
            }

            @Override
            public void onFailure(String erro) {
                Utils.closeProgress();
                Utils.toast(getContext(), erro);
            }
        });
    }

    private void recalculaQuantProd(boolean isTelaDiv) {
        if (isTelaDiv) {
            quantDivergencias = 0;

            for (OtProduto otProduto : produtosDiv) {
                quantDivergencias += otProduto.quantidade;
            }

            tvQntDiv.setText(String.valueOf(quantDivergencias));
            tvSemProdDiv.setVisibility(quantDivergencias > 0 ? View.GONE : View.VISIBLE);

            adapterProdDiv = new TransfOtProdAdapter(getContext(), produtosDiv, true, formaColeta, onClickOtProduto());
            rvProdDiv.setAdapter(adapterProdDiv);
        } else {
            quantLinhas = 0;
            quantPares = 0;

            for (OtProduto otProduto : produtosOk) {
                quantPares += otProduto.quantidade;
                quantLinhas++;
            }

            tvQntPares.setText(String.valueOf(quantPares));
            tvQntLinha.setText(String.valueOf(quantLinhas));
            tvSemProdOk.setVisibility(quantLinhas > 0 && quantPares > 0 ? View.GONE : View.VISIBLE);

            adapterProdOk = new TransfOtProdAdapter(getContext(), produtosOk, false, formaColeta, onClickOtProduto());
            rvProdOk.setAdapter(adapterProdOk);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private TransfOtProdAdapter.TransfOtOnClickListener onClickOtProduto() {
        return (view, idx, isTelaDiv) -> {
            OtProduto otProduto = isTelaDiv ? produtosDiv.get(idx) : produtosOk.get(idx);

            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            Menu menu = popupMenu.getMenu();
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_ent, menu);

            popupMenu.setOnMenuItemClickListener(menuItem -> false);

            if (isTelaDiv) {
                menu.removeItem(R.id.menu_ent_quantidade);
            }

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.menu_ent_excluir:
                        new AlertDialog.Builder(getContext())
                                .setTitle("Atenção!!!")
                                .setMessage("Excluir o produto '"+ otProduto.codigo +"' ?")
                                .setNegativeButton("Não", null)
                                .setPositiveButton("Sim", (dialog, which) -> {
                                    if (isTelaDiv) {
                                        produtosDiv.remove(otProduto);
                                    } else {
                                        produtosOk.remove(otProduto);
                                    }

                                    recalculaQuantProd(isTelaDiv);
                                }).show();

                        break;
                    case R.id.menu_ent_quantidade:
                        QuantidadeDialog.show(getSupportFragmentManager(), otProduto.quantidade, quantidade -> {
                            otProduto.quantidade = quantidade;

                            recalculaQuantProd(isTelaDiv);
                        });

                        break;
                }

                return false;
            });

            popupMenu.show();
        };
    }

    ViewPager.OnPageChangeListener viewPager = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            indicadorPontos(position);

            int paginaAux = paginaAtual;
            paginaAtual = position;

            if (position == 0) {
                bProximo.setEnabled(true);
                bVoltar.setEnabled(false);
                bVoltar.setVisibility(View.INVISIBLE);

                bProximo.setText(getString(R.string.acti_et_proximo));
                bVoltar.setText("");

                rlPasso3.setVisibility(View.GONE);

                Animation slide_left = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right_down);
                rlPasso2.startAnimation(slide_left);
                rlPasso2.setVisibility(View.INVISIBLE);

                Animation slide_right = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right_up);
                rlPasso1.startAnimation(slide_right);
                rlPasso1.setVisibility(View.VISIBLE);
            } else if (position == pontos.length - 1) {
                bProximo.setEnabled(true);
                bVoltar.setEnabled(true);
                bVoltar.setVisibility(View.VISIBLE);

                bProximo.setText(getString(R.string.acti_et_finalizar));
                bVoltar.setText(getString(R.string.acti_et_voltar));

                rlPasso1.setVisibility(View.GONE);

                Animation slide_left = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_down);
                rlPasso2.startAnimation(slide_left);
                rlPasso2.setVisibility(View.INVISIBLE);

                Animation slide_right = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_up);
                rlPasso3.startAnimation(slide_right);
                rlPasso3.setVisibility(View.VISIBLE);
            } else {
                bProximo.setEnabled(true);
                bVoltar.setEnabled(true);
                bVoltar.setVisibility(View.VISIBLE);

                bProximo.setText(getString(R.string.acti_et_proximo));
                bVoltar.setText(getString(R.string.acti_et_voltar));

                if (position > paginaAux) {
                    Animation slide_left = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_down);
                    rlPasso1.startAnimation(slide_left);
                    rlPasso1.setVisibility(View.INVISIBLE);

                    Animation slide_right = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_up);
                    rlPasso2.startAnimation(slide_right);
                    rlPasso2.setVisibility(View.VISIBLE);
                } else if (position < paginaAux) {
                    Animation slide_left = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right_down);
                    rlPasso3.startAnimation(slide_left);
                    rlPasso3.setVisibility(View.INVISIBLE);

                    Animation slide_right = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right_up);
                    rlPasso2.startAnimation(slide_right);
                    rlPasso2.setVisibility(View.VISIBLE);
                }

                rlPasso3.setVisibility(View.INVISIBLE);

                etCodProduto.requestFocus();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}