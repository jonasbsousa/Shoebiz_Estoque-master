package br.com.shoebiz.shoeconf_2.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.activity.CustomScannerActivity;
import br.com.shoebiz.shoeconf_2.adapter.DanfePedidoAdapter;
import br.com.shoebiz.shoeconf_2.fragment.dialog.AboutDialog;
import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Nota;
import br.com.shoebiz.shoeconf_2.service.EntradaMercadoriaService;
import br.com.shoebiz.shoeconf_2.utils.MaskUtils;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

import static br.com.shoebiz.shoeconf_2.utils.MaskUtils.MaskType.CNPJ;
import static br.com.shoebiz.shoeconf_2.utils.Utils.toast;

public class ValidaDanfeFragment extends BaseFragment {
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;
    private final String DANFE_KEY = "danfe";

    private Nota nota;

    private EditText etChaveDanfe;
    private NestedScrollView nscDescInfoDanfe;
    private TextView tvSemDanfe;
    private TextView tvLoja;
    private TextView tvNumNota;
    private TextView tvEmissao;
    private TextView tvCodFor;
    private TextView tvCnpj;
    private TextView tvFornecedor;
    private TextView tvChaveDanfe;
    private TextView tvRecebData;
    private TextView tvRecebHora;
    private TextView tvLibstatus;
    private TextView tvLibData;
    private TextView tvLibHora;
    private TextView tvLibUser;

