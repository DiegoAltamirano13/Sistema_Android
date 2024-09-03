package com.diego.lina.sistemadealmacenes;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diego.lina.sistemadealmacenes.Adaptador.ListaImagenesAdapter;
import com.diego.lina.sistemadealmacenes.ClassCanvas.ClassConection;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.mail.Session;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ImportFragment extends Fragment {
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
    EditText content, factura, cod_bars, desc_merca, cliente, fecha;
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


    ImageButton imgBtnScan;

    //Correo
    String correo ="arcuos1113@gmail.com";;
    String psw = "Diegoalta13#";
    Session session;

    //PLAZA Y CORREO DE LAS PERSONAS A LAS QUE SE LES ENVIARA EL CORREO
    String plaza;
    String correo_usuario;

    //Actualizador
    private Context context;

    //Spinner plaza
    private Spinner spinnerPlaza;
    ArrayList<String>plazas;
    //Spinner solicitudes
    private  Spinner spinnerSolicitud;
    ArrayList<String> arraySolicitud;
    //RADIO BUTTON
    RadioButton carga, descarga;
    RadioButton nacional, fiscal;
    //sCANNER
    private int CODE_SCAN = 1;
    public String nombreImagen;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        String usuario = preferences.getString("as_usr_nombre", "No estas logueado");
        plaza = preferences.getString("as_plaza_u", "No estas logueado");
        correo_usuario = preferences.getString("correo_usuario", "sin correo");

        //Asignar variables con interfaz
        View fragmento =  inflater.inflate(R.layout.fragment_import, container, false);
        reg = fragmento.findViewById(R.id.reg);
        //Campos de registro
        content= fragmento.findViewById(R.id.content);
        factura = fragmento.findViewById(R.id.factura);
        cod_bars = fragmento.findViewById(R.id.cod_bars);
        desc_merca = fragmento.findViewById(R.id.desc_merca);

        //Botones de envio
        btn_registro = fragmento.findViewById(R.id.registrar);
        btn_imagen = fragmento.findViewById(R.id.btnFoto);

        //Volley Library
        request = Volley.newRequestQueue(getContext());
        listiMGURI = new ArrayList<>();
        imageView2 = fragmento.findViewById(R.id.imagen_firma);


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
        //Creacion de scanners
        imgBtnScan = fragmento.findViewById(R.id.scanerCB);
        imgBtnScan.setOnClickListener(mOnClickListener);

        //Plaza
        spinnerPlaza = fragmento.findViewById(R.id.spinner_plaza);
        plazas = new ArrayList<>();
        listarPlaza();
        //Solicitud
        spinnerSolicitud = fragmento.findViewById(R.id.spinner_solicitud);
        arraySolicitud = new ArrayList<>();

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

        //RB
        carga = fragmento.findViewById(R.id.carga);
        descarga = fragmento.findViewById(R.id.descarga);
        nacional = fragmento.findViewById(R.id.nacional);
        fiscal = fragmento.findViewById(R.id.fiscal);
        cliente = fragmento.findViewById(R.id.cliente);
        fecha = fragmento.findViewById(R.id.fecha_reg);
        return fragmento;
    }

    private void informacionExtra() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        SharedPreferences preferences = this.getActivity().getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        final String sp_plaza, sp_cliente_num;
        sp_plaza = spinnerPlaza.getSelectedItem().toString();
        sp_cliente_num = spinnerSolicitud.getSelectedItem().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ClassConection.URL_WEBB_SERVICES + "solicitud_extra.php?nombreplaza="+sp_plaza+"&solicitud="+sp_cliente_num, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("Error", ClassConection.URL_WEBB_SERVICES + "solicitud_extra.php?nombreplaza="+sp_plaza+"&solicitud="+sp_cliente_num);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("usuario");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        content.setText(jsonObject1.getString("CONTENEDOR"));
                        factura.setText(jsonObject1.getString("FACTURA"));
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

                        cliente.setText(jsonObject1.getString("NOMBRECL"));
                        fecha.setText(jsonObject1.getString("FECHA"));
                    }
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ClassConection.URL_WEBB_SERVICES + "solicitudes.php?nombreplaza="+sp_plaza, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("Error", ClassConection.URL_WEBB_SERVICES + "solicitudes.php?plaza="+sp_plaza);
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

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(ImportFragment.this);
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


    private void validarCampos() {
        String n_mercas  = content.getText().toString();
        String n_factura = factura.getText().toString();
        int valor = listiMGURI.size();


        boolean a = validar_merca(n_mercas);
        boolean c = validarFactura(n_factura);
        boolean f = validarImagenes(valor);

        if (a  && c && f  ){
            cargarService();
        }
    }

    //Validacion de mercancia
    private boolean validar_merca(String n_mercas) {
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_mercas).matches() || n_mercas.length()>30) {
            content.setError("Descripcion invalida");
            return false;
        }
        else {
            content.setError(null);
        }
        return true;
    }


    private boolean validarFactura(String n_factura){
        Pattern patron = Pattern.compile("^[a-zA-ZÁáÀàÉéÈèÍíÌìÓóÒòÚúÙùÑñüÜ00-9ñN !@#\\$%\\^&\\*\\?_~\\/]+$");
        if (!patron.matcher(n_factura).matches() || n_factura.length()>30) {
            factura.setError("Error En Factura");
            return false;
        }
        else {
            factura.setError(null);
        }
        return true;
    }

    private boolean validarImagenes(int valor) {
        if (valor == 0){
            Toast.makeText(getContext(), "Error no ha tomado imagenes ", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return  true;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    MediaScannerConnection.scanFile(getContext(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("Path imagen", ""+path);
                        }
                    });
                }
                bitmap = BitmapFactory.decodeFile(path);

                if(bitmap == null){
                    //foto.setImageResource(R.drawable.img_base);
                }
                else {
                    listiMGURI.add(Uri.parse(path));
                    adapter.notifyDataSetChanged();
                }
            }
        }
        //bitmap = redimensionarImagen(bitmap, 600, 600);
    }


    int x;


    private void cargarService() {

        final String variable1 = content.getText().toString();
        final  String var3 = factura.getText().toString();

        if (variable1.length() == 0 ||  var3.length() == 0 ) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
            builder.setMessage("Es necesario llenar todos los campos").setNegativeButton("Aceptar", null)
                    .create().show();
        }
        else {
            progress = new ProgressDialog(getContext());
            progress.setMessage("Procesando registro...");
            progress.show();
            final String url = "http://187.141.70.75:8181/android_app/FotosMercancia/RegistroInfoImagenes.php";

            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //envioCorreo();
                    if (response.trim().equalsIgnoreCase("registra")) {
                        for (int x = 0; x < listiMGURI.size(); x++){
                                nombreImagen = String.valueOf(x);
                                Log.e("VALOR IMAGEN", nombreImagen);
                                enviarImagenes(x);

                        }


                    } else {
                        Toast.makeText(getContext(), "NO SE LLEVO ACABO EL REGISTRO", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.hide();
                    Toast.makeText(getContext(), "Conexion incorrecta" + error, Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String plazaN = spinnerPlaza.getSelectedItem().toString();
                    String vehiculoN = spinnerSolicitud.getSelectedItem().toString();
                    String clienteN = cliente.getText().toString();
                    String ncontent = content.getText().toString();
                    String nfactura = factura.getText().toString();
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
                    String des_merc = desc_merca.getText().toString();
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("nplaza", plazaN);
                    parametros.put("vehiculon", vehiculoN);
                    parametros.put("clienten", clienteN);
                    parametros.put("ncontent", ncontent);
                    parametros.put("nfactura", nfactura);
                    parametros.put("tipocargan", tipoCarga);
                    parametros.put("tiporegimen", tipoRegimen);
                    parametros.put("ncodbar", ncodbar);
                    parametros.put("des_merc", des_merc);
                    parametros.put("fecha_real", fecha.getText().toString());
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
        cod_bars.setText("");
        desc_merca.setText("");
        Toast.makeText(getContext(), "Registro Correcto", Toast.LENGTH_LONG).show();
    }



    private void enviarImagenes(final int x) {

        final  String url2 = "http://187.141.70.75:8181/android_app/FotosMercancia/RegistroImagenes.php";
        stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Aviso Imagen", response);
                if (response.trim().equalsIgnoreCase("registra")) {
                    if (x == listiMGURI.size()-1){
                        limpiarTodo();
                    }
                }
                else {
                    progress.hide();
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
                String plazaN = spinnerPlaza.getSelectedItem().toString();
                String vehiculoN = spinnerSolicitud.getSelectedItem().toString();
                String clienteN = cliente.getText().toString();
                String ncontent = content.getText().toString();
                String nfactura = factura.getText().toString();
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
                String des_merc = desc_merca.getText().toString();
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

        return false; //implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MIS_PERMISOS){
            if(grantResults.length==2
                    && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){//el dos representa los 2 permisos
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