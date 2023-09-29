package br.com.shoebiz.shoeconf_2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.Nota;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class EntradaMercadoriaAdapter extends RecyclerView.Adapter<EntradaMercadoriaAdapter.FornecedorDanfeViewHolder> {

    private final List<Nota> notas;
    private final Context context;
    private DanfeOnClickListener danfeOnClickListener;

    public interface DanfeOnClickListener {
        void onLongClickDanfe(View view, int idx);
    }

    public EntradaMercadoriaAdapter(Context context, List<Nota> notas, DanfeOnClickListener danfeOnClickListener) {
        this.notas = notas;
        this.context = context;
        this.danfeOnClickListener = danfeOnClickListener;
    }

    @NonNull
    @Override
    public FornecedorDanfeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_entrada_mercadoria, viewGroup, false);

        return new FornecedorDanfeViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull final FornecedorDanfeViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Nota nota = notas.get(position);

        holder.tvNumNota.setText(context.getString(R.string.frag_vd_desc, nota.notaNumero, nota.notaSerie));
        holder.tvEmissao.setText(new SimpleDateFormat(Utils.formatoData()).format(nota.emissao));
        holder.tvFornecedor.setText(nota.fornecedorDesc);

        if (nota.status.equals(Utils.getStatusNota(R.string.status_nota_finalizado))) {
            holder.vStatusNota.setBackgroundResource(R.color.statusNotaFinalizado);
        } else if (nota.status.equals(Utils.getStatusNota(R.string.status_nota_pendente))) {
            holder.vStatusNota.setBackgroundResource(R.color.statusNotaPendente);

            if (nota.statusProc.isEmpty()) {
                holder.tvDiasParado.setVisibility(View.VISIBLE);
                holder.tvDiasParado.setText(context.getString(R.string.dias_parado, Utils.diasParado(nota.recebimentoData)));
            }

            if (nota.statusProc.equals("E")) {
                holder.tvErroProcessar.setVisibility(View.VISIBLE);
            } else if (nota.statusProc.equals("P")) {
                holder.tvProcessando.setVisibility(View.VISIBLE);
            }
        } else if (nota.status.equals(Utils.getStatusNota(R.string.status_nota_a_receber))) {
            holder.vStatusNota.setBackgroundResource(R.color.statusNotaAReceber);
        }

        if (danfeOnClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                danfeOnClickListener.onLongClickDanfe(holder.view, position);

                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.notas != null ? this.notas.size() : 0;
    }

    class FornecedorDanfeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumNota;
        private TextView tvEmissao;
        private TextView tvFornecedor;
        private TextView tvProcessando;
        private TextView tvErroProcessar;
        private TextView tvDiasParado;
        private View view;
        private View vStatusNota;

        FornecedorDanfeViewHolder(View view) {
            super(view);
            this.view = view;

            tvNumNota = view.findViewById(R.id.tvNumNota);
            tvEmissao = view.findViewById(R.id.tvEmissao);
            tvFornecedor = view.findViewById(R.id.tvFornecedor);
            tvProcessando = view.findViewById(R.id.tvProcessando);
            tvErroProcessar = view.findViewById(R.id.tvErroProcessar);
            tvDiasParado = view.findViewById(R.id.tvDiasParado);
            vStatusNota = view.findViewById(R.id.vStatusNota);
        }
    }
}