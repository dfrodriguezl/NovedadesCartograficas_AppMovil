package co.gov.dane.danevisor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.Feature;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import jsqlite.Stmt;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GoogleMap.OnMyLocationClickListener,AdapterView.OnItemSelectedListener,
        GoogleMap.OnCameraMoveListener, SensorEventListener {

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    public static GoogleMap mMap;
    private Spinner mMapTypeSelector;

    private int mMapTypes[] = {
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE
    };

    private int tipo_edicion;

    private List<Polyline> line=new ArrayList<>();

    List<Polygon> polygon=new ArrayList<>();

    private List<Marker> puntos=new ArrayList<>();





    int sensor_activado=1;

    List<Marker> markers = new ArrayList<>();
    List<Marker> markers_intermedios = new ArrayList<>();

    Polygon Polygon_shape;
    Polyline Polyline_shape;
    Marker Point_shape;

    Polygon polygon_sel;
    List<Polygon> polygon_union=new ArrayList<>();

    private SensorManager mSensorManager;
    private float[] mRotationMatrix = new float[16];
    static ArrayList<MapaOffline> listado_mapas_offline;

    String codId="null";
    List<String> cod_poligonos_union=new ArrayList<>();

    SpatialAnalysis analisis=new SpatialAnalysis(MainActivity.this,MainActivity.this);

    Boolean CutPicker=true;
    Boolean JoinPicker=true;

    Boolean GeometriaUpdate=false;

    JSONObject atributos=new JSONObject();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        SpatiaLite sp=new SpatiaLite(MainActivity.this);

        sp.getWritableDatabase();

        */

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},00);


        //creación de los Folder para el aplicativo
        String ruta_mbtiles=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"mbtiles";
        String ruta_capturas=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"Capturas";

        File folder_mbtile = new File(ruta_mbtiles);

        if (!folder_mbtile.exists()) {
            folder_mbtile.mkdirs();
        }
        File folder_captura = new File(ruta_capturas);
        if (!folder_captura.exists()) {
            folder_captura.mkdirs();
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton save_edicion = (FloatingActionButton) findViewById(R.id.save_edicion);
        save_edicion.setVisibility(View.GONE);


        save_edicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FloatingActionButton save_edicion = (FloatingActionButton) findViewById(R.id.save_edicion);
                FloatingActionButton discard_save_editor = (FloatingActionButton) findViewById(R.id.discard_save_editor);
                drop_markers_intermediate();
                hide_add_punto();
                hide_undo_geom();
                hide_delete_geom();

                for (Marker marker : markers) {
                    if (marker.getTag().equals("edicion")) { //if a marker has desired tag
                        marker.remove();
                    }
                }
                markers.clear();

                if(tipo_edicion==1){


                    MarkerOptions opts=new MarkerOptions();

                    if (Point_shape != null) {
                        opts.position(Point_shape.getPosition());
                    }

                    puntos.add(mMap.addMarker(opts));


                    if(GeometriaUpdate){
                        for(int i=0;i<puntos.size();i++){

                            if(puntos.get(i).getId().equals(codId)){
                                puntos.get(i).remove();
                            }
                        }
                    }


                    Point_shape.remove();
                    Point_shape=null;



                }


                if(tipo_edicion==2){

                    List<LatLng> points = new ArrayList<>();
                    PolylineOptions opts=new PolylineOptions();

                    if (Polyline_shape != null) {
                        points=Polyline_shape.getPoints();
                        for (LatLng location : points) {
                            opts.add(location);
                        }
                    }

                    line.add(mMap.addPolyline(opts.width(5)));
                    line.get(line.size()-1).setClickable(true);
                    line.get(line.size()-1).setTag(atributos);

                    try {
                        line.get(line.size()-1).setColor(Color.parseColor(atributos.get("color").toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if(GeometriaUpdate){
                        for(int i=0;i<line.size();i++){

                            if(line.get(i).getId().equals(codId)){
                                line.get(line.size()-1).setTag(line.get(i).getTag());
                                line.get(i).remove();
                            }
                        }
                    }



                    Polyline_shape.remove();
                    Polyline_shape=null;

                }

                if(tipo_edicion==3){


                    List<LatLng> points = new ArrayList<>();
                    PolygonOptions opts=new PolygonOptions();

                    if (Polygon_shape != null) {
                        points=Polygon_shape.getPoints();
                        for (LatLng location : points) {
                            opts.add(location);
                        }
                    }

                    polygon.add(mMap.addPolygon(opts));
                    polygon.get(polygon.size()-1).setClickable(true);
                    polygon.get(polygon.size()-1).setTag(atributos);

                    try {
                        polygon.get(polygon.size()-1).setFillColor(Color.parseColor(atributos.get("color").toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(GeometriaUpdate){
                        for(int i=0;i<polygon.size();i++){

                            if(polygon.get(i).getId().equals(codId)){
                                polygon.get(i).remove();
                            }
                        }
                    }


                    Polygon_shape.remove();
                    Polygon_shape=null;




                }

                GeometriaUpdate=false;

                Snackbar.make(view, "Elemento Guardado", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                save_edicion.setVisibility(View.GONE);
                discard_save_editor.setVisibility(View.GONE);


            }
        });


        FloatingActionButton discard_save_editor = (FloatingActionButton) findViewById(R.id.discard_save_editor);
        discard_save_editor.setVisibility(View.GONE);


        discard_save_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FloatingActionButton discard_save_editor = (FloatingActionButton) findViewById(R.id.discard_save_editor);
                FloatingActionButton save_edicion = (FloatingActionButton) findViewById(R.id.save_edicion);
                hide_add_punto();
                hide_delete_geom();
                hide_undo_geom();
                drop_markers_intermediate();

                for (Marker marker : markers) {
                    if (marker.getTag().equals("edicion")) { //if a marker has desired tag
                        marker.remove();
                    }
                }
                markers.clear();

                if(tipo_edicion==1){
                    Point_shape.remove();
                    Point_shape=null;
                }

                if(tipo_edicion==2){
                    Polyline_shape.remove();
                    Polyline_shape=null;
                }
                if(tipo_edicion==3){
                    Polygon_shape.remove();
                    Polygon_shape=null;
                }


                Snackbar.make(view, "Elemento Borrado", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                discard_save_editor.setVisibility(View.GONE);
                save_edicion.setVisibility(View.GONE);
            }
        });



        FloatingActionButton delete_geom = (FloatingActionButton) findViewById(R.id.delete_geom);

        delete_geom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drop_markers_intermediate();
                for (Marker marker : markers) {
                    if (marker.getTag().equals("edicion")) { //if a marker has desired tag
                        marker.remove();
                    }
                }
                markers.clear();



                if(tipo_edicion==1){
                    for(int i=0;i<puntos.size();i++){

                        if(puntos.get(i).getId().equals(codId)){
                            puntos.get(i).remove();
                        }
                    }
                }

                if(tipo_edicion==2){
                    for(int i=0;i<line.size();i++){

                        if(line.get(i).getId().equals(codId)){
                            line.get(i).remove();
                        }
                    }
                    if(Polyline_shape!=null){
                        Polyline_shape.remove();
                    }
                    Polyline_shape=null;
                }
                if(tipo_edicion==3){
                    for(int i=0;i<polygon.size();i++){

                        if(polygon.get(i).getId().equals(codId)){
                            polygon.get(i).remove();
                        }
                    }
                    if(polygon_sel!=null){
                        polygon_sel.remove();
                    }
                    if(Polygon_shape!=null){
                        Polygon_shape.remove();
                    }

                    Polygon_shape=null;
                }

                hide_delete_geom();
                hide_undo_geom();
            }
        });

        FloatingActionButton undo_edit = (FloatingActionButton) findViewById(R.id.undo_edit);

        undo_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tipo_edicion==2){

                    markers.get(markers.size()-1).remove();
                    markers.remove(markers.size()-1);

                    List<LatLng> puntos=Polyline_shape.getPoints();
                    puntos.remove(puntos.size()-1);
                    Polyline_shape.setPoints(puntos);
                    distancia(Polyline_shape.getPoints());
                }
                if(tipo_edicion==3){
                    markers.get(markers.size()-1).remove();
                    markers.remove(markers.size()-1);

                    List<LatLng> puntos=Polygon_shape.getPoints();

                    puntos.remove(puntos.size()-2);
                    PolygonOptions opts=new PolygonOptions();
                    for(int i=0;i<puntos.size();i++){
                        opts.add(puntos.get(i));
                    }
                    Polygon_shape.remove();
                    Polygon_shape=null;
                    Polygon_shape=mMap.addPolygon(opts);
                    area(Polygon_shape);
                }

            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        int ZoomControl_id = 0x1;
        int position_control_id = 0x2;
        int brujula_control_id = 0x5;


        // Find ZoomControl view
        View zoomControls = mapFragment.getView().findViewById(ZoomControl_id);

        if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // ZoomControl is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

            // Align it to - parent top|left
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            params.setMargins(margin, 400, margin, margin);
        }

        View positioncontrol = mapFragment.getView().findViewById(position_control_id);

        if (positioncontrol != null && positioncontrol.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // ZoomControl is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) positioncontrol.getLayoutParams();

            // Align it to - parent top|left
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            params.setMargins(margin, 250, margin, margin);
        }
        View brujulacontrol = mapFragment.getView().findViewById(brujula_control_id);

        if (brujulacontrol != null && brujulacontrol.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // ZoomControl is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) brujulacontrol.getLayoutParams();

            // Align it to - parent top|left
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            params.setMargins(margin, 500, margin, margin);
        }


        mMapTypeSelector = (Spinner) findViewById(R.id.map_type_selector);
        mMapTypeSelector.setOnItemSelectedListener(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);



        final com.getbase.floatingactionbutton.FloatingActionsMenu menu_edicion = (com.getbase.floatingactionbutton.FloatingActionsMenu) findViewById(R.id.menu_fab);


        //codigo atributos
        com.getbase.floatingactionbutton.FloatingActionButton editarPunto = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.editarPunto);

        editarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            DialogoEdicion dialog =new DialogoEdicion(MainActivity.this,MainActivity.this,1);

            dialog.mostrarDialogoEdicion();
                menu_edicion.collapse();


            }
        });



        com.getbase.floatingactionbutton.FloatingActionButton editarLinea = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.editarLinea);

        editarLinea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DialogoEdicion dialog =new DialogoEdicion(MainActivity.this,MainActivity.this,2);

                dialog.mostrarDialogoEdicion();
                menu_edicion.collapse();
            }
        });




        com.getbase.floatingactionbutton.FloatingActionButton editarPolygono = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.editarPolygono);

        editarPolygono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogoEdicion dialog =new DialogoEdicion(MainActivity.this,MainActivity.this,3);

                dialog.mostrarDialogoEdicion();
                menu_edicion.collapse();
            }
        });



        hide_add_punto();
        hide_delete_geom();
        hide_undo_geom();


        final FloatingActionButton edit_atributos = (FloatingActionButton) findViewById(R.id.edit_atributos);


        edit_atributos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogoEdicion dialog =new DialogoEdicion(MainActivity.this,MainActivity.this,tipo_edicion,true,atributos);
                dialog.mostrarDialogoEdicion();

            }
        });








        final FloatingActionButton cut_geom = (FloatingActionButton) findViewById(R.id.cut_geom);

        cut_geom.setOnClickListener(new View.OnClickListener() {
            int num_clic=1;
            @Override
            public void onClick(View v) {
                if(CutPicker){
                    cut_geom.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.amarrillo)}));
                    CutPicker=false;

                    show_add_punto();
                    dibujo_linea();

                    num_clic=num_clic+1;

                }else{
                    cut_geom.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.dane)}));
                    CutPicker=true;
                    hide_add_punto();


                    if(num_clic>1){

                        String poligono=analisis.PoligonoWKT(polygon_sel);

                        String linea=analisis.LineaWKT(Polyline_shape);


                        String resultado=analisis.CutPolygonByLine(poligono,linea);

                        analisis.MultipolygonToPolygon(resultado);

                        for (Marker marker : markers) {
                            if (marker.getTag().equals("edicion")) {

                                marker.remove();

                            }
                        }
                        if(polygon_sel!=null){
                            polygon_sel.remove();
                        }

                        markers.clear();
                        Polyline_shape.remove();
                        Polyline_shape=null;
                        hide_cut_geom();
                        hide_undo_geom();
                    }
                    num_clic=1;

                }
            }
        });


        final FloatingActionButton edit_join = (FloatingActionButton) findViewById(R.id.edit_join);
        edit_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(JoinPicker){
                    edit_join.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.amarrillo)}));


                    JoinPicker=false;

                }else{
                    edit_join.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.dane)}));

                    List<String> geometrias=new ArrayList<>();

                    for (Polygon pol : polygon_union) {

                        String poligono=analisis.PoligonoWKT(pol);
                        geometrias.add(poligono);

                    }

                    for(String id: cod_poligonos_union){

                        for(int i=0;i<polygon.size();i++){

                            if(polygon.get(i).getId().equals(id)){
                                polygon.get(i).remove();
                                Log.d("poligono:","drop");
                            }
                        }

                    }

                    cod_poligonos_union.clear();

                    for(Polygon pol:polygon_union){
                        pol.remove();
                    }

                    polygon_union.clear();

                    if(polygon_sel!=null){
                        polygon_sel.remove();
                    }

                    analisis.unionPolygons(geometrias);

                    JoinPicker=true;

                }


            }
        });



        hide_cut_geom();
        hide_msg_distancia();
        hide_msg_area();

    }



    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners

    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float degree = Math.round(event.values[0]);

         updateCamera(degree);


    }
    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).tilt(60).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_layers) {

            AlertDialog.Builder mBuilder =new AlertDialog.Builder(MainActivity.this);
            final View mView =getLayoutInflater().inflate(R.layout.dialog_mapa_base,null);
            mBuilder.setView(mView);
            final AlertDialog dialog =mBuilder.create();

            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

            wmlp.gravity = Gravity.TOP | Gravity.CENTER;
            wmlp.y = 200;   //y position

            wmlp.width=mView.getWidth();
            dialog.getWindow().setDimAmount(0);
            dialog.show();

            LinearLayout mapa_base_normal = (LinearLayout) mView.findViewById(R.id.mapa_base_normal);

            mapa_base_normal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.setMapType(mMapTypes[0]);
                }
            });
            LinearLayout mapa_base_satelite = (LinearLayout) mView.findViewById(R.id.mapa_base_satelite);

            mapa_base_satelite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.setMapType(mMapTypes[1]);
                }
            });
            LinearLayout mapa_base_hibrido = (LinearLayout) mView.findViewById(R.id.mapa_base_hibrido);

            mapa_base_hibrido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.setMapType(mMapTypes[2]);
                }
            });
            LinearLayout mapa_base_tierra = (LinearLayout) mView.findViewById(R.id.mapa_base_tierra);

            mapa_base_tierra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.setMapType(mMapTypes[3]);
                }
            });
            LinearLayout mapa_base_none = (LinearLayout) mView.findViewById(R.id.mapa_base_none);

            mapa_base_none.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.setMapType(mMapTypes[4]);
                }
            });


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.mapas_offline){

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarMapasOffline();

        }
        else if (id == R.id.medir_distancia) {
            dibujo_linea();
            show_add_punto();

        }else if (id == R.id.medir_area) {
            dibujo_poligono();
            show_add_punto();

        } else if (id == R.id.limpiar_mapa) {
            mMap.clear();
        }
        else if (id == R.id.habilitar_giroscopio) {

            if(sensor_activado==0){
                Log.v("Sensor:", String.valueOf(sensor_activado));
                mSensorManager.unregisterListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
                sensor_activado=1;
            }
            if(sensor_activado==1){
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                        SensorManager.SENSOR_DELAY_NORMAL);
                sensor_activado=0;
            }


        }else if (id == R.id.captura) {

            //captureScreen();
            snapShot captura=new snapShot(MainActivity.this);
            captura.capturaPantalla();

        } else if (id == R.id.nav_buscar_coordenadas) {

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoCoordenadas();

        } else if (id == R.id.nav_acerca) {

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoAcerca();



        } else if (id == R.id.nav_ayuda) {

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoAyuda();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.getUiSettings().setZoomControlsEnabled(true);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }

        mMap.setOnMyLocationClickListener(this);
        mMap.setOnCameraMoveListener(this);



        //Mapa base del aplciativo
        listado_mapas_offline = new ArrayList<>();
        String ruta_mbtiles=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"mbtiles";
        File dir = new File(ruta_mbtiles);
        String[] files = dir.list(
                new FilenameFilter()
                {
                    public boolean accept(File dir, String name)
                    {
                        return name.endsWith(".mbtiles");
                    }
                });
        for (String s: files) {
            String nombre_capa=s.split("\\.")[0];
            listado_mapas_offline.add(new MapaOffline(nombre_capa,false,ruta_mbtiles+"/"+s,10));
        }


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                for (Marker marker : markers) {
                    if (marker.getTag().equals("edicion")) {

                        marker.remove();

                    }
                }
                markers.clear();
                if(polygon_sel!=null){
                    polygon_sel.remove();
                }

                hide_cut_geom();
                hide_delete_geom();
                drop_markers_intermediate();
                hide_msg_distancia();
                hide_msg_area();

            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {

                //edicion(polygon.getPoints());

                if(polygon_sel!=null){
                    polygon_sel.remove();
                }

                PolygonOptions opts=new PolygonOptions();
                opts.addAll(polygon.getPoints());

                polygon_sel= mMap.addPolygon(opts);
                polygon_sel.setStrokeColor(Color.BLUE);
                polygon_sel.setFillColor(getResources().getColor(R.color.poligono_touch));

                if(!JoinPicker){
                    Polygon p=mMap.addPolygon(opts);
                    cod_poligonos_union.add(polygon.getId());
                    polygon_union.add(p);

                    for (Polygon pol : polygon_union) {
                        pol.setStrokeColor(Color.BLUE);
                        pol.setFillColor(getResources().getColor(R.color.poligono_touch));
                    }
                }


                tipo_edicion=3;

                codId=polygon.getId();

                show_delete_geom();

                String poligono=analisis.PoligonoWKT(polygon);


                area(polygon);
                show_cut_geom();


                try {

                    atributos=new JSONObject(polygon.getTag().toString());

                } catch (Throwable t) {

                }

                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {


                    @Override
                    public void onMarkerDragStart(Marker arg0) {
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onMarkerDragEnd(Marker arg0) {

                        dragMarker(arg0,3);

                    }

                    @Override
                    public void onMarkerDrag(Marker arg0) {
                    }
                });

            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener(){

            @Override
            public void onPolylineClick(Polyline polyline) {

                tipo_edicion=2;
                edicion(polyline.getPoints());
                codId=polyline.getId();
                show_delete_geom();
                distancia(polyline.getPoints());

                try {

                    atributos=new JSONObject(polyline.getTag().toString());

                } catch (Throwable t) {

                }


                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                    @Override
                    public void onMarkerDragStart(Marker arg0) {
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onMarkerDragEnd(Marker arg0) {

                        dragMarker(arg0,2);

                    }

                    @Override
                    public void onMarkerDrag(Marker arg0) {
                    }
                });
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                tipo_edicion=1;
                edicion_puntos(marker.getPosition());
                codId=marker.getId();
                show_delete_geom();
                drop_markers_intermediate();

                try {

                    atributos=new JSONObject(marker.getTag().toString());

                } catch (Throwable t) {

                }

                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                    @Override
                    public void onMarkerDragStart(Marker arg0) {
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onMarkerDragEnd(Marker arg0) {

                        dragMarker(arg0,1);

                    }

                    @Override
                    public void onMarkerDrag(Marker arg0) {
                    }
                });

                return false;
            }
        });


        Controlador con=new Controlador(MainActivity.this,MainActivity.this);
        con.getData();


        dataBase db=new dataBase(MainActivity.this,MainActivity.this);
        db.getGeomFromDatabase();


    }




    public void dibujo_punto(){
        tipo_edicion=1;

        FloatingActionButton add_punto = (FloatingActionButton) findViewById(R.id.add_punto);

        add_punto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LatLng center = mMap.getCameraPosition().target;
                Punto mipunto=new Punto(center);
                Marker marker = mMap.addMarker(new MarkerOptions().position(mipunto.getPunto()));
                Point_shape=marker;
                hide_add_punto();

            }
        });


    }

    public void dibujo_linea(){

        tipo_edicion=2;



        FloatingActionButton add_punto = (FloatingActionButton) findViewById(R.id.add_punto);

        add_punto.setOnClickListener(new View.OnClickListener() {
            int secuencia=1;
            @Override
            public void onClick(View view) {

                if(secuencia>1){
                    show_undo_geom();
                    show_delete_geom();
                }
                secuencia=secuencia+1;


                LatLng center = mMap.getCameraPosition().target;


                Punto mipunto=new Punto(center);

                Marker marker=createMarker(mipunto.getLat(), mipunto.getLng(), "pto", "");

                List<LatLng> points = new ArrayList<>();

                PolylineOptions opts=new PolylineOptions();

                if (Polyline_shape != null) {
                    points=Polyline_shape.getPoints();
                    for (LatLng location : points) {
                        opts.add(location);
                    }
                    points.add(mipunto.getPunto());
                    opts.add(mipunto.getPunto());
                }else{
                    opts.add(mipunto.getPunto());
                }



                if (Polyline_shape != null) {
                    Polyline_shape.remove();
                }



                Polyline_shape=mMap.addPolyline(opts.width(2));
                Polyline_shape.setTag("edit_polygono");
                Polyline_shape.setClickable(true);
                distancia(Polyline_shape.getPoints());



                marker.setTag("edicion");

                markers.add(marker);


                double distance = SphericalUtil.computeLength(points);

                //Toast.makeText(MainActivity.this, "Distancia:\n"+ Double.toString(distance), Toast.LENGTH_LONG).show();

            }
        });






    }



    public void dibujo_poligono(){

        tipo_edicion=3;

        FloatingActionButton add_punto = (FloatingActionButton) findViewById(R.id.add_punto);

        add_punto.setOnClickListener(new View.OnClickListener() {
            int control=1;
            int secuencia=1;
            @Override
            public void onClick(View view) {

                if(secuencia>1){
                    show_undo_geom();
                    show_delete_geom();
                }
                secuencia=secuencia+1;

                LatLng center = mMap.getCameraPosition().target;

                Punto mipunto=new Punto(center);

                     Marker marker=createMarker(mipunto.getLat(), mipunto.getLng(), "pto", "");

                    List<LatLng> points = new ArrayList<>();

                    PolygonOptions opts=new PolygonOptions();

                    if (Polygon_shape != null) {
                        points=Polygon_shape.getPoints();
                        if(control>2){
                            points.remove( points.size() - 1 );
                        }
                        for(int i=0;i<points.size();i++){
                            opts.add(points.get(i));
                        }
                        opts.add(mipunto.getPunto());
                    }else{
                        opts.add(mipunto.getPunto());
                    }


                    if (Polygon_shape != null) {
                        Polygon_shape.remove();
                    }

                    Polygon_shape=mMap.addPolygon(opts);
                    Polygon_shape.setClickable(true);
                    area(Polygon_shape);

                marker.setTag("edicion");

                markers.add(marker);

                control=control+1;

            }
        });











    }

    //Permisos para acceder a utilidades del dispositivo.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
}


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mMap.setMapType(mMapTypes[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCameraMove() {

        TextView latitud = (TextView)findViewById(R.id.latitud);
        TextView longitud = (TextView)findViewById(R.id.longitud);

        double mylat = mMap.getCameraPosition().target.latitude;
        double mylon = mMap.getCameraPosition().target.longitude;

        latitud.setText("Lat: "+String.format("%.4f", mylat));
        longitud.setText("Lon: "+String.format("%.4f", mylon));

    }

public void edicion_puntos(LatLng ptos){
    for (Marker marker : markers) {
        if (marker.getTag().equals("edicion")) { //if a marker has desired tag
            marker.remove();
        }
    }
    markers.clear();

    Marker mimarker=createMarker( ptos.latitude,ptos.longitude,"ivan","edicion");

    markers.add(mimarker);
}

    @SuppressLint("RestrictedApi")
    public void edicion(List<LatLng> ptos){


        //se borran los marcadores de edición.
        for (Marker marker : markers) {
            if (marker.getTag().equals("edicion")) { //if a marker has desired tag
                marker.remove();
            }
        }
        markers.clear();

        drop_markers_intermediate();


        for (LatLng punto : ptos) {
            Marker mimarker=createMarker( punto.latitude,punto.longitude,"ivan","edicion");

            markers.add(mimarker);
        }


        add_markers_intermediate(ptos);


    }

    public void add_markers_intermediate(List<LatLng> ptos){
        List<LatLng> puntos_intermedios = new ArrayList<>();
        if(ptos.size()>1){
            for(int i=0;i<ptos.size()-1;i++){
                Double Xcoord=(ptos.get(i+1).latitude+ptos.get(i).latitude)/2;
                Double Ycoord=(ptos.get(i+1).longitude+ptos.get(i).longitude)/2;
                LatLng coord_middle=new LatLng(Xcoord,Ycoord);
                puntos_intermedios.add(coord_middle);
                Marker mimarker=createMarker1( Xcoord,Ycoord,"ivan","edicion");
                mimarker.setTag("drag:"+String.valueOf(i+1));
                markers_intermedios.add(mimarker);
            }
        }
    }

    public void drop_markers_intermediate(){
        for (Marker marker : markers_intermedios) {
            if (String.valueOf(marker.getTag()).contains("drag:")) { //if a marker has desired tag
                marker.remove();
            }
        }
        markers_intermedios.clear();
    }

    public void dragMarker(Marker arg0, int tipo_edicion){

        //para mover la vista al marcador
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));

        //se toman los marcadores que participan de la edición para actualizar su posición.
        ArrayList<LatLng> edit= new ArrayList<LatLng>();

        for (Marker marker : markers) {
            if (marker.getTag().equals("edicion")) { //if a marker has desired tag

                edit.add(marker.getPosition());

            }
        }
        if(String.valueOf(arg0.getTag()).contains("drag:")){
            int indice= Integer.parseInt(String.valueOf(arg0.getTag()).substring(5));
            edit.add(indice,arg0.getPosition());
            edicion(edit);
        }

        drop_markers_intermediate();
        add_markers_intermediate(edit);


        Log.d("total:",String.valueOf(edit));

        //si la edición es de poligonos
        if(tipo_edicion==3){
            edit_polygono(edit);

        }
        //si la edición es de polilineas
        if(tipo_edicion==2){
            edit_polyline(edit);

        }
        if(tipo_edicion==1){
            edit_point(edit.get(0));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.save_edicion);
        fab.setVisibility(View.VISIBLE);
        FloatingActionButton discard_save_editor = (FloatingActionButton) findViewById(R.id.discard_save_editor);
        discard_save_editor.setVisibility(View.VISIBLE);

        GeometriaUpdate=true;
    }

    public void edit_point(LatLng edit){

        MarkerOptions opts=new MarkerOptions();

        opts.position(edit);

        if (Point_shape != null) {
            Point_shape.remove();
        }

        Point_shape=mMap.addMarker(opts);


    }

    public void edit_polyline(ArrayList<LatLng> edit){
        PolylineOptions opts=new PolylineOptions();
        for (LatLng location : edit) {
            opts.add(location);
        }

        if (Polyline_shape != null) {
            Polyline_shape.remove();
        }

        Polyline_shape=mMap.addPolyline(opts.width(2));
        Polyline_shape.setTag("edit_polygono");
        Polyline_shape.setClickable(false);
        distancia(Polyline_shape.getPoints());
    }


    public void edit_polygono(ArrayList<LatLng> edit){

        //creación del nuevo poligono
        PolygonOptions opts=new PolygonOptions();
        for (LatLng location : edit) {
            opts.add(location);
        }

        if (Polygon_shape != null) {
            Polygon_shape.remove();
        }

        Polygon_shape = mMap.addPolygon(opts.strokeColor(Color.RED).fillColor(Color.parseColor("#8059A4FC")));

        Polygon_shape.setTag("edit_polygono");
        Polygon_shape.setClickable(false);

        //determinación del area del poligono
        area(Polygon_shape);

    }




    public void area(Polygon pol){
        TextView area_campo = (TextView)findViewById(R.id.area);

        double area = SphericalUtil.computeArea(pol.getPoints());
        area=area*0.0001;

        area_campo.setText("Área: "+String.format("%.2f",area)+" Ha");
        area_campo.setTextColor(Color.parseColor("#2F75C9"));
        show_msg_area();
        distancia(pol.getPoints());
    }

    public void distancia(List<LatLng> puntos){
        TextView distancia_campo = (TextView)findViewById(R.id.distancia);
        double distancia=SphericalUtil.computeLength(puntos);

        distancia_campo.setText("Distancia: "+String.format("%.2f",distancia)+" m");
        distancia_campo.setTextColor(Color.parseColor("#DF0000"));
        show_msg_distancia();
    }



    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        Marker mPerth;

        mPerth= mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .snippet(snippet)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ping))
        );

        mPerth.setTag("edicion");
        mPerth.setDraggable(true);

        return mPerth;
    }


    protected Marker createMarker1(double latitude, double longitude, String title, String snippet) {

        Marker mPerth;

        mPerth= mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .snippet(snippet)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.drag_marker))
        );

        mPerth.setTag("edicion");
        mPerth.setDraggable(true);

        return mPerth;
    }






