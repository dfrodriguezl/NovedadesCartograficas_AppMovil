package co.gov.dane.ceedvisor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.io.File;

public class splash extends Activity {
    private Session session;
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    public static final int MULTIPLE_PERMISSIONS = 6; // code you want.
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 101;


    String[] permissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA
    };


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);


        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_ACCOUNTS);
        }else{
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
        if(hasAllPermissionsGranted(grantResults)){

            logica();

        }else {
            Mensajes mensaje=new Mensajes(this);
            mensaje.generarToast("Debe aceptar todos los permisos!");
        }
    }
    public void logica(){
        //creación de los Folder para el aplicativo
        String ruta_mbtiles=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"mbtiles";
        String ruta_capturas=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"Capturas";
        String ruta_db=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"db";

        File folder_mbtile = new File(ruta_mbtiles);

        if (!folder_mbtile.exists()) {
            folder_mbtile.mkdirs();
        }
        File folder_captura = new File(ruta_capturas);
        if (!folder_captura.exists()) {
            folder_captura.mkdirs();
        }
        File folder_db = new File(ruta_db);
        if (!folder_db.exists()) {
            folder_db.mkdirs();
        }



        DownloadFileFromURL mTask = new DownloadFileFromURL();
        mTask.execute("http://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/file_get.php?name=ceed.db");


        Controlador con=new Controlador(splash.this);


        con.getUsers(new VolleyCallBack() {
            @Override
            public void onSuccess() {

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {


                        session = new Session(splash.this);

                        String usuario=session.getusename();

                        if(usuario.equals("")){

                            Intent mainIntent = new Intent(splash.this,login.class);
                            splash.this.startActivity(mainIntent);
                            splash.this.finish();

                        }else{

                            Intent mainIntent = new Intent(splash.this,MainActivity.class);
                            splash.this.startActivity(mainIntent);
                            splash.this.finish();


                        }


                    }
                }, SPLASH_DISPLAY_LENGTH);

            }
        });









    }

}