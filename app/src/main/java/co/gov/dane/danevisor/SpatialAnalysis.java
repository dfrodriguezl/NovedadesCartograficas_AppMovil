package co.gov.dane.danevisor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;
import org.json.JSONObject;
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

import co.gov.dane.danevisor.EstructuraDataBase.Estructura;
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
                    opts.add(punto);
                }


                main.polygon.add(main.mMap.addPolygon(opts));
                main.polygon.get(main.polygon.size()-1).setClickable(true);


                String[] array_color = new String[0];
                array_color = main.getResources().getStringArray(R.array.color_poligono);

                main.polygon.get(main.polygon.size()-1).setFillColor(android.graphics.Color.parseColor(array_color[3]));


                dataBase db=new dataBase(main,main);




                int id_padre=0;

                try {

                    id_padre= Integer.parseInt(main.atributos.get("id").toString())+i+1;

                } catch (JSONException e) {
                    e.printStackTrace();
                }



                Integer id=db.getMaxIdNovedad()+1;

                int tipo_geometria=3;

                SpatialAnalysis analisis=new SpatialAnalysis(main,main);

                String wkt=analisis.PoligonoWKT(main.polygon.get(main.polygon.size()-1));
                String tipo= "ENA";
                String descripcion= db.getsiguienterecorte(String.valueOf(id_padre));
                Novedades novedad=new Novedades(main,main,id,main.id_dispositivo,tipo_geometria,wkt,tipo,descripcion);
                Boolean inserto=novedad.insertarNovedad();
                Mensajes mitoast =new Mensajes(main);
                mitoast.generarToast("Elemento guardado");

                String centroide=main.analisis.centroidePoligono(main.polygon.get(main.polygon.size()-1));
                Coordinate[] coord1=reader.read(centroide).getCoordinates();

                MarkerOptions opts1=new MarkerOptions();
                LatLng punto;
                IconGenerator iconFactory = new IconGenerator(main);

                //iconFactory.setColor(main.getResources().getColor(android.R.color.transparent));

                for(int j=0;j<coord1.length;j++){
                    Double lat=coord1[j].y;
                    Double lon=coord1[j].x;
                    punto=new LatLng(lat,lon);
                    opts1.position(punto);
                    opts1.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(String.valueOf(id))));
                    opts1.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                }



                main.puntos.add(main.mMap.addMarker(opts1));



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
