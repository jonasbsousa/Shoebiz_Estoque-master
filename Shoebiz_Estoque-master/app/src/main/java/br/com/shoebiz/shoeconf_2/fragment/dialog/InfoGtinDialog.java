package br.com.shoebiz.shoeconf_2.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.adapter.GradeAdapter;
import br.com.shoebiz.shoeconf_2.adapter.GtinAdapter;
import br.com.shoebiz.shoeconf_2.model.Cor;
import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class InfoGtinDialog extends DialogFragment {

    public static void show(FragmentManager fragmentManager, Cor cor) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("dialog_info");

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable("cor", cor);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        InfoGtinDialog frag = new InfoGtinDialog();
        frag.setArguments(bundle);
        frag.show(fragmentManager, "dialog_info");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog_Shoebiz);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.Slide);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_info_gtin, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.frag_vd_grade_produto));
        toolbar.setNavigationIcon(R.drawable.ic_clear_white);
        toolbar.setNavigationOnClickListener(view1 -> dismiss());

        int qntPares = 0;

        TextView tvDescCorErp = view.findViewById(R.id.tvDescCorErp);
        TextView tvDescCorForn = view.findViewById(R.id.tvDescCorForn);
        TextView tvQuantTotal = view.findViewById(R.id.tvQuantTotal);

        RecyclerView rvGrade = view.findViewById(R.id.rvGrade);
        RecyclerView rvGtin = view.findViewById(R.id.rvGtin);

        if (getArguments() != null) {
            Cor cor = getArguments().getParcelable("cor");

            if (cor != null) {
                tvDescCorErp.setText(getString(R.string.frag_cp_descCor, cor.codigo, cor.descricaoErp));
                tvDescCorForn.setText(cor.descricaoFornecedor);

                for(ProdutoFilho produtoFilho : cor.produtoFilho) {
                    qntPares += produtoFilho.quantidade;
                }

                tvQuantTotal.setText(String.valueOf(qntPares));

                rvGrade.setHasFixedSize(true);
                rvGrade.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                rvGrade.setItemAnimator(new DefaultItemAnimator());
                rvGrade.setAdapter(new GradeAdapter(getContext(), cor.produtoFilho));

                rvGtin.setHasFixedSize(true);
                rvGtin.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                rvGtin.setItemAnimator(new DefaultItemAnimator());
                rvGtin.setAdapter(new GtinAdapter(getContext(), cor.produtoFilho));
            } else {
                Utils.toast(getContext(), "Problema ao carregar a grade da cor!");

                dismiss();
            }
        } else {
            Utils.toast(getContext(), "Problema ao carregar a grade da cor!");

            dismiss();
        }

        return view;
    }
}