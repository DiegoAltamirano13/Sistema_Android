package com.diego.lina.sistemadealmacenes;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.diego.lina.sistemadealmacenes.ClassCanvas.ClassCanvas;


public class FirmaEnFragment extends Fragment {

    private ClassCanvas canvas;
    Button btn_aceptar, btn_limpiar ;
    Bitmap bitmaps;


    public FirmaEnFragment() {

    }


    public static FirmaEnFragment newInstance(String param1, String param2) {
        FirmaEnFragment fragment = new FirmaEnFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmento = inflater.inflate(R.layout.fragment_firma_en, container, false);
        btn_aceptar = fragmento.findViewById(R.id.firma_aceptar);
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              canvas.Guardar();
                ImportFragment rp = new ImportFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.contenedor, rp);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_limpiar = fragmento.findViewById(R.id.Limpiar);
        btn_limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.Limpiar();
            }
        });
        canvas = fragmento.findViewById(R.id.view);
        return fragmento;
    }

    //public void Limpiar(View view) {
      //  canvas.Limpiar();
    //}
    //public void Guardar2(View view){
      //  canvas.Guardar();
    //}

}
