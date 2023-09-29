package br.com.shoebiz.shoeconf_2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.shoebiz.shoeconf_2.R;
import br.com.shoebiz.shoeconf_2.model.TransfProduto;

public class OtTransfAdapter extends RecyclerView.Adapter<OtTransfAdapter.OtTransfViewHolder> {
    private Context context;
    private List<TransfProduto> transfProdutos;
    private String formaColeta;

    public OtTransfAdapter(Context context, List<TransfProduto> transfProdutos, String formaColeta) {
        this.context = context;
        this.transfProdutos = transfProdutos;
        this.formaColeta = formaColeta;
    }

    @NonNull
    @Override
    public OtTransfViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_ot_transf, viewGroup, false);

        return new OtTransfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OtTransfViewHolder holder, int position) {
        TransfProduto transfProduto = this.transfProdutos.get(position);
        StringBuilder textoGtin = new StringBuilder();

        holder.tvDescrProduto.setText(transfProduto.descricao);
        holder.tvQuant.setText(context.getString(R.string.rv_nTextoQuant, String.valueOf(transfProduto.quantidade)));

        if (formaColeta.equals(context.getString(R.string.forma_coleta_gtin))) {
            for (int n = 0; n < transfProduto.transfGtins.size(); n++){
                textoGtin.append(transfProduto.transfGtins.get(n).codigo).append(n < (transfProduto.transfGtins.size() - 1) ? ", " : "");
            }

            holder.tvGrupoProduto.setText(context.getString(R.string.acti_co_gtin, textoGtin));
        } else {
            holder.tvGrupoProduto.setText(context.getString(R.string.acti_co_grupo, transfProduto.descricaoGrupo));
        }

        if (transfProduto.isTotal()) {
            holder.tvCodProduto.setText(context.getString(R.string.acti_co_codigo, context.getString(R.string.acti_co_qnt_total, transfProduto.codigoProduto)));
        } else {
            holder.tvCodProduto.setText(context.getString(R.string.acti_co_codigo, transfProduto.codigoProduto));
        }
    }

    @Override
    public int getItemCount() {
        return this.transfProdutos != null ? this.transfProdutos.size() : 0;
    }

    class OtTransfViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescrProduto;
        private TextView tvCodProduto;
        private TextView tvGrupoProduto;
        private TextView tvQuant;

        OtTransfViewHolder(View view) {
            super(view);

            tvDescrProduto = view.findViewById(R.id.tvDescrProduto);
            tvCodProduto = view.findViewById(R.id.tvCodProduto);
            tvGrupoProduto = view.findViewById(R.id.tvGrupoProduto);
            tvQuant = view.findViewById(R.id.tvQuant);
        }
    }
}