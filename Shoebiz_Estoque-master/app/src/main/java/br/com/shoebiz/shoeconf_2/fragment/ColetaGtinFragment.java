package br.com.shoebiz.shoeconf_2.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.Produto;
import br.com.shoebiz.shoeconf_2.service.ProdutoService;
import br.com.shoebiz.shoeconf_2.fragment.dialog.AboutDialog;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class ColetaGtinFragment extends BaseFragment {

    private EditText etCodigoProduto;
    private EditText etCodigoGtin;

    private TextView tvDescricao;

    private CardView cvInformacoes;

    private Button bGravar;
    private Button bLimpar;

    private ImageView ivFotoProduto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coleta_gtin, container, false);

        requireActivity().setTitle(R.string.nav_coleta_gtin);
        setHasOptionsMenu(true);

        etCodigoProduto = view.findViewById(R.id.etCodigoProduto);
        etCodigoGtin = view.findViewById(R.id.etCodigoGtin);

        tvDescricao = view.findViewById(R.id.tvDescricao);

        cvInformacoes = view.findViewById(R.id.cvInformacoes);

        bGravar = view.findViewById(R.id.bGravar);
        bLimpar = view.findViewById(R.id.bLimpar);

        ivFotoProduto = view.findViewById(R.id.ivFotoProduto);

        etCodigoProduto.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String codigoProduto = etCodigoProduto.getText().toString();

                if (codigoProduto.length() == 12 || codigoProduto.length() == 13 || codigoProduto.length() == 14) {
                    Utils.showProgress(getContext(),"Atenção!", "Consultando Produto");

                    switch (codigoProduto.length()) {
                        case 12:
                            codigoProduto = "0" + codigoProduto;
                            break;
                        case 14:
                            codigoProduto = codigoProduto.substring(1);
                            break;
                    }

                    final String finalCodigoProduto = codigoProduto;
                    new ProdutoService(getContext(), "", "").consultaProduto(codigoProduto, new ProdutoService.ConsultaProdutoCallback() {

                        @Override
                        public void onSuccess(Produto produto) {
                            Utils.closeProgress();

                            if (produto != null) {
                                etCodigoProduto.setText(finalCodigoProduto);
                                etCodigoProduto.setEnabled(false);

                                etCodigoGtin.setEnabled(true);
                                etCodigoGtin.requestFocus();

                                cvInformacoes.setVisibility(View.VISIBLE);

                                tvDescricao.setText(produto.descricao);

                                bLimpar.setVisibility(View.VISIBLE);

                                Glide.with(requireContext())
                                        .load(Utils.getUrlFotoProduto() + produto.codigoPai.substring(1) + ".JPG")
                                        .apply(new RequestOptions()
                                                .error(R.drawable.sem_foto)
                                                .placeholder(R.drawable.sem_foto)
                                                .fitCenter()
                                                .override(800, 400))
                                        .into(ivFotoProduto);
                            } else {
                                Utils.toast(getContext(), "Problema ao consultar produto!");
                            }
                        }

                        @Override
                        public void onFailure(String erro) {
                            Utils.closeProgress();
                            Utils.toast(getContext(), erro);
                        }
                    });
                } else {
                    etCodigoProduto.setText("");
                    Utils.toast(getContext(), "Código inválido!");
                }
            }

            return false;
        });

        etCodigoGtin.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                bGravar.setVisibility(View.VISIBLE);
            }

            return false;
        });

        bLimpar.setOnClickListener(v -> limpaTela());

        bGravar.setOnClickListener(v -> {
            final String codProduto = etCodigoProduto.getText().toString().trim();
            final String codGtin = etCodigoGtin.getText().toString().trim();

            new AlertDialog.Builder(requireContext())
                    .setTitle("Atenção")
                    .setMessage("Confirma a gravação do código GTIN?")
                    .setNegativeButton("Não", null)
                    .setPositiveButton("Sim", (dialog, which) -> {
                        Utils.showProgress(getContext(),"Atenção!", "Gravando código EAN");

                        new ProdutoService(getContext(), codProduto, codGtin).gravaCodigoGtin(new ProdutoService.EnviaCallBack() {

                            @Override
                            public void onSuccess(String mensagem) {
                                Utils.closeProgress();
                                Utils.toast(getContext(), mensagem);

                                limpaTela();
                            }

                            @Override
                            public void onFailure(String erro) {
                                Utils.closeProgress();
                                Utils.toast(getContext(), erro);
                            }
                        });
                    }).show();
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

    private void limpaTela() {
        etCodigoProduto.setText("");
        etCodigoProduto.setEnabled(true);
        etCodigoProduto.requestFocus();

        etCodigoGtin.setText("");
        etCodigoGtin.setEnabled(false);

        cvInformacoes.setVisibility(View.GONE);
        bLimpar.setVisibility(View.GONE);
        bGravar.setVisibility(View.GONE);
    }
}