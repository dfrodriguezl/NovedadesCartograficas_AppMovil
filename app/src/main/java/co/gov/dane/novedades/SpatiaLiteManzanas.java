package co.gov.dane.novedades;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOCUMENTS;


public class SpatiaLiteManzanas extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;

    private Context context;
    private String databaseName;

    public SpatiaLiteManzanas(Context context, String databaseName, String url) {

        super(context, url + databaseName, null, DATABASE_VERSION);

        this.context = context;
        this.databaseName = databaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("CREATE TABLE manzanas4( id TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public Map<String, PolygonOptions> getManzanas(LatLng userLocation) {

        Map<String, PolygonOptions> poligonos = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }
            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            Coordinate c_punto = new Coordinate(userLocation.longitude, userLocation.latitude);

            GeometryFactory gf = new GeometryFactory();

            Point p = gf.createPoint(c_punto);
            String tabla = databaseName.replace(".db", "").toLowerCase();
            Cursor c = sp1.rawQuery(
                    "select cod_dane,AsWKT(CastToPolygon(geometry)),Intersects(ST_Transform(ST_Buffer(ST_Transform(SetSRID(GeomFromText('" + p + "'),4326),3116),500),4326),geometry)as geom from " + tabla + " where geom=1", null);

            while (c.moveToNext()) {

                String cod_manzana = c.getString(0);
                String geometria_ini = c.getString(1);

                WKTReader wkt = new WKTReader();
                try {
                    if (wkt.read(geometria_ini).isValid()) {

                        Coordinate[] coord = wkt.read(geometria_ini).getCoordinates();
                        PolygonOptions opts = new PolygonOptions();
                        for (int j = 0; j < coord.length; j++) {
                            Double lat = coord[j].y;
                            Double lon = coord[j].x;
                            LatLng punto = new LatLng(lat, lon);
                            opts.add(punto);

                        }
                        poligonos.put(cod_manzana, opts);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

//        Map<String, PolygonOptions> poligonosRural = getSeccionesRurales(userLocation);
//        poligonos.putAll(poligonosRural);


        return poligonos;
    }

    public Map<String, PolygonOptions> getSeccionesRurales(LatLng userLocation) {


        Map<String, PolygonOptions> poligonos = new HashMap<>();


        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }
            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            Coordinate c_punto = new Coordinate(userLocation.longitude, userLocation.latitude);

            GeometryFactory gf = new GeometryFactory();

            Point p = gf.createPoint(c_punto);
            String tabla = "rural";
            Cursor c = sp1.rawQuery(
                    "select secr_ccnct as cod_dane,AsWKT(CastToPolygon(geometry)),Intersects(ST_Transform(ST_Buffer(ST_Transform(SetSRID(GeomFromText('" + p + "'),4326),3116),500),4326),geometry)as geom from " + tabla + " where geom=1", null);

            while (c.moveToNext()) {

                String cod_manzana = c.getString(0);
                String geometria_ini = c.getString(1);

                WKTReader wkt = new WKTReader();
                try {
                    if (wkt.read(geometria_ini).isValid()) {

                        Coordinate[] coord = wkt.read(geometria_ini).getCoordinates();
                        PolygonOptions opts = new PolygonOptions();
                        for (int j = 0; j < coord.length; j++) {
                            Double lat = coord[j].y;
                            Double lon = coord[j].x;
                            LatLng punto = new LatLng(lat, lon);
                            opts.add(punto);
                        }
                        poligonos.put(cod_manzana, opts);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return poligonos;
    }

    public PolygonOptions getManzana(String id_manzana) {
        PolygonOptions poligono = new PolygonOptions();

        String ruta_db = null;
        if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
            ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
        } else {
            ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
        }

        SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
        org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

        Cursor c = sp1.rawQuery(
                "select AsWKT(CastToPolygon(geometry)) from manzanas where cod_dane='" + id_manzana + "'", null);

        while (c.moveToNext()) {

            String geometria_ini = c.getString(0);

            WKTReader wkt = new WKTReader();
            try {
                if (wkt.read(geometria_ini).isValid()) {

                    Coordinate[] coord = wkt.read(geometria_ini).getCoordinates();
                    PolygonOptions opts = new PolygonOptions();
                    for (int j = 0; j < coord.length; j++) {
                        Double lat = coord[j].y;
                        Double lon = coord[j].x;
                        LatLng punto = new LatLng(lat, lon);
                        opts.add(punto);

                    }
                    poligono = opts;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        sp1.close();

        return poligono;
    }


    public Boolean puntoDentro(String punto, String polygono) {


        String ruta_db = null;
        if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
            ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
        } else {
            ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
        }
        SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
        org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

        Cursor c = sp1.rawQuery(
                "select Intersects(GeomFromText('" + punto + "'), GeomFromText('" + polygono + "'))as inter", null);

        int inter = 0;
        while (c.moveToNext()) {
            int index;

            inter = Integer.parseInt(c.getString(0));

        }
        sp1.close();

        Boolean retorno = false;
        if (inter == 1) {
            retorno = true;
        }
        return retorno;
    }

    public String[] getManzanasIntersect(LatLng userLocation) {

        String[] manzanaList = new String[1];

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }
            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            Coordinate c_punto = new Coordinate(userLocation.longitude, userLocation.latitude);

            GeometryFactory gf = new GeometryFactory();

            Point p = gf.createPoint(c_punto);
            String tabla = databaseName.replace(".db", "");
            Cursor c = sp1.rawQuery(
                    "select cod_dane,AsWKT(CastToPolygon(geometry)),Intersects(ST_Transform(ST_Transform(SetSRID(GeomFromText('" + p + "'),4326),3116),4326),geometry)as geom from " + tabla + " where geom=1", null);

            Log.d("cantidad interseccion:", String.valueOf(c.getCount()));

            while (c.moveToNext()) {

                String cod_manzana = c.getString(0);
                String geometria_ini = c.getString(1);

                WKTReader wkt = new WKTReader();
                try {
                    if (wkt.read(geometria_ini).isValid()) {
                        if (c.getCount() > 0) {
                            manzanaList[0] = cod_manzana;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return manzanaList;

    }

    public Map<String, String> getDptoUrbano() {
        Map<String, String> departamentos = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            Cursor c = sp1.rawQuery(
                    "select distinct cod_dpto from " + tabla, null);

            while (c.moveToNext()) {

                String dpto_ccdgo = c.getString(0);
                departamentos.put(dpto_ccdgo, "");

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return departamentos;
    }

    public Map<String, String> getDptoRural() {
        Map<String, String> departamentos = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = "rural";
            Cursor c = sp1.rawQuery(
                    "select distinct dpto_ccdgo from " + tabla, null);

            while (c.moveToNext()) {

                String dpto_ccdgo = c.getString(0);
                departamentos.put(dpto_ccdgo, "");

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return departamentos;
    }

    public Map<String, String> getMpioUrbano(String dpto) {
        Map<String, String> municipios = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            Cursor c = sp1.rawQuery(
                    "select distinct cod_mpio from " + tabla + " where cod_dpto = '" + dpto + "' order by cast(cod_mpio as int)", null);

            while (c.moveToNext()) {

                String cod_mpio = c.getString(0);
                municipios.put(cod_mpio, "");

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return municipios;
    }

    public Map<String, String> getMpioRural(String dpto) {
        Map<String, String> municipios = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = "rural";
            Cursor c = sp1.rawQuery(
                    "select distinct mpio_cdpmp from " + tabla + " where dpto_ccdgo = '" + dpto + "' order by cast(mpio_cdpmp as int)", null);

            while (c.moveToNext()) {

                String mpio_cdpmp = c.getString(0);
                municipios.put(mpio_cdpmp, "");

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return municipios;
    }

    public Map<String, String> getClaseUrbano(String mpio) {
        Map<String, String> clases = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            Cursor c = sp1.rawQuery(
                    "select distinct cod_clase from " + tabla + " where cod_mpio = '" + mpio + "' order by cod_clase", null);

            while (c.moveToNext()) {

                String cod_mpio = c.getString(0);
                clases.put(cod_mpio, cod_mpio);

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return clases;
    }

    public Map<String, String> getClaseRural(String mpio) {
        Map<String, String> clases = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = "rural";
            Cursor c = sp1.rawQuery(
                    "select distinct clas_ccdgo from " + tabla + " where mpio_cdpmp = '" + mpio + "'", null);

            while (c.moveToNext()) {

                String mpio_cdpmp = c.getString(0);
                clases.put(mpio_cdpmp, mpio_cdpmp);

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return clases;
    }

    public Map<String, String> getSectorUrbano(String mpio, String clase, String centroPoblado) {
        Map<String, String> clases = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            String rawQuery = "select distinct setu_ccdgo from " + tabla + " where cod_mpio = '" + mpio + "' and cod_clase = '" + clase + "' order by setu_ccdgo";

            if (clase.equals("2")) {
                rawQuery = "select distinct setu_ccdgo from " + tabla + " where cod_mpio = '" + mpio + "' and cod_clase = '" + clase + "' and zu_ccdgo = '" + centroPoblado + "' order by setu_ccdgo";
            }

            Cursor c = sp1.rawQuery(
                    rawQuery, null);

            while (c.moveToNext()) {

                String cod_mpio = c.getString(0);
                clases.put(cod_mpio, cod_mpio);

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return clases;
    }

    public Map<String, String> getSectorRural(String mpio, String clase) {
        Map<String, String> clases = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            String[] campos = new String[]{"setr_ccdgo", "cod_mpio", "cod_clase"};

            if (clase.equals("3")) {
                tabla = "rural";
                campos = new String[]{"setr_ccdgo", "mpio_cdpmp", "clas_ccdgo"};
            }

            String rawSQL = "select distinct " + campos[0] + " from " + tabla + " where " + campos[1] + " = '" + mpio + "' and " + campos[2] + " = '" + clase + "' order by " + campos[0];

            Cursor c = sp1.rawQuery(
                    rawSQL, null);

            while (c.moveToNext()) {

                String cod_mpio = c.getString(0);
                clases.put(cod_mpio, cod_mpio);

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return clases;
    }

    public Map<String, String> getSeccionUrbano(String mpio, String clase, String sectorUrbano, String centroPoblado) {
        Map<String, String> clases = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();

            String rawQuery = "select distinct secu_ccdgo from " + tabla + " where cod_mpio = '" + mpio + "' and cod_clase = '" + clase + "' and setu_ccdgo = '" + sectorUrbano + "' order by secu_ccdgo";

            if (clase.equals("2")) {
                rawQuery = "select distinct secu_ccdgo from " + tabla + " where cod_mpio = '" + mpio + "' and cod_clase = '" + clase + "' and setu_ccdgo = '" + sectorUrbano + "' and zu_ccdgo = '" + centroPoblado + "' order by secu_ccdgo";
            }

            Cursor c = sp1.rawQuery(
                    rawQuery, null);

            while (c.moveToNext()) {

                String cod_mpio = c.getString(0);
                clases.put(cod_mpio, cod_mpio);

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return clases;
    }

    public Map<String, String> getSeccionRural(String mpio, String clase, String sectorRural) {
        Map<String, String> clases = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            String[] campos = new String[]{"secr_ccdgo", "setr_ccdgo", "cod_mpio", "cod_clase"};

            if (clase.equals("3")) {
                tabla = "rural";
                campos = new String[]{"secr_ccdgo", "setr_ccdgo", "mpio_cdpmp", "clas_ccdgo"};
            }

            String rawSQL = "select distinct " + campos[0] + " from " + tabla + " where " + campos[2] + " = '" + mpio + "' and " + campos[3] + " = '" + clase + "' and " + campos[1] + " = '" + sectorRural + "' order by " + campos[0];

            Cursor c = sp1.rawQuery(
                    rawSQL, null);

            while (c.moveToNext()) {

                String cod_mpio = c.getString(0);
                clases.put(cod_mpio, cod_mpio);

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return clases;
    }

    public Map<String, String> getCentroPoblado(String mpio, String clase, String sectorRural, String seccionRural) {
        Map<String, String> clases = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            Cursor c = sp1.rawQuery(
                    "select distinct zu_ccdgo from " + tabla + " where cod_mpio = '" + mpio + "' and cod_clase = '" + clase + "' and setr_ccdgo = '" + sectorRural + "' and secr_ccdgo = '" + seccionRural + "' order by zu_ccdgo", null);

            while (c.moveToNext()) {

                String cod_mpio = c.getString(0);
                clases.put(cod_mpio, cod_mpio);

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return clases;
    }

    public Map<String, String> getManzana(String mpio, String clase, String sectorUrbano, String seccionUrbana, String centroPoblado) {
        Map<String, String> clases = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            String rawQuery = "select distinct manz_ccdgo from " + tabla + " where cod_mpio = '" + mpio + "' and cod_clase = '" + clase + "' and setu_ccdgo = '" + sectorUrbano + "' and secu_ccdgo = '" + seccionUrbana + "' order by secu_ccdgo";

            if (clase.equals("2")) {
                rawQuery = "select distinct manz_ccdgo from " + tabla + " where cod_mpio = '" + mpio + "' and cod_clase = '" + clase + "' and setu_ccdgo = '" + sectorUrbano + "' and secu_ccdgo = '" + seccionUrbana + "' and zu_ccdgo = '" + centroPoblado + "' order by secu_ccdgo";
            }

            Cursor c = sp1.rawQuery(
                    rawQuery, null);

            while (c.moveToNext()) {

                String cod_mpio = c.getString(0);
                clases.put(cod_mpio, cod_mpio);

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return clases;
    }

    public List<LatLng> getManzanaZoom(String mpio, String clase, String sectorUrbano, String seccionUrbana, String centroPoblado, String manzana) {
        List<LatLng> poligono = new ArrayList<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            String rawQuery = "select AsWKT(CastToPolygon(geometry)) from " + tabla + " where cod_mpio = '" + mpio + "' and cod_clase = '" + clase + "' and setu_ccdgo = '" + sectorUrbano + "' and secu_ccdgo = '" + seccionUrbana + "' and manz_ccdgo = '" + manzana + "' order by secu_ccdgo";

            if (clase.equals("2")) {
                rawQuery = "select AsWKT(CastToPolygon(geometry)) from " + tabla + " where cod_mpio = '" + mpio + "' and cod_clase = '" + clase + "' and setu_ccdgo = '" + sectorUrbano + "' and secu_ccdgo = '" + seccionUrbana + "' and zu_ccdgo = '" + centroPoblado + "' and manz_ccdgo = '" + manzana + "' order by secu_ccdgo";
            }

            Cursor c = sp1.rawQuery(
                    rawQuery, null);

            while (c.moveToNext()) {
                String geometria_ini = c.getString(0);

                WKTReader wkt = new WKTReader();
                try {
                    if (wkt.read(geometria_ini).isValid()) {
                        Coordinate[] coord = wkt.read(geometria_ini).getCoordinates();
                        for (int j = 0; j < coord.length; j++) {
                            Double lat = coord[j].y;
                            Double lon = coord[j].x;
                            LatLng punto = new LatLng(lat, lon);
                            poligono.add(punto);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return poligono;
    }

    public List<LatLng> getSeccionRuralZoom(String mpio, String clase, String sectorRural, String seccionRural) {
        List<LatLng> poligono = new ArrayList<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = "rural";
            String rawQuery = "select AsWKT(CastToPolygon(geometry)) from " + tabla + " where mpio_cdpmp = '" + mpio + "' and clas_ccdgo = '" + clase + "' and setr_ccdgo = '" + sectorRural + "' and secr_ccdgo = '" + seccionRural + "'";


            Cursor c = sp1.rawQuery(
                    rawQuery, null);

            while (c.moveToNext()) {
                String geometria_ini = c.getString(0);

                WKTReader wkt = new WKTReader();
                try {
                    if (wkt.read(geometria_ini).isValid()) {
                        Coordinate[] coord = wkt.read(geometria_ini).getCoordinates();
                        for (int j = 0; j < coord.length; j++) {
                            Double lat = coord[j].y;
                            Double lon = coord[j].x;
                            LatLng punto = new LatLng(lat, lon);
                            poligono.add(punto);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return poligono;
    }

    public Map<String, String> getAGUrbano() {
        Map<String, String> ags = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            Cursor c = sp1.rawQuery(
                    "select distinct manz_cag from " + tabla, null);

            while (c.moveToNext()) {

                String dpto_ccdgo = c.getString(0);
                ags.put(dpto_ccdgo, dpto_ccdgo);

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return ags;
    }

    public Map<String, String> getAGRural() {
        Map<String, String> ags = new HashMap<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = "rural";
            Cursor c = sp1.rawQuery(
                    "select distinct secr_cag from " + tabla, null);

            while (c.moveToNext()) {

                String dpto_ccdgo = c.getString(0);
                ags.put(dpto_ccdgo, "");

            }
            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return ags;
    }

    public List<LatLng> getAgZoom(String ag) {
        List<LatLng> poligono = new ArrayList<>();

        try {
            String ruta_db = null;
            if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            } else {
                ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db" + File.separator;
            }

            SpatiaLiteManzanas db1 = new SpatiaLiteManzanas(context, databaseName, ruta_db);
            org.spatialite.database.SQLiteDatabase sp1 = db1.getWritableDatabase();

            String tabla = databaseName.replace(".db", "").toLowerCase();
            String rawQuery = "select AsWKT(CastToPolygon(geometry)) from " + tabla + " where manz_cag = '" + ag + "'";

            Cursor c = sp1.rawQuery(
                    rawQuery, null);

            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    String geometria_ini = c.getString(0);

                    WKTReader wkt = new WKTReader();
                    try {
                        if (wkt.read(geometria_ini).isValid()) {
                            Coordinate[] coord = wkt.read(geometria_ini).getCoordinates();
                            for (int j = 0; j < coord.length; j++) {
                                Double lat = coord[j].y;
                                Double lon = coord[j].x;
                                LatLng punto = new LatLng(lat, lon);
                                poligono.add(punto);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                tabla = "rural";
                rawQuery = "select AsWKT(CastToPolygon(geometry)) from " + tabla + " where secr_cag = '" + ag + "'";

                c = sp1.rawQuery(
                        rawQuery, null);

                while (c.moveToNext()) {
                    String geometria_ini = c.getString(0);

                    WKTReader wkt = new WKTReader();
                    try {
                        if (wkt.read(geometria_ini).isValid()) {
                            Coordinate[] coord = wkt.read(geometria_ini).getCoordinates();
                            for (int j = 0; j < coord.length; j++) {
                                Double lat = coord[j].y;
                                Double lon = coord[j].x;
                                LatLng punto = new LatLng(lat, lon);
                                poligono.add(punto);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            sp1.close();

        } catch (Exception e) {
            Log.d("mensaje:", String.valueOf(e));
        }

        return poligono;
    }
}
