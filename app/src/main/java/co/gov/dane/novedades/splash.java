package co.gov.dane.novedades;

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
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

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
        String ruta_backup=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"backup";

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
        File folder_backup = new File(ruta_backup);
        if (!folder_backup.exists()) {
            folder_backup.mkdirs();
        }


        DownloadFileFromURL mTask = new DownloadFileFromURL(splash.this);

        mTask.execute("http://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/file_get.php?name=ceed.db");

        //copia la base de datos de geometria de manzanas a la base de datos interna de la aplicación
        try{

            final String inFileName = Environment.getExternalStorageDirectory()+ File.separator + "Editor Dane" + File.separator +"db"+ File.separator +"geom.db";
            final String renombre = Environment.getExternalStorageDirectory()+ File.separator + "Editor Dane" + File.separator +"db"+ File.separator +"geom_imported.db";

            File dbFile = new File(inFileName);

            if(dbFile.exists()){
                FileInputStream fis = new FileInputStream(dbFile);

                String outFileName = "/data/data/co.gov.dane.novedades/databases/manzanas.db";

                // Open the empty db as the output stream
                OutputStream output = new FileOutputStream(outFileName);

                // Transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer))>0){
                    output.write(buffer, 0, length);
                }

                // Close the streams
                output.flush();
                output.close();
                fis.close();
                File from = new File(inFileName);
                File to=new File(renombre);
                from.renameTo(to);
            }


        }catch (Exception e){

        }



    }





}