package com.diego.lina.sistemadealmacenes.Adaptador;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class Adaptador_argo extends BroadcastReceiver {
    DownloadManager manager;
    long size;
    IntentFilter filter;

    private Context context1;
    private Activity activity;

    public Adaptador_argo(Activity activity) {
        this.context1=activity;
        this.activity=activity;
        filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public void Descargar(){
        String url = "";
        DownloadManager.Request myReq;

        manager= (DownloadManager) context1.getSystemService(context1.DOWNLOAD_SERVICE);
    }
}
