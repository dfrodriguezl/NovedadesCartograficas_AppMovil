package co.gov.dane.danevisor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dataBase extends SQLiteOpenHelper {

    String sqlCreateUsuarios = "CREATE TABLE Usuarios (codigo INTEGER,usuario TEXT, clave TEXT, nombre TEXT, vigencia TEXT)";


    public dataBase(Context contexto, String nombre,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateUsuarios);

        db.execSQL("INSERT INTO Usuarios values (1,'idcarrillod','1234','Ivan Dario Carrillo Duran','2018-12-12')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Usuarios");

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreateUsuarios);

    }
}
