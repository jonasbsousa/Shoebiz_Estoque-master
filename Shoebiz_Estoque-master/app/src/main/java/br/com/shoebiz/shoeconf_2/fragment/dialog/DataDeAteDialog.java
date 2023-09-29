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
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class DataDeAteDialog extends DialogFragment {
    private Callback callback;

    private Calendar dataDe;
    private Calendar dataAte;

    public interface Callback {
        void onReturn(Calendar dataDe, Calendar dataAte);
    }

    @SuppressLint("CommitTransaction")
    public static void show(FragmentManager fragmentManager, Calendar dataDe, Calendar dataAte, Callback callback) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("data_de_ate");

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("dataDe", dataDe);
        bundle.putSerializable("dataAte", dataAte);

        fragmentTransaction.addToBackStack(null);
        DataDeAteDialog frag = new DataDeAteDialog();
        frag.callback = callback;
        frag.setArguments(bundle);
        frag.show(fragmentManager, "data_de_ate");
    }

    @Override
    public void onResume() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();

        if (window != null) {
            Point size = new Point();

            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);

            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

        super.onResume();
    }

    @SuppressLint("SimpleDateFormat")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_data_de_ate, container, false);

        if (getArguments() != null) {
            this.dataDe = (Calendar) getArguments().getSerializable("dataDe");
            this.dataAte = (Calendar) getArguments().getSerializable("dataAte");
        } else {
            this.dataDe = Calendar.getInstance();
            this.dataAte = Calendar.getInstance();
        }

        final EditText etDataDe = view.findViewById(R.id.etDataDe);
        etDataDe.setText(new SimpleDateFormat(Utils.formatoData()).format(dataDe.getTime()));

        final EditText etDataAte = view.findViewById(R.id.etDataAte);
        etDataAte.setText(new SimpleDateFormat(Utils.formatoData()).format(dataAte.getTime()));

        view.findViewById(R.id.ibCalendarioDataDe).setOnClickListener(v -> DataAlteracaoDialog.show(getChildFragmentManager(), dataDe, (dia, mes, ano) -> {
            dataDe.set(ano, mes, dia);
            etDataDe.setText(new SimpleDateFormat(Utils.formatoData()).format(dataDe.getTime()));
        }));

        view.findViewById(R.id.ibCalendarioDataAte).setOnClickListener(v -> DataAlteracaoDialog.show(getChildFragmentManager(), dataAte, (dia, mes, ano) -> {
            dataAte.set(ano, mes, dia);
            etDataAte.setText(new SimpleDateFormat(Utils.formatoData()).format(dataAte.getTime()));
        }));

        view.findViewById(R.id.bFechar).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.bOk).setOnClickListener(v -> {
            if (dataDe.getTimeInMillis() <= dataAte.getTimeInMillis()) {
                callback.onReturn(dataDe, dataAte);

                dismiss();
            } else {
                Utils.toast(getContext(), "A 'Data de' deve ser menor ou igual que a 'Data até'!");
            }
        });

        return view;
    }
}