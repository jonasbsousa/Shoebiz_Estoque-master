package br.com.shoebiz.shoeconf_2.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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
import br.com.shoebiz.shoeconf_2.adapter.EntradaMercadoriaAdapter;
import br.com.shoebiz.shoeconf_2.fragment.dialog.AboutDialog;
import br.com.shoebiz.shoeconf_2.fragment.dialog.OcorrenciaDialog;
import br.com.shoebiz.shoeconf_2.model.Nota;
import br.com.shoebiz.shoeconf_2.model.Ocorrencia;
import br.com.shoebiz.shoeconf_2.model.TipoOcorrencia;
import br.com.shoebiz.shoeconf_2.service.EntradaMercadoriaService;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class EntradaMercadoriaFragment extends BaseFragment {
    private final String NOTAS_KEY = "notas";
    private final String STATUS_KEY = "status";

    private List<Nota> notas;
    private int status;

    private RecyclerView rvNotas;
    private TextView tvSemNotas;

    private SwipeRefreshLayout srlNotas;

    public static EntradaMercadoriaFragment newInstance(int status) {
        Bundle args = new Bundle();
        args.putInt("status", status);
        EntradaMercadoriaFragment entradaMercadoriaFragment = new EntradaMercadoriaFragment();
        entradaMercadoriaFragment.setArguments(args);
        return entradaMercadoriaFragment;
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
        View view = inflater.inflate(R.layout.fragment_entrada_mercadoria, container, false);

        ProgressBar progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.GONE);

        rvNotas = view.findViewById(R.id.rvNotas);
        rvNotas.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvNotas.setItemAnimator(new DefaultItemAnimator());
        rvNotas.setHasFixedSize(true);

        tvSemNotas = view.findViewById(R.id.tvSemNotas);

        srlNotas = view.findViewById(R.id.srNotas);
        srlNotas.setOnRefreshListener(OnRefreshListener());

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(NOTAS_KEY, (ArrayList<? extends Parcelable>) this.notas);
        outState.putSerializable(STATUS_KEY, this.status);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            this.notas = savedInstanceState.getParcelableArrayList(NOTAS_KEY);
            this.status = (int) savedInstanceState.getSerializable(STATUS_KEY);
        }

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
    public void onBusAtualizaListaNotas(String refreshEntradaMercadoria) {
        buscaListaNotas(false);
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return () -> buscaListaNotas(true);
    }

    private EntradaMercadoriaAdapter.DanfeOnClickListener onClickDanfe() {
        return (view, idx) -> {
            final Nota nota = notas.get(idx);

            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            Menu menu = popupMenu.getMenu();
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_danfe, menu);

            if (!nota.status.equals(Utils.getStatusNota(R.string.status_nota_finalizado))) {
                menu.removeItem(R.id.menu_danfe_ocorrencia);
            }

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_danfe_ocorrencia) {
                    ocorrencia(nota);
                }

                return false;
            });

            popupMenu.show();
        };
    }

    private void buscaListaNotas(final boolean swipeRefresh){
        if (!swipeRefresh) {
            Utils.showProgress(getContext(),"Atenção!", "Consultando notas");
        }

        new EntradaMercadoriaService(getContext(), "").consultaNotas(Utils.getStatusNota(this.status), new EntradaMercadoriaService.ConsultaNotasCallback() {

            @Override
            public void onSuccess(List<Nota> notas) {
                onFinish(notas);
            }

            @Override
            public void onFailure(String erro) {
                Utils.toast(getContext(), erro);

                onFinish(null);
            }

            @Override
            public void onFinish(List<Nota> notas) {
                if (swipeRefresh) {
                    srlNotas.setRefreshing(false);
                } else {
                    Utils.closeProgress();
                }

                atualizaLista(notas);
            }
        });
    }

    private void atualizaLista(List<Nota> notas) {
        Activity activity = getActivity();

        if (activity != null && isAdded()) {
            if (notas != null) {
                this.notas = notas;
            } else {
                this.notas = new ArrayList<>();
            }

            if (this.notas.size() > 0) {
                tvSemNotas.setText("");
            } else {
                tvSemNotas.setText(getString(R.string.rv_textoSemNota, getString(this.status).trim()));
            }

            rvNotas.setAdapter(new EntradaMercadoriaAdapter(getContext(), notas, onClickDanfe()));
        }
    }

    private void ocorrencia(final Nota nota) {
        Utils.showProgress(getContext(), "Aguarde...", "Consultando tipos de ocorrência!");

        new EntradaMercadoriaService(getContext(), "").consutaTipoOcorrencia(new EntradaMercadoriaService.ConsultaTipoOcorrenciaCallback() {

            @Override
            public void onSuccess(List<TipoOcorrencia> tiposOcorrencia) {
                Utils.closeProgress();
                abreDialogOcorrencia(tiposOcorrencia, nota);
            }

            @Override
            public void onFailure(String erro) {
                Utils.closeProgress();
                Utils.toast(getContext(), erro);
            }
        });
    }

    private void abreDialogOcorrencia(List<TipoOcorrencia> tiposOcorrencia, Nota nota) {
        Activity activity = getActivity();

        if (activity != null && isAdded()) {
            OcorrenciaDialog.show(getChildFragmentManager(), tiposOcorrencia, nota, this::enviaOcorrencia);
        }
    }

    private void enviaOcorrencia(Ocorrencia ocorrencia) {
        Utils.showProgress(getContext(), "Aguarde...", "Enviando ocorrencia!");

        new EntradaMercadoriaService(getContext(), "").enviaOcorrencia(ocorrencia, new EntradaMercadoriaService.EnviaCallback() {

            @Override
            public void onSuccess(String mensagem) {
                Utils.closeProgress();

                Utils.toast(getContext(), mensagem);

                Bundle bundle = new Bundle();
                bundle.putString("Loja", new PrefsUtils(getContext()).isCodLoja());
                mFirebaseAnalytics.logEvent("Ocorrencia", bundle);
            }

            @Override
            public void onFailure(String erro) {
                Utils.closeProgress();
                Utils.toast(getContext(), erro);
            }
        });
    }
}