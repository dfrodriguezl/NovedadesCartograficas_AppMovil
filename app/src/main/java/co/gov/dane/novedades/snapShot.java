package co.gov.dane.novedades;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;

import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class snapShot {

    MainActivity main;

    public snapShot(MainActivity main){
        this.main=main;
    }

    public void capturaPantalla(){


            GoogleMap.SnapshotReadyCallback callback=new GoogleMap.SnapshotReadyCallback () {
                Bitmap bitmap;
                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    bitmap=snapshot;

                    try{
                        Date now = new Date();
                        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
                        String ruta_capturas=null;
                        if(Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT){
                            ruta_capturas =Environment.getExternalStorageDirectory() + File.separator + "Editor Nc"+ File.separator+"Capturas/";
                        }else{
                            ruta_capturas = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc"+ File.separator+"Capturas/";
                        }

                        File file = new File(ruta_capturas, now+".png");
                        FileOutputStream fout=new FileOutputStream (file);
                        bitmap.compress (Bitmap.CompressFormat.PNG,90,fout);

                        final MediaPlayer mp = MediaPlayer.create(main,R.raw.camera_shutter);
                        mp.start();

                        main.mitoast.generarToast("Captura almacendada en:\n"+ruta_capturas);

                    }catch (Exception e){
                        e.printStackTrace ();

                    }


                }
            };main.mMap.snapshot (callback);
        }



}
