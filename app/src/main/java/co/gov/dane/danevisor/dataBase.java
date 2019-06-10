package co.gov.dane.danevisor;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.Arrays;

import co.gov.dane.danevisor.EstructuraDataBase.Estructura;

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

    Cursor c = sp.query(
            Estructura.GeometriaEntry.TABLE_NAME,  // Nombre de la tabla
            null,  // Lista de Columnas a consultar
            null,  // Columnas para la cláusula WHERE
            null,  // Valores a comparar con las columnas del WHERE
            null,  // Agrupar con GROUP BY
            null,  // Condición HAVING para GROUP BY
            null  // Cláusula ORDER BY
    );

    while(c.moveToNext()){
        String geometria_ini = c.getString(c.getColumnIndex(Estructura.GeometriaEntry.GEOMETRIA_INI));

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

                Polygon p=main.mMap.addPolygon(opts);

                p.setClickable(true);


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
        sp.close();
        return Integer.parseInt(max_id);
    }else{
        sp.close();
        return 0;
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

                            main.line.add(main.mMap.addPolyline(opts.width(2)));
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
                            main.polygon.get(main.polygon.size()-1).setFillColor(Color.parseColor(atributos.get("color").toString()));
                            main.polygon.get(main.polygon.size()-1).setClickable(true);
                            main.polygon.get(main.polygon.size()-1).setTag(atributos);

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


                            main.puntos.add(main.mMap.addMarker(opts));
                            main.puntos.get(main.puntos.size()-1).setTag(atributos);

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




}
