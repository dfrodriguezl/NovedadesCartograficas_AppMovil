package co.gov.dane.novedades;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class splash extends Activity {
    private Session session;
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    public static final int MULTIPLE_PERMISSIONS = 6; // code you want.
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 101;


    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA
    };


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    try {
////                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
////                        intent.addCategory("android.intent.category.DEFAULT");
////                        intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
////                        startActivityForResult(intent, 2296);
//                    } catch (Exception e) {
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                        startActivityForResult(intent, 2296);
//                    }
                } else {
                    //below android 11
                    ActivityCompat.requestPermissions(splash.this, new String[]{WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_ACCOUNTS);
                }
            }
            ActivityCompat.requestPermissions(splash.this, permissions, MY_PERMISSIONS_REQUEST_ACCOUNTS);
        } else {
            logica();
        }


    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {


        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (hasAllPermissionsGranted(grantResults)) {


            Bundle extras = getIntent().getExtras();
            String usuario, investigacion;
            if (extras != null) {
                usuario = extras.getString("username");
                investigacion = extras.getString("id_encuesta");

                session = new Session(splash.this);
                session.setusename(usuario, "Usuario: " + usuario, "1", investigacion);

                Mensajes mensaje = new Mensajes(this);
                mensaje.generarToast("Login externo");

            }
            logica();


        } else {

            Mensajes mensaje = new Mensajes(this);
            mensaje.generarToast("Debe aceptar todos los permisos!");
        }
    }


    public void logica() {


        String ruta = null;

        if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
            ruta = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db";
        } else {
            ruta = getExternalFilesDir("db").getAbsolutePath();
        }

        FileDownloader fileDownloader = new FileDownloader(this);
        fileDownloader.downloadFile("https://geoportal.dane.gov.co/descargas/edicion_mobile/ceed.db",
                ruta,
                "ceed.db",
                new FileDownloader.FileDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                    }

                    @Override
                    public void onDownloadFailed(String errorMessage) {
                        Mensajes mensaje = new Mensajes(splash.this);
                        mensaje.generarToast("Error en la descarga: " + errorMessage);
                    }

                    @Override
                    public void onProgressUpdate(int progress) {

                    }
                });

        try {
            Session session = new Session(splash.this);
            String usuario = session.getusename();

            if (usuario.equals("")) {

                Intent mainIntent = new Intent(splash.this, login.class);
                splash.this.startActivity(mainIntent);
                ((Activity) splash.this).finish();

            } else {
                Intent mainIntent = new Intent(splash.this, MainActivity.class);
                splash.this.startActivity(mainIntent);
                ((Activity) splash.this).finish();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(splash.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(splash.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }


}