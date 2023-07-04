package co.gov.dane.novedades;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
    String url_usuarios_get = "https://geoportal.dane.gov.co/laboratorio/serviciosjson/recuentos/servicios/login.php";

    String url_obras_get = "http://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/obras_get.php?sector=";

//    String subir_novedades = "https://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/sincronizar_get.php";
//    String subir_novedades = "http://192.168.20.21/edicion_mobile/sincronizar_get.php";
//    String subir_novedades = "https://nowsoft.app/geoportal/laboratorio/serviciosjson/edicion_mobile/sincronizar_get.php";
    String subir_novedades = "https://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/sincronizar_get_2.php";

    String subir_novedades2 = "http://10.57.44.236:8080/edicion_mobile/sincronizar_get_3.php";

    String folder_insumos = "https://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/file_list.php";

    int descargas = 0;

    public Controlador(Context context) {
        this.context = context;
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

    public void getUsers(final String usuario, final String clave, final VolleyCallBack callBack) {

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        Boolean hay_internet = isNetworkAvailable();

        if (hay_internet) {

            String url = url_usuarios_get;

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("response", response);
                            callBack.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("error", String.valueOf(error));
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("usr", usuario);
                    params.put("pwd", clave);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };

            postRequest.setShouldCache(false);

            requestQueue.add(postRequest);


        } else {

            Mensajes mitoast = new Mensajes(context);
            mitoast.generarToast("Debe conectarse a internet");

        }


    }


    public void getInfo(final VolleyCallBackJSON callBack) {


        Boolean hay_internet = isNetworkAvailable();


        if (hay_internet) {

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    folder_insumos,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {


                            callBack.onSuccess(response);


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("error", String.valueOf(error));
                            callBack.onError();
                        }
                    }
            );


            request.setShouldCache(false);

            requestQueue.add(request);


        } else {


        }


    }


    public void uploadData() {

        Boolean hay_internet = isNetworkAvailable();

        if (hay_internet) {

            Session session = new Session(context);
            final String usuario = session.getusename();
            final String investigacion = session.getInvestigacion();

            ProgressDialog barProgressDialog = new ProgressDialog(context);

            barProgressDialog.setTitle("Subiendo Información ...");
            barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
            barProgressDialog.setProgress(0);


            try {
                SpatiaLite db = new SpatiaLite(context);

                org.spatialite.database.SQLiteDatabase sp = db.getWritableDatabase();


                Cursor c = sp.query(
                        Estructura.NovedadEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                Cursor c2 = sp.query(
                        Estructura.ConteoEntry.TABLE_NAME,
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

                        final String id = c.getString(c.getColumnIndex(Estructura.NovedadEntry.ID));
                        final String tipo = c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO_GEOMETRIA));
                        final String descripcion = c.getString(c.getColumnIndex(Estructura.NovedadEntry.DESCRIPCION));
                        final String novedad = c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO));
                        final String geometria = c.getString(c.getColumnIndex(Estructura.NovedadEntry.WKT));
                        final String id_dispositivo = c.getString(c.getColumnIndex(Estructura.NovedadEntry.ID_DISPOSITIVO));
                        final String lat_gps = c.getString(c.getColumnIndex(Estructura.NovedadEntry.LAT_GPS));
                        final String lon_gps = c.getString(c.getColumnIndex(Estructura.NovedadEntry.LON_GPS));


                        String url = subir_novedades;



                        JSONObject json = new JSONObject();
                        json.put("id", id);
                        json.put("tipo", tipo);
                        json.put("descripcion", descripcion);
                        json.put("novedad", novedad);
                        json.put("geometria", geometria);
                        json.put("usuario", usuario);
                        json.put("id_dispositivo", id_dispositivo);
                        json.put("investigacion", investigacion);
                        json.put("lat_gps", lat_gps);
                        json.put("lon_gps", lon_gps);

                        JsonObjectRequest postRequest = new JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                json,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("response", response.toString());
                                        Mensajes mitoast = new Mensajes(context);
                                        mitoast.generarToast("Datos Enviados");
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("des", descripcion);
                                        Log.d("error", String.valueOf(error));
                                        Mensajes mitoast = new Mensajes(context);
                                        mitoast.generarToast("Datos Enviados");
                                    }
                                }
                        ) {
//                            @Override
//                            protected Map<String, String> getParams() {
//                                Map<String, String> params = new HashMap<>();
//
//                                params.put("id", id);
//                                params.put("tipo", tipo);
//                                params.put("descripcion", descripcion);
//                                params.put("novedad", novedad);
//                                params.put("geometria", geometria);
//                                params.put("usuario", usuario);
//                                params.put("id_dispositivo", id_dispositivo);
//                                params.put("investigacion", investigacion);
//                                params.put("lat_gps", lat_gps);
//                                params.put("lon_gps", lon_gps);
//                                return params;
//                            }
//
//                            @Override
//                            public Map<String, String> getHeaders() throws AuthFailureError {
//                                Map<String, String> params = new HashMap<String, String>();
//                                params.put("Content-Type", "application/x-www-form-urlencoded");
//                                return params;
//                            }
                        };

                        postRequest.setShouldCache(false);

                        requestQueue.add(postRequest);


                    } while (c.moveToNext());
                }
                if (c != null) {
                    c.close();
                }

                if (c2.moveToFirst()) {
                    do {

                        final String id = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.ID));
                        final String tipo = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.TIPO_GEOMETRIA));
                        final String manzana = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.MANZANA));
                        final String edificaciones = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.EDIFICACIONES));
                        final String viviendas = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.VIVIENDAS));
                        final String ue = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.UE));
                        final String tipo_nov = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.TIPO_NOV));
                        final String descripcion = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.DESCRIPCION));
                        final String geometria = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.WKT));
                        final String id_dispositivo = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.ID_DISPOSITIVO));
                        final String lat_gps = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.LAT_GPS));
                        final String lon_gps = c2.getString(c2.getColumnIndex(Estructura.ConteoEntry.LON_GPS));


                        String url = subir_novedades2;


                        JSONObject json = new JSONObject();
                        json.put("id", id);
                        json.put("tipo", tipo);
                        json.put("manzana", manzana);
                        json.put("edificaciones", edificaciones);
                        json.put("viviendas", viviendas);
                        json.put("ue", ue);
                        json.put("tipo_nov", tipo_nov);
                        json.put("descripcion", descripcion);
                        json.put("geometria", geometria);
                        json.put("usuario", usuario);
                        json.put("id_dispositivo", id_dispositivo);
                        json.put("investigacion", investigacion);
                        json.put("lat_gps", lat_gps);
                        json.put("lon_gps", lon_gps);


                        Log.d("yeiner mendivelso", String.valueOf(json));


                        JsonObjectRequest postRequest2 = new JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                json,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("response", response.toString());
                                        Mensajes mitoast = new Mensajes(context);
                                        mitoast.generarToast("Datos Enviados conteo");
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("des", descripcion);
                                        Log.d("error", String.valueOf(error));
                                        Mensajes mitoast = new Mensajes(context);
                                        mitoast.generarToast("Datos Enviados");
                                    }
                                }
                        ){};


                        postRequest2.setShouldCache(false);

                        requestQueue.add(postRequest2);


                    } while (c2.moveToNext());
                }



                barProgressDialog.dismiss();

                sp.close();




            } catch (SQLiteConstraintException | JSONException e) {

                Mensajes mitoast = new Mensajes(context);
                mitoast.generarToast("Error al subir información");

            }


        } else {
            Mensajes mitoast = new Mensajes(context);
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