public void show_add_punto(){

    FloatingActionButton add_punto = (FloatingActionButton) findViewById(R.id.add_punto);
    add_punto.setVisibility(View.VISIBLE);

}
public void hide_add_punto(){
    FloatingActionButton add_punto = (FloatingActionButton) findViewById(R.id.add_punto);
    add_punto.setVisibility(View.GONE);
    }

public void hide_delete_geom(){
    FloatingActionButton delete_geom = (FloatingActionButton) findViewById(R.id.delete_geom);
    delete_geom.setVisibility(View.GONE);
}

public void show_delete_geom(){
    FloatingActionButton delete_geom = (FloatingActionButton) findViewById(R.id.delete_geom);
    delete_geom.setVisibility(View.VISIBLE);
}


public void show_undo_geom(){
    FloatingActionButton undo_edit = (FloatingActionButton) findViewById(R.id.undo_edit);
    undo_edit.setVisibility(View.VISIBLE);
}
public void hide_undo_geom(){
    FloatingActionButton undo_edit = (FloatingActionButton) findViewById(R.id.undo_edit);
    undo_edit.setVisibility(View.GONE);
}

public void show_save_edicion(){
    FloatingActionButton  save_edicion = (FloatingActionButton ) findViewById(R.id.save_edicion);
    save_edicion.setVisibility(View.VISIBLE);
}

