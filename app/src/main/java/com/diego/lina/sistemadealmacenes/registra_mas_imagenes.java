package com.diego.lina.sistemadealmacenes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class registra_mas_imagenes extends Fragment {
    private static final String CARPETA_PRINCIPAL = ".imagenes_argo/";
    private static final String CARPETA_IMAGEN = "mercancia_imagenes";
    private static final String DIRECTORIO_IMAGEN =CARPETA_PRINCIPAL+CARPETA_IMAGEN;
    private String path;
    File fileImagen;
    Bitmap bitmap;
    int identificador;
    ListaImagenesAdapterConsulta adapter;
    private static final int COD_FOTO = 20;
    EditText cliente, n_merca, n_buque, n_factura, cod_bars, fecha;
    EditText desc_Merca, fecha_reg;
    ImageButton imgBtnScan;
    com.getbase.floatingactionbutton.FloatingActionButton consultar;
    com.getbase.floatingactionbutton.FloatingActionButton addPhoto;
    com.getbase.floatingactionbutton.FloatingActionButton cerrar;
    com.getbase.floatingactionbutton.FloatingActionButton btnGuardar;
    com.getbase.floatingactionbutton.FloatingActionsMenu menu_fab;
    ProgressDialog progress;
    RecyclerView recyclerViewRep;
    String correo_usuario;

    // es new
    String plaza;
    private Spinner spinner;
    ArrayList<String>clientes;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    JsonObjectRequest jsonObjectRequest2;
    ArrayList<Usuario>listImg = new ArrayList<>();
    boolean enviarcorreo = false;

    //NUEVAS VARIABLES
    //Spinner plaza
    private Spinner spinnerPlaza;
    ArrayList<String>plazas;
    //Spinner solicitudes
    private  Spinner spinnerSolicitud;
    ArrayList<String> arraySolicitud;
    //RADIO BUTTON
    RadioButton carga, descarga;
    RadioButton nacional, fiscal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_registra_mas_imagenes, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        plaza = preferences.getString("as_plaza_u", "No estas logueado");
        correo_usuario = preferences.getString("correo_usuario", "sin correo");
        //Creacion de Ligue Back With Front

        spinnerPlaza = fragment.findViewById(R.id.spinner_plaza);
        plazas = new ArrayList<>();
        spinnerSolicitud = fragment.findViewById(R.id.spinner_solicitud);
        arraySolicitud = new ArrayList<>();
        cliente = fragment.findViewById(R.id.cliente);
        n_merca = fragment.findViewById(R.id.content);
        n_factura = fragment.findViewById(R.id.factura);
        fecha = fragment.findViewById(R.id.fecha_reg);
        carga = fragment.findViewById(R.id.carga);
        descarga = fragment.findViewById(R.id.descarga);
        nacional = fragment.findViewById(R.id.nacional);
        fiscal = fragment.findViewById(R.id.fiscal);
        cod_bars = fragment.findViewById(R.id.cod_bars);
        desc_Merca = fragment.findViewById(R.id.desc_merca);

        //Llenar Spinner
        listarPlaza();

        //Botones
        imgBtnScan = fragment.findViewById(R.id.scanerCB);
        imgBtnScan.setEnabled(false);
        consultar = fragment.findViewById(R.id.consultar);
        addPhoto = fragment.findViewById(R.id.addPhoto);
        cerrar = fragment.findViewById(R.id.cerrar);
        listImg = new ArrayList<>();
        //ADAPTER
        recyclerViewRep = fragment.findViewById(R.id.recyclerImg);
        //recyclerViewRep.setHasFixedSize(true);
        //
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerViewRep.setLayoutManager(gridLayoutManager);
        adapter  = new  ListaImagenesAdapterConsulta(listImg, getContext());


        menu_fab = fragment.findViewById(R.id.menu_fab);
        request = Volley.newRequestQueue(getContext());

        btnGuardar = (com.getbase.floatingactionbutton.FloatingActionButton) fragment.findViewById(R.id.btnGuardar);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
                enviarcorreo = true;
                menu_fab.collapse();
                //envioCorreo();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImagen();
                menu_fab.collapse();
                //menu_fab.collapse();
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mandar a consultar
                validarDatos();
                menu_fab.collapse();

            }
        });
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamera();

            }
        });

        setRetainInstance(true);
        imgBtnScan.setOnClickListener(mOnClickListener);

        //Al cambiar spinner
        spinnerPlaza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                arraySolicitud.clear();
                spinnerSolicitud.setAdapter(null);
                listarSolicitud();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSolicitud.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                informacionExtra();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return fragment;
    }

    private void listarPlaza() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        String usr_usuario = preferences.getString("as_usr_nombre", "No estas logueado");
        String usr_password = preferences.getString("as_password", "No estas logueado");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ClassConection.URL_WEBB_SERVICES + "plazas.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("usuario");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String country = jsonObject1.getString("V_RAZON_SOCIAL");
                        plazas.add(country);
                    }
                    spinnerPlaza.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, plazas));
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

    private void listarSolicitud() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        final String sp_plaza, sp_cliente_num;
        sp_plaza = spinnerPlaza.getSelectedItem().toString();
        sp_cliente_num =preferences.getString("as_cliente", "No Tiene Cliente");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ClassConection.URL_WEBB_SERVICES + "ConsultaInfoFotosHechas.php?nombreplaza="+sp_plaza, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("Error", ClassConection.URL_WEBB_SERVICES + "ConsultaInfoFotosHechas.php?plaza="+sp_plaza);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("usuario");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String country = jsonObject1.getString("SOLICITUD");
                        arraySolicitud.add(country);
                    }
                    spinnerSolicitud.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, arraySolicitud));
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

    private void informacionExtra() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        final String sp_plaza, sp_cliente_num;
        sp_plaza = spinnerPlaza.getSelectedItem().toString();
        sp_cliente_num = spinnerSolicitud.getSelectedItem().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ClassConection.URL_WEBB_SERVICES + "ConfultaInfoExtraFotos.php?nombreplaza="+sp_plaza+"&solicitud="+sp_cliente_num, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("Error", ClassConection.URL_WEBB_SERVICES + "solicitud_extra.php?nombreplaza="+sp_plaza+"&solicitud="+sp_cliente_num);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("usuario");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        n_merca.setText(jsonObject1.getString("CONTENEDOR"));
                        n_factura.setText(jsonObject1.getString("FACTURA"));
                        if (jsonObject1.getString("TIPO").equals("1")){
                            carga.setChecked(true);
                            descarga.setChecked(false);
                        }else if (jsonObject1.getString("TIPO").equals("2")){
                            carga.setChecked(false);
                            descarga.setChecked(true);
                        }

                        if (jsonObject1.getString("REGIMEN").equals("1")){
                            nacional.setChecked(true);
                            fiscal.setChecked(false);
                        }else if (jsonObject1.getString("REGIMEN").equals("2")){
                            fiscal.setChecked(true);
                            nacional.setChecked(false);
                        }
                        if (jsonObject1.getString("CODIGOBARS").equals("null") ){

                        }else {
                            cod_bars.setText(jsonObject1.getString("CODIGOBARS"));
                        }

                        cliente.setText(jsonObject1.getString("NOMBRECL"));
                        fecha.setText(jsonObject1.getString("FECHA"));

                    }
                    CargarWebService();
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


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(registra_mas_imagenes.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("ESCANEA CODIGO");
            integrator.setCameraId(0);
            integrator.setOrientationLocked(false);
            integrator.setBeepEnabled(false);
            integrator.setCaptureActivity(CaptureActivituPortrait.class);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        }
    };


    StringRequest stringRequest;

    private void subirImagen() {
        menu_fab.collapse();
        progress = new ProgressDialog(getContext());
        progress.setMessage("Procesando registro...");
        progress.show();

        final  String url2 = "http://187.141.70.75:8181/android_app/FotosMercancia/RegistroImagenes.php";
        stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("okokok",""+response);
                if (response.trim().equalsIgnoreCase("registra")) {
                    progress.dismiss();
                }
                else {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
                Log.e("Error",""+error);
                Toast.makeText(getContext(), "Error" + error, Toast.LENGTH_LONG).show();

            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.e("path",""+path);

                Bitmap bitmap2 = BitmapFactory.decodeFile( new File(path).toString());
                final String img = convertirImg(bitmap2);
                String plazaN = spinnerPlaza.getSelectedItem().toString();
                String vehiculoN = spinnerSolicitud.getSelectedItem().toString();
                String clienteN = cliente.getText().toString();
                String ncontent = n_merca.getText().toString();
                String nfactura = n_factura.getText().toString();
                String tipoCarga = "";

                if (carga.isChecked()){
                    tipoCarga = "1";
                }else if (descarga.isChecked()){
                    tipoCarga = "2";
                }

                String tipoRegimen = "";
                if (nacional.isChecked()){
                    tipoRegimen = "1";
                }else if (fiscal.isChecked()){
                    tipoRegimen = "2";
                }
                String ncodbar = cod_bars.getText().toString();
                String des_merc = desc_Merca.getText().toString();
                String nombreImagen = "R";

                Map<String, String> parametro = new HashMap<>();
                parametro.put("img_url", img);
                parametro.put("nplaza", plazaN);
                parametro.put("vehiculon", vehiculoN);
                parametro.put("clienten", clienteN);
                parametro.put("ncontent", ncontent);
                parametro.put("nfactura", nfactura);
                parametro.put("tipocargan", tipoCarga);
                parametro.put("tiporegimen", tipoRegimen);
                parametro.put("ncodbar", ncodbar);
                parametro.put("des_merc", des_merc);
                parametro.put("fecha_real", fecha.getText().toString());
                parametro.put("nombreImagens", nombreImagen);
                return parametro;

            }

        };
        request.add(stringRequest);

    }

    private String convertirImg(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, array);
        byte[] bytes = array.toByteArray();
        String img_string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return img_string;

    }

    //Funcion para validar datos
    private void validarDatos(){

        String n_mercas = n_merca.getText().toString();
        String n_buques = n_buque.getText().toString();
        String n_facturas = n_factura.getText().toString();
        String cod_barras = cod_bars.getText().toString();
        boolean a = esMercaValida(n_mercas);
        boolean b = esBuqueValido(n_buques);
        boolean c = esFacturaValida(n_facturas);
        boolean d = esCodBarsValida(cod_barras);

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
            progress.setMessage("Consultando...");
            progress.show();
            CargarWebService();
        }
    }

    private boolean esCodBarsValida(String cod_barras) {
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(cod_barras).matches()){
            if (cod_bars.getText().toString().isEmpty()){

            }else {
                cod_bars.setError("Contenedor invalido");
                return false;
            }
        }
        else{
            n_factura.setError(null);
        }
        return true;
    }

    private boolean esMercaValida(String n_mercas){
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_mercas).matches()){
            if (n_merca.getText().toString().isEmpty()){

            }else {
                n_merca.setError("Contenedor invalido");
                return false;
            }
        }
        else{
            n_merca.setError(null);
        }
        return true;
    }


    private boolean esBuqueValido(String n_buques){
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_buques).matches()){
            if (n_buque.getText().toString().isEmpty()){

            }else {
                n_buque.setError("Contenedor invalido");
                return false;
            }
        }
        else{
            n_buque.setError(null);
        }
        return true;
    }

    private boolean esFacturaValida(String n_facturas){
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_facturas).matches()){
            if (n_factura.getText().toString().isEmpty()){

            }else {
                n_factura.setError("Contenedor invalido");
                return false;
            }
        }
        else{
            n_factura.setError(null);
        }
        return true;
    }

    private void CargarWebService() {
        listImg.clear();
        adapter.notifyDataSetChanged();
        progress = new ProgressDialog(getContext());
        progress.setMessage("Consultando...");
        progress.show();
        menu_fab.collapse();
        listImg.clear();
        String sp_plaza;
        String sp_cliente_num;

        sp_plaza = spinnerPlaza.getSelectedItem().toString();
        sp_cliente_num = spinnerSolicitud.getSelectedItem().toString();

        String url2 = ClassConection.URL_WEBB_SERVICES + "Imagenes.php?nombreplaza="+sp_plaza+"&solicitud="+sp_cliente_num;
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
                                usuario2.setUrl_de_imagen(jsonObject2.optString("URL_IMAGEN"));
                                Log.e("Imagen",""+jsonObject2.optString("URL_IMAGEN"));
                                listImg.add(usuario2);
                                adapter.notifyDataSetChanged();
                            }
                            progress.hide();
                            ListaImagenesAdapterConsulta lista_img = new ListaImagenesAdapterConsulta(listImg, getContext());
                            recyclerViewRep.setAdapter(lista_img);

                            if (enviarcorreo)
                            {
                                envioCorreo();
                                enviarcorreo = false;
                            }


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


    private void abrirCamera(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Recupera donde se va a guardar la imagen
                File miArchivo = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
                //Valida que la foto haya sido tomada
                boolean isTomada = miArchivo.exists();
                if (isTomada == false) {
                    //Si no es tomada o guardada vuelve a intentar
                    isTomada = miArchivo.mkdirs();
                }
                if (isTomada == true) {
                    Long consecutivo = System.currentTimeMillis() / 1000;
                    String nombre = consecutivo.toString() + ".jpg";
                    //Guardar la imagen con un nombre
                    path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN
                            + File.separator + nombre;

                    fileImagen = new File(path);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));

                    //Valida la version de android
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        String authorities = getContext().getPackageName() + ".provider";
                        Uri imageUri = FileProvider.getUriForFile(getContext(), authorities, fileImagen);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                    } else {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
                    }

                    //Manda el valor COD_FOTO  al onActivityResult
                    startActivityForResult(intent, COD_FOTO);
                }
                Log.d("TAG HILO", "HILO TERMINADO");
            }
        }).start();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            Log.i("Data", String.valueOf(data));
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() != null){
                Toast.makeText(getContext(), result.getContents(), Toast.LENGTH_SHORT).show();
                cod_bars.setText(result.getContents());
            }
            else {
                Toast.makeText(getContext(), result.getContents(), Toast.LENGTH_SHORT).show();
                cod_bars.setText("Error no se escaneo nada");
            }
        }else {
            if (requestCode == COD_FOTO){

                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("Path imagen", ""+path);
                    }
                });
                bitmap = BitmapFactory.decodeFile(path);

                Log.e("Aviso bitman", path);
                if(bitmap == null){
                    Log.e("Aviso bitman", "aqui");
                    //foto.setImageResource(R.drawable.img_base);
                }
                else {
                    Log.e("aviso ", "" +path);
                    redimensionarImagen(path);
                    Uri urin;
                    String stringUri;
                    urin = Uri.parse(path);
                    redimensionarImagen(path);
                    Usuario usuario3 = null;
                    usuario3 = new Usuario();
                    stringUri = urin.toString();
                    Bitmap btm;
                    byte[] encodeByte = Base64.decode(path, Base64.DEFAULT);
                    btm = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    usuario3.setImg_url(btm);
                    usuario3.setUrl_de_imagen(path);
                    listImg.add(usuario3);
                    Log.e("Aviso bitman", "aqui2");
                    adapter.notifyDataSetChanged();
                    Log.e("Adapter ", "pa" +path);
                }
            }
        }
    }
    private void redimensionarImagen(String path){
        Bitmap btm;
        Log.i("Path", path);
    }

    int x;


    String correo ="diegoaltamirano9411@gmail.com";
    String psw = "Diegoalta13";
    Session session;

    private void envioCorreo() {
        Log.e("envioCorreo","envioCorreo");
        final String d_email = "email@gmail.com",
                d_password = "Diegoalta13",
                d_uname = "diegoaltamirano9411@gmail.com",
                d_host = "smtp.gmail.com",
                d_port  = "587", //465,587
                m_to = "email@gmail.com",
                m_subject = "Testing",
                m_text = "This is a test.";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port","587");
        properties.put("mail.smtp.socketFactory.class","java.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","587");
        try{
            session= Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correo, psw);
                }

            });
            try {
                if (session != null) {
                    javax.mail.Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(correo));
                    message.setSubject("CONTENEDOR  = "+n_merca.getText());
                    message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(correo_usuario));

                    BodyPart text = new MimeBodyPart();
                    //text.setText(n_merca.getText()+ "<b>" + desc_merca.getText()+ "</b>", "utf-8", "HTML");
                    ((MimeBodyPart) text).setText("Nombre de mercancia= "+ n_merca.getText() +" Descripcion mercancia= "+ identificador, "utf-8", "HTML");
                    BodyPart img_new = new MimeBodyPart();


                    MimeMultipart multipart = new MimeMultipart();

                    MimeBodyPart textPart = new MimeBodyPart();
                    textPart.setText("Imagenes Enbarque", "utf-8");

                    String  html= "";
                    for (int x = 0 ; x<listImg.size();x++)
                    {
                        String url = listImg.get(x).getUrl_de_imagen();
                        String nuevo = url.replace(" ", "%20");
                        Log.i("url", url);

                        //html += "<h1>" + "<img src ='"+listImg.get(x).getUrl_de_imagen()+"'>" + "</h1> <br>";
                        html += "<br> <img src ='"+nuevo+"' width = '200' height = '200'>";

                         Log.e("Enviar:",""+listImg.get(x).getUrl_de_imagen());

                    }

                    //html += "<h1><img src ='https://www.chiquipedia.com/imagenes/imagenes-amor20.jpg'></h1> <br>";
                    Log.e("html",""+html);



                    MimeBodyPart htmlPart = new MimeBodyPart();
                    htmlPart.setContent( html, "text/html; charset=utf-8" );

                    multipart.addBodyPart( textPart );
                    multipart.addBodyPart( htmlPart );
                    message.setContent( multipart );




                    //protocolo de mensaje
                    Transport transport = session.getTransport("smtps");
                    transport.connect(d_host, 465, d_uname, d_password);
                    transport.sendMessage(message,message.getAllRecipients());
                    transport.close();
                    //Transport.send(message);
                }
            }
            catch (MessagingException e ){
                e.printStackTrace();
                Log.e("Error",""+e.toString());
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e("Error2",""+e.toString());
        }
    }

}
