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
import br.com.shoebiz.shoeconf_2.model.ColetorProduto;

public class ColetorProdutoAdapter extends RecyclerView.Adapter<ColetorProdutoAdapter.ColetorProdutoViewHolder> {
    private Context context;
    private List<ColetorProduto> coletorProdutos;

    public ColetorProdutoAdapter(Context context, List<ColetorProduto> coletorProdutos) {
        this.context = context;
        this.coletorProdutos = coletorProdutos;
    }

    @NonNull
    @Override
    public ColetorProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ColetorProdutoViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_coletor_produto, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColetorProdutoViewHolder holder, int position) {
        ColetorProduto coletorProduto = coletorProdutos.get(position);

        holder.tvCodigoProduto.setText(coletorProduto.codigo);
        holder.tvQuantidade.setText(context.getString(R.string.rv_nTextoQuant, String.valueOf(coletorProduto.quantidade)));
    }

    @Override
    public int getItemCount() {
        return this.coletorProdutos != null ? this.coletorProdutos.size() : 0;
    }

    class ColetorProdutoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCodigoProduto;
        private TextView tvQuantidade;

        ColetorProdutoViewHolder(@NonNull View view) {
            super(view);

            tvCodigoProduto = view.findViewById(R.id.tvCodigoProduto);
            tvQuantidade = view.findViewById(R.id.tvQuantidade);
        }
    }
}