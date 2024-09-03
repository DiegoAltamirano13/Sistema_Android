package com.diego.lina.sistemadealmacenes.Entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Usuario {

    private String contenedor;
    private String buque;
    private String factura;
    private String cod_bars;
    private String desc_merca;
    private String url_de_imagen;
    private Bitmap img_url;

    public String getContenedor() {
        return contenedor;
    }

    public void setContenedor(String contenedor) {
        this.contenedor = contenedor;
    }

    public String getBuque() { return buque; }

    public void setBuque(String buque) { this.buque = buque; }

    public String getFactura() { return factura; }

    public void setFactura(String factura) { this.factura = factura; }

    public String getCod_bars() {
        return cod_bars;
    }

    public void setCod_bars(String cod_bars) {
        this.cod_bars = cod_bars;
    }

    public String getDesc_merca() {
        return desc_merca;
    }

    public void setDesc_merca(String desc_merca) {
        this.desc_merca = desc_merca;
    }

    public String getUrl_de_imagen() {
        return url_de_imagen;
    }

    public void setUrl_de_imagen(String url_de_imagen) {
        this.url_de_imagen = url_de_imagen;
    }


    public Bitmap getImg_url() {
        return img_url;
    }

    public void setImg_url(Bitmap img_url) {
        this.img_url = img_url;
    }
}
