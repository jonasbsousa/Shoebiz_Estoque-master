package br.com.shoebiz.shoeconf_2.fragment.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class DataAlteracaoDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private CallBack callBack;

    public interface CallBack {
        void onAlteraData(int dia, int mes, int ano);
    }

    public static void show(FragmentManager fragmentManager, Calendar data, CallBack callBack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("DatePicker");

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);

        fragmentTransaction.addToBackStack(null);
        DataAlteracaoDialog dataAlteracaoDialog = new DataAlteracaoDialog();
        dataAlteracaoDialog.callBack = callBack;
        dataAlteracaoDialog.setArguments(bundle);
        dataAlteracaoDialog.show(fragmentManager, "DatePicker");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar dataPesquisa = null;
        int ano, mes, dia;

        if (getArguments() != null) {
            dataPesquisa = (Calendar) getArguments().getSerializable("data");
        }

        if (dataPesquisa == null) {
            dataPesquisa = Calendar.getInstance();
        }


        ano = dataPesquisa.get(Calendar.YEAR);
        mes = dataPesquisa.get(Calendar.MONTH);
        dia = dataPesquisa.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireActivity(), this, ano, mes, dia);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (callBack != null) {
            callBack.onAlteraData(dayOfMonth, month, year);
        }
    }
}