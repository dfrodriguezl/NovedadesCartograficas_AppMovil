package co.gov.dane.novedades;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class DownloadFileFromURL extends AsyncTask<String, String, String> {

    private Context context;

    private final ProgressDialog dialog;

    private String fileName_temp;

    DownloadFileFromURL(Context context, String name) {
        this.context = context;
        dialog = new ProgressDialog(context);
        this.fileName_temp = name;
    }


    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Descargando bases de datos");
        this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.dialog.setCancelable(false);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.show();
    }

    protected void onProgressUpdate(String... progress) {
        this.dialog.setProgress(Integer.parseInt(progress[0]));
    }


    @Override
    protected String doInBackground(String... f_url) {


        Boolean hay_internet = isNetworkAvailable();

        if (hay_internet) {
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100%           progress bar
                int lenghtOfFile = conection.getContentLength();

                String raw = conection.getHeaderField("Content-Disposition");
                String fileName = "";
                if (raw != null && raw.indexOf("=") != -1) {
                    fileName = raw.split("=")[1]; //getting value after '='
                } else {
                    // fall back to random generated file name?
                }
                String fecha_sistema = conection.getHeaderField("Last-Modified");


//                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
//                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));

//                Date fecha_ultima_modificacion = dateFormatGmt.parse(fecha_sistema);

                File file = null;

                if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                    file = new File(Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator + fileName);
                } else {
                    file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator + fileName);
                }

//                descarga(url,lenghtOfFile,fileName);

                if (file.exists()) {

                    //fechas del fichero en el dispositivo
//                    Date fecha_actual = new Date(file.lastModified());
//
//                    String fecha_hoy = dateFormatGmt.format(fecha_actual);
//                    Date fecha_actual1 = dateFormatGmt.parse(fecha_hoy);
//
//                    Log.d("fecha_ultima_mod", String.valueOf(fecha_ultima_modificacion));
                    descarga(url, lenghtOfFile, fileName);
//                    if (fecha_actual.compareTo(fecha_ultima_modificacion) < 0) {
//                        descarga(url, lenghtOfFile, fileName);
//                    } else {
//                        // si el fichero está actualizado, es decir la fecha de ultima modificación
//                        // es igual al fichero en el dispositivo no se descarga
//
//                    }

                } else {
                    //si no existe el fichero en el dispositivo se descarga
                    descarga(url, lenghtOfFile, fileName);
                }


            } catch (Exception e) {
                Log.d("error", e.getMessage());
            }

            return "1";
        } else {


            return "0";
        }


    }


    // can use UI thread here
    @Override
    protected void onPostExecute(final String errMsg) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();

        }
        if (errMsg.equals("0")) {

            String url_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator + "ceed.db";

            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                url_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator + "ceed.db";
            } else {
                url_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator + "ceed.db";
            }

            File file = new File(url_db);
            if (!file.exists()) {
                Mensajes msg = new Mensajes(context);
                msg.generarToast("Debe Conectarse a Internet");
            } else {
                iniciar();
            }

        } else if (errMsg.equals("1")) {
            iniciar();
        }


    }

    public void iniciar() {

    }

    public void descarga(URL url, int lenghtOfFile, String fileName) {

        if(this.fileName_temp != null){
            fileName = this.fileName_temp;
        }
        int count;
        // download the file
        InputStream input = null;
        try {
            input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream
            OutputStream output = null;

            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                output = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator + fileName);
            } else {
                output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator + fileName);
            }

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
        } catch (IOException e) {
            Log.d("error:", "error");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
