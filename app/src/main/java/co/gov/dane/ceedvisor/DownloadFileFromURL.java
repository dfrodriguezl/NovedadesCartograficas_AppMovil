package co.gov.dane.ceedvisor;

import android.os.AsyncTask;
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

public class DownloadFileFromURL extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... f_url) {

        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // this will be useful so that you can show a tipical 0-100%           progress bar
            int lenghtOfFile = conection.getContentLength();

            String raw = conection.getHeaderField("Content-Disposition");
            String fileName="";
            if(raw != null && raw.indexOf("=") != -1) {
                fileName = raw.split("=")[1]; //getting value after '='
            } else {
                // fall back to random generated file name?
            }
            String fecha_sistema = conection.getHeaderField("Last-Modified");


            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date fecha_ultima_modificacion = dateFormatGmt.parse(fecha_sistema);

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"db"+ File.separator+fileName);
            if(file.exists()){

                //fechas del fichero en el dispositivo
                Date fecha_actual = new Date(file.lastModified());

                String fecha_hoy=dateFormatGmt.format(fecha_actual);
                Date fecha_actual1 = dateFormatGmt.parse(fecha_hoy);

                if (fecha_actual1.compareTo(fecha_ultima_modificacion)<0)
                {
                    descarga(url,lenghtOfFile,fileName);
                }else{
                // si el fichero está actualizado, es decir la fecha de ultima modificación
                    // es igual al fichero en el dispositivo no se descarga
                }

            }else{
                //si no existe el fichero en el dispositivo se descarga
                descarga(url,lenghtOfFile,fileName);
            }


        } catch (Exception e) {
            Log.d("error",e.getMessage());
        }

        return null;
    }

    public void descarga(URL url,int lenghtOfFile,String fileName ){
        int count;
        // download the file
        InputStream input = null;
        try {
            input = new BufferedInputStream(url.openStream(), 8192);


        // Output stream
        OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"db"+ File.separator+fileName);

        byte data[] = new byte[1024];

        long total = 0;

        while ((count = input.read(data)) != -1) {
            total += count;
            // publishing the progress....
            // After this onProgressUpdate will be called
            publishProgress(""+(int)((total*100)/lenghtOfFile));

            // writing data to file
            output.write(data, 0, count);
        }

        // flushing output
        output.flush();

        // closing streams
        output.close();
        input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
