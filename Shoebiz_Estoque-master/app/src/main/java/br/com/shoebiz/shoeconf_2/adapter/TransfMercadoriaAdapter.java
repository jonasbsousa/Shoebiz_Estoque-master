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
import br.com.shoebiz.shoeconf_2.model.TransfNota;
import br.com.shoebiz.shoeconf_2.utils.Utils;

public class TransfMercadoriaAdapter extends RecyclerView.Adapter<TransfMercadoriaAdapter.TransfMercadoriaViewHolder> {

    private List<TransfNota> transfNotas;
    private Context context;
    private TransfOnClickListener transfOnClickListener;

    public interface TransfOnClickListener{
        void onLongClickTransf(View view, int idx);
    }

    public TransfMercadoriaAdapter(Context context, List<TransfNota> transfNotas, TransfOnClickListener transfOnClickListener) {
        this.context = context;
        this.transfNotas = transfNotas;
        this.transfOnClickListener = transfOnClickListener;
    }

    @NonNull
    @Override
    public TransfMercadoriaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_transf_mercadoria, viewGroup, false);

        return new TransfMercadoriaViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull final TransfMercadoriaViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        TransfNota transfNota = this.transfNotas.get(position);

        String tipoOperacao = Utils.getTipoOpTransf(transfNota.status);

        holder.tvEmissao.setText(new SimpleDateFormat(Utils.formatoData()).format(transfNota.emissao));

        if (tipoOperacao.equals(String.valueOf(Utils.OP_TRANSF_OT))) {
            holder.tvNumNota.setText(context.getString(R.string.frag_tm_NumOt, transfNota.otNumero));
            holder.tvFilOrigem.setText(context.getString(R.string.frag_tm_FilDestino, transfNota.codLojaDestino, transfNota.descLojaDestino));

            if (transfNota.status.equals(Utils.getStatusOt(R.string.status_transf_ot_aberto))) {
                holder.vStatusNota.setBackgroundResource(R.color.statusNotaPendente);
            } else if ((transfNota.status.equals(Utils.getStatusOt(R.string.status_transf_ot_pendente)))) {
                holder.vStatusNota.setBackgroundResource(R.color.statusNotaFinalizado);
            }
        } else if (tipoOperacao.equals(String.valueOf(Utils.OP_TRANSF_NOTA))) {
            holder.tvNumNota.setText(context.getString(R.string.frag_tm_NumDanfe, transfNota.numero, transfNota.serie));
            holder.tvFilOrigem.setText(context.getString(R.string.frag_tm_FilOrigem, transfNota.codLojaOrigem, transfNota.descLojaOrigem));

            if (transfNota.status.equals(Utils.getStatusTransf(R.string.status_transf_pendente))) {
                holder.vStatusNota.setBackgroundResource(R.color.statusNotaPendente);

                if (transfNota.statusProc.isEmpty()) {
                    holder.tvDiasParado.setVisibility(View.VISIBLE);
                    holder.tvDiasParado.setText(context.getString(R.string.dias_parado, Utils.diasParado(transfNota.emissao)));
                }

                if (transfNota.statusProc.equals("E")) {
                    holder.tvErroProcessar.setVisibility(View.VISIBLE);
                } else if (transfNota.statusProc.equals("P")) {
                    holder.tvProcessando.setVisibility(View.VISIBLE);
                }
            } else if (transfNota.status.equals(Utils.getStatusTransf(R.string.status_transf_recebido))) {
                holder.vStatusNota.setBackgroundResource(R.color.statusNotaFinalizado);
            }
        }

        if (transfOnClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                transfOnClickListener.onLongClickTransf(holder.view, position);

                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.transfNotas != null ? this.transfNotas.size() : 0;
    }

    class TransfMercadoriaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumNota;
        private TextView tvFilOrigem;
        private TextView tvEmissao;
        private TextView tvErroProcessar;
        private TextView tvProcessando;
        private TextView tvDiasParado;
        private View vStatusNota;
        private View view;

        TransfMercadoriaViewHolder(View view) {
            super(view);
            this.view = view;

            tvNumNota = view.findViewById(R.id.tvNumNota);
            tvFilOrigem = view.findViewById(R.id.tvFilOrigem);
            tvEmissao = view.findViewById(R.id.tvEmissao);
            vStatusNota = view.findViewById(R.id.vStatusNota);
            tvErroProcessar = view.findViewById(R.id.tvErroProcessar);
            tvProcessando = view.findViewById(R.id.tvProcessando);
            tvDiasParado = view.findViewById(R.id.tvDiasParado);
        }
    }
}