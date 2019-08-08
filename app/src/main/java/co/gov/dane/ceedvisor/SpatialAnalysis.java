package co.gov.dane.ceedvisor;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jsqlite.Stmt;

public class SpatialAnalysis {


    Context context;
    MainActivity main;

    SpatialAnalysis(Context context,MainActivity main){
        this.context=context;
        this.main=main;
    }

    public String puntoWKT(Marker marker){

        Coordinate punto=new Coordinate(marker.getPosition().longitude,marker.getPosition().latitude);

        GeometryFactory gf = new GeometryFactory();

        Point p= gf.createPoint(punto);

        return String.valueOf(p);
    }

    public String LineaWKT(Polyline pol){

        Coordinate[] puntos=new Coordinate[pol.getPoints().size()];

        for(int i=0;i<pol.getPoints().size();i++){

            puntos[i]=new Coordinate(pol.getPoints().get(i).longitude,pol.getPoints().get(i).latitude);
        }
        GeometryFactory gf = new GeometryFactory();

        LineString line = gf.createLineString(puntos);

        return String.valueOf(line);

    }

    public String PoligonoWKT(Polygon pol){

        Coordinate[] puntos=new Coordinate[pol.getPoints().size()];

        for(int i=0;i<pol.getPoints().size();i++){

            puntos[i]=new Coordinate(pol.getPoints().get(i).longitude,pol.getPoints().get(i).latitude);
        }
        GeometryFactory gf = new GeometryFactory();


        org.locationtech.jts.geom.Polygon p=gf.createPolygon(puntos);

        return String.valueOf(p);

    }

    public Boolean PolygonValid(String poligono){

        Boolean valido=false;

        try {
            File spatialDbFile=context.getDatabasePath("geom.db");

            jsqlite.Database db = new jsqlite.Database();
            db.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE
                    | jsqlite.Constants.SQLITE_OPEN_CREATE);

            StringBuilder sb = new StringBuilder();

            String sql=
                    "SELECT ST_IsValid(ST_GeomFromText('"+poligono+"')) as valid;";


            Stmt stmt = db.prepare(sql);

            if (stmt.step()) {

                sb.append(stmt.column_string(0));

            }
            db.close();

            String resultado=sb.toString();

            if(resultado.equals("1")){
                valido=true;
            }



        } catch (Exception e) {
            e.printStackTrace();
            return valido;

        }

