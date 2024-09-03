package com.diego.lina.sistemadealmacenes;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.diego.lina.sistemadealmacenes.Adaptador.ImagenesAdapter;
import com.diego.lina.sistemadealmacenes.Adaptador.ListaImagenesAdapter;
import com.diego.lina.sistemadealmacenes.Adaptador.ListaImagenesAdapterConsulta;
import com.diego.lina.sistemadealmacenes.Adaptador.ListaImagenesAdapterConsultaMensaje;
import com.diego.lina.sistemadealmacenes.Adaptador.MercanciasAdapter;
import com.diego.lina.sistemadealmacenes.Entidades.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Reportes2Fragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{

    RecyclerView recyclerMercancias;
    ArrayList<Usuario> listaMercancia;
    ProgressDialog progress;

    RequestQueue requestQueue;
    ImageView img_no_con;
    private OnFragmentInteractionListener mListener;
    JsonObjectRequest jsonObjectRequest;
    ImagenesAdapter adapter;
    Dialog dialog;
    Typeface typeface;

    Button btn_fec_ini;
    Button btn_fec_fin;
    Button buscar;
    EditText fec_ini;
    EditText fec_fin;

    TextInputLayout layuout_ini;
    TextInputLayout layout_fin;
    String plaza;
    String fechasinicial;
    String fechafinal;
    private  int dia, mes, anio;
    private  int dia2, mes2, anio2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_reportes2, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        plaza = preferences.getString("as_plaza_u", "No estas logueado");
        img_no_con = fragment.findViewById(R.id.no_conect);
        listaMercancia = new ArrayList<>();
        recyclerMercancias = fragment.findViewById(R.id.idReciclerMercancia);
        recyclerMercancias.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerMercancias.setHasFixedSize(true);
        img_no_con.setVisibility(View.INVISIBLE);
        requestQueue= Volley.newRequestQueue(getContext());

        //Fechas
        btn_fec_ini = fragment.findViewById(R.id.btn_ini);
        fec_ini = fragment.findViewById(R.id.edit_ini);
        layuout_ini = fragment.findViewById(R.id.layout_inicio);
        layuout_ini.setHintAnimationEnabled(false);
        fec_ini.setOnFocusChangeListener(null);
        btn_fec_fin = fragment.findViewById(R.id.btn_fin);
        fec_fin = fragment.findViewById(R.id.edit_fin);
        layout_fin = fragment.findViewById(R.id.layout_fin);
        layout_fin.setHintAnimationEnabled(false);
        fec_fin.setOnFocusChangeListener(null);

        btn_fec_ini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btn_fec_ini){
                    final Calendar calendar = Calendar.getInstance();
                    dia = calendar.get(Calendar.DAY_OF_MONTH);
                    mes = calendar.get(Calendar.MONTH);
                    anio = calendar.get(Calendar.YEAR);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            month = month+1;
                            String fomatoMonth = "" + month;
                            String formatoDia = "" + dayOfMonth;
                            if (month < 10){
                                fomatoMonth = "0"+month;
                            }
                            if (dayOfMonth<10){
                                formatoDia = "0" + dayOfMonth;
                            }
                            fechasinicial = year + "-" + fomatoMonth + "-" + formatoDia;
                            fec_ini.setText(formatoDia +"/" + fomatoMonth +"/" + year);
                            //fec_ini.setText(dayOfMonth +"/" + (month+1) +"/" + year);
                        }
                    }, anio, mes, dia);
                    datePickerDialog.show();
                }
            }
        });

        btn_fec_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btn_fec_fin){
                    final Calendar calendar = Calendar.getInstance();
                    String recibido;
                    dia2 = calendar.get(Calendar.DAY_OF_MONTH);
                    mes2 = calendar.get(Calendar.MONTH);
                    anio2 = calendar.get(Calendar.YEAR);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            month = month+1;
                            String fomatoMonth = "" + month;
                            String formatoDia = "" + dayOfMonth;
                            if (month+1 < 10){
                                fomatoMonth = "0"+month;
                            }
                            if (dayOfMonth<10){
                                formatoDia = "0" + dayOfMonth;
                            }
                            fechafinal = year + "-" + fomatoMonth + "-" +formatoDia;
                            fec_fin.setText(formatoDia +"/" + fomatoMonth +"/" + year);
                            //fec_fin.setText(dayOfMonth + "/" + (month+1) + "/" + year);

                        }
                    }, anio2, mes2, dia2);
                    datePickerDialog.show();
                }
            }
        });
        //BTON DE BUSQUEDA
        buscar = fragment.findViewById(R.id.btn_buscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listaMercancia.clear();

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
                recyclerMercancias.setLayoutManager(gridLayoutManager);

                ConnectivityManager conn = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = conn.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()) {
                    if (fec_ini.length() == 0 || fec_fin.length() == 0){
                        Toast.makeText(getContext(), "Ingrese Fecha", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        cargaWebService();
                    }
                }
                else{
                    img_no_con.setVisibility(View.VISIBLE);
                }
            }
        });

        Typeface fuente = Typeface.createFromAsset(getContext().getAssets(),"fonts/SpindleRefined-Regular.otf" );
        ConnectivityManager conn = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {

        }
        else{
            img_no_con.setVisibility(View.VISIBLE);
        }
        adapter = new ImagenesAdapter(listaMercancia, getContext());
        
        return fragment;

    }

    private void cargaWebService() {
        progress = new ProgressDialog(getContext());
        progress.setMessage("Consulta");
        progress.show();

        String url = "http://sistemasdecontrolderiego.esy.es/Consulta_Mercancia_General.php?as_plaza="+plaza+"&fecha_inicial="+fechasinicial+"&fecha_final="+fechafinal;
        Log.i("error", url);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Sin registros", Toast.LENGTH_SHORT).show();
        System.out.println();
        img_no_con.setImageResource(R.drawable.search);
        img_no_con.setVisibility(View.VISIBLE);
        Log.d("Error", "Error no reconocido");
        progress.hide();
    }

    @Override
    public void onResponse(JSONObject response) {
        Usuario usuario = null;
        JSONArray jsonArray = response.optJSONArray("usuario");
        try {

            for (int i = 0 ; i<jsonArray.length();i++){
                usuario = new Usuario();
                JSONObject  jsonObject = null;

                    jsonObject = jsonArray.getJSONObject(i);

                    /*usuario.setId_merca(jsonObject.optInt("id_merca"));

                    usuario.setN_merca(jsonObject.optString("n_merca"));

                    usuario.setCliente(jsonObject.optString("cliente"));

                    usuario.setBuque(jsonObject.optString("buque"));

                    usuario.setFactura(jsonObject.optString("factura"));

                    //usuario.setDesc_merca(jsonObject.optString("desc_merca"));

                    usuario.setFecha_reg(jsonObject.optString("fecha_reg"));*/

                    listaMercancia.add(usuario);
                }
                progress.hide();
            final ImagenesAdapter mercanciasAdapter = new ImagenesAdapter(listaMercancia, getContext());
            recyclerMercancias.setAdapter(mercanciasAdapter);


            mercanciasAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    progress.show();
                    int identificador;
                    img_no_con.setVisibility(View.INVISIBLE);
                    //identificador = listaMercancia.get(recyclerMercancias.getChildAdapterPosition(v)).getId_merca();
                    identificador = 1;
                    String url2 = "http://sistemasdecontrolderiego.esy.es/Consultar_Imagenes_Nombre.php?id_merca="+identificador;
                    JsonObjectRequest  jsonObjectRequest2;
                    listaMercancia.clear();
                    jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response2) {
                            Usuario usuario3 = null;
                            JSONArray jsonArray3 = response2.optJSONArray("usuario");
                            try{
                                for(int i= 0; i<jsonArray3.length(); i++){
                                    usuario3 = new Usuario();
                                    JSONObject jsonObject2 = null;
                                        jsonObject2 = jsonArray3.getJSONObject(i);
                                        usuario3.setUrl_de_imagen(jsonObject2.optString("url_de_imagen"));
                                        listaMercancia.add(usuario3);


                                }
                                progress.hide();

                                ListaImagenesAdapterConsultaMensaje listAdapter = new ListaImagenesAdapterConsultaMensaje(listaMercancia, getContext());
                                //GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
                                //recyclerMercancias.setLayoutManager(gridLayoutManager);
                                recyclerMercancias.setAdapter(listAdapter);
                                listAdapter.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Reportes2Fragment rp = new Reportes2Fragment();
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.contenedor, rp);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }
                                });
                               ImageView img;

                            }catch (Exception e){
                                progress.hide();
                                Toast.makeText(getContext(), "Mostrar Url catch" +e,Toast.LENGTH_SHORT).show();
                                //Log.i("error", "error"+listaMercancia.get(recyclerMercancias.getChildLayoutPosition(v)).getUrl_de_imagen());
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    requestQueue.add(jsonObjectRequest2);
                }
            });
            recyclerMercancias.setAdapter(mercanciasAdapter);

        }
        catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No Hay Registros", Toast.LENGTH_SHORT).show();

            progress.hide();
        }
    }


    public interface OnFragmentInteractionListener {
    }
}
