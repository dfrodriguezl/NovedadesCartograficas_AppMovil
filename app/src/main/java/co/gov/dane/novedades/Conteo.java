package co.gov.dane.novedades;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.location.Location;

import co.gov.dane.novedades.EstructuraDataBase.Estructura;

import static org.spatialite.database.SQLiteDatabase.CONFLICT_REPLACE;

public class Conteo {

    int id;
    int tipo_geometria;
    String wkt;
    String manzana;
    String edificaciones;
    String viviendas;
    String ue;
    String tipo_nov;
    String descripcion;
    Context context;
    MainActivity main;
    String id_dispositivo;
    String usuario="";
    String fecha="";

    Conteo(Context context, MainActivity main, int id, String id_dispositivo, int tipo_geometria,String wkt,String manzana, String edificaciones, String viviendas, String ue, String tipo_nov, String descripcion) {
        this.context=context;
        this.main=main;
        this.id=id;
        this.tipo_geometria=tipo_geometria;
        this.wkt=wkt;
        this.manzana=manzana;
        this.edificaciones=edificaciones;
        this.viviendas=viviendas;
        this.ue=ue;
        this.tipo_nov=tipo_nov;
        this.descripcion=descripcion;
        this.id_dispositivo=id_dispositivo;
        this.fecha=fecha;
    }

    public Boolean insertarConteo(){

        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Estructura.ConteoEntry.ID, id);
            values.put(Estructura.ConteoEntry.ID_DISPOSITIVO, id_dispositivo);
            values.put(Estructura.ConteoEntry.TIPO_GEOMETRIA, tipo_geometria);
            values.put(Estructura.ConteoEntry.WKT, wkt);
            values.put(Estructura.ConteoEntry.MANZANA, manzana);
            values.put(Estructura.ConteoEntry.EDIFICACIONES, edificaciones);
            values.put(Estructura.ConteoEntry.VIVIENDAS, viviendas);
            values.put(Estructura.ConteoEntry.UE, ue);
            values.put(Estructura.ConteoEntry.TIPO_NOV, tipo_nov);
            values.put(Estructura.ConteoEntry.DESCRIPCION, descripcion);
            values.put(Estructura.ConteoEntry.FECHA, fecha);

            Location location = main.getLastKnownLocation();
            if(location!= null) {
                values.put(Estructura.ConteoEntry.LAT_GPS, location.getLatitude());
                values.put(Estructura.ConteoEntry.LON_GPS, location.getLongitude());
            }

            //llena la tabla de novedades.
            sp.insertWithOnConflict(Estructura.ConteoEntry.TABLE_NAME, null, values,CONFLICT_REPLACE);
            sp.close();
        } catch (SQLiteConstraintException e) {

            return false;
        }

        return true;
    }
}
