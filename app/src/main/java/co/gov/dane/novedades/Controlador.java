package co.gov.dane.novedades;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.gov.dane.novedades.EstructuraDataBase.Estructura;


public class Controlador {

    private Context context;

    String url_geometria_get = "http://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/geometria_get.php";
    String url_usuarios_get = "http://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/usuarios_get.php";

    String url_obras_get = "http://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/obras_get.php?sector=";

    String subir_novedades="https://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/sincronizar_get.php";


    int descargas=0;

    public Controlador(Context context){
        this.context=context;
    }


/*
    public  void getData(final VolleyCallBack callBack){


        Boolean hay_internet=isNetworkAvailable();

       if(hay_internet){

           final ProgressDialog progress = new ProgressDialog(context);
           progress.setMessage("Descargando datos...");
           progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
           progress.setIndeterminate(true);
           progress.show();


           // Instantiate the RequestQueue
           RequestQueue requestQueue = Volley.newRequestQueue(context);


           final CeedDB db =new CeedDB(context);
           final List<String> listado_setu = db.get_SetU("11","1","1","0","0","0");

           Log.d("sectores", String.valueOf(listado_setu));

           SpatiaLite db1=new SpatiaLite(context);

           final org.spatialite.database.SQLiteDatabase sp=db1.getWritableDatabase();
           sp.delete(Estructura.ObrasEntry.TABLE_NAME, null, null);

           for(int i=0;i<listado_setu.size();i++){

               String sector=listado_setu.get(i);

               String ur=url_obras_get.concat(sector);


               JsonArrayRequest jsonArrayRequestObras = new JsonArrayRequest(
                       Request.Method.GET,
                       ur,
                       null,
                       new Response.Listener<JSONArray>() {
                           @Override
                           public void onResponse(JSONArray response) {


                               try{


                                   for(int i=0;i<response.length();i++){

                                       JSONObject obj = response.getJSONObject(i);

                                       ContentValues values = new ContentValues();

                                       values.put(Estructura.ObrasEntry.SERIAL, obj.getString("SERIAL"));
                                       values.put(Estructura.ObrasEntry.FINICIO, obj.getString("FINICIO"));
                                       values.put(Estructura.ObrasEntry.NOFORMULAR, obj.getString("NOFORMULAR"));
                                       values.put(Estructura.ObrasEntry.NOMBREOBRA, obj.getString("NOMBREOBRA"));
                                       values.put(Estructura.ObrasEntry.DIREOBRA, obj.getString("DIREOBRA"));
                                       values.put(Estructura.ObrasEntry.BARRIO, obj.getString("BARRIO"));
                                       values.put(Estructura.ObrasEntry.GEOMETRIA, obj.getString("GEOMETRIA"));

                                       sp.insert(Estructura.ObrasEntry.TABLE_NAME, null, values);


                                   }

                               }catch (JSONException e){
                                   e.printStackTrace();

                               }
                           }
                       },
                       new Response.ErrorListener(){
                           @Override
                           public void onErrorResponse(VolleyError error){
                               Log.i("bye",error.toString());

                           }
                       }
               );

               // Add JsonArrayRequest to the RequestQueue
               requestQueue.add(jsonArrayRequestObras);

           }






           requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
               @Override
               public void onRequestFinished(Request<String> request) {
                   descargas=descargas+1;

                   if(descargas==listado_setu.size()){
                       progress.hide();
                       callBack.onSuccess();

                       Mensajes mitoast =new Mensajes(context);
                       mitoast.generarToast("Descarga de datos finalizada");
                       sp.close();
                   }

               }

           });


       }else{
           Mensajes mitoast =new Mensajes(context);
           mitoast.generarToast("No hay conexión a internet");
       }





    }
*/
/* servicio para traer los usuarios -- se comenta dado que el login provisionalmente es por cédula.
    public  void getUsers(final VolleyCallBack callBack){


        Boolean hay_internet=isNetworkAvailable();

        if(hay_internet){

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonArrayRequest jsonRequestUusaurios = new JsonArrayRequest(
                    Request.Method.GET,
                    url_usuarios_get,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try{
                                SpatiaLite db=new SpatiaLite(context);

                                org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

                                //borra la tabla de usuarios cada vez.
                                sp.delete(Estructura.UsuarioEntry.TABLE_NAME, null, null);

                                for(int i=0;i<response.length();i++){

                                    JSONObject obj = response.getJSONObject(i);

                                    Log.d("Usuarios:", String.valueOf(obj));

                                    ContentValues values = new ContentValues();

                                    values.put(Estructura.UsuarioEntry.ID, obj.getInt("ID"));
                                    values.put(Estructura.UsuarioEntry.USUARIO, obj.getString("USUARIO"));
                                    values.put(Estructura.UsuarioEntry.CLAVE, obj.getString("CLAVE"));
                                    values.put(Estructura.UsuarioEntry.NOMBRE, obj.getString("NOMBRE"));
                                    values.put(Estructura.UsuarioEntry.CORREO, obj.getString("CORREO"));
                                    values.put(Estructura.UsuarioEntry.VIGENCIA, obj.getString("VIGENCIA"));
                                    values.put(Estructura.UsuarioEntry.ROL, obj.getInt("ROL"));

                                    //llena la tabla de usuarios.
                                    sp.insert(Estructura.UsuarioEntry.TABLE_NAME, null, values);

                                }
                                sp.close();
                                callBack.onSuccess();

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){

                            callBack.onSuccess();

                        }
                    }
            );



            requestQueue.add(jsonRequestUusaurios);



        } else{

            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();
            Cursor res =  sp.rawQuery( "select count(*) from usuarios", null );

            int usuarios=0;
            while (res.moveToNext()) {
                usuarios= Integer.parseInt(res.getString(0));
            }

            res.close();
            db.close();

            if(usuarios>0){
                callBack.onSuccess();
            }else{
                Mensajes mitoast =new Mensajes(context);
                mitoast.generarToast("Debe conectarse a internet la primer vez");
            }



    }



    }

*/


