package co.gov.dane.ceedvisor;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Environment;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

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
                        String ruta_capturas=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"Capturas/";

                        File file = new File(ruta_capturas, now+".png");
                        FileOutputStream fout=new FileOutputStream (file);
                        bitmap.compress (Bitmap.CompressFormat.PNG,90,fout);

                        final MediaPlayer mp = MediaPlayer.create(main,R.raw.camera_shutter);
                        mp.start();

                        Toast.makeText(main, "Captura de pantalla: \n"+ now+".png", Toast.LENGTH_LONG).show();

                    }catch (Exception e){
                        e.printStackTrace ();

                    }


                }
            };main.mMap.snapshot (callback);
        }



}
