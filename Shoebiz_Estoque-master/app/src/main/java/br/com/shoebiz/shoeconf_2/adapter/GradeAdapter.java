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
import br.com.shoebiz.shoeconf_2.model.ProdutoFilho;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private final List<ProdutoFilho> produtosFilho;
    private final Context context;

    public GradeAdapter(Context context, List<ProdutoFilho> produtosFilho) {
        this.context = context;
        this.produtosFilho = produtosFilho;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_grade, viewGroup, false);

        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        ProdutoFilho produtoFilho = produtosFilho.get(position);

        holder.tvTxtTamanho.setText(produtoFilho.codigo);
        holder.tvTamanho.setText(String.valueOf(produtoFilho.quantidade));
    }

    @Override
    public int getItemCount() {
        return this.produtosFilho != null ? this.produtosFilho.size() : 0;
    }

    class GradeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTxtTamanho;
        private TextView tvTamanho;

        GradeViewHolder(View view) {
            super(view);

            tvTxtTamanho = view.findViewById(R.id.tvTxtTamanho);
            tvTamanho = view.findViewById(R.id.tvTamanho);
        }
    }
}