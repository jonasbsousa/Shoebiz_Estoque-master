package br.com.shoebiz.shoeconf_2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.activity.EntradaMercadoriaActivity;
import br.com.shoebiz.shoeconf_2.adapter.EntradaMercadoriaTabsAdapter;
import br.com.shoebiz.shoeconf_2.model.APIMensagem;
import br.com.shoebiz.shoeconf_2.model.Nota;
import br.com.shoebiz.shoeconf_2.service.EntradaMercadoriaService;
import br.com.shoebiz.shoeconf_2.fragment.dialog.IncluirChaveDialog;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class EntradaMercadoriaTabFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrada_mercadoria_tab, viewGroup, false);

        requireActivity().setTitle(R.string.nav_entrada_mercadori);

        ViewPager viewPager = view.findViewById(R.id.vpFornecedorDanfe);
        viewPager.setAdapter(new EntradaMercadoriaTabsAdapter(getContext(), getChildFragmentManager()));
        viewPager.setCurrentItem(1);

        TabLayout tabLayout = view.findViewById(R.id.tlFornecedorDanfe);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fabIncluiDanfe = view.findViewById(R.id.fabFornecedorDanfe);
        fabIncluiDanfe.setOnClickListener(v -> IncluirChaveDialog.show(getChildFragmentManager(), chave -> new EntradaMercadoriaService(getContext(), "C")
                .validaDanfe(chave, new EntradaMercadoriaService.ValidaDanfeCallback() {

            @Override
            public void onSuccess(APIMensagem apiMensagem) {
                consultaInfoDanfe(apiMensagem, chave);
            }

            @Override
            public void onFailure(String erro) {
                Utils.toast(getContext(), erro);
            }
        })));

        return view;
    }


    private void consultaInfoDanfe(APIMensagem apiMensagem, String chave) {
        Activity activity = getActivity();

        if (activity != null && isAdded()) {
            if (apiMensagem != null) {
                final String msgStatusDanfe = apiMensagem.mensagem;

                new EntradaMercadoriaService(getContext(), "C").consultaNota(chave, new EntradaMercadoriaService.ConsultaNotaCallback() {

                    @Override
                    public void onSuccess(Nota nota) {
                        if (nota != null) {
                            if (nota.notaProtheus.equals("N")) {
                                nota.liberacaoMensagem = msgStatusDanfe;

                                Intent intent = new Intent(getContext(), EntradaMercadoriaActivity.class);
                                intent.putExtra("nota", nota);
                                startActivity(intent);
                            } else {
                                Utils.toast(getContext(), "Essa nota já foi recebida!");
                            }
                        } else {
                            Utils.toast(getContext(), "Danfe com erro nas informações!");
                        }
                    }

                    @Override
                    public void onFailure(String erro) {
                        Utils.toast(getContext(), erro);
                    }
                });
            }
        }
    }
}