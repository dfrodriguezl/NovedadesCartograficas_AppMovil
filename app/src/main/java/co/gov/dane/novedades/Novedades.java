package co.gov.dane.novedades;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import co.gov.dane.novedades.EstructuraDataBase.Estructura;

import static org.spatialite.database.SQLiteDatabase.CONFLICT_REPLACE;

public class Novedades {

    int id;
    int tipo_geometria;
    String wkt;
    String tipo;
    String descripcion;
    Context context;
    MainActivity main;
    String id_dispositivo;
    String usuario="";
    String fecha="";

    Novedades(Context context, MainActivity main, int id, String id_dispositivo, int tipo_geometria,String wkt,String tipo, String descripcion){
        this.context=context;
        this.main=main;
        this.id=id;
        this.tipo_geometria=tipo_geometria;
        this.wkt=wkt;
        this.tipo=tipo;
        this.descripcion=descripcion;
        this.id_dispositivo=id_dispositivo;
    }
    Novedades(Context context, MainActivity main, int id,String tipo, String descripcion){
        this.context=context;
        this.main=main;
        this.id=id;
        this.tipo_geometria=tipo_geometria;
        this.wkt=wkt;
        this.tipo=tipo;
        this.descripcion=descripcion;
    }
    Novedades(Context context, MainActivity main, int id, String id_dispositivo, int tipo_geometria,String wkt,String tipo, String descripcion,String fecha){
        this.context=context;
        this.main=main;
        this.id=id;
        this.tipo_geometria=tipo_geometria;
        this.wkt=wkt;
        this.tipo=tipo;
        this.descripcion=descripcion;
        this.id_dispositivo=id_dispositivo;
        this.fecha=fecha;
    }

    Novedades(Context context, MainActivity main, int id,String usuario){
        this.context=context;
        this.main=main;
        this.id=id;
        this.usuario=usuario;
    }



    public Boolean insertarNovedad(){

        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Estructura.NovedadEntry.ID, id);
            values.put(Estructura.NovedadEntry.ID_DISPOSITIVO, id_dispositivo);
            values.put(Estructura.NovedadEntry.TIPO_GEOMETRIA, tipo_geometria);
            values.put(Estructura.NovedadEntry.WKT, wkt);
            values.put(Estructura.NovedadEntry.TIPO, tipo);
            values.put(Estructura.NovedadEntry.DESCRIPCION, descripcion);
            values.put(Estructura.NovedadEntry.FECHA, fecha);

            Location location = main.getLastKnownLocation();
            if(location!= null) {
                values.put(Estructura.NovedadEntry.LAT_GPS, location.getLatitude());
                values.put(Estructura.NovedadEntry.LON_GPS, location.getLongitude());
            }

            //llena la tabla de novedades.
            sp.insertWithOnConflict(Estructura.NovedadEntry.TABLE_NAME, null, values,CONFLICT_REPLACE);
            sp.close();
        } catch (SQLiteConstraintException e) {

            return false;
        }

        return true;
    }

    public Boolean updateNovedadAtributos(){

        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Estructura.NovedadEntry.TIPO, tipo);
            values.put(Estructura.NovedadEntry.DESCRIPCION, descripcion);
            long rowInserted=sp.update(Estructura.NovedadEntry.TABLE_NAME, values, " id = ?", new String[]{String.valueOf(id)});
            sp.close();

            Mensajes mitoast =new Mensajes(main);

            if(rowInserted != -1){
                mitoast.generarToast("Datos actualizados");

            }else{
                mitoast.generarToast("Error al actualizar los datos");
            }

        } catch (SQLiteConstraintException e) {

            return false;
        }

        return true;
    }



    public Boolean eliminarNovedad(){

        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            String table = Estructura.NovedadEntry.TABLE_NAME;
            String whereClause = Estructura.NovedadEntry.ID+"=?";
            String[] whereArgs = new String[] { String.valueOf(id) };
            sp.delete(table, whereClause, whereArgs);

            sp.close();
        } catch (SQLiteConstraintException e) {
            return false;
        }

        return true;

    }

    public Boolean cambioEstadoGeometria(){

        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            String table = Estructura.GeometriaEntry.TABLE_NAME;

            ContentValues cv = new ContentValues();
            cv.put("estado","2");

            sp.update(table, cv, Estructura.GeometriaEntry.ID_PADRE+"="+String.valueOf(id), null);

            sp.close();
        } catch (SQLiteConstraintException e) {
            return false;
        }

        return true;

    }


    public Boolean asignacion_geometria(){

        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            String table = Estructura.GeometriaEntry.TABLE_NAME;

            ContentValues cv = new ContentValues();
            cv.put("usuario_asignado",usuario);

            sp.update(table, cv, Estructura.GeometriaEntry.ID_PADRE+"="+String.valueOf(id), null);

            sp.close();
        } catch (SQLiteConstraintException e) {
            return false;
        }

        return true;

    }




}

