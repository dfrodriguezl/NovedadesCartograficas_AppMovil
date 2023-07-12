package co.gov.dane.novedades;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.gov.dane.novedades.EstructuraDataBase.Estructura;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class dataBase  {

Context context;
MainActivity main;


dataBase(Context context,MainActivity main){
    this.context=context;
    this.main=main;
}


public void getGeomFromDatabase(){

    SpatiaLite db=new SpatiaLite(context);
    org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();


    int rol =main.session.getrol();

    String usuario=main.session.getusename();

    String selection =null;
    String selectionArgs[] = null;

    if(rol==1){

    }else if(rol==2){
        selection = Estructura.GeometriaEntry.USUARIO_ASIGNADO + " = ?";
        selectionArgs = new String[]{usuario};
    }


    Cursor c = sp.query(
            Estructura.GeometriaEntry.TABLE_NAME,  // Nombre de la tabla
            null,  // Lista de Columnas a consultar
            selection,  // Columnas para la cláusula WHERE
            selectionArgs,  // Valores a comparar con las columnas del WHERE
            null,  // Agrupar con GROUP BY
            null,  // Condición HAVING para GROUP BY
            null  // Cláusula ORDER BY
    );



    while(c.moveToNext()){

        String geometria_ini = c.getString(c.getColumnIndex(Estructura.GeometriaEntry.GEOMETRIA_INI));

        String id=c.getString(c.getColumnIndex(Estructura.GeometriaEntry.ID_PADRE));
        String estado=c.getString(c.getColumnIndex(Estructura.GeometriaEntry.ESTADO));

        String usuario_asignado="";
        try {
            usuario_asignado=c.getString(c.getColumnIndex(Estructura.GeometriaEntry.USUARIO_ASIGNADO));
        }catch (Exception e){

        }


        WKTReader wkt=new WKTReader();

        try {
            if(wkt.read(geometria_ini).isValid()){

                Coordinate[] coord=wkt.read(geometria_ini).getCoordinates();
                PolygonOptions opts=new PolygonOptions();
                for(int j=0;j<coord.length;j++){
                    Double lat=coord[j].y;
                    Double lon=coord[j].x;
                    LatLng punto=new LatLng(lat,lon);
                    opts.add(punto);
                }

                Point pt=wkt.read(geometria_ini).getCentroid();

                Double lon=pt.getX();
                Double lat=pt.getY();

                try{
                    main.atributos =new JSONObject();
                    main.atributos.put("id", id);
                    main.atributos.put("tipo", c.getString(c.getColumnIndex(Estructura.GeometriaEntry.NOMBRE_ENCUESTA)));
                    main.atributos.put("descripcion", c.getString(c.getColumnIndex(Estructura.GeometriaEntry.ID_PADRE)));

                }catch (JSONException e) {

                }


                if(estado.equals("2")){

                }else{
                    main.polygon.add(main.mMap.addPolygon(opts));
                    main.polygon.get(main.polygon.size()-1).setClickable(true);
                    main.polygon.get(main.polygon.size()-1).setTag(main.atributos);
                }


                if(rol==1){
                    if(usuario_asignado.equals("Sin Asignar") || usuario_asignado.isEmpty()){

                    }else{
                        main.polygon.get(main.polygon.size()-1).setStrokeColor(Color.GREEN);
                    }
                }


                //parte del label
                String centroide=main.analisis.centroidePoligono(main.polygon.get(main.polygon.size()-1));
                String descripcion=c.getString(c.getColumnIndex(Estructura.GeometriaEntry.ID_PADRE));
                String identificador=main.polygon.get(main.polygon.size()-1).getId();
                Util util=new Util(main,main);
                util.generarLabel(centroide,descripcion,"ENA_LABEL");

                main.listado_busqueda.add(new Busqueda(c.getString(c.getColumnIndex(Estructura.GeometriaEntry.ID_PADRE)),centroide,3));


            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    sp.close();


}

public int getMaxIdNovedad(){

    SpatiaLite db=new SpatiaLite(context);

    org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

    Cursor c = sp.query(Estructura.NovedadEntry.TABLE_NAME, new String[]{"MAX("+Estructura.NovedadEntry.ID+")"}, null, null, null, null, null);


    if(c.getCount()>0){
        c.moveToFirst();
        String max_id=c.getString(0);
        if(max_id == null || max_id.equals("")){
            sp.close();
            return 0;
        }else{
            sp.close();
            return Integer.parseInt(max_id);
        }

    }else{
        sp.close();
        return 0;
    }



}

public String getsiguienterecorte(String descripcion){

    SpatiaLite db=new SpatiaLite(context);

    org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

    Cursor c = sp.rawQuery(

        "select max(substr('0000'||(select cast(substr(descripcion,10) as integer)+1),-4)) as next "+
        "from novedad "+
        "where descripcion like ? and cast(substr(descripcion,10) as integer) !=0"

        , new String[] {descripcion+ "%"});

    if(c.getCount()>0){
        c.moveToFirst();
        String max_id=c.getString(0);

        sp.close();

        if(max_id.equals("")){
            return descripcion+"0001";
        }else{
            return descripcion+max_id;
        }


    }else{
        sp.close();
        return descripcion;
    }



}


public void getObrasCeed(){

    try {
        SpatiaLite db=new SpatiaLite(context);

        org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();


        Cursor c = sp.query(
                Estructura.ObrasEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );


        while (c.moveToNext()) {

            String geometria_ini = c.getString(c.getColumnIndex(Estructura.ObrasEntry.GEOMETRIA));

            WKTReader wkt=new WKTReader();

            try {
                if(wkt.read(geometria_ini).isValid()){



                    Coordinate[] coord=wkt.read(geometria_ini).getCoordinates();

                    MarkerOptions opts=new MarkerOptions();
                    LatLng punto = null;
                    for(int j=0;j<coord.length;j++){
                        Double lat=coord[j].y;
                        Double lon=coord[j].x;
                        punto=new LatLng(lat,lon);
                        opts.position(punto);
                    }


                    JSONObject atributos=new JSONObject();
                    try {
                        atributos.put("id",c.getString(c.getColumnIndex(Estructura.ObrasEntry.SERIAL)));
                        atributos.put("tipo","obra");
                        atributos.put("descripcion", "");
                        atributos.put("serial", c.getString(c.getColumnIndex(Estructura.ObrasEntry.SERIAL)));
                        atributos.put("nombreobra", c.getString(c.getColumnIndex(Estructura.ObrasEntry.NOMBREOBRA)));
                        atributos.put("direccion", c.getString(c.getColumnIndex(Estructura.ObrasEntry.DIREOBRA)));
                        atributos.put("barrio", c.getString(c.getColumnIndex(Estructura.ObrasEntry.BARRIO)));
                        atributos.put("noformular", c.getString(c.getColumnIndex(Estructura.ObrasEntry.NOFORMULAR)));

                        main.puntos.add(main.mMap.addMarker(opts));
                        main.puntos.get(main.puntos.size()-1).setTag(atributos);

                        int height = 60;
                        int width = 40;
                        BitmapDrawable bitmapdraw=(BitmapDrawable)main.getResources().getDrawable(R.drawable.ping_obra);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                        main.puntos.get(main.puntos.size()-1).setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));


                        //parte del label
                        SpatialAnalysis spatial=new SpatialAnalysis(main,main);


                        String centroide=spatial.puntoWKT(main.puntos.get(main.puntos.size()-1));


                        String descripcion=c.getString(c.getColumnIndex(Estructura.ObrasEntry.NOFORMULAR));
                        String identificador=main.puntos.get(main.puntos.size()-1).getId();
                        Util util=new Util(main,main);
                        util.generarLabel(centroide,descripcion,identificador);


                        main.listado_busqueda.add(new Busqueda(c.getString(c.getColumnIndex(Estructura.ObrasEntry.SERIAL)),centroide,3));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }



        sp.close();
    } catch (SQLiteConstraintException e) {

        Log.e("error:", String.valueOf(e));

    }

}


    public void getNovedades(){

        String ruta_db;
        if(Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT){
            ruta_db= Environment.getExternalStorageDirectory() + File.separator + "Editor Nc"+ File.separator+"db"+File.separator+"ceed.db";
        }else{
            ruta_db= Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc"+ File.separator+"db"+File.separator+"ceed.db";
        }
        CeedDB ceeddb =new CeedDB(main,ruta_db);
        Util utilidad=new Util(main,main);

        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            String selection = Estructura.NovedadEntry.TIPO_GEOMETRIA + " LIKE ?"; // WHERE id LIKE ?
            String selectionArgs[] = new String[]{"2"};

            Cursor c = sp.query(
                    Estructura.NovedadEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            while (c.moveToNext()) {

                String geometria_ini = c.getString(c.getColumnIndex(Estructura.NovedadEntry.WKT));

                WKTReader wkt=new WKTReader();

                try {
                    if(wkt.read(geometria_ini).isValid()){

                        Coordinate[] coord=wkt.read(geometria_ini).getCoordinates();

                        PolylineOptions opts=new PolylineOptions();

                        for(int j=0;j<coord.length;j++){
                            Double lat=coord[j].y;
                            Double lon=coord[j].x;
                            LatLng punto=new LatLng(lat,lon);
                            opts.add(punto);
                        }
                        JSONObject atributos=new JSONObject();
                        try {
                            atributos.put("id",c.getString(c.getColumnIndex(Estructura.NovedadEntry.ID)));
                            atributos.put("tipo",c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)));
                            atributos.put("descripcion", c.getString(c.getColumnIndex(Estructura.NovedadEntry.DESCRIPCION)));

                            String color=ceeddb.get_LineaColor(c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)));
                            String style=ceeddb.get_LineaStyle(c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)));

                            atributos.put("color",color);

                            main.line.add(main.mMap.addPolyline(opts.width(8)));

                            main.line.get(main.line.size()-1).setColor(Color.parseColor(color));
                            main.line.get(main.line.size()-1).setClickable(true);
                            main.line.get(main.line.size()-1).setTag(atributos);

                            List<PatternItem> PATTERN_POLYGON_ALPHA =utilidad.LineStyle(style);
                            main.line.get(main.line.size()-1).setPattern(PATTERN_POLYGON_ALPHA);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


            sp.close();
        } catch (SQLiteConstraintException e) {

            Log.e("error:", String.valueOf(e));

        }


        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            String selection = Estructura.NovedadEntry.TIPO_GEOMETRIA + " LIKE ?"; // WHERE id LIKE ?
            String selectionArgs[] = new String[]{"3"};

            Cursor c = sp.query(
                    Estructura.NovedadEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            while (c.moveToNext()) {

                String geometria_ini = c.getString(c.getColumnIndex(Estructura.NovedadEntry.WKT));

                WKTReader wkt=new WKTReader();

                try {
                    if(wkt.read(geometria_ini).isValid()){



                        Coordinate[] coord=wkt.read(geometria_ini).getCoordinates();
                        PolygonOptions opts=new PolygonOptions();
                        for(int j=0;j<coord.length;j++){
                            Double lat=coord[j].y;
                            Double lon=coord[j].x;
                            LatLng punto=new LatLng(lat,lon);
                            opts.add(punto);
                        }


                        JSONObject atributos=new JSONObject();
                        try {
                            atributos.put("id",c.getString(c.getColumnIndex(Estructura.NovedadEntry.ID)));
                            atributos.put("tipo",c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)));
                            atributos.put("descripcion", c.getString(c.getColumnIndex(Estructura.NovedadEntry.DESCRIPCION)));

                            String color=ceeddb.get_PoligonoColor(c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)));

                            atributos.put("color",color);

                            main.polygon.add(main.mMap.addPolygon(opts));

                            main.polygon.get(main.polygon.size()-1).setClickable(true);
                            main.polygon.get(main.polygon.size()-1).setTag(atributos);
                            main.polygon.get(main.polygon.size()-1).setZIndex(2);
                            main.polygon.get(main.polygon.size()-1).setFillColor(Color.parseColor(color));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


            sp.close();
        } catch (SQLiteConstraintException e) {

            Log.e("error:", String.valueOf(e));

        }


        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            String selection = Estructura.NovedadEntry.TIPO_GEOMETRIA + " = ?"; // WHERE id LIKE ?
            String selectionArgs[] = new String[]{"1"};

            Cursor c = sp.query(
                    Estructura.NovedadEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );



            while (c.moveToNext()) {

                String geometria_ini = c.getString(c.getColumnIndex(Estructura.NovedadEntry.WKT));

                WKTReader wkt=new WKTReader();

                try {
                    if(wkt.read(geometria_ini).isValid()){

                        Coordinate[] coord=wkt.read(geometria_ini).getCoordinates();

                        MarkerOptions opts=new MarkerOptions();
                        LatLng punto;
                        for(int j=0;j<coord.length;j++){
                            Double lat=coord[j].y;
                            Double lon=coord[j].x;
                            punto=new LatLng(lat,lon);
                            opts.position(punto);
                        }


                        JSONObject atributos=new JSONObject();
                        try {
                            atributos.put("id",c.getString(c.getColumnIndex(Estructura.NovedadEntry.ID)));
                            atributos.put("tipo",c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)));
                            atributos.put("descripcion", c.getString(c.getColumnIndex(Estructura.NovedadEntry.DESCRIPCION)));


                            main.puntos.add(main.mMap.addMarker(opts));
                            main.puntos.get(main.puntos.size()-1).setTag(atributos);

                            if(c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)).equals("ObraProyectada")){
                                main.puntos.get(main.puntos.size()-1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.obra_futura));
                            }else{
                                String imagen=c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO));

                                AssetManager mg = main.getResources().getAssets();
                                InputStream is = null;
                                try {
                                    is = mg.open("img/"+imagen+".png");
                                    main.puntos.get(main.puntos.size()-1).setIcon(BitmapDescriptorFactory.fromAsset("img/"+imagen+".png"));
                                } catch (IOException ex) {
                                    //file does not exist
                                } finally {
                                    if (is != null) {
                                        try {
                                            is.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


            sp.close();
        } catch (SQLiteConstraintException e) {

            Log.e("error:", String.valueOf(e));

        }









    }


    public List<String> getUsuarios() {
        List<String> usuario = new ArrayList<String>();

        SpatiaLite db=new SpatiaLite(context);

        org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

        Cursor c = sp.query(Estructura.UsuarioEntry.TABLE_NAME, null, null, null, null, null, null);


        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                // adding to tags list
                usuario.add(c.getString(c.getColumnIndex(Estructura.UsuarioEntry.NOMBRE))+" - "+c.getString(c.getColumnIndex(Estructura.UsuarioEntry.USUARIO)));
            } while (c.moveToNext());
        }

        sp.close();

        return usuario;
    }

}
