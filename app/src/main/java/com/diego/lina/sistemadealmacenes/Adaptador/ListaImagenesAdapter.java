package com.diego.lina.sistemadealmacenes.Adaptador;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.diego.lina.sistemadealmacenes.Entidades.Usuario;
import com.diego.lina.sistemadealmacenes.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListaImagenesAdapter extends RecyclerView.Adapter<ListaImagenesAdapter.ListaImagenesHolder>
implements View.OnClickListener {
    ArrayList<Uri> listiMG;
    private View.OnClickListener listener;
    Context context;

    public ListaImagenesAdapter(ArrayList<Uri> listiMg){
        this.listiMG = (ArrayList<Uri>) listiMg;
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
    public void onBindViewHolder(@NonNull ListaImagenesAdapter.ListaImagenesHolder listaImagenesHolder, int i) {
        listaImagenesHolder.asignarImg(listiMG.get(i));
    }

    @Override
    public int getItemCount() {
        return listiMG.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener= listener;
    }
    @Override
    public void onClick(View v) {
        if (listener != null){
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
            //img.setImageBitmap(s);
           // img.setImageURI(s);
        }
    }
}
