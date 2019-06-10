package co.gov.dane.danevisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import co.gov.dane.danevisor.EstructuraDataBase.Estructura;

import static org.spatialite.database.SQLiteDatabase.CONFLICT_REPLACE;

public class Novedades {

    int id;
    int tipo_geometria;
    String wkt;
    String tipo;
    String descripcion;
    Context context;
    MainActivity main;

    Novedades(Context context, MainActivity main, int id,int tipo_geometria,String wkt,String tipo, String descripcion){
        this.context=context;
        this.main=main;
        this.id=id;
        this.tipo_geometria=tipo_geometria;
        this.wkt=wkt;
        this.tipo=tipo;
        this.descripcion=descripcion;
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

    public Boolean insertarNovedad(){

        try {
            SpatiaLite db=new SpatiaLite(context);

            org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Estructura.NovedadEntry.ID, id);
            values.put(Estructura.NovedadEntry.TIPO_GEOMETRIA, tipo_geometria);
            values.put(Estructura.NovedadEntry.WKT, wkt);
            values.put(Estructura.NovedadEntry.TIPO, tipo);
            values.put(Estructura.NovedadEntry.DESCRIPCION, descripcion);

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







}