        return valido;

    }


    public Boolean PolylineItsSimple(String polyline){

        Boolean simple=false;

        try {
            File spatialDbFile=context.getDatabasePath("geom.db");

            jsqlite.Database db = new jsqlite.Database();
            db.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE
                    | jsqlite.Constants.SQLITE_OPEN_CREATE);

            StringBuilder sb = new StringBuilder();

            String sql=
                    "SELECT ST_IsSimple(ST_GeomFromText('"+polyline+"'));";


            Stmt stmt = db.prepare(sql);

            if (stmt.step()) {

                sb.append(stmt.column_string(0));

            }
            db.close();

            String resultado=sb.toString();

            if(resultado.equals("1")){
                simple=true;
            }



        } catch (Exception e) {
            e.printStackTrace();
            return simple;

        }


        return simple;

    }



    public String CutPolygonByLine(String poligono, String linea){


        try {
            File spatialDbFile=context.getDatabasePath("geom.db");

            jsqlite.Database db = new jsqlite.Database();
            db.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE
                    | jsqlite.Constants.SQLITE_OPEN_CREATE);

            StringBuilder sb = new StringBuilder();
            StringBuilder sb1 = new StringBuilder();

            String sql=
                        "SELECT SetDecimalPrecision(15), st_astext(CastToPolygon(Split(input.g, blade.g))) , AsText(Split(input.g, blade.g)) as cut "+
                        "FROM "+
                        "(SELECT GeomFromText('"+poligono+"') AS g) AS input,"+
                        "(SELECT GeomFromText('"+linea+"') AS g) AS blade";


            Stmt stmt = db.prepare(sql);

            if (stmt.step()) {

                sb1.append(stmt.column_string(1));
                sb.append(stmt.column_string(2));

            }
            db.close();

            //quiere decir que se esta devolviendo un poligono, lo cual esta mal, se espera un multipoligono del recorte
            if(!sb1.toString().equals("null")){
                return "fallo";
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "fallo";
        }



    }

    public String CutPointPolygon(String linea,ArrayList<Integer> PolygonIndex){

        try {
            File spatialDbFile=context.getDatabasePath("geom.db");

            jsqlite.Database db = new jsqlite.Database();
            db.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE
                    | jsqlite.Constants.SQLITE_OPEN_CREATE);

            StringBuilder sb = new StringBuilder();

            ArrayList<Integer> values = new ArrayList<>();

            int maximo=PolygonIndex.size();


            Boolean borrar_geom=false;

            for(int k=0;k<maximo;k++){

                int i=PolygonIndex.get(k);

                String geom=main.analisis.PoligonoWKT(main.polygon.get(i));

                String sql=
                        "SELECT SetDecimalPrecision(15), AsText(ST_Snap( "+
                                "ST_GeomFromText('"+geom+"'), "+
                                "(SELECT (ST_Intersection(blade.g, ST_ExteriorRing(input.g))) as cut "+
                                "FROM "+
                                " (SELECT GeomFromText('"+geom+"') AS g) AS input, "+
                                "(SELECT GeomFromText('"+linea+"') AS g) AS blade "+
                                " ),0.000000001));";


                Stmt stmt = db.prepare(sql);

                if (stmt.step()) {
                    sb.setLength(0);
                    sb.append(stmt.column_string(1));
                }

                if(sb.toString().equals("null")){

                }else{

                    borrar_geom=true;
                    Log.d("upd:",sb.toString());


                    values.add(i);



                    String geometria_ini = sb.toString();

                    WKTReader wkt=new WKTReader();

                    Coordinate[] coord=wkt.read(geometria_ini).getCoordinates();

                    PolygonOptions opts=new PolygonOptions();
                    for(int j=0;j<coord.length;j++){
                        Double lat=coord[j].y;
                        Double lon=coord[j].x;
                        LatLng punto=new LatLng(lat,lon);
                        opts.add(punto);
                    }


                    main.polygon.add(main.mMap.addPolygon(opts));
                    main.polygon.get(main.polygon.size()-1).setClickable(true);

                    String[] array_color = new String[0];
                    array_color = main.getResources().getStringArray(R.array.color_poligono);

                    main.polygon.get(main.polygon.size()-1).setFillColor(android.graphics.Color.parseColor(array_color[3]));


                    dataBase db1=new dataBase(main,main);


                    String descripcion="";

                    try {

                        String code=main.atributos.get("descripcion").toString();

                        descripcion=code;


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Integer id=db1.getMaxIdNovedad()+1;

                    int tipo_geometria=3;

                    SpatialAnalysis analisis=new SpatialAnalysis(main,main);

                    String wkt1=analisis.PoligonoWKT(main.polygon.get(main.polygon.size()-1));
                    String tipo= "ENA";

                    Novedades novedad=new Novedades(main,main,id,main.id_dispositivo,tipo_geometria,wkt1,tipo,descripcion);
                    Boolean inserto=novedad.insertarNovedad();

                    Mensajes mitoast =new Mensajes(main);
                    mitoast.generarToast("Elemento guardado");

                    JSONObject atributos=new JSONObject();
                    try{

                        atributos.put("id", id);
                        atributos.put("tipo","ENA");
                        atributos.put("descripcion",descripcion);

                    }catch (JSONException e) {

                    }
                    main.polygon.get(main.polygon.size()-1).setTag(atributos);


                }


            }


            if(borrar_geom){

                for(int x = values.size() - 1; x >= 0; x--)
                {
                    Integer num=values.get(x);

                    main.codId=main.polygon.get(num).getId();

                    main.update_atributos(main.polygon.get(num).getTag().toString());
                    Integer id_p = Integer.parseInt(main.atributos.get("id").toString());
                    main.borrar_poligono(id_p);

                }


            }






            db.close();

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "fallo";
        }

    }



    public boolean MultipolygonToPolygon(String Multipolygon){


        try {
            WKTReader reader = new WKTReader();

            Geometry geom=reader.read(Multipolygon);

            MultiPolygon Mpoly = (MultiPolygon) geom;


            int numGeometries=Mpoly.getNumGeometries();



            for(int i=0;i<numGeometries;i++){

                Coordinate[] coord=Mpoly.getGeometryN(i).getCoordinates();



                PolygonOptions opts=new PolygonOptions();
                for(int j=0;j<coord.length;j++){
                    Double lat=coord[j].y;
                    Double lon=coord[j].x;
                    LatLng punto=new LatLng(lat,lon);
                    opts.add(punto);
                }


                main.polygon.add(main.mMap.addPolygon(opts));
                main.polygon.get(main.polygon.size()-1).setClickable(true);


                String[] array_color = new String[0];
                array_color = main.getResources().getStringArray(R.array.color_poligono);

                main.polygon.get(main.polygon.size()-1).setFillColor(android.graphics.Color.parseColor(array_color[3]));


                dataBase db=new dataBase(main,main);


                String descripcion="";

                try {

                    String code=main.atributos.get("descripcion").toString();
                    descripcion=code;


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Integer id=db.getMaxIdNovedad()+1;

                int tipo_geometria=3;

                SpatialAnalysis analisis=new SpatialAnalysis(main,main);

                String wkt=analisis.PoligonoWKT(main.polygon.get(main.polygon.size()-1));
                String tipo= "ENA";

                Novedades novedad=new Novedades(main,main,id,main.id_dispositivo,tipo_geometria,wkt,tipo,descripcion);
                Boolean inserto=novedad.insertarNovedad();

                Mensajes mitoast =new Mensajes(main);
                mitoast.generarToast("Elemento guardado");

                JSONObject atributos=new JSONObject();
                try{

                    atributos.put("id", id);
                    atributos.put("tipo","ENA");
                    atributos.put("descripcion",descripcion);

                }catch (JSONException e) {

                }
                main.polygon.get(main.polygon.size()-1).setTag(atributos);


            }




        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }


    public void unionPolygons(List<String> poligonos,String descripcion){



        String wkt_union="";
        try {
            File spatialDbFile=context.getDatabasePath("geom.db");

            jsqlite.Database db = new jsqlite.Database();
            db.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE
                    | jsqlite.Constants.SQLITE_OPEN_CREATE);

            StringBuilder sb = new StringBuilder();



            String sql=
                    "SELECT SetDecimalPrecision(15), st_astext(geom) FROM ( SELECT st_union(input.g,blade.g) as geom "+
                    "FROM "+
                    "(SELECT geomFromText('"+poligonos.get(0)+"') AS g) AS input, "+
                    "(SELECT geomFromText('"+poligonos.get(1)+"') AS g) AS blade) as sub";

            Log.d("sql:",sql);

            Stmt stmt = db.prepare(sql);

            if (stmt.step()) {
                sb.append(stmt.column_string(1));

            }
            db.close();
            wkt_union=sb.toString();




        WKTReader wkt = new WKTReader();

        Coordinate[] coord=wkt.read(wkt_union).getCoordinates();



        //FORMA DOS DE UNION//
        /*
        try {
        WKTReader wkt = new WKTReader();
        GeometryFactory geoFac = new GeometryFactory();
        ArrayList<Geometry> geometries = new ArrayList<>();

        for(int i=0;i<poligonos.size();i++){

            try {

                Geometry geom = wkt.read(poligonos.get(i));

                geometries.add(geom);


            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        GeometryCollection geometryCollection = (GeometryCollection) geoFac.buildGeometry(geometries);

        Geometry geom=geometryCollection.union();



        Coordinate[] coord=geom.getCoordinates();

        */


        PolygonOptions opts=new PolygonOptions();
        for(int j=0;j<coord.length;j++){
            Double lat=coord[j].y;
            Double lon=coord[j].x;
            LatLng punto=new LatLng(lat,lon);
            opts.add(punto);
        }

        Polygon p=main.mMap.addPolygon(opts);
        p.setClickable(true);
        main.polygon.add(p);

        String[] array_color = new String[0];
        array_color = main.getResources().getStringArray(R.array.color_poligono);
        main.polygon.get(main.polygon.size()-1).setFillColor(android.graphics.Color.parseColor(array_color[3]));

        dataBase db1=new dataBase(main,main);
        Integer id=db1.getMaxIdNovedad()+1;

        int tipo_geometria=3;
        SpatialAnalysis analisis=new SpatialAnalysis(main,main);

        String geometria=analisis.PoligonoWKT(main.polygon.get(main.polygon.size()-1));
        String tipo= "ENA";
        Novedades novedad=new Novedades(main,main,id,main.id_dispositivo,tipo_geometria,geometria,tipo,descripcion);
        Boolean inserto=novedad.insertarNovedad();

        Mensajes mitoast =new Mensajes(main);
        mitoast.generarToast("Elemento guardado");


        JSONObject atributos=new JSONObject();
        try{

            atributos.put("id", id);
            atributos.put("tipo","ENA");
            atributos.put("descripcion",descripcion);

        }catch (JSONException e) {

        }
        main.polygon.get(main.polygon.size()-1).setTag(atributos);


        } catch (Exception e) {
            e.printStackTrace();

        }



    }

    public String centroidePoligono(Polygon pol){

        Coordinate[] puntos=new Coordinate[pol.getPoints().size()];

        for(int i=0;i<pol.getPoints().size();i++){

            puntos[i]=new Coordinate(pol.getPoints().get(i).longitude,pol.getPoints().get(i).latitude);
        }
        GeometryFactory gf = new GeometryFactory();

        org.locationtech.jts.geom.Polygon p=gf.createPolygon(puntos);

        return String.valueOf(p.getCentroid());

    }







}
