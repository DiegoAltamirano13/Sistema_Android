package com.diego.lina.sistemadealmacenes.Adaptador;


import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.diego.lina.sistemadealmacenes.Entidades.Usuario;
import com.diego.lina.sistemadealmacenes.R;
import com.diego.lina.sistemadealmacenes.Reportes2Fragment;

import java.util.List;

public class MercanciasAdapter extends RecyclerView.Adapter<MercanciasAdapter.MercanciasHolder> implements View.OnClickListener {
    Button btn_info , btn_img;

    List<Usuario> listaMercancias;
    private View.OnClickListener listener;
    public MercanciasAdapter(List<Usuario> listaMercancias){
        this.listaMercancias = listaMercancias;
    }
    @NonNull
    @Override
    public MercanciasHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View fragmento = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mercancia_lista, viewGroup, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        fragmento.setLayoutParams(layoutParams);

        return new MercanciasHolder(fragmento);
    }

    @Override
    public void onBindViewHolder(@NonNull MercanciasHolder mercanciasHolder, int i) {
        mercanciasHolder.n_merca.setText(listaMercancias.get(i).getN_merca().toString());
        mercanciasHolder.est_merca.setText(listaMercancias.get(i).getEst_merca().toString());
        mercanciasHolder.desc_merca.setText(listaMercancias.get(i).getDesc_merca().toString());


    }

    @Override
    public int getItemCount() {
        return listaMercancias.size();
    }

    @Override
    public void onClick(View v) {
       if(listener != null){
           listener.onClick(v);
       }
    }

    public class MercanciasHolder extends RecyclerView.ViewHolder {
        TextView n_merca, est_merca, desc_merca;
        ImageView img_url;
        public MercanciasHolder(View fragmento) {
            super(fragmento);
            n_merca = fragmento.findViewById(R.id.n_merca);
            est_merca = fragmento.findViewById(R.id.fecha_reg);
            desc_merca = fragmento.findViewById(R.id.desc_merca);
        }
    }
}
