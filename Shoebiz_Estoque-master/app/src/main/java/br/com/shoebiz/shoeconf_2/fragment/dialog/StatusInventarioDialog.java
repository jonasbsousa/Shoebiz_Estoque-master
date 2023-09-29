package br.com.shoebiz.shoeconf_2.fragment.dialog;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.dao.InventarioDao;
import br.com.shoebiz.shoeconf_2.utils.PrefsUtils;

public class StatusInventarioDialog extends DialogFragment {

    @SuppressLint("CommitTransaction")
    public static void show(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("status_inventario");

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        fragmentTransaction.addToBackStack(null);
        StatusInventarioDialog frag = new StatusInventarioDialog();
        frag.show(fragmentManager, "status_inventario");
    }

    @Override
    public void onResume() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();

        if (window != null) {
            Point size = new Point();

            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);

            window.setLayout(size.x, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_status_inventario, container, false);

        PrefsUtils prefsUtils = new PrefsUtils(getContext());

        TextView tvStatus = view.findViewById(R.id.tvStatus);
        TextView tvUltimaAtualizacao = view.findViewById(R.id.tvUltimaAtualizacao);
        TextView tvAreasColetadas = view.findViewById(R.id.tvAreasColetadas);
        TextView tvCodInv = view.findViewById(R.id.tvCodInv);
        TextView tvModoOnLine = view.findViewById(R.id.tvModoOnLine);

        tvAreasColetadas.setText("0");

        tvUltimaAtualizacao.setText(getString(R.string.fi_data_hora_cad_prod, prefsUtils.isCadProdData(), prefsUtils.isCadProdHora()));

        tvAreasColetadas.setText(String.valueOf(new InventarioDao(getContext()).quantidade()));
        tvCodInv.setText(prefsUtils.isInventCod());

        if (prefsUtils.isModoOnline().equals("S")) {
            tvModoOnLine.setText(R.string.ativo);
            tvModoOnLine.setTextColor(getResources().getColor(R.color.green));
        } else {
            tvModoOnLine.setText(R.string.inativo);
            tvModoOnLine.setTextColor(getResources().getColor(R.color.red));
        }

        if (prefsUtils.isCadProdOk().equals("S")) {
            tvStatus.setText(getString(R.string.fi_atualizado));
            tvStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            tvStatus.setText(getString(R.string.fi_desatualizado));
            tvStatus.setTextColor(getResources().getColor(R.color.red));
        }

        view.findViewById(R.id.bOk).setOnClickListener(v -> dismiss());

        return view;
    }
}