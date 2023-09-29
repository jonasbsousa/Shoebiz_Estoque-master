package br.com.shoebiz.shoeconf_2.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.activity.CustomScannerActivity;
import br.com.shoebiz.shoeconf_2.adapter.ColetorProdutoAdapter;
import br.com.shoebiz.shoeconf_2.fragment.dialog.AboutDialog;
import br.com.shoebiz.shoeconf_2.fragment.dialog.ColetorDialog;
import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.APIResponseAdapter;
import br.com.shoebiz.shoeconf_2.model.APIResponseCallback;
import br.com.shoebiz.shoeconf_2.model.Coletor;
import br.com.shoebiz.shoeconf_2.model.ColetorProduto;
import br.com.shoebiz.shoeconf_2.service.ColetorService;
import br.com.shoebiz.shoeconf_2.utils.FTPUtils;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class ColetorFragment extends BaseFragment {
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;

    private Coletor coletor = new Coletor();
    private ArrayList<String> departamentos;

    private RecyclerView rvProdutos;
    private TextView tvSemProd;
    private TextView etCodProd;
    private Spinner sOperacoes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().setTitle(R.string.nav_coletor);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coletor, container, false);

        coletor.coletorProdutos = new ArrayList<>();

        ArrayList<String> operacoes = new ArrayList<>(Arrays.asList(
                "Escolha a operação",
                getString(R.string.coletor_defeito_analise),
                getString(R.string.coletor_gato_pequeimado),
                getString(R.string.coletor_contagem),
                getString(R.string.coletor_etiqueta)));

        departamentos = new ArrayList<>(Arrays.asList(
                getString(R.string.coletor_prevencao),
                getString(R.string.coletor_compras)));

        ArrayAdapter<String> operacoesAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_operacoes_coletor, operacoes);
        operacoesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        sOperacoes = view.findViewById(R.id.sOperacoes);
        sOperacoes.setAdapter(operacoesAdapter);

        tvSemProd = view.findViewById(R.id.tvSemProd);

        rvProdutos = view.findViewById(R.id.rvProdutos);
        rvProdutos.setHasFixedSize(true);
        rvProdutos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProdutos.setItemAnimator(new DefaultItemAnimator());

        etCodProd = view.findViewById(R.id.etCodProd);
        etCodProd.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String codDigitado = etCodProd.getText().toString().toUpperCase().trim();

                if (codDigitado.length() == 8 || codDigitado.length() == 12 || codDigitado.length() == 14 || codDigitado.length() == 13) {
                    incluiProduto(codDigitado);
                } else {
                    Utils.toast(getContext(), "Código Inválido");
                }

                etCodProd.setText("");
                etCodProd.requestFocus();
            }

            return false;
        });

        ImageButton ibPesquisaCamera = view.findViewById(R.id.ibPesquisaCamera);
        ibPesquisaCamera.setOnClickListener(v ->
                IntentIntegrator
                        .forSupportFragment(ColetorFragment.this)
                        .setDesiredBarcodeFormats(IntentIntegrator.CODE_128)
                        .setOrientationLocked(false)
                        .setCaptureActivity(CustomScannerActivity.class)
                        .setBeepEnabled(false)
                        .initiateScan()
        );

        Button bLimpar = view.findViewById(R.id.bLimpar);
        bLimpar.setOnClickListener(v -> {
            if (coletor.coletorProdutos.size() > 0 || sOperacoes.getSelectedItemPosition() > 0) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Atenção!!!")
                        .setMessage("Deseja as informações da tela?")
                        .setNegativeButton("Não", null)
                        .setPositiveButton("Sim", (dialog, which) -> limpaTela()).show();
            }
        });

        Button bEnviar = view.findViewById(R.id.bEnviar);
        bEnviar.setOnClickListener(v -> {
            int posicaoSpinner = sOperacoes.getSelectedItemPosition();

            if (coletor.coletorProdutos.size() > 0 && posicaoSpinner > 0) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Atenção!!!")
                        .setMessage("Confirma o envio do arquivo?")
                        .setNegativeButton("Não", null)
                        .setPositiveButton("Sim", (dialog, which) -> {
                            int operacao = 0;

                            switch (posicaoSpinner) {
                                case 1:
                                    operacao = R.string.coletor_status_defeito_analise;
                                    break;
                                case 2:
                                    operacao = R.string.coletor_status_gato_pequeimado;
                                    break;
                                case 3:
                                    operacao = R.string.coletor_status_contagem;
                                    break;
                                case 4:
                                    operacao = R.string.coletor_status_etiqueta;
                                    break;
                            }

                            enviaArquivo(operacao);
                        }).show();
            } else {
                if (posicaoSpinner == 0) {
                    Utils.toast(getContext(), "Favor selecionar uma operação!");
                } else if (coletor.coletorProdutos.size() == 0){
                    Utils.toast(getContext(), "Favor incluir os produtos!");
                }
            }
        });

        return view;
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
            incluiProduto(result.getContents());
        }
    }

    private void incluiProduto(String codDigitado) {
        boolean incluiu = false;

        if (codDigitado.length() == 12) {
            codDigitado = "0" + codDigitado;
        } else if (codDigitado.length() == 14) {
            codDigitado = codDigitado.substring(1);
        }

        for (ColetorProduto coletorProduto : coletor.coletorProdutos) {
            if (coletorProduto.codigo.equals(codDigitado)) {
                coletorProduto.quantidade++;

                incluiu = true;
                break;
            }
        }

        if (!incluiu) {
            ColetorProduto coletorProduto = new ColetorProduto();
            coletorProduto.codigo = codDigitado;
            coletorProduto.quantidade = 1;

            coletor.coletorProdutos.add(coletorProduto);
        }

        if (coletor.coletorProdutos.size() > 0) {
            tvSemProd.setVisibility(View.GONE);
        }

        rvProdutos.setAdapter(new ColetorProdutoAdapter(getContext(), coletor.coletorProdutos));
    }

    private void limpaTela() {
        if (activityIsAlive()) {
            coletor = new Coletor();

            coletor.coletorProdutos = new ArrayList<>();
            rvProdutos.setAdapter(new ColetorProdutoAdapter(getContext(), coletor.coletorProdutos));

            tvSemProd.setVisibility(View.VISIBLE);

            sOperacoes.setSelection(0);

            etCodProd.setText("");
            etCodProd.requestFocus();
        }
    }

    private void enviaArquivo(int operacao) {
        String nomeArquivo = new PrefsUtils(getContext()).isCodLoja() + "_" + Utils.getDataAtualSQL_2() + "_" + Utils.getHoraAtualSQL();

        if (operacao == R.string.coletor_status_defeito_analise || operacao == R.string.coletor_status_gato_pequeimado
                || operacao == R.string.coletor_status_etiqueta) {

            Utils.showProgress(getContext(), "Aguarde...", "Validando produtos!");

            new ColetorService(getContext()).validaProdutos(coletor.coletorProdutos, new APIResponseAdapter<>(new APIResponseCallback<APIMensagem>() {

                @Override
                public void onSuccess(APIMensagem apiMensagem) {
                    enviaFtp(nomeArquivo, operacao);
                }

                @Override
                public void onFailure(String erro) {
                    Utils.toast(getContext(), erro);
                    Utils.closeProgress();
                }
            }));
        } else {
            ColetorDialog.show(getChildFragmentManager(), departamentos, (idxDepartamento, observacao) -> {
                coletor.departamento = departamentos.get(idxDepartamento).substring(0,1);
                coletor.observacao = observacao;

                Utils.showProgress(getContext(), "Aguarde...", "Enviando contagem!");

                new ColetorService(getContext()).contagemProdutos(coletor, nomeArquivo, new APIResponseAdapter<>(new APIResponseCallback<APIMensagem>() {

                    @Override
                    public void onSuccess(APIMensagem apiMensagem) {
                        Utils.toast(getContext(), apiMensagem.mensagem);
                        Utils.closeProgress();

                        limpaTela();
                    }

                    @Override
                    public void onFailure(String erro) {
                        Utils.toast(getContext(), erro);
                        Utils.closeProgress();
                    }
                }));
            });
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void enviaFtp(String nomeArquivo, int operacao) {
        String diretorioFtp = "";

        switch (operacao){
            case R.string.coletor_status_defeito_analise:
                diretorioFtp = "coletor_def/";
                break;
            case R.string.coletor_status_gato_pequeimado:
                diretorioFtp = "coletor_gto/";
                break;
            case R.string.coletor_status_etiqueta:
                diretorioFtp = "etiqueta/";
                break;
        }

        new FTPUtils(getContext()).enviaArquivoColetor(coletor.coletorProdutos, nomeArquivo, diretorioFtp, "Enviando arquivo dos produtos!", new FTPUtils.RetornoCallback() {

            @Override
            public void onSuccess() {
                Utils.toast(getContext(), "Arquivo '"+ nomeArquivo +".txt' enviado");
                limpaTela();
            }

            @Override
            public void onFailure(String mensagem) {
                Utils.toast(getContext(), mensagem);
            }
        });
    }
}