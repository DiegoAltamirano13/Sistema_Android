package com.example.servicio_clientes.ui.Login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.servicio_clientes.BuildConfig
import com.example.servicio_clientes.MainActivity
import com.example.servicio_clientes.R
import com.example.servicio_clientes.ui.API_REST.URL_CONN
import java.util.ArrayList
import com.example.servicio_clientes.ui.API_REST.URL_CONN.URL_LOGIN
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.kotlinx.serializer.KotlinxSerializer
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType


class Login : AppCompatActivity() {

    var plaza = ArrayList<String>()
    lateinit var editUsr: EditText
    lateinit var editPwd: EditText
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        //Obtengo los campos
        editUsr = findViewById<EditText>(R.id.usuario)
        editPwd = findViewById<EditText>(R.id.usuario_pwd)

        //Boton para acceder
        val btn_ing = findViewById<Button>(R.id.btn_aceptar)

        //Recuperar la version de app
        val txtVers = findViewById<TextView>(R.id.version_text)


        // Establecer el texto con la versión de la aplicación
        txtVers.text = "Version: ${BuildConfig.VERSION_NAME}"

        //Configurar texto para mostrar contraseña
        val togglePassword = findViewById<ToggleButton>(R.id.toggle_password_visibility)
        togglePassword.text = "Mostrar Contraseña"
        togglePassword.setButtonDrawable(R.drawable.ic_eye_password)

        //Configurar el boton de carga
        var progreso = findViewById<LottieAnimationView>(R.id.lottie_animation)

        togglePassword.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            if (isChecked){
                togglePassword.text = "Ocultar Contraseña"
                togglePassword.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_eye_password_close, 0)
                editPwd.transformationMethod = null
            }else{
                togglePassword.text = "Mostrar Contraseña"
                togglePassword.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_eye_password, 0)
                editPwd.transformationMethod = PasswordTransformationMethod.getInstance()
            }

            //Ponemos el puntero al final
            editPwd.setSelection(editPwd.text.length)

        }

        //Obtener contexto de app
        val context = applicationContext;

        //Modo noche o modo claro
        val currentNight = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val relativeLay = findViewById<RelativeLayout>(R.id.fondo_dark_light)

        if (currentNight == Configuration.UI_MODE_NIGHT_YES){
            relativeLay.setBackgroundResource(R.color.colorBackground)
        }else{
            relativeLay.setBackgroundResource(R.color.md_white_1000)
        }

        editPwd.setOnKeyListener { _, keyCode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    plaza = ArrayList()
                    true
                }else{
                    false
                }
        }

        //Enter para boton de aceptar
        var botonAccess = findViewById<Button>(R.id.btn_aceptar)
        botonAccess.setOnClickListener{
                    botonAccess.visibility = View.GONE
                    progreso.visibility = View.VISIBLE
                    iniciarSesion()

        };
        cargarPreferencias();

    }

    private fun cargarPreferencias() {
        //Leer informacion almacenada
        var preferences = getSharedPreferences("usr_name", Context.MODE_PRIVATE)
        var usr_name = preferences.getString("usr_name", "No ingreso")
        if (!"No ingreso".equals(usr_name)){
            var intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("usr_name", usr_name)
                    startActivity(intent)
        }
    }

    private suspend fun iniciarSesion(): String {
         if (editUsr.text.isEmpty() || editPwd.text.isEmpty()){
             //mostrarError("Error", "Usuario y contraseña no pueden estar vacios")
         }else{


             val result = login(editUsr.text.toString(), editPwd.text.toString())
         }

    }

    private suspend fun login(user: String, pwd: String): String {
        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }

        return try {
            val response: HttpResponse = client.post(URL_CONN.URL_LOGIN) {
                contentType(ContentType.Application.FormUrlEncoded)
                parameter("usr_usuario", user)
                parameter("usr_password", pwd)
            }

            val responseBody: String = response.body()
            responseBody
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        } finally {
            client.close()
        }
    }

}