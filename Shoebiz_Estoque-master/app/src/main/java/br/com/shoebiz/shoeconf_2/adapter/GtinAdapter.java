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
import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;

public class GtinAdapter extends RecyclerView.Adapter<GtinAdapter.GtinAdapterViewHolder> {

    private List<ProdutoFilho> produtosFilho;
    private Context context;

    public GtinAdapter(Context context, List<ProdutoFilho> produtosFilho) {
        this.produtosFilho = produtosFilho;
        this.context = context;
    }

    @NonNull
    @Override
    public GtinAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_gtin, viewGroup, false);

        return new GtinAdapterViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull GtinAdapterViewHolder holder, int position) {
        ProdutoFilho produtoFilho = this.produtosFilho.get(position);

        if (produtoFilho.codigoGtin != null && produtoFilho.codigoGtin.trim().isEmpty()) {
            holder.tvCodigoGtin.setText(context.getText(R.string.frag_vd_não_cadastrado));
            holder.tvCodigoGtin.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.tvCodigoGtin.setText(produtoFilho.codigoGtin);
        }

        holder.tvNumero.setText(produtoFilho.codigo);
        holder.tvQuantidade.setText(String.valueOf(produtoFilho.quantidade));
    }

    @Override
    public int getItemCount() {
        return this.produtosFilho != null ? this.produtosFilho.size() : 0;
    }

    class GtinAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCodigoGtin;
        private TextView tvNumero;
        private TextView tvQuantidade;

        GtinAdapterViewHolder(@NonNull View view) {
            super(view);

            tvCodigoGtin = view.findViewById(R.id.tvCodigoGtin);
            tvNumero = view.findViewById(R.id.tvNumero);
            tvQuantidade = view.findViewById(R.id.tvQuantidade);
        }
    }
}