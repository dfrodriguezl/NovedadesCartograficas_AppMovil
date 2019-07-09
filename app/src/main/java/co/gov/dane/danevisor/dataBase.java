package co.gov.dane.danevisor;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.BubbleIconFactory;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.gov.dane.danevisor.EstructuraDataBase.Estructura;

import static com.google.maps.android.ui.IconGenerator.STYLE_BLUE;

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
         selection = Estructura.GeometriaEntry.ESTADO + " = ? AND "+Estructura.GeometriaEntry.USUARIO_ASIGNADO + " = ?";
         selectionArgs = new String[]{"1",usuario};
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

        String usuario_asignado=c.getString(c.getColumnIndex(Estructura.GeometriaEntry.USUARIO_ASIGNADO));




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

                }catch (JSONException e) {

                }


                main.polygon.add(main.mMap.addPolygon(opts));
                main.polygon.get(main.polygon.size()-1).setClickable(true);
                main.polygon.get(main.polygon.size()-1).setTag(main.atributos);

                if(rol==1){
                    if(usuario_asignado.equals("Sin Asignar") || usuario_asignado.isEmpty()){

                    }else{
                        main.polygon.get(main.polygon.size()-1).setStrokeColor(Color.GREEN);
                    }
                }



                String centroide=main.analisis.centroidePoligono(main.polygon.get(main.polygon.size()-1));
                Coordinate[] coord1=wkt.read(centroide).getCoordinates();

                MarkerOptions opts1=new MarkerOptions();
                LatLng punto;
                IconGenerator iconFactory = new IconGenerator(main);

                //iconFactory.setColor(main.getResources().getColor(android.R.color.transparent));

                for(int j=0;j<coord1.length;j++){
                    Double lat1=coord1[j].y;
                    Double lon1=coord1[j].x;
                    punto=new LatLng(lat1,lon1);
                    opts1.position(punto);
                    opts1.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(c.getString(c.getColumnIndex(Estructura.GeometriaEntry.ID_PADRE)))));
                    opts1.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                }


                main.label.add(main.mMap.addMarker(opts1));



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
        if(max_id.equals("")){
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

public String getsiguienterecorte(String id){

    SpatiaLite db=new SpatiaLite(context);

    org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

    Cursor c = sp.rawQuery(

        "SELECT ifnull(max(cast(REPLACE(substr(descripcion, INSTR(descripcion,'-')),'-','') as integer)),0)+1 as cut "+
        "from novedad "+
        "where descripcion like  '%' || "+
        "(SELECT cast(substr(descripcion,0, INSTR(descripcion,'-')) as integer) as cut "+
        "from novedad "+
        "where id=?) || '%';"

        , new String[] {id});

    if(c.getCount()>0){
        c.moveToFirst();
        String max_id=c.getString(0);

        Log.d("Máximo valor detectado:",max_id);


        String code=id;

        if(id.indexOf("-") >= 0){
            String str = id;
            String substr = "-";
            code = str.substring(0, str.indexOf(substr));
        }


        sp.close();
        return code+"-"+max_id;

    }else{
        sp.close();
        return "";
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
                    LatLng punto;
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

                        main.puntos.add(main.mMap.addMarker(opts));
                        main.puntos.get(main.puntos.size()-1).setTag(atributos);
                        main.puntos.get(main.puntos.size()-1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ping_obra));

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

                            String[] tipo_linea = main.getResources().getStringArray(R.array.novedad_linea);
                            String[] color = main.getResources().getStringArray(R.array.color_linea);
                            int k=0;
                            for (String s : tipo_linea) {
                                int i = s.indexOf(c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)));
                                if (i >= 0) {
                                    atributos.put("color",color[k]);
                                }
                                k=k+1;
                            }

                            main.line.add(main.mMap.addPolyline(opts.width(15)));
                            main.line.get(main.line.size()-1).setColor(Color.parseColor(atributos.get("color").toString()));
                            main.line.get(main.line.size()-1).setClickable(true);
                            main.line.get(main.line.size()-1).setTag(atributos);

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

                            String[] tipo_poligono = main.getResources().getStringArray(R.array.novedad_poligono);
                            String[] color = main.getResources().getStringArray(R.array.color_poligono);
                            int k=0;
                            for (String s : tipo_poligono) {
                                int i = s.indexOf(c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)));
                                if (i >= 0) {
                                    atributos.put("color",color[k]);
                                }
                                k=k+1;
                            }

                            main.polygon.add(main.mMap.addPolygon(opts));

                            main.polygon.get(main.polygon.size()-1).setClickable(true);
                            main.polygon.get(main.polygon.size()-1).setTag(atributos);
                            main.polygon.get(main.polygon.size()-1).setFillColor(Color.parseColor(atributos.get("color").toString()));



                            String centroide=main.analisis.centroidePoligono(main.polygon.get(main.polygon.size()-1));
                            Coordinate[] coord1=wkt.read(centroide).getCoordinates();

                            MarkerOptions opts1=new MarkerOptions();
                            LatLng punto;
                            IconGenerator iconFactory = new IconGenerator(main);

                            //iconFactory.setColor(main.getResources().getColor(android.R.color.transparent));

                            for(int j=0;j<coord1.length;j++){
                                Double lat=coord1[j].y;
                                Double lon=coord1[j].x;
                                punto=new LatLng(lat,lon);
                                opts1.position(punto);
                                opts1.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(c.getString(c.getColumnIndex(Estructura.NovedadEntry.ID)))));
                                opts1.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                            }


                            main.label.add(main.mMap.addMarker(opts1));



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




                            String[] tipo_punto = main.getResources().getStringArray(R.array.novedad_punto);
                            String[] color = main.getResources().getStringArray(R.array.color_punto);
                            int k=0;
                            for (String s : tipo_punto) {
                                int i = s.indexOf(c.getString(c.getColumnIndex(Estructura.NovedadEntry.TIPO)));
                                if (i >= 0) {
                                    atributos.put("color",color[k]);
                                }
                                k=k+1;
                            }




                            main.puntos.add(main.mMap.addMarker(opts));
                            main.puntos.get(main.puntos.size()-1).setTag(atributos);

                            main.puntos.get(main.puntos.size()-1).setIcon(BitmapDescriptorFactory.defaultMarker(Float.parseFloat(atributos.get("color").toString())));

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