public void hide_save_edicion(){
    FloatingActionButton  save_edicion = (FloatingActionButton ) findViewById(R.id.save_edicion);
    save_edicion.setVisibility(View.GONE);
}

public void show_discard_save_edicion(){
    FloatingActionButton discard_save_editor = (FloatingActionButton) findViewById(R.id.discard_save_editor);
    discard_save_editor.setVisibility(View.VISIBLE);
}

public void hide_discard_save_edicion(){
    FloatingActionButton discard_save_editor = (FloatingActionButton) findViewById(R.id.discard_save_editor);
    discard_save_editor.setVisibility(View.GONE);
}

public void hide_cut_geom(){
    final FloatingActionButton cut_geom = (FloatingActionButton) findViewById(R.id.cut_geom);
    cut_geom.setVisibility(View.GONE);
}

public void show_cut_geom(){
    final FloatingActionButton cut_geom = (FloatingActionButton) findViewById(R.id.cut_geom);
    cut_geom.setVisibility(View.VISIBLE);
}

public void show_msg_distancia(){
    TextView distancia_campo = (TextView)findViewById(R.id.distancia);
    distancia_campo.setVisibility(View.VISIBLE);

}
public void hide_msg_distancia(){
    TextView distancia_campo = (TextView)findViewById(R.id.distancia);
    distancia_campo.setVisibility(View.GONE);

}

public void show_msg_area(){
    TextView area_campo = (TextView)findViewById(R.id.area);
    area_campo.setVisibility(View.VISIBLE);
}

public void hide_msg_area(){
    TextView area_campo = (TextView)findViewById(R.id.area);
    area_campo.setVisibility(View.GONE);
}




}