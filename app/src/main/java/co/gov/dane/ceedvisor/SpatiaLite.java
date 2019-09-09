package co.gov.dane.ceedvisor;

import android.content.Context;

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

import co.gov.dane.ceedvisor.EstructuraDataBase.Estructura;



public class SpatiaLite extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "geom.db";


    public static final String CREAR_USUARIO="CREATE TABLE " + Estructura.UsuarioEntry.TABLE_NAME + " ("
            + Estructura.UsuarioEntry.ID +" INTEGER PRIMARY KEY,"
            + Estructura.UsuarioEntry.USUARIO + " TEXT NOT NULL,"
            + Estructura.UsuarioEntry.CLAVE + " TEXT NOT NULL,"
            + Estructura.UsuarioEntry.NOMBRE + " TEXT NOT NULL,"
            + Estructura.UsuarioEntry.CORREO + " TEXT ,"
            + Estructura.UsuarioEntry.VIGENCIA + " TEXT NOT NULL,"
            + Estructura.UsuarioEntry.ROL + " INTEGER"
            + ")";

    public static final String CREAR_NOVEDAD="CREATE TABLE " + Estructura.NovedadEntry.TABLE_NAME + " ("
            + Estructura.NovedadEntry.ID +" INTEGER PRIMARY KEY,"
            + Estructura.NovedadEntry.ID_DISPOSITIVO +" TEXT,"
            + Estructura.NovedadEntry.TIPO_GEOMETRIA + " INTEGER NOT NULL,"
            + Estructura.NovedadEntry.WKT + " TEXT NOT NULL,"
            + Estructura.NovedadEntry.TIPO + " TEXT,"
            + Estructura.NovedadEntry.DESCRIPCION + " TEXT, "
            + Estructura.NovedadEntry.FECHA + " TEXT "
            + ")";

    public static final String CREAR_GEOMETRIA="CREATE TABLE " + Estructura.GeometriaEntry.TABLE_NAME + " ("
            + Estructura.GeometriaEntry.ID_PADRE +" TEXT PRIMARY KEY,"
            + Estructura.GeometriaEntry.ID_HIJO + " TEXT,"
            + Estructura.GeometriaEntry.NOMBRE_ENCUESTA + " TEXT,"
            + Estructura.GeometriaEntry.NOMBRE_CAPA + " TEXT ,"
            + Estructura.GeometriaEntry.ESTADO + " INTEGER ,"
            + Estructura.GeometriaEntry.OBSERVACIONES + " TEXT,"
            + Estructura.GeometriaEntry.GEOMETRIA_INI + " TEXT NOT NULL,"
            + Estructura.GeometriaEntry.USUARIO_ASIGNADO + " TEXT"
            + ")";

    public static final String CREAR_OBRAS="CREATE TABLE " + Estructura.ObrasEntry.TABLE_NAME + " ("
            + Estructura.ObrasEntry.SERIAL +" TEXT PRIMARY KEY,"
            + Estructura.ObrasEntry.FINICIO + " TEXT,"
            + Estructura.ObrasEntry.NOFORMULAR + " TEXT,"
            + Estructura.ObrasEntry.NOMBREOBRA + " TEXT,"
            + Estructura.ObrasEntry.DIREOBRA + " TEXT,"
            + Estructura.ObrasEntry.BARRIO + " TEXT,"
            + Estructura.ObrasEntry.GEOMETRIA + " TEXT "
            + ")";




    public SpatiaLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREAR_USUARIO);

        sqLiteDatabase.execSQL(CREAR_GEOMETRIA);

        sqLiteDatabase.execSQL(CREAR_NOVEDAD);

        sqLiteDatabase.execSQL(CREAR_OBRAS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Estructura.UsuarioEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Estructura.NovedadEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Estructura.GeometriaEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Estructura.ObrasEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }


}
