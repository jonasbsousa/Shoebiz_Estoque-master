package br.com.shoebiz.shoeconf_2.fragment.dialog;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class ColetorDialog extends DialogFragment {
    private Callback callback;

    public interface Callback {
        void onConfirma(int idxDepartamento, String observacao);
    }

    public static void show(FragmentManager fragmentManager, List<String> departamentos, Callback callback) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("coletor");

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        Bundle args = new Bundle();
        args.putStringArrayList("departamentos", (ArrayList<String>) departamentos);

        fragmentTransaction.addToBackStack(null);
        ColetorDialog frag = new ColetorDialog();
        frag.setArguments(args);
        frag.callback = callback;
        frag.show(fragmentManager, "coletor");
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_coletor, container, false);

        TextInputLayout tilObservacao = view.findViewById(R.id.tilObservacao);
        Spinner sDepartamento = view.findViewById(R.id.sDepartamento);

        if (getArguments() != null) {
            ArrayList<String> departamentos = getArguments().getStringArrayList("departamentos");

            if (departamentos != null) {
                ArrayAdapter<String> departamentosAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_operacoes_coletor, departamentos);
                departamentosAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

                sDepartamento.setAdapter(departamentosAdapter);

                view.findViewById(R.id.bCancelar).setOnClickListener(v -> {
                    if (callback != null) {
                        dismiss();
                    }
                });

                view.findViewById(R.id.bConfirmar).setOnClickListener(v -> {
                    if (callback != null) {
                        callback.onConfirma(sDepartamento.getSelectedItemPosition(), Objects.requireNonNull(tilObservacao.getEditText()).getText().toString());

                        dismiss();
                    }
                });

                Objects.requireNonNull(tilObservacao.getEditText()).requestFocus();
            } else {
                Utils.toast(getContext(), "Problema ao abrir a tela!");
                dismiss();
            }
        } else {
            Utils.toast(getContext(), "Problema ao abrir a tela!");
            dismiss();
        }

        return view;
    }
}