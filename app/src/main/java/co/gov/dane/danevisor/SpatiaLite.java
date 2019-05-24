package co.gov.dane.danevisor;

import android.content.Context;

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

import co.gov.dane.danevisor.EstructuraDataBase.Estructura;



public class SpatiaLite extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "geom.db";

    public SpatiaLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + Estructura.UsuarioEntry.TABLE_NAME + " ("
                + Estructura.UsuarioEntry.ID +" INTEGER PRIMARY KEY,"
                + Estructura.UsuarioEntry.USUARIO + " TEXT NOT NULL,"
                + Estructura.UsuarioEntry.CLAVE + " TEXT NOT NULL,"
                + Estructura.UsuarioEntry.NOMBRE + " TEXT NOT NULL,"
                + Estructura.UsuarioEntry.CORREO + " TEXT ,"
                + Estructura.UsuarioEntry.VIGENCIA + " TEXT NOT NULL,"
                + Estructura.UsuarioEntry.ROL + " INTEGER"
                + ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + Estructura.GeometriaEntry.TABLE_NAME + " ("
                + Estructura.GeometriaEntry.ID_PADRE +" TEXT PRIMARY KEY,"
                + Estructura.GeometriaEntry.ID_HIJO + " TEXT,"
                + Estructura.GeometriaEntry.NOMBRE_ENCUESTA + " TEXT,"
                + Estructura.GeometriaEntry.NOMBRE_CAPA + " TEXT ,"
                + Estructura.GeometriaEntry.ESTADO + " INTEGER ,"
                + Estructura.GeometriaEntry.OBSERVACIONES + " TEXT,"
                + Estructura.GeometriaEntry.GEOMETRIA_INI + " TEXT NOT NULL"
                + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
