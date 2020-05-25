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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    EditText n_merca, n_buque, n_factura;
    EditText desc_Merca, fecha_reg;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_registra_mas_imagenes, container, false);
        n_merca = fragment.findViewById(R.id.n_merca);
        n_buque = fragment.findViewById(R.id.n_buque);
        n_factura = fragment.findViewById(R.id.n_factura);
        desc_Merca = fragment.findViewById(R.id.desc_merca);
        fecha_reg = fragment.findViewById(R.id.fecha_reg);

    //Compartir preferencias sexuales
        listar();
        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        plaza = preferences.getString("as_plaza_u", "No estas logueado");

        correo_usuario = preferences.getString("correo_usuario", "sin correo");
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

        clientes = new ArrayList<>();
        spinner = (Spinner) fragment.findViewById(R.id.spinner);


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
        Log.i("Tamaño ", String.valueOf(listImg.size()));

        return fragment;
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


    StringRequest stringRequest;

    private void subirImagen() {
        menu_fab.collapse();
        progress = new ProgressDialog(getContext());
        progress.setMessage("Procesando registro...");
        progress.show();

        final  String url2 = "http://sistemasdecontrolderiego.esy.es/controladores/Registro_Imagenes_Mercancia.php";
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
                Map<String, String> parametro = new HashMap<>();
                parametro.put("img_url", img);
                parametro.put("id_merca", String.valueOf(identificador));
                parametro.put("n_merca", n_merca.getText().toString());
                parametro.put("nbuque", n_buque.getText().toString());
                parametro.put("nfactura", n_factura.getText().toString());
                parametro.put("cliente", spinner.getSelectedItem().toString());
                parametro.put("plaza", plaza);

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
        boolean a = esMercaValida(n_mercas);
        boolean b = esBuqueValido(n_buques);
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
            progress.setMessage("Consultando...");
            progress.show();
            CargarWebService();
        }
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
        menu_fab.collapse();
        listImg.clear();
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

                    //Campos extra
                    miUsuario.setBuque(jsonObject.optString("buque"));
                    miUsuario.setFactura(jsonObject.optString("factura"));
                    miUsuario.setCliente(jsonObject.optString("cliente"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                 identificador = miUsuario.getId_merca();
                n_merca.setText(miUsuario.getN_merca());
                desc_Merca.setText(miUsuario.getDesc_merca());
                fecha_reg.setText(miUsuario.getFecha_reg());

                //Campos extra
                n_buque.setText(miUsuario.getBuque());
                n_factura.setText(miUsuario.getFactura());
                //spinner.setSelection(miUsuario.getCliente());
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
                                Log.e("Imagen",""+jsonObject2.optString("url_de_imagen"));
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error de Conexión", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        request.add(jsonObjectRequest);
        //progress.hide();
    }// Mensaje de salida


    private void abrirCamera(){
        //recupera donde se va a guardar la imagen
        File miArchivo = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isTomada = miArchivo.exists();
        if (isTomada == false){
            //Es tomada
            isTomada = miArchivo.mkdirs();
        }
        if (isTomada == true){
            Long consecutivo = System.currentTimeMillis()/1000;
            String nombre = consecutivo.toString() +  ".jpg";
            path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN
                    + File.separator + nombre;
            fileImagen = new File(path);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));

            //Valida la version de android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                String authorities = getContext().getPackageName()+ ".provider";
                Uri imageUri = FileProvider.getUriForFile(getContext(), authorities, fileImagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            }
            startActivityForResult(intent, COD_FOTO);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            Log.i("Data", String.valueOf(data));


            switch (requestCode){
                case COD_FOTO:
                    MediaScannerConnection.scanFile(getContext(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.e("Path de imagen", ""+path);
                        }
                    });
                    bitmap = BitmapFactory.decodeFile(path);
                    if (bitmap == null){

                    }else {
                        redimensionarImagen(path);
                        Log.e("aviso ", "" +path);
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
                        adapter.notifyDataSetChanged();
                        Log.e("Adapter ", "pa" +path);
                    }
                    break;
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
