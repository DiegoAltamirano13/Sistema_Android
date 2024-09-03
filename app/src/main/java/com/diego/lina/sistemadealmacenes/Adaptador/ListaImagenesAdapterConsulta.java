package com.diego.lina.sistemadealmacenes.Adaptador;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListaImagenesAdapterConsulta extends RecyclerView.Adapter<ListaImagenesAdapterConsulta.ListaImagenesHolder> implements View.OnClickListener {
    ArrayList<Usuario> lista_img;
    RequestQueue requestQueue;
    private View.OnClickListener listener;
    Context context;
    Dialog dialog;
    ImageView imageView;
    //ArrayList<Usuario>listImg = new ArrayList<>();

    public ListaImagenesAdapterConsulta(List<Usuario> lista_img, Context context){
        this.lista_img = (ArrayList<Usuario>)lista_img;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }


    @NonNull
    @Override
    public ListaImagenesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View fragment = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.galeria_images, viewGroup, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fragment.setLayoutParams(layoutParams);
        fragment.setOnClickListener(this);
        return new ListaImagenesHolder(fragment);

    }

    @Override
    public void onBindViewHolder(@NonNull final ListaImagenesAdapterConsulta.ListaImagenesHolder listaImagenesHolder, final int i) {
        listaImagenesHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "HolaRec" + lista_img.get(i).getUrl_de_imagen(), Toast.LENGTH_LONG).show();
                //toast.show();
                dialog = new Dialog(context, android.R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.mensaje_imagen);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                imageView = dialog.findViewById(R.id.imagen);
                final Button btn_delete;

                if (lista_img.get(i).getUrl_de_imagen().startsWith("http"))
                {
                   Picasso.get().load(lista_img.get(i).getUrl_de_imagen()).into( imageView);
                   btn_delete = dialog.findViewById(R.id.btn_del);
                   btn_delete.setVisibility(View.GONE);
                }
                else
                {
                    Picasso.get().load(new File(lista_img.get(i).getUrl_de_imagen())).into(imageView);
                    btn_delete = dialog.findViewById(R.id.btn_del);
                }
                Button btn_imagenes = dialog.findViewById(R.id.btn_imagen);

                btn_imagenes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("Aviso de I ", String.valueOf(lista_img.size()));
                        lista_img.remove(i);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        if(lista_img.get(i).getUrl_de_imagen() != null){
            Log.e("AVISO IMAGENES ", "PICASO IMAGENES ");
           // CargarIMG(lista_img.get(i).getUrl_de_imagen(), listaImagenesHolder);

            if (lista_img.get(i).getUrl_de_imagen().startsWith("http"))
            {
                Picasso.get().load(lista_img.get(i).getUrl_de_imagen()).resize(200, 200).into( listaImagenesHolder.img);
            }
            else
            {
                Log.e("AVISO", lista_img.get(i).getUrl_de_imagen());
                Picasso.get().load(new File(lista_img.get(i).getUrl_de_imagen())).resize(200, 200).into( listaImagenesHolder.img);
            }

        }
        else {
            Log.e("AVISO IMAGENES ", "PICASO IMAGENES ");
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

    public void setOnClickListener(View.OnClickListener listener) {
        //Toast toast = Toast.makeText(context, "HolaRec2", Toast.LENGTH_LONG);
        //toast.show();
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            //Toast toast = Toast.makeText(context, "HolaRec", Toast.LENGTH_LONG);
            //toast.show();
            listener.onClick(v);
        }
    }


    public class ListaImagenesHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ListaImagenesHolder(View fragment) {
            super(fragment);
            img = fragment.findViewById(R.id.url_de_imagen);

        }

        public void asignarImg(Uri s) {
            img.setImageURI(s);
        }
    }
}
