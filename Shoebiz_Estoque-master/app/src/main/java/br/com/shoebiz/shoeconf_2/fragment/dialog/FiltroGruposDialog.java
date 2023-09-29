package br.com.shoebiz.shoeconf_2.fragment.dialog;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.Grupo;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class FiltroGruposDialog extends DialogFragment {
    private CallBack callBack;

    public interface CallBack {
        void onFiltro(String grupoFiltro);
    }

    public static void show(FragmentManager fragmentManager, List<Grupo> grupos, String filtroAux, CallBack callBack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("filtro_grupos");

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        Bundle args = new Bundle();
        args.putParcelableArrayList("grupos", (ArrayList<? extends Parcelable>) grupos);
        args.putString("filtroAux", filtroAux);

        fragmentTransaction.addToBackStack(null);
        FiltroGruposDialog frag = new FiltroGruposDialog();
        frag.callBack = callBack;
        frag.setArguments(args);
        frag.show(fragmentManager, "filtro_grupo");
    }

    @Override
    public void onResume() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();

        Display display = Objects.requireNonNull(window).getWindowManager().getDefaultDisplay();
        display.getSize(size);

        window.setLayout(size.x, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        super.onResume();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_filtro_grupos, container, false);

        List<Grupo> grupos = requireArguments().getParcelableArrayList("grupos");
        String filtroAux = getArguments().getString("filtroAux");

        if (grupos != null && grupos.size() > 0) {
            boolean bFiltroAux = filtroAux != null && !filtroAux.isEmpty() && !filtroAux.equals("TODOS");
            boolean checkboxOk = grupos.size() != 1;
            int qntFiltros = grupos.size() + 1;

            final LinearLayout llCheckBox = view.findViewById(R.id.llCheckBox);

            final RadioButton[] radioButton = new RadioButton[qntFiltros];
            RadioGroup radioGroup = new RadioGroup(getContext());
            radioGroup.setOrientation(RadioGroup.VERTICAL);

            for (int n = 0; n <= grupos.size(); n++) {
                radioButton[n] = new RadioButton(getContext());
                radioButton[n].setId(n);
                radioButton[n].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_dialog_alt_prc));
                radioButton[n].setEnabled(checkboxOk);

                if (n == grupos.size()) {
                    radioButton[n].setText("TODOS");
                    radioButton[n].setChecked(!bFiltroAux);
                } else {
                    radioButton[n].setText(grupos.get(n).descricao);

                    if (bFiltroAux && grupos.get(n).descricao.equals(filtroAux)) {
                        radioButton[n].setChecked(true);
                    }
                }

                radioGroup.addView(radioButton[n]);
            }

            llCheckBox.addView(radioGroup);

            view.findViewById(R.id.bAplicar).setOnClickListener(v -> {
                if (callBack != null) {
                    String selectedText = "";

                    for (int i = 0; i < llCheckBox.getChildCount(); i++) {
                        View viewAux = llCheckBox.getChildAt(i);

                        if (viewAux instanceof RadioGroup) {
                            RadioGroup radioGroup1 = (RadioGroup) viewAux;

                            int radioButtonId = radioGroup1.getCheckedRadioButtonId();
                            View radioButtonIdx = radioGroup1.findViewById(radioButtonId);
                            int idx = radioGroup1.indexOfChild(radioButtonIdx);

                            RadioButton radioButton1 = (RadioButton) radioGroup1.getChildAt(idx);
                            selectedText = radioButton1.getText().toString();
                        }
                    }

                    if (selectedText.isEmpty()) {
                        Utils.toast(getContext(), "Filtro inválido!");
                    } else {
                        callBack.onFiltro(selectedText);

                        dismiss();
                    }
                } else {
                    dismiss();
                }
            });

            view.findViewById(R.id.bCancelar).setOnClickListener(v -> {
                if (callBack != null) {
                    dismiss();
                }
            });
        } else {
            Utils.toast(getContext(), "Problemas prar carregar os grupos!");
            dismiss();
        }

        return view;
    }
}