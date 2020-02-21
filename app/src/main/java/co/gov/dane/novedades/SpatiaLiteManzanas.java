package co.gov.dane.novedades;

import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;



public class SpatiaLiteManzanas extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "manzanas.db";

    private Context context;
    public SpatiaLiteManzanas(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("CREATE TABLE manzanas4( id TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

     public Map<String,PolygonOptions> getManzanas(LatLng userLocation){

         Map<String,PolygonOptions> poligonos=new HashMap<>();

         SpatiaLiteManzanas db1=new SpatiaLiteManzanas(context);
         org.spatialite.database.SQLiteDatabase sp1=db1.getWritableDatabase();

         Coordinate c_punto=new Coordinate(userLocation.longitude,userLocation.latitude);

         GeometryFactory gf = new GeometryFactory();

         Point p= gf.createPoint(c_punto);

         Cursor c = sp1.rawQuery(
         "select manz_ccnct,AsWKT(CastToPolygon(geometry)),Intersects(ST_Transform(ST_Buffer(ST_Transform(SetSRID(GeomFromText('"+p+"'),4326),3116),500),4326),geometry)as geom from manzanas where geom=1",null);

         while(c.moveToNext()){

             String cod_manzana=c.getString(0);
             String geometria_ini = c.getString(1);

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
                     poligonos.put(cod_manzana,opts);
                 }
             } catch (ParseException e) {
                 e.printStackTrace();
             }

         }

         sp1.close();

         return poligonos;
     }

     public PolygonOptions getManzana (String id_manzana){
         PolygonOptions poligono=new PolygonOptions();

         SpatiaLiteManzanas db1=new SpatiaLiteManzanas(context);
         org.spatialite.database.SQLiteDatabase sp1=db1.getWritableDatabase();

         Cursor c = sp1.rawQuery(
                 "select AsWKT(CastToPolygon(geometry)) from manzanas where manz_ccnct='"+id_manzana+"'",null);

         while(c.moveToNext()){

             String geometria_ini = c.getString(0);

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
                     poligono=opts;
                 }
             } catch (ParseException e) {
                 e.printStackTrace();
             }

         }

         sp1.close();

         return poligono;
     }




    public Boolean puntoDentro(String punto, String polygono){

        SpatiaLiteManzanas db1=new SpatiaLiteManzanas(context);
        org.spatialite.database.SQLiteDatabase sp1=db1.getWritableDatabase();

        Cursor c = sp1.rawQuery(
                "select Intersects(GeomFromText('"+punto+"'), GeomFromText('"+polygono+"'))as inter",null);

        int inter=0;
        while (c.moveToNext()) {
            int index;

            inter= Integer.parseInt(c.getString(0));

        }
        sp1.close();

        Boolean retorno=false;
        if(inter==1){
            retorno= true;
        }
        return retorno;
    }


}
