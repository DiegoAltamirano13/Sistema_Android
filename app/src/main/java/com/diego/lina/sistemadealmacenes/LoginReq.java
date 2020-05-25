package com.diego.lina.sistemadealmacenes;

import android.net.sip.SipSession;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LoginReq extends StringRequest {

    //private  static  final  String LOGIN_REQUEST_URL = "http://sistemasdecontrolderiego.esy.es/Login.php";
    private  static  final  String LOGIN_REQUEST_URL = "https://argodashboard.dnsalias.org/android_app/Imagenes_Llantas/Login.php";

    private Map<String , String>params;
    public  LoginReq(String usr_usuario, String usr_password, Response.Listener<String> listener){
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("usr_usuario", usr_usuario);
        params.put("usr_password", usr_password);
    }
    @Override
    public Map<String, String>getParams(){return params;}

}
