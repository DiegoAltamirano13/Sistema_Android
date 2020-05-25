package com.diego.lina.sistemadealmacenes.Adaptador;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.diego.lina.sistemadealmacenes.Entidades.Usuario;
import com.diego.lina.sistemadealmacenes.R;
import com.diego.lina.sistemadealmacenes.Reportes2Fragment;

import java.util.ArrayList;
import java.util.List;

public class ListaImagenesAdapterConsultaMensaje extends RecyclerView.Adapter<ListaImagenesAdapterConsultaMensaje.ListaImagenesHolder> implements View.OnClickListener {
    ArrayList<Usuario> lista_img;
    RequestQueue requestQueue;
    Context context;
    private View.OnClickListener listener;


    public ListaImagenesAdapterConsultaMensaje(List<Usuario> lista_img, Context context){
        this.lista_img = (ArrayList<Usuario>)lista_img;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }


    @NonNull
    @Override
    public ListaImagenesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View fragment = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_dialog_fragment, viewGroup, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fragment.setLayoutParams(layoutParams);
        fragment.setOnClickListener(this);
        return new ListaImagenesHolder(fragment);

    }

    @Override
    public void onBindViewHolder(@NonNull ListaImagenesAdapterConsultaMensaje.ListaImagenesHolder listaImagenesHolder, int i) {
        if(lista_img.get(i).getUrl_de_imagen() != null){
            CargarIMG(lista_img.get(i).getUrl_de_imagen(), listaImagenesHolder);
        }
        else {
            listaImagenesHolder.img.setImageResource(R.drawable.img_base);
        }

    }

    private void CargarIMG(String url_de_imagen, final ListaImagenesHolder listaImagenesHolder) {
        String site_imagen = url_de_imagen;
        site_imagen.replace(" ", "%20");
        ImageRequest imageRequest = new ImageRequest(site_imagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                listaImagenesHolder.img.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Error de conversion de imagen", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(imageRequest);
    }

    @Override
    public int getItemCount() {
        return lista_img.size();
    }

    public  void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
            Log.i("Esta muerto", "Muerto");
        }
    }


    public class ListaImagenesHolder extends RecyclerView.ViewHolder {
        ImageView img;
        Button btn_img;

        public ListaImagenesHolder(View fragment) {
            super(fragment);
            img = fragment.findViewById(R.id.url_de_imagen);
            btn_img = fragment.findViewById(R.id.mybutton);
        }

        public void asignarImg(Uri s) {
            img.setImageURI(s);
        }
    }
}
