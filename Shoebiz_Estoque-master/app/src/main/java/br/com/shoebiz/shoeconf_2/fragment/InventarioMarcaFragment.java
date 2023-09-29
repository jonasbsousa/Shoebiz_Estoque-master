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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.fragment.dialog.AboutDialog;
import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;
import br.com.shoebiz.shoeconf_2.utils.FTPUtils;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class InventarioMarcaFragment extends BaseFragment {
    private List<ProdutoFilho> produtosFilho;
    private int qntProdutos = 0;

    private EditText etCodigoProduto;
    private TextView tvQuantProd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario_marca, container, false);

        requireActivity().setTitle(R.string.nav_inventario_marca);
        setHasOptionsMenu(true);

        produtosFilho = new ArrayList<>();

        tvQuantProd = view.findViewById(R.id.tvQuantProd);

        etCodigoProduto = view.findViewById(R.id.etCodigoProduto);
        etCodigoProduto.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                String codDigitado = etCodigoProduto.getText().toString().trim();
                boolean inclui = true;

                if (codDigitado.length() == 12 || codDigitado.length() == 13 || codDigitado.length() == 14) {
                    switch (codDigitado.length()) {
                        case 12:
                            codDigitado = "0" + codDigitado;
                            break;
                        case 14:
                            codDigitado = codDigitado.substring(1);
                            break;
                    }

                    for (ProdutoFilho produtoFilhoArr : produtosFilho) {
                        if (produtoFilhoArr.codigo.toUpperCase().trim().equals(codDigitado)) {
                            produtoFilhoArr.quantidade++;

                            inclui = false;
                        }
                    }

                    if (inclui) {
                        ProdutoFilho produtoFilhoAux = new ProdutoFilho();
                        produtoFilhoAux.id = 0;
                        produtoFilhoAux.codigo = codDigitado;
                        produtoFilhoAux.quantidade = 1;

                        produtosFilho.add(produtoFilhoAux);
                    }

                    qntProdutos++;
                    tvQuantProd.setText(String.valueOf(qntProdutos));
                } else {
                    Utils.toast(getContext(),"Código '" + codDigitado + "' inválido!");
                }

                etCodigoProduto.setText("");
                etCodigoProduto.requestFocus();
            }

            return false;
        });


        Button bLimpar = view.findViewById(R.id.bLimpar);
        bLimpar.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Atenção")
                .setMessage("Confirma a limpeza da lista de produtos?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", (dialog, which) -> {
                    limpaProdutos();

                    Utils.toast(getContext(), "Lista de produtos excluída!");
                }).show());

        Button bEnviar = view.findViewById(R.id.bEnviar);
        bEnviar.setOnClickListener(v -> {
            if (produtosFilho.size() > 0) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Atenção!")
                        .setMessage("Confirma o envio do arquivo?")
                        .setNegativeButton("Não", null)
                        .setPositiveButton("Sim", (dialog, which) -> enviaArquivo(produtosFilho)).show();
            } else {
                Utils.toast(getContext(), "Sem produtos coletados!");
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

    private void limpaProdutos() {
        produtosFilho = new ArrayList<>();

        etCodigoProduto.setText("");
        etCodigoProduto.requestFocus();

        qntProdutos = 0;

        tvQuantProd.setText(String.valueOf(qntProdutos));
    }

    private void enviaArquivo(List<ProdutoFilho> produtosFilho) {
        String nomeArquivo = new PrefsUtils(getContext()).isCodLoja() + "_" + Utils.getDataAtualSQL_2() + "_" + Utils.getHoraAtualSQL() + ".txt";

        new FTPUtils(getContext()).enviaArquivoInventarioMarca(produtosFilho, nomeArquivo, "Enviando arquivo dos produtos!", new FTPUtils.RetornoCallback() {

            @Override
            public void onSuccess() {
                Utils.toast(getContext(), "Arquivo enviado");
                limpaProdutos();
            }

            @Override
            public void onFailure(String mensagem) {
                Utils.toast(getContext(), mensagem);
            }
        });
    }
}