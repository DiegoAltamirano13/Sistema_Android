package com.diego.lina.sistemadealmacenes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Struct;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static com.diego.lina.sistemadealmacenes.R.drawable.ic_action_cancel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_usr, et_password;
    Button btn_aceptar;

    //CircularProgressButton btn_aceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        et_usr = findViewById(R.id.usuario);
        et_password = findViewById(R.id.usuario_pwd);
        btn_aceptar = findViewById(R.id.btn_aceptar);

        //btn = findViewById(R.id.btn_id);

        btn_aceptar.setOnClickListener(this);

        btn_aceptar.setText("Aceptar");
        //btn_aceptar.revertAnimation();

        cargarPreferencias();
    }

    private void cargarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        String  as_usr_nombre =  preferences.getString("as_usr_nombre", "No existe");
        if (as_usr_nombre != "No existe"){
            Intent intent = new Intent(MainActivity.this, principal_pagina_menu.class);
            intent.putExtra("as_usr_nombre", as_usr_nombre);
            MainActivity.this.startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        btn_aceptar.setText("Entrando");
        //btn_aceptar.startAnimation();
        final SharedPreferences preferences = getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
        final String usr_usuario = et_usr.getText().toString();
        final String usr_password = et_password.getText().toString();

        if(usr_password.length() == 0|| usr_usuario.length() == 0){

            btn_aceptar.setText("Error");
        } else {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            String as_usr_nombre = jsonResponse.getString("as_usr_nombre");
                            String as_plaza_u = jsonResponse.getString("as_plaza_u");
                            String correo_usuario = jsonResponse.getString("correo_usuario");
                            Intent intent = new Intent(MainActivity.this, principal_pagina_menu.class);
                            intent.putExtra("as_usr_nombre", as_usr_nombre);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("as_usr_nombre", usr_usuario);
                            editor.putString("as_plaza_u", as_plaza_u);
                            editor.putString("correo_usuario", correo_usuario );
                            editor.commit();
                            MainActivity.this.startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Error No Se Encuentra Registrado")
                                    .setNegativeButton("Volver a intentar", null)
                                    .create().show();
                            //btn_aceptar.revertAnimation();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };


            LoginReq loginReq = new LoginReq(usr_usuario, usr_password, responseListener);

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(loginReq);

        }
    }
}
