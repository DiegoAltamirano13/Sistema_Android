package com.diego.lina.sistemadealmacenes.ClassCanvas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.diego.lina.sistemadealmacenes.FirmaEnFragment;
import com.diego.lina.sistemadealmacenes.ImportFragment;
import com.diego.lina.sistemadealmacenes.R;
import com.diego.lina.sistemadealmacenes.principal_pagina_menu;

import java.io.ByteArrayOutputStream;

public class ClassCanvas extends View{

    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    Context context;
    private Paint paint;
    private float mx, my;
    private static final float TOLERANCIA = 5;
    ImportFragment importFragment = new ImportFragment();

    public ClassCanvas(Context c, AttributeSet attrs){
        super(c, attrs);
        context = c;
        path= new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4f);

    }

    protected void onSizeChanged(int w, int h , int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);

        bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    private void IniciaTouch(float x, float y){
        path.moveTo(x,y);
        mx = x;
        my = y;
    }
    private  void MovimientoTouch(float x , float y){
        float dx = Math.abs(x-mx);
        float dy = Math.abs(y-my);
        if(dx >= TOLERANCIA || dy >= TOLERANCIA){
            path.quadTo(mx,my,(x+mx)/2, (y+my)/2);
            mx= x;
            my = y;
        }
    }

    public void Limpiar(){
        path.reset();
        invalidate();
    }
    private void SueltaTouch(){
        path.lineTo(mx, my);
    }
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                IniciaTouch(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                MovimientoTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                SueltaTouch();
                invalidate();
                break;
        }
        return true;
    }
    public void Guardar(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0, byteArray.length);
        Bundle bundle = new Bundle();
        bundle.putByteArray("Imagen", byteArray);
        importFragment.setArguments(bundle);
        Toast.makeText(getContext(), "AVISO"+ byteArray, Toast.LENGTH_LONG).show();




    }
}
