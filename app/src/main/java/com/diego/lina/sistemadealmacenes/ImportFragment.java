package com.diego.lina.sistemadealmacenes;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;

import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diego.lina.sistemadealmacenes.Actualizador.AutoUpdater;
import com.diego.lina.sistemadealmacenes.Adaptador.ListaImagenesAdapter;
import com.diego.lina.sistemadealmacenes.ClassCanvas.ClassConection;
import com.sun.mail.iap.ByteArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;

public class ImportFragment extends Fragment{
    //Validaciones de imagenes Guardar
    private static final String CARPETA_PRINCIPAL = ".imagenes_argo/";
    private static final String CARPETA_IMAGEN = "mercancia_imagenes";
    private static final String DIRECTORIO_IMAGEN =CARPETA_PRINCIPAL+CARPETA_IMAGEN;
    private String path;
    File fileImagen;
    Bitmap bitmap;
    Bitmap bitmap2;
    //Finalizar
    //Permisos android 6+
    private final int MIS_PERMISOS = 100;
    //Finalizar
    //imagenes opciones
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    //Finalizar
    //Campos
    EditText n_merca, desc_merca, nbuque, nfactura;
    FloatingActionButton btn_registro;
    StringRequest stringRequest;
    TextView reg;
    //finalizar
    //Detalles de conexion y carga
    ProgressDialog progress;
    RequestQueue request;
    //Finalizar
    RecyclerView recyclerView;

    //ARREGLO DE IMAGENES
    ArrayList<Uri> listiMGURI = new ArrayList<>();
    ListaImagenesAdapter adapter ;
    Dialog dialog;
    ImageView imageView;
    FloatingActionButton btn_imagen;
    Button firmas;
    ImageView imageView2;


    //Correo
    String correo ="arcuos1113@gmail.com";;
    String psw = "Diegoalta13#";
    Session session;

    //PLAZA Y CORREO DE LAS PERSONAS A LAS QUE SE LES ENVIARA EL CORREO
    String plaza;
    String correo_usuario;

    //Actualizador
    private AutoUpdater updater;
    private Context context;

    private Spinner spinner;
    ArrayList<String>clientes;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //ACTUALIZACION
        Toast.makeText(getContext(),"Buscando Actualizaciones",Toast.LENGTH_LONG);
        try {
            //comenzarActualizacion()
            Toast.makeText(getContext(),"AVISO",Toast.LENGTH_LONG);
        }catch (Exception ex){
            Toast.makeText(getContext(),ex.getMessage(),Toast.LENGTH_LONG);
        }

        //Shared preferences
        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        String usuario = preferences.getString("as_usr_nombre", "No estas logueado");
        plaza = preferences.getString("as_plaza_u", "No estas logueado");
        correo_usuario = preferences.getString("correo_usuario", "sin correo");

        //Asignar variables con interfaz
        View fragmento =  inflater.inflate(R.layout.fragment_import, container, false);
        reg = fragmento.findViewById(R.id.reg);
        //Campos de registro
        n_merca= fragmento.findViewById(R.id.n_merca);
        nbuque = fragmento.findViewById(R.id.buque);
        nfactura = fragmento.findViewById(R.id.factura);
        desc_merca = fragmento.findViewById(R.id.desc_merca);
        //Botones de envio
        btn_registro = fragmento.findViewById(R.id.registrar);
        btn_imagen = fragmento.findViewById(R.id.btnFoto);

        //Volley Library
        request = Volley.newRequestQueue(getContext());
        listiMGURI = new ArrayList<>();
        imageView2 = fragmento.findViewById(R.id.imagen_firma);

        clientes = new ArrayList<>();
        spinner = (Spinner)fragmento.findViewById(R.id.spinner);
        listar();


        if(solicitaPermisosVersionesSuperiores()){
            btn_imagen.setEnabled(true);
        }else{
            btn_imagen.setEnabled(false);
        }


