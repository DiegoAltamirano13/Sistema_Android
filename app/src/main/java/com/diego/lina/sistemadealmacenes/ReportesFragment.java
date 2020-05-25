package com.diego.lina.sistemadealmacenes;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diego.lina.sistemadealmacenes.Adaptador.ListaImagenesAdapterConsulta;
import com.diego.lina.sistemadealmacenes.ClassCanvas.ClassConection;
import com.diego.lina.sistemadealmacenes.Entidades.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ReportesFragment extends Fragment{
    EditText n_merca, n_buque, n_factura;
    TextView desc_merca, fecha_reg;
    TextView reg;
    Button consultar;
    ProgressDialog progress;

    //Imagene
    RecyclerView recyclerViewRep;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    JsonObjectRequest jsonObjectRequest2;
    ArrayList<Usuario>listiMG;

    private Spinner spinner;
    ArrayList<String>clientes;
    String plaza;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_reportes, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        plaza = preferences.getString("as_plaza_u", "NO TIENE");

        //Campos para busqueda
        n_merca = fragment.findViewById(R.id.n_merca);
        n_buque = fragment.findViewById(R.id.buque);
        n_factura = fragment.findViewById(R.id.factura);
        //Mostrar
        desc_merca = fragment.findViewById(R.id.desc_merca);
        fecha_reg = fragment.findViewById(R.id.fecha_reg);

        clientes = new ArrayList<>();
        spinner = fragment.findViewById(R.id.spinner);
        listar();

        consultar = fragment.findViewById(R.id.consultar);
        recyclerViewRep = fragment.findViewById(R.id.recyclerImg);
        recyclerViewRep.setHasFixedSize(true);
        listiMG = new ArrayList<>();
        recyclerViewRep.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        request =Volley.newRequestQueue(getContext());
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });

        Log.i("Tamaño", String.valueOf(listiMG.size()));


        return  fragment;
    }

    private void listar() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ClassConection.URL_WEB_SERVICES + "lista-cliente.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("usuario");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String country = jsonObject1.getString("V_RAZON_SOCIAL");
                        clientes.add(country);
                    }
                    spinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, clientes));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void validarDatos(){
        String n_mercas = n_merca.getText().toString();
        String n_buques = n_buque.getText().toString();
        String n_facturas = n_factura.getText().toString();
        //Validacion por patrones
        boolean a = esMercaValida(n_mercas);
        boolean b = esBuquesValida(n_buques);
        boolean c = esFacturaValida(n_facturas);
        if (n_buque.getText().toString() == "" && n_factura.getText().toString() == "" && n_merca.getText().toString() == ""){
            a = false;
            b = false;
            c= false;
        }
        else {
            a = true;
            b = true;
            c = true;
        }
        if (a && b && c){
            progress = new ProgressDialog(getContext());
            progress.setMessage("Consultando..");
            progress.show();
            Cargar_web_Service();
        }
    }

    private boolean esMercaValida(String n_mercas) {
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_mercas).matches()) {
            if (n_merca.getText().toString().isEmpty()){

            }
            else {
                n_merca.setError("Contenedor invalido");
                return false;
            }
        }
        else {
            n_merca.setError(null);
        }
        return true;
    }

    private boolean esBuquesValida(String n_buques) {
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_buques).matches()) {
            if (n_buque.getText().toString().isEmpty()){

            }
            else {
                n_buque.setError("Contenedor invalido");
                return false;
            }
        }
        else {
            n_buque.setError(null);
        }
        return true;
    }

    private boolean esFacturaValida(String n_facturas) {
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_facturas).matches()) {
            if (n_factura.getText().toString().isEmpty()){

            }
            else{
            n_factura.setError("Contenedor invalido");
            return false;
            }
        }
        else {
            n_factura.setError(null);
        }
        return true;
    }


    private void Cargar_web_Service() {
        listiMG.clear();
        String cliente = spinner.getSelectedItem().toString();
        String url = "http://sistemasdecontrolderiego.esy.es/Consulta_Mercancias_Nombre.php?n_merca="+n_merca.getText().toString()+"&n_buque="+n_buque.getText().toString()+"&n_factura="+n_factura.getText().toString()+"&cliente="+cliente+"&plaza="+plaza;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final Usuario miUsuario= new Usuario();
                JSONArray json = response.optJSONArray("usuario");
                JSONObject jsonObject = null;

                try {
                    jsonObject = json.getJSONObject(0);
                    miUsuario.setId_merca(jsonObject.optInt("id_merca"));
                    miUsuario.setN_merca(jsonObject.optString("n_merca"));
                    miUsuario.setDesc_merca(jsonObject.optString("desc_merca"));
                    miUsuario.setFecha_reg(jsonObject.optString("fecha_reg"));

                    miUsuario.setBuque(jsonObject.optString("buque"));
                    miUsuario.setFactura(jsonObject.optString("factura"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int identificador = miUsuario.getId_merca();
                n_merca.setText(miUsuario.getN_merca());
                desc_merca.setText(miUsuario.getDesc_merca());
                fecha_reg.setText(miUsuario.getFecha_reg());

                n_buque.setText(miUsuario.getBuque());
                n_factura.setText(miUsuario.getFactura());

                String url2 = "http://sistemasdecontrolderiego.esy.es/Consultar_Imagenes_Nombre.php?id_merca="+identificador;
                jsonObjectRequest2 =  new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response2) {
                        Usuario usuario2 = null;
                        JSONArray jsonArray2 = response2.optJSONArray("usuario");

                        try{

                            for (int i=0; i<jsonArray2.length();i++){
                                usuario2 = new Usuario();
                                JSONObject jsonObject2 = null;
                                    jsonObject2 = jsonArray2.getJSONObject(i);
                                    usuario2.setUrl_de_imagen(jsonObject2.optString("url_de_imagen"));
                                    listiMG.add(usuario2);

                            }
                            progress.hide();
                            ListaImagenesAdapterConsulta lista_img = new ListaImagenesAdapterConsulta(listiMG, getContext());
                            recyclerViewRep.setAdapter(lista_img);

                        }catch (Exception e){
                            progress.hide();
                            Toast.makeText(getContext(), "Mostrar Url catch" +e,Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hide();
                        Toast.makeText(getContext(), "Mostrar Url error",Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
                request.add(jsonObjectRequest2);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Error de Conexión", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
            }
        });
        request.add(jsonObjectRequest);
        //progress.hide();
    }
}
