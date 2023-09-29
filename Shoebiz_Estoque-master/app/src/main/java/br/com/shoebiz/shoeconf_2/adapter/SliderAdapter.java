package br.com.shoebiz.shoeconf_2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Objects;

import br.com.shoebiz.shoeconf_2.R;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private int[] textoPassos;
    private int[] textoDesc;

    public SliderAdapter(Context context, int[] textoPassos, int[] textoDesc) {
        this.context = context;
        this.textoPassos = textoPassos;
        this.textoDesc = textoDesc;
    }

    @Override
    public int getCount() {
        return textoPassos.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = (Objects.requireNonNull(layoutInflater)).inflate(R.layout.adapter_slider, container, false);


        TextView tvCabecPasso = view.findViewById(R.id.tvCabecPasso);
        TextView tvCabecDesc = view.findViewById(R.id.tvCabecDesc);

        tvCabecPasso.setText(context.getString(textoPassos[position]));
        tvCabecDesc.setText(context.getString(textoDesc[position]));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