    private RecyclerView rvPedidos;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_valida_danfe, container, false);

        requireActivity().setTitle(R.string.nav_valida_danfe);
        setHasOptionsMenu(true);

        nscDescInfoDanfe = view.findViewById(R.id.nscDescInfoDanfe);

        tvSemDanfe = view.findViewById(R.id.tvSemDanfe);
        tvLoja = view.findViewById(R.id.tvLoja);
        tvNumNota = view.findViewById(R.id.tvNumNota);
        tvEmissao = view.findViewById(R.id.tvEmissao);
        tvCodFor = view.findViewById(R.id.tvCodFor);
        tvCnpj = view.findViewById(R.id.tvCnpj);
        tvFornecedor = view.findViewById(R.id.tvFornecedor);
        tvChaveDanfe = view.findViewById(R.id.tvChaveDanfe);
        tvRecebData = view.findViewById(R.id.tvRecebData);
        tvRecebHora = view.findViewById(R.id.tvRecebHora);
        tvLibstatus = view.findViewById(R.id.tvLibstatus);
        tvLibData = view.findViewById(R.id.tvLibData);
        tvLibHora = view.findViewById(R.id.tvLibHora);
        tvLibUser = view.findViewById(R.id.tvLibUser);

        etChaveDanfe = view.findViewById(R.id.etChaveDanfe);
        etChaveDanfe.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                consultaChaveDanfe();
            }

            return false;
        });

        ImageButton ibPesquisaCamera = view.findViewById(R.id.ibPesquisaCamera);
        ibPesquisaCamera.setOnClickListener(v ->
                IntentIntegrator
                        .forSupportFragment(ValidaDanfeFragment.this)
                        .setDesiredBarcodeFormats(IntentIntegrator.CODE_128)
                        .setOrientationLocked(false)
                        .setCaptureActivity(CustomScannerActivity.class)
                        .setBeepEnabled(false)
                        .initiateScan()
        );

        rvPedidos = view.findViewById(R.id.rvPedidos);
        rvPedidos.setHasFixedSize(true);
        rvPedidos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPedidos.setItemAnimator(new DefaultItemAnimator());

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(DANFE_KEY, this.nota);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            this.nota = savedInstanceState.getParcelable(DANFE_KEY);

            showInfoDanfe(this.nota);
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
            etChaveDanfe.setText(result.getContents());
            consultaChaveDanfe();
        }
    }

    private void consultaChaveDanfe() {
        final String chaveDanfe = etChaveDanfe.getText().toString().trim();

        if ((chaveDanfe.length() == 44)) {
            Utils.showProgress(getContext(),"Atenção!", "Validando danfe!");

            new EntradaMercadoriaService(getContext(), "V").validaDanfe(chaveDanfe, new EntradaMercadoriaService.ValidaDanfeCallback() {

                @Override
                public void onSuccess(APIMensagem apiMensagem) {
                    Utils.closeProgress();
                    consultaInfoDanfe(apiMensagem, chaveDanfe);
                }

                @Override
                public void onFailure(String erro) {
                    Utils.closeProgress();
                    Utils.toast(getContext(), erro);
                }
            });
        } else {
            toast(getContext(), "Código da chave incompleto!");

            limpaTela();
        }
    }

    private void consultaInfoDanfe(APIMensagem apiMensagem, String chave) {
        if (apiMensagem != null) {
            final String msgStatusDanfe = apiMensagem.mensagem;

            Utils.showProgress(getContext(),"Atenção!", "Consultando dados da nota!");

            new EntradaMercadoriaService(getContext(), "").consultaNota(chave, new EntradaMercadoriaService.ConsultaNotaCallback() {
                @Override
                public void onSuccess(Nota nota) {
                    Utils.closeProgress();

                    if (nota != null) {
                        nota.liberacaoMensagem = msgStatusDanfe;
                    }

                    showInfoDanfe(nota);
                }

                @Override
                public void onFailure(String erro) {
                    Utils.closeProgress();
                    Utils.toast(getContext(), erro);
                }
            });
        } else {
            limpaTela();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void showInfoDanfe(Nota nota) {
        Activity activity = getActivity();

        if (activity != null){
            if (nota != null) {
                this.nota = nota;

                tvLoja.setText(nota.lojaDest);
                tvNumNota.setText(getString(R.string.frag_vd_desc, nota.notaNumero, nota.notaSerie));
                tvEmissao.setText(new SimpleDateFormat(Utils.formatoData()).format(nota.emissao));
                tvCodFor.setText(getString(R.string.frag_vd_desc, nota.fornecedorCod, nota.fornecedorLoja));
                tvCnpj.setText(MaskUtils.addMaskCnpj(nota.fornecedorCnpj, CNPJ));
                tvFornecedor.setText(nota.fornecedorDesc);
                tvChaveDanfe.setText(nota.chave);
                tvRecebData.setText(new SimpleDateFormat(Utils.formatoData()).format(nota.recebimentoData));
                tvRecebHora.setText(nota.recebimentoHora);
                tvLibstatus.setText(nota.liberacaoMensagem);
                tvLibData.setText(new SimpleDateFormat(Utils.formatoData()).format(nota.liberacaoData));
                tvLibHora.setText(nota.liberacaoHora);
                tvLibUser.setText(nota.liberacaoUser);

                tvSemDanfe.setVisibility(View.GONE);
                nscDescInfoDanfe.setVisibility(View.VISIBLE);

                rvPedidos.setAdapter(new DanfePedidoAdapter(getContext(), nota.notaPedidos));

                Bundle bundle = new Bundle();
                bundle.putString("Loja", new PrefsUtils(getContext()).isCodLoja());
                bundle.putString("Consulta", "Ok");
                mFirebaseAnalytics.logEvent("ValidaDanfe", bundle);

                etChaveDanfe.setText("");
                etChaveDanfe.requestFocus();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("Loja", new PrefsUtils(getContext()).isCodLoja());
                bundle.putString("Consulta", "nOk");
                mFirebaseAnalytics.logEvent("ValidaDanfe", bundle);

                limpaTela();
            }
        }
    }

    private void limpaTela() {
        this.nota = null;

        tvSemDanfe.setVisibility(View.VISIBLE);
        nscDescInfoDanfe.setVisibility(View.GONE);

        etChaveDanfe.setText("");
        etChaveDanfe.requestFocus();
    }
}