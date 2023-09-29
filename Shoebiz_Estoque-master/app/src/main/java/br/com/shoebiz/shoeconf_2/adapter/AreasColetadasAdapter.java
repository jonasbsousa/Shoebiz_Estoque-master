package br.com.shoebiz.shoeconf_2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.Inventario;

public class AreasColetadasAdapter extends RecyclerView.Adapter<AreasColetadasAdapter.AreasColetadasViewHolder> {

    private final List<Inventario> inventarios;
    private final Context context;

    public AreasColetadasAdapter(Context context, List<Inventario> inventarios) {
        this.context = context;
        this.inventarios = inventarios;
    }

    @NonNull
    @Override
    public AreasColetadasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_areas_coletadas, viewGroup, false);

        return new AreasColetadasViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AreasColetadasViewHolder holder, int position) {
        Inventario inventario = inventarios.get(position);

        holder.tvCodigo.setText(inventario.area);

        if (inventario.integrado.equals("S")) {
            holder.tvIntegrado.setText("Sincronizado");
            holder.tvIntegrado.setTextColor(context.getResources().getColor(R.color.statusNotaFinalizado));
        } else {
            holder.tvIntegrado.setText("Não Sincronizado");
            holder.tvIntegrado.setTextColor(context.getResources().getColor(R.color.statusNotaPendente));
        }
    }

    @Override
    public int getItemCount() {
        return this.inventarios != null ? this.inventarios.size() : 0;
    }

    class AreasColetadasViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCodigo;
        private TextView tvIntegrado;

        AreasColetadasViewHolder(View view) {
            super(view);

            tvCodigo = view.findViewById(R.id.tvCodigo);
            tvIntegrado = view.findViewById(R.id.tvIntegrado);
        }
    }
}