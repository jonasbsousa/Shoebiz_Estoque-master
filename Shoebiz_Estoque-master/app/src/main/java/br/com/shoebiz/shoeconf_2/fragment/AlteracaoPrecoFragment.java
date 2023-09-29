package br.com.shoebiz.shoeconf_2.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.activity.AlteracaoPrecoActivity;
import br.com.shoebiz.shoeconf_2.adapter.AlteracaoPrecoAdapter;
import br.com.shoebiz.shoeconf_2.fragment.dialog.AboutDialog;
import br.com.shoebiz.shoeconf_2.fragment.dialog.DataDeAteDialog;
import br.com.shoebiz.shoeconf_2.fragment.dialog.FiltroGruposDialog;
import br.com.shoebiz.shoeconf_2.model.AlteracaoPreco;
import br.com.shoebiz.shoeconf_2.model.Grupo;
import br.com.shoebiz.shoeconf_2.model.Produto;
import br.com.shoebiz.shoeconf_2.service.AlteracaoPrecoService;
import br.com.shoebiz.shoeconf_2.service.ProdutoService;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class AlteracaoPrecoFragment extends BaseFragment {
    private final String DATA_DE_ALTERACAO_KEY = "dataDeAlteracao";
    private final String DATA_ATE_ALTERACAO_KEY = "dataAteAlteracao";

    private List<Grupo> filtroGrupos;

    private String filtroAux;

    private Calendar dataDeAlteracao = Calendar.getInstance();
    private Calendar dataAteAlteracao = Calendar.getInstance();

    private RecyclerView rvAlteracaoPrecoPai;
    private RecyclerView rvAlteracaoPrecoCor;

    private AlteracaoPrecoAdapter alteracaoPrecoAdapterPai;
    private AlteracaoPrecoAdapter alteracaoPrecoAdapterCor;

    private EditText etDataDeAlteracao;
    private EditText etDataAteAlteracao;

    private TextView tvSemAlteracoesPai;
    private TextView tvSemAlteracoesCor;

    @Override
    @SuppressLint({"SimpleDateFormat"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alteracao_preco, container, false);

        requireActivity().setTitle(R.string.nav_alteracao_preco);
        setHasOptionsMenu(true);

        tvSemAlteracoesPai = view.findViewById(R.id.tvSemAlteracoesPai);
        tvSemAlteracoesCor = view.findViewById(R.id.tvSemAlteracoesCor);

        etDataDeAlteracao = view.findViewById(R.id.etDataDeAlteracao);
        etDataDeAlteracao.setText(new SimpleDateFormat(Utils.formatoData()).format(dataDeAlteracao.getTime()));

        etDataAteAlteracao = view.findViewById(R.id.etDataAteAlteracao);
        etDataAteAlteracao.setText(new SimpleDateFormat(Utils.formatoData()).format(dataAteAlteracao.getTime()));

        ImageButton ibCalendario = view.findViewById(R.id.ibCalendario);
        ibCalendario.setOnClickListener(v -> DataDeAteDialog.show(getChildFragmentManager(), dataDeAlteracao, dataAteAlteracao, (dataDe, dataAte) -> {
            dataDeAlteracao = dataDe;
            dataAteAlteracao = dataAte;

            etDataDeAlteracao.setText(new SimpleDateFormat(Utils.formatoData()).format(dataDeAlteracao.getTime()));
            etDataAteAlteracao.setText(new SimpleDateFormat(Utils.formatoData()).format(dataAteAlteracao.getTime()));

            alteracoesPreco(dataDeAlteracao.getTime(), dataAteAlteracao.getTime());
        }));

        rvAlteracaoPrecoPai = view.findViewById(R.id.rvAlteracaoPrecoPai);
        rvAlteracaoPrecoPai.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAlteracaoPrecoPai.setItemAnimator(new DefaultItemAnimator());
        rvAlteracaoPrecoPai.setHasFixedSize(true);

        rvAlteracaoPrecoCor = view.findViewById(R.id.rvAlteracaoPrecoCor);
        rvAlteracaoPrecoCor.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAlteracaoPrecoCor.setItemAnimator(new DefaultItemAnimator());
        rvAlteracaoPrecoCor.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(DATA_DE_ALTERACAO_KEY, this.dataDeAlteracao);
        outState.putSerializable(DATA_ATE_ALTERACAO_KEY, this.dataAteAlteracao);

        super.onSaveInstanceState(outState);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            dataDeAlteracao = (Calendar) savedInstanceState.getSerializable(DATA_DE_ALTERACAO_KEY);
            dataAteAlteracao = (Calendar) savedInstanceState.getSerializable(DATA_ATE_ALTERACAO_KEY);
        } else {
            dataDeAlteracao.add(Calendar.DAY_OF_MONTH, -1);
            dataAteAlteracao.add(Calendar.DAY_OF_MONTH, -1);
        }

        alteracoesPreco(dataDeAlteracao.getTime(), dataAteAlteracao.getTime());

        etDataDeAlteracao.setText(new SimpleDateFormat(Utils.formatoData()).format(dataDeAlteracao.getTime()));
        etDataAteAlteracao.setText(new SimpleDateFormat(Utils.formatoData()).format(dataAteAlteracao.getTime()));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_alteracao_preco, menu);
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                if (this.filtroGrupos != null && this.filtroGrupos.size() > 0) {
                    FiltroGruposDialog.show(getChildFragmentManager(), filtroGrupos, filtroAux, grupoFiltro -> {
                        snackFiltro(grupoFiltro, true);
                        aplicaFiltro(grupoFiltro);
                    });
                } else {
                    Utils.toast(getContext(), "Sem produtos para filtrar!");
                }

                break;
            case R.id.action_about:
                AboutDialog.showAbout(getChildFragmentManager());

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlteracaoPrecoAdapter.AlteracaoOnClickListener onClickNota() {
        return (view, idx, isAlteracaoCor) -> {
            Produto produto;

            if (isAlteracaoCor) {
                produto = alteracaoPrecoAdapterCor.getListProduto().get(idx);
            } else {
                produto = alteracaoPrecoAdapterPai.getListProduto().get(idx);
            }

            Utils.showProgress(getContext(),"Atenção!", "Consultando Produto");

            new ProdutoService(getContext(), "", "").consultaProdutoV2(isAlteracaoCor ? produto.codigoPai.substring(0, 9) : produto.codigoPai,
                    new ProdutoService.ConsultaProdutoCallback() {

                @Override
                public void onSuccess(Produto produto) {
                    Utils.closeProgress();

                    visualizaproduto(produto);
                }

                @Override
                public void onFailure(String erro) {
                    Utils.closeProgress();
                    Utils.toast(getContext(), erro);
                }
            });
        };
    }

    @SuppressLint("SimpleDateFormat")
    private void  alteracoesPreco(Date dataDe, Date dateAte) {
        new AlteracaoPrecoService(getContext()).consultaAlteracaoPrecoAPI(new SimpleDateFormat(Utils.formatoDataSql()).format(dataDe),
                new SimpleDateFormat(Utils.formatoDataSql()).format(dateAte), this::showAlteracoesPreco);
    }

    private void visualizaproduto(Produto produto) {
        Intent intent = new Intent(getContext(), AlteracaoPrecoActivity.class);
        intent.putExtra("produto", produto);
        startActivity(intent);
    }

    private void showAlteracoesPreco(AlteracaoPreco alteracaoPreco) {
        List<Grupo> grupos = new ArrayList<>();
        boolean inclui = true;

        if (alteracaoPreco != null && (
                (alteracaoPreco.produtosCor != null && alteracaoPreco.produtosCor.size() > 0) ||
                (alteracaoPreco.produtosPai != null && alteracaoPreco.produtosPai.size() > 0)
        )) {

            for (Produto produto : alteracaoPreco.produtosPai) {
                for (Grupo grupo : grupos) {
                    if (grupo.descricao.trim().equals(produto.grupo.descricao.trim())) {
                        inclui = false;
                        break;
                    }
                }

                if (inclui) {
                    grupos.add(produto.grupo);
                }

                inclui = true;
            }

            for (Produto produto : alteracaoPreco.produtosCor) {
                for (Grupo grupo : grupos) {
                    if (grupo.descricao.trim().equals(produto.grupo.descricao.trim())) {
                        inclui = false;
                        break;
                    }
                }

                if (inclui) {
                    grupos.add(produto.grupo);
                }

                inclui = true;
            }

            this.filtroGrupos = grupos;

            snackFiltro("TODOS", true);

            if (alteracaoPreco.produtosPai.size() > 0) {
                tvSemAlteracoesPai.setVisibility(View.GONE);
                alteracaoPrecoAdapterPai = new AlteracaoPrecoAdapter(getContext(), alteracaoPreco.produtosPai, onClickNota(), false);

                rvAlteracaoPrecoPai.setAdapter(alteracaoPrecoAdapterPai);
                rvAlteracaoPrecoPai.setVisibility(View.VISIBLE);
            } else {
                tvSemAlteracoesPai.setVisibility(View.VISIBLE);
                rvAlteracaoPrecoPai.setVisibility(View.GONE);
            }

            if (alteracaoPreco.produtosCor.size() > 0) {
                tvSemAlteracoesCor.setVisibility(View.GONE);
                alteracaoPrecoAdapterCor = new AlteracaoPrecoAdapter(getContext(), alteracaoPreco.produtosCor, onClickNota(), true);

                rvAlteracaoPrecoCor.setAdapter(alteracaoPrecoAdapterCor);
                rvAlteracaoPrecoCor.setVisibility(View.VISIBLE);
            } else {
                tvSemAlteracoesCor.setVisibility(View.VISIBLE);
                rvAlteracaoPrecoCor.setVisibility(View.GONE);
            }
        } else {
            this.filtroGrupos = null;

            snackFiltro("", false);

            tvSemAlteracoesPai.setVisibility(View.VISIBLE);
            tvSemAlteracoesCor.setVisibility(View.VISIBLE);
        }
    }

    private void snackFiltro(String filtro, boolean open) {
        this.filtroAux = filtro;

        if (getView() != null) {
            Snackbar.make(getView(), "Filtro: " + this.filtroAux, open ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_SHORT).show();
        }
    }

    private void aplicaFiltro(String grupoFiltro) {
        if (alteracaoPrecoAdapterPai != null) {
            alteracaoPrecoAdapterPai.getFilter().filter(grupoFiltro);
        }

        if (alteracaoPrecoAdapterCor != null) {
            alteracaoPrecoAdapterCor.getFilter().filter(grupoFiltro);
        }
    }
}