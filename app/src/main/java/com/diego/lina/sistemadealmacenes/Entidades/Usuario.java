package com.diego.lina.sistemadealmacenes.Entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Usuario {
    private int id_merca;
    private String n_merca;
    private String est_merca;
    private String desc_merca;
    private Bitmap img_url;
    private String fecha_reg;
    private String url_de_imagen;
    private String buque;
    private String factura;
    private String cliente;


    public int getId_merca() {
        return id_merca;
    }

    public void setId_merca(int id_merca) {
        this.id_merca = id_merca;
    }

    public String getUrl_de_imagen() {
        return url_de_imagen;
    }

    public void setUrl_de_imagen(String url_de_imagen) {
        this.url_de_imagen = url_de_imagen;
    }

    public String getFecha_reg() {
        return fecha_reg;
    }

    public void setFecha_reg(String fecha_reg) {
        this.fecha_reg = fecha_reg;
    }

    public void setImg_url(Bitmap img_url) {
        this.img_url = img_url;
    }

    public String getN_merca() {
        return n_merca;
    }

    public void setN_merca(String n_merca) {
        this.n_merca = n_merca;
    }

    public String getEst_merca() {
        return est_merca;
    }

    public String getDesc_merca() {
        return desc_merca;
    }

    public void setDesc_merca(String desc_merca) {
        this.desc_merca = desc_merca;
    }

    public String getBuque() { return buque; }

    public void setBuque(String buque) { this.buque = buque; }

    public String getFactura() { return factura; }

    public void setFactura(String factura) { this.factura = factura; }


    public String getCliente() { return cliente;  }

    public void setCliente(String cliente) { this.cliente = cliente; }
}
