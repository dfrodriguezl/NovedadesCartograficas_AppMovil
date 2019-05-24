package co.gov.dane.danevisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.gov.dane.danevisor.EstructuraDataBase.Estructura;


public class Controlador {

    private Context context;
    MainActivity main;

    String url_geometria_get = "http://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/geometria_get.php";
    String url_usuarios_get = "http://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/usuarios_get.php";



    public Controlador(Context context, MainActivity  main){
        this.context=context;
        this.main=main;
    }



    public void getData(){


        // Instantiate the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url_geometria_get,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{

                            SpatiaLite db=new SpatiaLite(context);

                            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();
                            sp.delete(Estructura.GeometriaEntry.TABLE_NAME, null, null);
                            for(int i=0;i<response.length();i++){

                                JSONObject obj = response.getJSONObject(i);

                                ContentValues values = new ContentValues();

                                values.put(Estructura.GeometriaEntry.ID_PADRE, obj.getString("ID_PADRE"));
                                values.put(Estructura.GeometriaEntry.ID_HIJO, obj.getString("ID_HIJO"));
                                values.put(Estructura.GeometriaEntry.NOMBRE_ENCUESTA, obj.getString("NOMBRE_ENCUESTA"));
                                values.put(Estructura.GeometriaEntry.NOMBRE_CAPA, obj.getString("NOMBRE_CAPA"));
                                values.put(Estructura.GeometriaEntry.ESTADO, obj.getInt("ESTADO"));
                                values.put(Estructura.GeometriaEntry.OBSERVACIONES, obj.getString("OBSERVACIONES"));
                                values.put(Estructura.GeometriaEntry.GEOMETRIA_INI, obj.getString("GEOMETRIA_INI"));

                                //llena la tabla de geometria.
                                sp.insert(Estructura.GeometriaEntry.TABLE_NAME, null, values);


                            }
                            sp.close();
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
        requestQueue.add(jsonArrayRequest);


        JsonArrayRequest jsonRequestUusaurios = new JsonArrayRequest(
                Request.Method.GET,
                url_usuarios_get,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{

                            for(int i=0;i<response.length();i++){

                                JSONObject obj = response.getJSONObject(i);

                                SpatiaLite db=new SpatiaLite(context);

                                org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

                                ContentValues values = new ContentValues();

                                values.put(Estructura.UsuarioEntry.ID, obj.getInt("ID"));
                                values.put(Estructura.UsuarioEntry.USUARIO, obj.getString("USUARIO"));
                                values.put(Estructura.UsuarioEntry.CLAVE, obj.getString("CLAVE"));
                                values.put(Estructura.UsuarioEntry.NOMBRE, obj.getString("NOMBRE"));
                                values.put(Estructura.UsuarioEntry.CORREO, obj.getString("CORREO"));
                                values.put(Estructura.UsuarioEntry.VIGENCIA, obj.getString("VIGENCIA"));
                                values.put(Estructura.UsuarioEntry.ROL, obj.getInt("ROL"));

                                //borra la tabla de usuarios cada vez.
                                sp.delete(Estructura.UsuarioEntry.TABLE_NAME, null, null);
                                //llena la tabla de usuarios.
                                sp.insert(Estructura.UsuarioEntry.TABLE_NAME, null, values);
                                sp.close();

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


        requestQueue.add(jsonRequestUusaurios);

    }

}