        if (getArguments()!= null){
            byte[] byteArray = getArguments().getByteArray("Imagen");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray,0, byteArray.length);
            Toast.makeText(getContext(), "ESTO"+getArguments().getByteArray("Imagen"), Toast.LENGTH_LONG);
            imageView2.setImageBitmap(bmp);

        }
        //En caso de dar clic sobre el boton de foto
        btn_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamera();
            }
        });

        //En caso de dar click sobre el boton de registro
        btn_registro.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    validarCampos();
                }
            });

        recyclerView = fragmento.findViewById(R.id.recyclerImag);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter  = new  ListaImagenesAdapter(listiMGURI);

        recyclerView.setAdapter(adapter);
        //En caso de dar click sobre alguna imagen.
        adapter.setOnClickListener(new View.OnClickListener() {

              @Override
              public void onClick(View v) {

                  mostrarDialog(recyclerView.getChildAdapterPosition(v));


              }

        });



        return fragmento;
    }

    private void mostrarDialog(final int i) {


        dialog = new Dialog(getContext(), android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mensaje_imagen);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        imageView = dialog.findViewById(R.id.imagen);
        imageView.setImageURI(listiMGURI.get(i));
        Button btn_delete = dialog.findViewById(R.id.btn_del);
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
                listiMGURI.remove(i);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });




        dialog.show();

    }


    public void listar() {
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

    private void validarCampos() {
        String n_mercas  = n_merca.getText().toString();
        String desc_mercas = desc_merca.getText().toString();
        String n_buque = nbuque.getText().toString();
        String n_factura = nfactura.getText().toString();
        int valor = listiMGURI.size();


        boolean a = validar_merca(n_mercas);
        boolean b = validar_desc_merca(desc_mercas);
        boolean c = validarImagenes(valor);
        boolean d = validarBuque(n_buque);
        boolean e = validarFactura(n_factura);

        if (a && b && c && d && e){
            cargarService();
        }
    }

    private boolean validarImagenes(int valor) {
        if (valor == 0){
            Toast.makeText(getContext(), "Error no ha tomado imagenes ", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return  true;
    }

    private boolean validar_desc_merca(String desc_mercas) {
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(desc_mercas).matches() || desc_mercas.length()>160) {
            desc_merca.setError("Contenedor invalido");
            return false;
        }
        else {
            desc_merca.setError(null);
        }
        return true;
    }

    private boolean validar_merca(String n_mercas) {
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_mercas).matches() || n_mercas.length()>30) {
            n_merca.setError("Descripcion invalida");
            return false;
        }
        else {
            n_merca.setError(null);
        }
        return true;
    }

    private boolean validarBuque(String n_buque){
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_buque).matches() || n_buque.length()>30) {
            nbuque.setError("Error En Buque");
            return false;
        }
        else {
            nbuque.setError(null);
        }
        return true;
    }
    private boolean validarFactura(String n_factura){
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_factura).matches() || n_factura.length()>30) {
            nfactura.setError("Error En Factura");
            return false;
        }
        else {
            nfactura.setError(null);
        }
        return true;
    }

    static String importFrag;
    public static ImportFragment entrar (String importFrag) {
        importFrag = importFrag;
        return new ImportFragment();
    }
    //Guardar instancia
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

    }

    //Abrir camara
    private void abrirCamera(){
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case COD_SELECCIONA:
                //En caso de no seleccionar una imagen te deja
                if (resultCode != RESULT_CANCELED) {
                    Uri miPath = data.getData();
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), miPath);

                        listiMGURI.add(miPath);
                        adapter.notifyDataSetChanged();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    //foto.setImageResource(R.drawable.img_base);
                    Toast.makeText(getContext(), "No seleccion de imagenes", Toast.LENGTH_SHORT).show();
                }
                break;
            case COD_FOTO:

                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Path imagen", ""+path);
                            }
                        });
                bitmap = BitmapFactory.decodeFile(path);

                if(bitmap == null){
                   //foto.setImageResource(R.drawable.img_base);
                }
                else {
                    redimensionarImagen(path);
                    listiMGURI.add(Uri.parse(path));
                    adapter.notifyDataSetChanged();
                }

            break;
        }
        //bitmap = redimensionarImagen(bitmap, 600, 600);
    }

    private void redimensionarImagen(String path) {
        Bitmap btm;
        Log.i("Patch" , path);
    }

    int x;


    private void cargarService() {

        final String variable1 = n_merca.getText().toString();
        final String variable3 = desc_merca.getText().toString();
        if (variable1.length() == 0 || variable3.length() == 0){
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
            builder.setMessage("Es necesario llenar todos los campos").setNegativeButton("Aceptar", null)
                    .create().show();
        }
        else {
            progress = new ProgressDialog(getContext());
            progress.setMessage("Procesando registro...");
            progress.show();
            final String url = "http://sistemasdecontrolderiego.esy.es/Registro_Mercancia.php";

            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //envioCorreo();
                    if (response.trim().equalsIgnoreCase("registra")) {
                        for (int x = 0; x < listiMGURI.size(); x++){
                        //while (x < listiMGURI.size()){
                            enviarImagenes(x);
                           // x++;
                        }


                    } else {
                        Toast.makeText(getContext(), "NO SE LLEVO ACABO EL REGISTRO", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.hide();
                    Toast.makeText(getContext(), n_merca.getText().toString() + "  " + desc_merca.getText().toString(), Toast.LENGTH_LONG).show();

                    Toast.makeText(getContext(), "Conexion incorrecta" + error, Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String merca = n_merca.getText().toString();
                    String des_merc = desc_merca.getText().toString();
                    String nbuques = nbuque.getText().toString();
                    String nfacturas = nfactura.getText().toString();
                    String cliente = spinner.getSelectedItem().toString();
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("n_merca", merca);
                    parametros.put("desc_merca", des_merc);
                    parametros.put("as_plaza", plaza);
                    parametros.put("nbuque", nbuques);
                    parametros.put("nfactura", nfacturas);
                    parametros.put("cliente", cliente);
                    return parametros;
                }
            };
            request.add(stringRequest);


        }
    }

    private void limpiarTodo() {
        progress.hide();
        listiMGURI.clear();
        adapter.notifyDataSetChanged();
        n_merca.setText("");
        nbuque.setText("");
        nfactura.setText("");
        desc_merca.setText("");
        Toast.makeText(getContext(), "Registro Correcto", Toast.LENGTH_LONG).show();
    }

    /*private void envioCorreo() {
        String d_email = "email@gmail.com",
                d_password = "Diegoalta13#",
                d_uname = "diegoaltamirano1113@gmail.com",
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
            session=Session.getDefaultInstance(properties, new Authenticator() {
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
                    ((MimeBodyPart) text).setText("Nombre de mercancia= "+ n_merca.getText() +" Descripcion mercancia= "+ desc_merca.getText(), "utf-8", "HTML");
                    BodyPart img_new = new MimeBodyPart();


                    MimeMultipart multipart = new MimeMultipart();
                    int y = 0;
                    while (y < listiMGURI.size()){
                        BodyPart adjunt = new MimeBodyPart();
                        adjunt.setDataHandler(new DataHandler(new FileDataSource(listiMGURI.get(y).toString())));
                        adjunt.setFileName(String.valueOf(listiMGURI.get(y)));
                        multipart.addBodyPart(adjunt);
                        message.setContent(multipart);
                        y++;
                    }
                    //multipart.addBodyPart(text);
                    ((MimeBodyPart) img_new).setText("<img src =\"http://sistemasdecontrolderiego.esy.es/img/1.jpg\">", "utf-8", "html");
                    multipart.addBodyPart(img_new);
                    message.setContent(multipart);
                    message.setContent(multipart, "text/html; charset=utf-8");



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
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    private void enviarImagenes(final int x) {

        final  String url2 = "http://sistemasdecontrolderiego.esy.es/controladores/Registro_Imagenes_Mercancia.php";
        stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equalsIgnoreCase("registra")) {
                    if (x == listiMGURI.size()-1){
                        limpiarTodo();

                    }
                }
                else {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
                Toast.makeText(getContext(), "Error" + error, Toast.LENGTH_LONG).show();
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                bitmap2 = BitmapFactory.decodeFile( new File(listiMGURI.get(x).toString()).toString());
                final String img = convertirImg(bitmap2);
                String merca = n_merca.getText().toString();
                String nbuques = nbuque.getText().toString();
                String nfacturas = nfactura.getText().toString();
                String cliente = spinner.getSelectedItem().toString();
                Map<String, String> parametro = new HashMap<>();
                parametro.put("img_url", img);
                parametro.put("n_merca", merca);
                parametro.put("nbuque", nbuques);
                parametro.put("nfactura", nfacturas);
                parametro.put("cliente", cliente);
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

    private boolean solicitaPermisosVersionesSuperiores() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){//validamos si estamos en android menor a 6 para no buscar los permisos
            return true;
        }

        //validamos si los permisos ya fueron aceptados
        if((getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&getContext().checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED){
            return true;
        }


        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)||(shouldShowRequestPermissionRationale(CAMERA)))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MIS_PERMISOS);
        }

        return false;//implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MIS_PERMISOS){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){//el dos representa los 2 permisos
                Toast.makeText(getContext(),"Permisos aceptados",Toast.LENGTH_SHORT);
                btn_imagen.setEnabled(true);
            }
        }else{
            solicitarPermisosManual();
        }
    }


    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }


    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }



}