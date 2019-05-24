package co.gov.dane.danevisor;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
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

    public String CutPolygonByLine(String poligono, String linea){


        try {
            File spatialDbFile=context.getDatabasePath("geom.db");

            jsqlite.Database db = new jsqlite.Database();
            db.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE
                    | jsqlite.Constants.SQLITE_OPEN_CREATE);

            StringBuilder sb = new StringBuilder();

            String sql="SELECT AsText(Split(input.g, blade.g)) as cut "+
                    "FROM "+
                    "(SELECT GeomFromText('"+poligono+"') AS g) AS input,"+
                    "(SELECT GeomFromText('"+linea+"') AS g) AS blade";


            Stmt stmt = db.prepare(sql);

            if (stmt.step()) {
                sb.append(stmt.column_string(0));

            }
            db.close();
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "fallo";
        }



    }

    public void MultipolygonToPolygon(String Multipolygon){


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
                    Log.d("coord:", String.valueOf(punto));
                    opts.add(punto);
                }

                Polygon p=main.mMap.addPolygon(opts);
                p.setClickable(true);
                main.polygon.add(p);



                Log.d("Pol:", String.valueOf(Mpoly.getGeometryN(i)));
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }



    }


    public void unionPolygons(List<String> poligonos){

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

    }




}