    public void uploadData(){

        Boolean hay_internet=isNetworkAvailable();

        if(hay_internet){

            Session session=new Session(context);
            final String usuario=session.getusename();
            final String investigacion=session.getInvestigacion();

            ProgressDialog barProgressDialog = new ProgressDialog(context);

            barProgressDialog.setTitle("Subiendo Información ...");
            barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
            barProgressDialog.setProgress(0);


            try {
                SpatiaLite db=new SpatiaLite(context);

                org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();


                Cursor c = sp.query(
                        Estructura.NovedadEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                barProgressDialog.setMax(c.getCount());//In this part you can set the  MAX value of data
                barProgressDialog.show();


                final RequestQueue requestQueue = Volley.newRequestQueue(context);

                if (c.moveToFirst()) {
                    do {

                        barProgressDialog.incrementProgressBy(1);

                        final String id=c.getString(c.getColumnIndex(Estructura.NovedadEntry.ID));
                        final String tipo=c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO_GEOMETRIA));
                        final String descripcion=c.getString(c.getColumnIndex(Estructura.NovedadEntry.DESCRIPCION));
                        final String novedad=c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO));
                        final String geometria=c.getString(c.getColumnIndex(Estructura.NovedadEntry.WKT));
                        final String id_dispositivo=c.getString(c.getColumnIndex(Estructura.NovedadEntry.ID_DISPOSITIVO));
                        final String lat_gps=c.getString(c.getColumnIndex(Estructura.NovedadEntry.LAT_GPS));
                        final String lon_gps=c.getString(c.getColumnIndex(Estructura.NovedadEntry.LON_GPS));


                        String url = subir_novedades;

                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("response",response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("des",descripcion);
                                        Log.d("error",String.valueOf(error));
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams()
                            {
                                Map<String, String>  params = new HashMap<>();

                                params.put("id", id);
                                params.put("tipo",tipo);
                                params.put("descripcion", descripcion);
                                params.put("novedad", novedad);
                                params.put("geometria", geometria);
                                params.put("usuario", usuario);
                                params.put("id_dispositivo", id_dispositivo);
                                params.put("investigacion", investigacion);
                                params.put("lat_gps", lat_gps);
                                params.put("lon_gps", lon_gps);
                                return params;
                            }
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("Content-Type","application/x-www-form-urlencoded");
                                return params;
                            }
                        };

                        postRequest.setShouldCache(false);

                        requestQueue.add(postRequest);


                    } while (c.moveToNext());
                }
                if (c != null) {
                    c.close();
                }

                Mensajes mitoast =new Mensajes(context);
                mitoast.generarToast("Datos Enviados");

                barProgressDialog.dismiss();

                sp.close();


            } catch (SQLiteConstraintException e) {

                Mensajes mitoast =new Mensajes(context);
                mitoast.generarToast("Error al subir información");

            }


        }else{
            Mensajes mitoast =new Mensajes(context);
            mitoast.generarToast("No hay conexión a internet");
        }


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
