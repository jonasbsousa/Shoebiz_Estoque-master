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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.ShoebizApplication;
import br.com.shoebiz.shoeconf_2.adapter.SliderAdapter;
import br.com.shoebiz.shoeconf_2.adapter.TransfEntAdapter;
import br.com.shoebiz.shoeconf_2.fragment.dialog.QuantidadeDialog;
import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.TransfGtin;
import br.com.shoebiz.shoeconf_2.model.TransfNota;
import br.com.shoebiz.shoeconf_2.model.TransfProduto;
import br.com.shoebiz.shoeconf_2.service.EntradaMercadoriaService;
import br.com.shoebiz.shoeconf_2.service.TransfMercadoriaService;
import br.com.shoebiz.shoeconf_2.utils.FTPUtils;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

import static br.com.shoebiz.shoeconf_2.utils.Utils.maskMoney;

public class EntradaTransfActivity extends BaseActivity {

    private final String FORMA_COLETA = "formaColeta";
    private final String ENTRADA_AUTO = "entAuto";
    private final String TRANSF_NOTA = "transfNota";
    private final String PRODUTO_ENTRADA = "produtoEntrada";
    private final String QUANTIDADE_PRODUTOS = "quantidadeProduto";

    private final DecimalFormat DECIMALFORMAT = new DecimalFormat(maskMoney());

    private TransfNota transfNota;
    private String formaColeta;

    private List<TransfProduto> transfProdutosOk = new ArrayList<>();

    private RecyclerView rvProdutosOk;
    private RecyclerView rvProdutosDiv;

    private RecyclerView.Adapter adapterProdutosOk;

    private RelativeLayout rlPasso1;
    private RelativeLayout rlPasso2;
    private RelativeLayout rlPasso3;
    private LinearLayout llPontos;
    private ViewPager vpPassos;
    private Button bVoltar;
    private Button bProximo;

    private EditText etCodigoProduto;

    private TextView tvSemProduto;
    private TextView tvNumeroNota;
    private TextView tvLojaOrigem;
    private TextView tvLojaDestino;
    private TextView tvEmissao;
    private TextView tvChave;
    private TextView tvQntPares;
    private TextView tvValorTotal;
    private TextView tvQntParesPasso2;

