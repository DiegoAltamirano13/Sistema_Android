package com.diego.lina.sistemadealmacenes;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class principal_pagina_menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView nombre;
    //Pruebas
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_pagina_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Poner nombre de usuario
        View navHeader = navigationView.getHeaderView(0);
        nombre = navHeader.findViewById(R.id.as_usr_nombre2);
        //= findViewById(R.id.as_usr_nombre2);
        Intent intent = getIntent();
        String as_usr_nombre = intent.getStringExtra("as_usr_nombre");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contenedor, new ImportFragment()).commit();

        nombre.setText(as_usr_nombre);

    }


    @SuppressLint("ResourceType")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_camera) {
            // Handle the camera action
            //fragmentManager.beginTransaction().replace(R.id.contenedor, new ImportFragment()).commit();
            ImportFragment importFragment = new ImportFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.addToBackStack(null);
            transaction.replace(R.id.contenedor, importFragment);
            transaction.commit();
        } else if (id == R.id.nav_add) {
            //fragmentManager.beginTransaction().replace(R.id.contenedor, new registra_mas_imagenes()).commit();
            registra_mas_imagenes registraMasImagenes = new registra_mas_imagenes();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.addToBackStack(null);
            transaction.replace(R.id.contenedor, registraMasImagenes);
            transaction.commit();
        } else if (id == R.id.nav_gallery) {
            //fragmentManager.beginTransaction().replace(R.id.contenedor, new ReportesFragment()).commit();
            ReportesFragment reportesFragment = new ReportesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.addToBackStack(null);
            transaction.replace(R.id.contenedor, reportesFragment);
            transaction.commit();
        } else if (id == R.id.nav_gallery2) {
            //fragmentManager.beginTransaction().replace(R.id.contenedor, new Reportes2Fragment()).commit();
            Reportes2Fragment reportes2Fragment = new Reportes2Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.addToBackStack(null);
            transaction.replace(R.id.contenedor, reportes2Fragment);
            transaction.commit();
            //fragmentManager.beginTransaction().replace(R.id.contenedor, new Reportes2Fragment()).commit();
        }
        else if (id == R.id.nav_log){
            SharedPreferences preferences = getSharedPreferences("as_usr_nombre", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this).
                    setIcon(R.drawable.warning).
                    setTitle("Salir").
                    setMessage("¿Quiere salir de la aplicacion").
                    setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
