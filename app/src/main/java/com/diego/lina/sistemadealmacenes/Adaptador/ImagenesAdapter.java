package com.diego.lina.sistemadealmacenes.Adaptador;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.diego.lina.sistemadealmacenes.Entidades.Usuario;
import com.diego.lina.sistemadealmacenes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.diego.lina.sistemadealmacenes.R.style.Theme_AppCompat_Dialog;

public class ImagenesAdapter extends RecyclerView.Adapter<ImagenesAdapter.MercanciasHolder> implements View.OnClickListener {

    List<Usuario> listaMercancias;
    RequestQueue requestQueue;
    Context context;
    Button btn_info_img;
    private View.OnClickListener listener;

    public ImagenesAdapter(ArrayList<Usuario> listaMercancias, Context context){
        this.listaMercancias = listaMercancias;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }


    @NonNull
    @Override
    public MercanciasHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View fragmento = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mercancia_lista, viewGroup, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        fragmento.setLayoutParams(layoutParams);
        fragmento.setOnClickListener(this);


        return new MercanciasHolder(fragmento);
    }

    @Override
    public void onBindViewHolder(@NonNull MercanciasHolder mercanciasHolder, final int i) {
        /*mercanciasHolder.id_merca = listaMercancias.get(i).getId_merca();
        mercanciasHolder.n_merca.setText(listaMercancias.get(i).getN_merca().toString());
       // mercanciasHolder.desc_merca.setText(listaMercancias.get(i).getDesc_merca().toString());
        mercanciasHolder.fecha_reg.setText(listaMercancias.get(i).getFecha_reg().toString());
        mercanciasHolder.buque.setText(listaMercancias.get(i).getBuque().toString());
        mercanciasHolder.factura.setText(listaMercancias.get(i).getFactura().toString());
        mercanciasHolder.cliente.setText(listaMercancias.get(i).getCliente().toString());*/

        /*if ((i %2 )== 0){
            mercanciasHolder.cardView.setBackgroundColor(Color.rgb(93, 173, 226));
        }
        else {
            mercanciasHolder.cardView.setBackgroundColor(Color.rgb(127, 179, 213));
        }*/
    }

    @Override
    public int getItemCount() {
        return listaMercancias.size();
    }

    public  void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }


    public class MercanciasHolder extends RecyclerView.ViewHolder{
        TextView n_merca,  desc_merca, fecha_reg, buque, factura, cliente;
        TextView titulo1,  titulo3, titulo4;
        ImageView img_url;
        CardView cardView;
        int id_merca;
        Typeface fuente = Typeface.createFromAsset(context.getAssets(),"fonts/Biysk.ttf" );
        Typeface fuente2 = Typeface.createFromAsset(context.getAssets(),"fonts/boldblue.ttf" );
        public MercanciasHolder(View fragmento) {
            super(fragmento);
            n_merca = fragmento.findViewById(R.id.n_merca);
            desc_merca = fragmento.findViewById(R.id.desc_merca);
            fecha_reg = fragmento.findViewById(R.id.fecha_reg);
            buque = fragmento.findViewById(R.id.buque);
            factura = fragmento.findViewById(R.id.factura);
            cliente = fragmento.findViewById(R.id.cliente);
            cardView = fragmento.findViewById(R.id.cardview);


            titulo1 = fragmento.findViewById(R.id.titulo1);
            //titulo3 = fragmento.findViewById(R.id.titulo3);
            titulo4 = fragmento.findViewById(R.id.titulo4);
        }
    }

}