    private TextView[] pontos;
    private boolean entAuto;
    private boolean enviaNota = false;
    private boolean notaAcerto = false;
    private boolean notaComplemento = false;
    private int paginaAtual;
    private int passo;
    private int quantPares2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alteraTema();
        alteraStatusBarColor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada_transf);
        setUpToolBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_recebe_transferencia);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        transfNota = getIntent().getParcelableExtra("nota");

        if (transfNota != null) {
            tvNumeroNota = findViewById(R.id.tvNumeroNota);
            tvLojaOrigem = findViewById(R.id.tvLojaOrigem);
            tvLojaDestino = findViewById(R.id.tvLojaDestino);
            tvEmissao = findViewById(R.id.tvEmissao);
            tvChave = findViewById(R.id.tvChave);
            tvSemProduto = findViewById(R.id.tvSemProduto);
            tvQntPares = findViewById(R.id.tvQntPares);
            tvValorTotal = findViewById(R.id.tvValorTotal);
            tvQntParesPasso2 = findViewById(R.id.tvQntParesPasso2);

            bVoltar = findViewById(R.id.bVoltar);
            bProximo = findViewById(R.id.bProximo);

            llPontos = findViewById(R.id.llPontos);
            rlPasso1 = findViewById(R.id.rlPasso1);
            rlPasso2 = findViewById(R.id.rlPasso2);
            rlPasso3 = findViewById(R.id.rlPasso3);

            rvProdutosOk = findViewById(R.id.rvProdutos);
            rvProdutosOk.setHasFixedSize(true);
            rvProdutosOk.setLayoutManager(new LinearLayoutManager(getContext()));
            rvProdutosOk.setItemAnimator(new DefaultItemAnimator());

            rvProdutosDiv = findViewById(R.id.rvProdutosDivergentes);
            rvProdutosDiv.setHasFixedSize(true);
            rvProdutosDiv.setLayoutManager(new LinearLayoutManager(getContext()));
            rvProdutosDiv.setItemAnimator(new DefaultItemAnimator());

            vpPassos = findViewById(R.id.vpPassos);
            vpPassos.setAdapter(new SliderAdapter(getContext(),
                    new int[] { R.string.acti_et_passo1, R.string.acti_et_passo2, R.string.acti_et_passo3 },
                    new int[] { R.string.acti_et_passo1_desc, R.string.acti_et_passo2_desc, R.string.acti_et_passo3_desc }));

            indicadorPontos(0);

            vpPassos.addOnPageChangeListener(viewPager);

            etCodigoProduto = findViewById(R.id.etCodigoProduto);
            etCodigoProduto.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String codDigitado = etCodigoProduto.getText().toString().toUpperCase().trim();

                    if (codDigitado.length() == 8 ||codDigitado.length() == 12 || codDigitado.length() == 13 || codDigitado.length() == 14) {
                        incluiProduto(codDigitado);
                    } else {
                        Utils.toast(getContext(), "Código Inválido");
                    }

                    etCodigoProduto.setText("");
                    etCodigoProduto.requestFocus();
                }

                return false;
            });

            bVoltar.setOnClickListener(v -> {
                vpPassos.setCurrentItem(paginaAtual - 1);
                passo = vpPassos.getCurrentItem();
            });

            bProximo.setOnClickListener(v -> {
                vpPassos.setCurrentItem(paginaAtual + 1);
                passo += passo < 3 ? 1 : 0;

                if (passo == 3 && bProximo.getText().equals(getString(R.string.acti_et_finalizar))) {
                    validaEnvioNota();
                }
            });

            ImageButton ibLimpaLista = findViewById(R.id.ibLimpaLista);
            ibLimpaLista.setOnClickListener(v -> {
                if (transfProdutosOk.size() > 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Atenção!!!")
                            .setMessage("Deseja excluir a lista de produtos?")
                            .setNegativeButton("Não", null)
                            .setPositiveButton("Sim", (dialog, which) -> {
                                transfProdutosOk.clear();

                                adapterProdutosOk = new TransfEntAdapter(getContext(), transfProdutosOk, false, "", onClickTransProduto());
                                rvProdutosOk.setAdapter(adapterProdutosOk);

                                quantPares2 = 0;
                                tvQntParesPasso2.setText(String.valueOf(quantPares2));

                                tvSemProduto.setVisibility(View.VISIBLE);

                                enviaNota = false;
                                notaAcerto = false;
                                notaComplemento = false;
                            }).show();
                } else {
                    Utils.toast(getContext(), "Sem produtos para excluir!");
                }
            });

            if (savedInstanceState != null) {
                formaColeta = savedInstanceState.getString(FORMA_COLETA, getString(R.string.forma_coleta_cod_shoebiz));
                entAuto = savedInstanceState.getBoolean(ENTRADA_AUTO, false);
                transfNota = savedInstanceState.getParcelable(TRANSF_NOTA);
                transfProdutosOk = savedInstanceState.getParcelableArrayList(PRODUTO_ENTRADA);
                quantPares2 = savedInstanceState.getInt(QUANTIDADE_PRODUTOS, 0);

                tvQntPares.setText(String.valueOf(quantPares2));

                if (transfProdutosOk.size() > 0) {
                    tvSemProduto.setVisibility(View.GONE);
                }

                infoNota();
            } else {
                consultaEntAuto();
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
        outState.putBoolean(ENTRADA_AUTO, entAuto);
        outState.putParcelable(TRANSF_NOTA, transfNota);
        outState.putParcelableArrayList(PRODUTO_ENTRADA, (ArrayList<? extends Parcelable>) transfProdutosOk);
        outState.putInt(QUANTIDADE_PRODUTOS, quantPares2);
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
        Utils.showProgress(getContext(), "Aguarde...", "Consultado dados da Nota!");

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

            infoNota();
        } else {
            Utils.toast(getContext(), "Problema ao consultar a forma de coleta! Tente novamente.");
            finish();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void infoNota() {
        int quantidade = 0;

        if (this.transfNota != null && this.formaColeta != null) {
            tvNumeroNota.setText(getString(R.string.frag_tm_desc_1, transfNota.numero, transfNota.serie));
            tvLojaOrigem.setText(getString(R.string.frag_tm_desc_2, transfNota.codLojaOrigem, transfNota.descLojaOrigem));
            tvLojaDestino.setText(getString(R.string.frag_tm_desc_2, transfNota.codLojaDestino, transfNota.descLojaDestino));
            tvEmissao.setText(new SimpleDateFormat(Utils.formatoData()).format(transfNota.emissao));
            tvChave.setText(transfNota.chave);
            tvValorTotal.setText(String.valueOf(DECIMALFORMAT.format(transfNota.valorNota)));

            for (TransfProduto transfProduto : transfNota.transfProdutos) {
                quantidade += transfProduto.quantidade;
            }

            tvQntPares.setText(String.valueOf(quantidade));

            updateListProd();
        }
    }

    private void consultaEntAuto() {
        new TransfMercadoriaService(getContext(), "", "").ConsultaAutoEnt(new TransfMercadoriaService.ConsultaAutoEntCallback() {

            @Override
            public void onSuccess(APIMensagem apiMensagem) {
                gravaEntAuto(apiMensagem.mensagem.equals("S"));
            }

            @Override
            public void onFailure(String erro) {
                Utils.toast(getContext(), erro);

                finish();
            }
        });
    }

    private void gravaEntAuto(boolean entAuto) {
        this.entAuto = entAuto;
    }

    private void incluiProduto(String codDigitado) {
        boolean inclui = true;
        String descricao = null;
        String codigoProduto = "";

        if (formaColeta.equals(getString(R.string.forma_coleta_cod_shoebiz)) || formaColeta.equals(getString(R.string.forma_coleta_hibrido))) {
            switch (codDigitado.length()) {
                case 14:
                    codDigitado = codDigitado.substring(1);
                    break;
                case 12:
                    codDigitado = "0" + codDigitado;
                    break;
            }

            for (TransfProduto transfProduto : this.transfNota.transfProdutos) {
                if (transfProduto.codigoProduto.equals(codDigitado)) {
                    descricao = transfProduto.descricao;
                    codigoProduto = transfProduto.codigoProduto;

                    break;
                }
            }
        }

        if (formaColeta.equals(getString(R.string.forma_coleta_gtin)) || formaColeta.equals(getString(R.string.forma_coleta_hibrido))) {
            FORPAI : for (TransfProduto transfProduto : this.transfNota.transfProdutos) {
                for (TransfGtin transfGtin : transfProduto.transfGtins) {
                    if (transfGtin.codigo.equals(codDigitado)) {
                        descricao = transfProduto.descricao;
                        codigoProduto = transfProduto.codigoProduto;

                        break FORPAI;
                    }
                }
            }
        }

        for (TransfProduto transfProduto : transfProdutosOk) {
            if (transfProduto.coletado.equals(codDigitado)) {
                transfProduto.quantidade++;

                inclui = false;
                break;
            }
        }

        if (inclui) {
            TransfProduto transfProduto = new TransfProduto();
            transfProduto.coletado = codDigitado;
            transfProduto.descricao = descricao;
            transfProduto.codigoProduto = codigoProduto;
            transfProduto.quantidade = 1;

            tvSemProduto.setVisibility(View.GONE);
            transfProdutosOk.add(transfProduto);
        }

        quantPares2++;
        tvQntParesPasso2.setText(String.valueOf(quantPares2));

        updateListProd();
    }

    private void updateListProd() {
        adapterProdutosOk = new TransfEntAdapter(getContext(), transfProdutosOk, false, "", onClickTransProduto());
        rvProdutosOk.setAdapter(adapterProdutosOk);
    }

    private void recalculaQuantProd() {
        quantPares2 = 0;

        for(TransfProduto transfProduto : transfProdutosOk) {
            quantPares2 += transfProduto.quantidade;
        }

        tvQntParesPasso2.setText(String.valueOf(quantPares2));
        tvSemProduto.setVisibility(quantPares2 > 0 ? View.GONE : View.VISIBLE);

        updateListProd();
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

    private void analisaDivergencia() {
        List<TransfProduto> transfProdutosDiv = new ArrayList<>();

        boolean achou = false;

        notaComplemento = false;
        notaAcerto = false;

        Utils.showProgress(getContext(), "Atenção...", "Analisando divergência(s)");

        for (TransfProduto transfProduto : this.transfNota.transfProdutos) {
            TransfProduto produto = new TransfProduto();

            produto.codigoProduto = transfProduto.codigoProduto;
            produto.transfGtins = transfProduto.transfGtins;
            produto.quantidade = transfProduto.quantidade;
            produto.descricao = transfProduto.descricao;
            produto.divergencia = transfProduto.quantidade * -1;

            for (TransfProduto transfProdutoOk : transfProdutosOk) {
                if (transfProdutoOk.codigoProduto.equals(transfProduto.codigoProduto)) {
                    produto.divergencia += transfProdutoOk.quantidade;
                }
            }

            transfProdutosDiv.add(produto);
        }

        for (TransfProduto transfProdutoOk : transfProdutosOk) {
            for (TransfProduto transfProduto : transfProdutosDiv) {
                if (transfProdutoOk.codigoProduto.equals(transfProduto.codigoProduto)) {
                    achou = true;
                    break;
                }
            }

            if (!achou) {
                TransfProduto produto = new TransfProduto();
                produto.codigoProduto = transfProdutoOk.coletado;
                produto.coletado = transfProdutoOk.coletado;
                produto.transfGtins = new ArrayList<>();
                produto.descricao = transfProdutoOk.descricao;
                produto.divergencia = transfProdutoOk.quantidade * -1;

                transfProdutosDiv.add(produto);
            }

            achou = false;
        }

        for (TransfProduto transfProduto : transfProdutosDiv) {
            if (transfProduto.divergencia > 0) {
                notaComplemento = true;

                break;
            } else if (transfProduto.divergencia < 0) {
                notaAcerto = true;

                break;
            }
        }

        if (notaAcerto || notaComplemento) {
            Utils.toast(getContext(), "Existe produto(s) com divergência!");

            enviaNota = false;
        } else {
            Utils.toast(getContext(), "Produto(s) sem divergência!");

            enviaNota = true;
        }

        rvProdutosDiv.setAdapter(new TransfEntAdapter(getContext(), transfProdutosDiv, true, this.formaColeta, null));

        Utils.closeProgress();
    }

    private void validaEnvioNota() {
        String msgAlerta = "";

        if (enviaNota) {
            msgAlerta = "Confirma a entrada?";
        } else {
            if (notaAcerto && notaComplemento) {
                msgAlerta = "Será criado uma nota de 'acerto'/'complemento' com a(s) divergência(s)! Deseja continuar?";
            } else if (notaComplemento) {
                msgAlerta = "Será criado uma nota de 'complemento' com a(s) divergência(s)! Deseja continuar?";
            } else if (notaAcerto){
                msgAlerta = "Será criado uma nota de 'acerto' com a(s) divergência(s)! Deseja continuar?";
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Atenção!")
                .setMessage(msgAlerta)
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", (dialog, which) -> new FTPUtils(getContext()).enviaArquivoTransferencia(
                        transfProdutosOk, transfNota.chave + ".json", "Enviando arquivo FTP!", new FTPUtils.RetornoCallback() {

                    @Override
                    public void onSuccess() {
                        enviaNota();
                    }

                    @Override
                    public void onFailure(String mensagem) {
                        Utils.toast(getContext(), mensagem);
                    }
                })).show();
    }

    private void enviaNota() {
        Utils.showProgress(getContext(), "Atenção", "Enviando Nota!");

        new TransfMercadoriaService(getContext(), "", "").enviaNotaAPI(transfNota, new TransfMercadoriaService.EnviaCallBack() {

            @Override
            @SuppressLint("InvalidAnalyticsName")
            public void onSuccess(String mensagem) {
                Utils.closeProgress();
                Utils.toast(getContext(), mensagem);

                Bundle bundle = new Bundle();
                bundle.putString("Loja", new PrefsUtils(getContext()).isCodLoja());
                bundle.putString("Operação", "Recebe");
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

    private void validaEntAuto() {
        int quantidadePares = 0;

        if (this.entAuto) {
            this.transfProdutosOk = new ArrayList<>();

            for (TransfProduto transfProdutoAux : transfNota.transfProdutos) {
                TransfProduto transfProduto = new TransfProduto();
                transfProduto.codigoProduto = transfProdutoAux.codigoProduto;
                transfProduto.transfGtins = transfProdutoAux.transfGtins;
                transfProduto.coletado = formaColeta.equals(getString(R.string.forma_coleta_gtin)) ? transfProdutoAux.transfGtins.get(0).codigo : transfProdutoAux.codigoProduto;
                transfProduto.descricao = transfProdutoAux.descricao;
                transfProduto.quantidade = transfProdutoAux.quantidade;

                this.transfProdutosOk.add(transfProduto);

                quantidadePares += transfProduto.quantidade;
            }

            tvSemProduto.setVisibility(View.GONE);
            tvQntParesPasso2.setText(String.valueOf(quantidadePares));

            updateListProd();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private TransfEntAdapter.TransfEntOnClickListener onClickTransProduto() {
        return (view, idx) -> {
            final TransfProduto transfProduto = transfProdutosOk.get(idx);

            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            Menu menu = popupMenu.getMenu();
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_ent, menu);

            popupMenu.setOnMenuItemClickListener(menuItem -> false);

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.menu_ent_excluir:
                        new AlertDialog.Builder(getContext())
                                .setTitle("Atenção!!!")
                                .setMessage("Excluir o produto '"+ transfProduto.codigoProduto +"' ?")
                                .setNegativeButton("Não", null)
                                .setPositiveButton("Sim", (dialog, which) -> {
                                    transfProdutosOk.remove(transfProduto);
                                    recalculaQuantProd();
                                }).show();

                        break;
                    case R.id.menu_ent_quantidade:
                        QuantidadeDialog.show(getSupportFragmentManager(), transfProduto.quantidade, quantidade -> {
                            transfProduto.quantidade = quantidade;
                            recalculaQuantProd();
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

                analisaDivergencia();
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

                etCodigoProduto.requestFocus();

                validaEntAuto();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}