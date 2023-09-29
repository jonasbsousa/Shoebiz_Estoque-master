package br.com.shoebiz.shoeconf_2.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.ShoebizApplication;
import br.com.shoebiz.shoeconf_2.activity.ColetaOtActivity;
import br.com.shoebiz.shoeconf_2.activity.EntradaTransfActivity;
import br.com.shoebiz.shoeconf_2.adapter.TransfMercadoriaAdapter;
import br.com.shoebiz.shoeconf_2.model.TransfNota;
import br.com.shoebiz.shoeconf_2.service.TransfMercadoriaService;
import br.com.shoebiz.shoeconf_2.fragment.dialog.AboutDialog;
import br.com.shoebiz.shoeconf_2.fragment.dialog.IncluirChaveDialog;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class TransfMercadoriaFragment extends BaseFragment {
    private List<TransfNota> transfNotas;
    private int status;

    private RecyclerView rvTransferencia;
    private TextView tvSemTransferencia;
    private SwipeRefreshLayout srlNotasTransf;

    public static TransfMercadoriaFragment newInstance(int status) {
        Bundle args = new Bundle();
        args.putInt("status", status);
        TransfMercadoriaFragment fragment = new TransfMercadoriaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.status = getArguments().getInt("status");
        }

        setHasOptionsMenu(true);

        ShoebizApplication.getInstance().getBus().register(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transf_mercadoria, container, false);

        rvTransferencia = view.findViewById(R.id.rvTransferencia);
        rvTransferencia.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTransferencia.setItemAnimator(new DefaultItemAnimator());
        rvTransferencia.setHasFixedSize(true);
        rvTransferencia.setAdapter(new TransfMercadoriaAdapter(getContext(), new ArrayList<>(), onClickTransf()));

        tvSemTransferencia = view.findViewById(R.id.tvSemTransferencia);

        srlNotasTransf = view.findViewById(R.id.srlNotasTransf);
        srlNotasTransf.setOnRefreshListener(OnRefreshListener());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        buscaListaNotas(false);
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
    public void onDestroy() {
        super.onDestroy();

        ShoebizApplication.getInstance().getBus().unregister(this);
    }

    @Subscribe
    public void onBusAtualizaListanotas(String refresh) {
        buscaListaNotas(false);
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return () -> buscaListaNotas(true);
    }

    private void buscaListaNotas(final boolean swipeRefresh) {
        if (Utils.getTipoOpTransf(status).equals(String.valueOf(Utils.OP_TRANSF_NOTA))) {
            if (!swipeRefresh) {
                Utils.showProgress(getContext(),"Atenção!", "Consultando notas de transferência");
            }

            new TransfMercadoriaService(getContext(), "", "")
                    .consultaNotasAPI(Utils.getStatusTransf(status), new TransfMercadoriaService.ConsultaNotasCallback() {

                @Override
                public void onSuccess(List<TransfNota> transfNotas) {
                    onFinishi(transfNotas);
                }

                @Override
                public void onFailure(String erro) {
                    Utils.toast(getContext(), erro);

                    onFinishi(null);
                }

                private void onFinishi(List<TransfNota> transfNotas) {
                    if (swipeRefresh) {
                        srlNotasTransf.setRefreshing(false);
                    } else {
                        Utils.closeProgress();
                    }

                    atualizaLista(transfNotas);
                }
            });
        } else if (Utils.getTipoOpTransf(status).equals(String.valueOf(Utils.OP_TRANSF_OT))) {
            if (!swipeRefresh) {
                Utils.showProgress(getContext(),"Atenção!", "Consultando ots de transferência");
            }

            new TransfMercadoriaService(getContext(), "", "")
                    .consultaOtsAPI(Utils.getStatusOt(this.status), new TransfMercadoriaService.ConsultaNotasCallback() {

                @Override
                public void onSuccess(List<TransfNota> transfNotas) {
                    onFinishi(transfNotas);
                }

                @Override
                public void onFailure(String erro) {
                    Utils.toast(getContext(), erro);

                    onFinishi(null);
                }

                private void onFinishi(List<TransfNota> transfNotas) {
                    if (swipeRefresh) {
                        srlNotasTransf.setRefreshing(false);
                    } else {
                        Utils.closeProgress();
                    }

                    atualizaLista(transfNotas);
                }
            });
        } else {
            Utils.toast(getContext(), "Problema ao consulta nota!");
        }
    }


    private String textoSem() {
        String retorno = "";

        if (Utils.getTipoOpTransf(this.status).equals(String.valueOf(Utils.OP_TRANSF_OT))) {
            retorno = getString(R.string.rv_textoSemOt, getString(this.status));
        } else if (Utils.getTipoOpTransf(this.status).equals(String.valueOf(Utils.OP_TRANSF_NOTA))) {
            retorno = getString(R.string.rv_textoSemNota, getString(this.status));
        }

        return retorno;
    }

    private void atualizaLista(List<TransfNota> transfNotas) {
        Activity activity = getActivity();

        if (activity != null && isAdded()) {
            if (transfNotas != null) {
                this.transfNotas = transfNotas;
            } else {
                this.transfNotas = new ArrayList<>();
            }

            if (this.transfNotas.size() > 0) {
                tvSemTransferencia.setText("");
            } else {
                tvSemTransferencia.setText(textoSem());
            }

            rvTransferencia.setAdapter(new TransfMercadoriaAdapter(getContext(), this.transfNotas, onClickTransf()));
        }
    }

    private void validaChave(String chave, TransfNota transfNota) {
        if (transfNota.chave.equals(chave)) {
            Utils.showProgress(getContext(),"Atenção!", "Consultando nota");

            new TransfMercadoriaService(getContext(), transfNota.codLojaOrigem, "").consultaNotaAPI(chave, new TransfMercadoriaService.ConsultaNotaCallback() {

                @Override
                public void onSuccess(TransfNota transfNota) {
                    Utils.closeProgress();

                    if (transfNota != null) {
                        Intent intent = new Intent(getContext(), EntradaTransfActivity.class);
                        intent.putExtra("nota", transfNota);
                        startActivity(intent);
                    } else {
                        Utils.toast(getContext(), "Erro ao consultar a nota!");
                    }
                }

                @Override
                public void onFailure(String erro) {
                    Utils.closeProgress();
                    Utils.toast(getContext(), erro);
                }
            });
        } else {
            Utils.toast(getContext(), "Chave incorreta!");
        }
    }

    private void consultaOt(TransfNota transfNota) {
        Utils.showProgress(getContext(),"Atenção!", "Consultando Ot");

        new TransfMercadoriaService(getContext(), "", "").consultaOtAPI(transfNota.otNumero, new TransfMercadoriaService.ConsultaNotaCallback() {

            @Override
            public void onSuccess(TransfNota transfNota) {
                Utils.closeProgress();

                if (transfNota != null) {
                    Intent intent = new Intent(getContext(), ColetaOtActivity.class);
                    intent.putExtra("ot", transfNota);
                    startActivity(intent);
                } else {
                    Utils.toast(getContext(), "Erro ao consultar a ot!");
                }
            }

            @Override
            public void onFailure(String erro) {
                Utils.closeProgress();
                Utils.toast(getContext(), erro);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private TransfMercadoriaAdapter.TransfOnClickListener onClickTransf() {
        return (view, idx) -> {
            final TransfNota transfNota = transfNotas.get(idx);

            String tipoOperacao = Utils.getTipoOpTransf(transfNota.status);

            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            Menu menu = popupMenu.getMenu();
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_transf, menu);

            if (tipoOperacao.equals(String.valueOf(Utils.OP_TRANSF_OT))) {
                menu.removeItem(R.id.menu_transf_receber);

                if (!transfNota.status.equals(Utils.getStatusOt(R.string.status_transf_ot_aberto))) {
                    menu.removeItem(R.id.menu_ot_coletar);
                }
            } else if (tipoOperacao.equals(String.valueOf(Utils.OP_TRANSF_NOTA))) {
                menu.removeItem(R.id.menu_ot_coletar);

                if (!transfNota.status.equals(Utils.getStatusTransf(R.string.status_transf_pendente)) || (transfNota.statusProc.equals("P") || transfNota.statusProc.equals("E"))) {
                    menu.removeItem(R.id.menu_transf_receber);
                }
            }

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.menu_transf_receber:
                        IncluirChaveDialog.show(getChildFragmentManager(), chave -> validaChave(chave, transfNota));

                        break;
                    case R.id.menu_ot_coletar:
                        consultaOt(transfNota);

                        break;
                }

                return false;
            });

            popupMenu.show();
        };
    }
}