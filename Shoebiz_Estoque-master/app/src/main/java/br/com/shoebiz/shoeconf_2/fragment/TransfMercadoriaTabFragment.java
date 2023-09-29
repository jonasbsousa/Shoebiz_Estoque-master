package br.com.shoebiz.shoeconf_2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.adapter.TransfMercadoriaTabsAdapter;
import br.com.shoebiz.shoeconf_2.adapter.TransfOtTabAdapter;

public class TransfMercadoriaTabFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_transf_mercadoria_tab, container, false);

        requireActivity().setTitle(R.string.nav_transf_mercadoria);

        TabLayout tlTransfMercadoriaTipo = view.findViewById(R.id.tlTransfMercadoriaTipo);
        tlTransfMercadoriaTipo.addTab(tlTransfMercadoriaTipo.newTab().setText("Ot"));
        tlTransfMercadoriaTipo.addTab(tlTransfMercadoriaTipo.newTab().setText("Nota"));
        tlTransfMercadoriaTipo.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                carregaMenu(view, tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        carregaMenu(view, 0);

        return view;
    }

    private void carregaMenu(View view, int posicao) {
        if (view != null) {
            ViewPager vpTransfMercadoriaOt = view.findViewById(R.id.vpTransfMercadoriaOt);
            ViewPager vpTransfMercadoriaNota = view.findViewById(R.id.vpTransfMercadoriaNota);
            TabLayout tlTransfMercadoriaStatus = view.findViewById(R.id.tlTransfMercadoriaStatus);

            if (posicao == 0) {
                vpTransfMercadoriaNota.setVisibility(View.GONE);

                vpTransfMercadoriaOt.setAdapter(new TransfOtTabAdapter(getContext(), getChildFragmentManager()));
                tlTransfMercadoriaStatus.setupWithViewPager(vpTransfMercadoriaOt);

                vpTransfMercadoriaOt.setVisibility(View.VISIBLE);
            } else {
                vpTransfMercadoriaOt.setVisibility(View.GONE);

                vpTransfMercadoriaNota.setAdapter(new TransfMercadoriaTabsAdapter(getContext(), getChildFragmentManager()));
                vpTransfMercadoriaNota.setCurrentItem(1);
                tlTransfMercadoriaStatus.setupWithViewPager(vpTransfMercadoriaNota);

                vpTransfMercadoriaNota.setVisibility(View.VISIBLE);
            }
        }
    }
}