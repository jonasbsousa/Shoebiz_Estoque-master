package br.com.shoebiz.shoeconf_2.fragment.dialog;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.Nota;
import br.com.shoebiz.shoeconf_2.model.Ocorrencia;
import br.com.shoebiz.shoeconf_2.model.TipoOcorrencia;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class OcorrenciaDialog extends DialogFragment {
    private Callback callback;

    public interface Callback {
        void onConfirma(Ocorrencia ocorrencia);
    }

    @SuppressLint("CommitTransaction")
    public static void show(FragmentManager fragmentManager, List<TipoOcorrencia> tiposOcorrencia, Nota nota, Callback callback) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("ocorrencia");

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        Bundle args = new Bundle();
        args.putParcelableArrayList("tipoOcorrencias", (ArrayList<? extends Parcelable>) tiposOcorrencia);
        args.putParcelable("nota", nota);

        fragmentTransaction.addToBackStack(null);
        OcorrenciaDialog frag = new OcorrenciaDialog();
        frag.setArguments(args);
        frag.callback = callback;
        frag.show(fragmentManager, "ocorrencia");
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
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ocorrencia, container, false);
        final Spinner sTipoOcorrencia = view.findViewById(R.id.sTipoOcorrencia);
        final EditText etDetalhes = view.findViewById(R.id.etDetalhes);
        // TextView tvNumeroNota = view.findViewById(R.id.tvNumeroNota);

        final Button bConfirmar = view.findViewById(R.id.bConfirmar);
        bConfirmar.setEnabled(false);
        bConfirmar.setAlpha(.5f);

        if (getArguments() != null) {
            List<TipoOcorrencia> tiposOcorrencia = getArguments().getParcelableArrayList("tipoOcorrencias");
            final Nota nota = getArguments().getParcelable("nota");

            if (tiposOcorrencia != null) {
                ArrayAdapter<TipoOcorrencia> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_ocorrencia, tiposOcorrencia);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

                sTipoOcorrencia.setAdapter(adapter);
            }

            if (nota != null) {
                //tvNumeroNota.setText(getString(R.string.fcd_desc,nota.danfe.NotaNumero, nota.danfe.NotaSerie));

                etDetalhes.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void afterTextChanged(Editable s) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        bConfirmar.setEnabled(count > 0);
                        bConfirmar.setAlpha(count > 0 ? 1f : .5f);
                    }
                });

                view.findViewById(R.id.bCancelar).setOnClickListener(v -> {
                    if (callback != null) {
                        dismiss();
                    }
                });

                bConfirmar.setOnClickListener(v -> {
                    if (callback != null) {
                        Ocorrencia ocorrencia = new Ocorrencia();
                        ocorrencia.chave = nota.chave;
                        ocorrencia.codigo = sTipoOcorrencia.getSelectedItem().toString();
                        ocorrencia.descricao = etDetalhes.getText().toString();

                        callback.onConfirma(ocorrencia);

                        dismiss();
                    }
                });
            } else {
                Utils.toast(getContext(), "Problema com a nota!");
                dismiss();
            }
        } else {
            Utils.toast(getContext(), "Problema com a nota!");
            dismiss();
        }

        return view;
    }
}