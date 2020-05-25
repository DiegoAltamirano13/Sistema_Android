package com.diego.lina.sistemadealmacenes.Actualizador;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AutoUpdater {
    Context context;
    Runnable listener;

    private static final String INFO_FILE = "http://sistemasdecontrolderiego.esy.es/argo_verification_version.txt";

    private int currentVersionCode;

    private String currentVersionName;

    private int lastesVersionCode;

    private String lastesVersionName;

    private String downloadURL;

    public AutoUpdater(Context context) {
        this.context = context;
    }

    private void getData() {
        try {
            Log.d("AutoUpdater", "GetData");
            PackageInfo pckInfo = null;
            pckInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            currentVersionCode = pckInfo.versionCode;
            currentVersionName = pckInfo.versionName;

            String data = downloadHttp(new URL(INFO_FILE));
            Log.d("AVISO", data);
            JSONObject json = new JSONObject(data);
            lastesVersionCode = json.getInt("versionCode");
            lastesVersionName = json.getString("versionName");
            downloadURL = json.getString("downloadURL");
            Log.d("Actualizacion", "Datos obtenidos excitosamente");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AutoUpdate", "Ha habido un error con el JSON", e);
        } catch (MalformedURLException e) {
            Log.e("AutoUpdate", "Ha habido un error con el packete :S", e);
        } catch (JSONException e) {
            Log.e("AutoUpdate", "Ha habido un error con el JSON", e);
        } catch (IOException e) {
            Log.e("AutoUpdate", "Ha habido un error con la descarga", e);
        }
    }

    public boolean isNewVersionAvailable(){
        return getLastesVersionCode() > getCurrentVersionCode();
    }

    public int getCurrentVersionCode(){
        return currentVersionCode;
    }
    public String getCurrentVersionName(){
        return currentVersionName;
    }

    public int getLastesVersionCode(){
        return lastesVersionCode;
    }
    public String getLastestVersionName(){
        return lastesVersionName;
    }

    public String getDownloadURL(){
        return downloadURL;
    }

    private static String downloadHttp(URL url) throws IOException{
        HttpURLConnection c = (HttpURLConnection)url.openConnection();
        c.setRequestMethod("GET");
        c.setReadTimeout(15*1000);
        c.setUseCaches(false);
        c.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null){
            stringBuilder.append(line + "n");
        }
        return stringBuilder.toString();
    }

    public void DownloadData(Runnable OnFinishRunnable){
        this.listener = OnFinishRunnable;
        downloaderData.execute();
    }

    public void InstallNewVersion(Runnable OnFinishRunnable){
        if (isNewVersionAvailable()){
            if (getDownloadURL() == "")return;
            listener = OnFinishRunnable;
            String params[] = {getDownloadURL()};
            downloadInstaller.execute(params);
        }
    }

    private AsyncTask downloaderData = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] objects) {
            getData();
            return null;
        }
        @Override
        protected void onPostExecute(Object o){
            super.onPostExecute(o);
            if (listener != null)listener.run();
            listener = null;
        }
    };

    private AsyncTask<String, Integer, Intent> downloadInstaller = new AsyncTask<String, Integer, Intent>() {
        @Override
        protected Intent doInBackground(String... strings) {
            try {

                URL url = new URL(strings[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                //c.setDoOutput(true);
                c.connect();
                Log.d("URL", String.valueOf(url));
                String PATH = Environment.getExternalStorageDirectory()+"/download/";
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "app.apk");
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1){
                    fos.write(buffer, 0, len1);
                }

                fos.close();
                is.close();

                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" +"app.apk")),
                      "application/vnd.android.package-archive");


                context.startActivity(intent);

            }  catch (IOException e) {
                Log.e("Update error IO!", e.getMessage());
            }

            return null;
        }
        @Override
        protected void onPostExecute(Intent intent){
            super.onPostExecute(intent);
            if (listener != null)listener.run();
            listener = null;
        }
    };
}
