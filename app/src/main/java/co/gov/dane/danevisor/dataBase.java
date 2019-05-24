package co.gov.dane.danevisor;


import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

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

                main.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 15));
                main.mMap.animateCamera(CameraUpdateFactory.zoomIn());


            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    sp.close();


}




}
