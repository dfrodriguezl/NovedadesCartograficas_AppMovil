package co.gov.dane.ceedvisor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.google.maps.android.SphericalUtil;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GoogleMap.OnMyLocationClickListener,AdapterView.OnItemSelectedListener,
        GoogleMap.OnCameraMoveListener, SensorEventListener {

    private Menu menu;

    Session session;

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

    private Sensor mAccelerometer;


    private int tipo_edicion;

     List<Polyline> line=new ArrayList<>();

    List<Polygon> polygon=new ArrayList<>();

    List<Marker> puntos=new ArrayList<>();

    List<Marker> label=new ArrayList<>();

    int sensor_activado=1;
    int tipo_edicion_guardar=1;

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
    Boolean medicion=false;
    int control_dibujo=1;

    List<String> cod_poligonos_union=new ArrayList<>();

    SpatialAnalysis analisis=new SpatialAnalysis(MainActivity.this,MainActivity.this);


    Boolean JoinPicker=true;

    Boolean GeometriaUpdate=false;
    Boolean isChecked=false;

    JSONObject atributos=new JSONObject();

    Mensajes mitoast =new Mensajes(MainActivity.this);
    String id_dispositivo;

    FloatingActionButton asignar_tarea=null;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},00);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},00);



        Context context = this;

        UniqueId llave=new UniqueId();

        id_dispositivo= llave.id(context);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton save_edicion = (FloatingActionButton) findViewById(R.id.save_edicion);
        save_edicion.setVisibility(View.GONE);


        save_edicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hide_discard_save_edicion();
                hide_save_edicion();
                drop_markers_intermediate();
                hide_add_punto();
                hide_undo_geom();
                hide_delete_geom();
                hide_edit_atributos();

                drop_markers_edicion();



                if(tipo_edicion_guardar==1 ){

                    if(tipo_edicion==1){


                        MarkerOptions opts=new MarkerOptions();

                        if (Point_shape != null) {
                            opts.position(Point_shape.getPosition());
                        }



                        try {
                            dataBase db=new dataBase(MainActivity.this,MainActivity.this);

                            Integer id=db.getMaxIdNovedad()+1;

                            if (atributos.has("id")) {
                                id= Integer.valueOf(atributos.get("id").toString());
                            }
                            atributos.put("id",String.valueOf(id));

                            puntos.add(mMap.addMarker(opts));

                            int tipo_geometria=1;
                            String wkt=analisis.puntoWKT(puntos.get(puntos.size()-1));
                            String tipo= atributos.get("tipo").toString();
                            String descripcion= atributos.get("descripcion").toString();
                            Novedades novedad=new Novedades(MainActivity.this,MainActivity.this,id,id_dispositivo,tipo_geometria,wkt,tipo,descripcion);
                            Boolean inserto=novedad.insertarNovedad();



                            mitoast.generarToast("Elemento guardado");

                            puntos.get(puntos.size()-1).setTag(atributos);

                            String imagen=atributos.get("tipo").toString();

                            AssetManager mg = getResources().getAssets();
                            InputStream is = null;
                            try {
                                is = mg.open("img/"+imagen+".png");
                                puntos.get(puntos.size()-1).setIcon(BitmapDescriptorFactory.fromAsset("img/"+imagen+".png"));
                            } catch (IOException ex) {
                                //file does not exist
                            } finally {
                                if (is != null) {
                                    try {
                                        is.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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



                        try {
                            dataBase db=new dataBase(MainActivity.this,MainActivity.this);

                            Integer id=db.getMaxIdNovedad()+1;
                            if (atributos.has("id")) {
                                id= Integer.valueOf(atributos.get("id").toString());
                            }
                            atributos.put("id",String.valueOf(id));
                            line.add(mMap.addPolyline(opts.width(5)));
                            line.get(line.size()-1).setClickable(true);

                            int tipo_geometria=2;

                            String wkt=analisis.LineaWKT(line.get(line.size()-1));
                            String tipo= atributos.get("tipo").toString();
                            String descripcion= atributos.get("descripcion").toString();
                            Novedades novedad=new Novedades(MainActivity.this,MainActivity.this,id,id_dispositivo,tipo_geometria,wkt,tipo,descripcion);
                            Boolean inserto=novedad.insertarNovedad();
                            mitoast.generarToast("Elemento guardado");


                            line.get(line.size()-1).setTag(atributos);
                            line.get(line.size()-1).setColor(Color.parseColor(atributos.get("color").toString()));
                            line.get(line.size()-1).setWidth(15);

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


                        String wkt=analisis.PoligonoWKT(Polygon_shape);
                        Boolean valido=analisis.PolygonValid(wkt);

                        if(valido){

                            List<LatLng> points = new ArrayList<>();
                            PolygonOptions opts=new PolygonOptions();

                            if (Polygon_shape != null) {
                                points=Polygon_shape.getPoints();
                                for (LatLng location : points) {
                                    opts.add(location);
                                }
                            }



                            try {
                                dataBase db=new dataBase(MainActivity.this,MainActivity.this);

                                Integer id=db.getMaxIdNovedad()+1;
                                if (atributos.has("id")) {
                                    id= Integer.valueOf(atributos.get("id").toString());
                                }
                                atributos.put("id",String.valueOf(id));
                                polygon.add(mMap.addPolygon(opts));
                                polygon.get(polygon.size()-1).setClickable(true);

                                int tipo_geometria=3;

                                String tipo= atributos.get("tipo").toString();
                                String descripcion= atributos.get("descripcion").toString();
                                Novedades novedad=new Novedades(MainActivity.this,MainActivity.this,id,id_dispositivo,tipo_geometria,wkt,tipo,descripcion);
                                Boolean inserto=novedad.insertarNovedad();
                                mitoast.generarToast("Elemento guardado");


                                polygon.get(polygon.size()-1).setTag(atributos);
                                polygon.get(polygon.size()-1).setFillColor(Color.parseColor(atributos.get("color").toString()));

                                borrar_poligono_seleccionado();

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

                        }else{
                            mitoast.generarToast("Poligono no valido");
                        }



                        Polygon_shape.remove();
                        Polygon_shape=null;


                    }

                    GeometriaUpdate=false;

                }else if(tipo_edicion_guardar==2){
                    cortar_geometria();
                }else if(tipo_edicion_guardar==3){
                    unir_geometria();
                }

                estado_mapa_sin_edicion();
                control_dibujo=1;

            }
        });


        FloatingActionButton discard_save_editor = (FloatingActionButton) findViewById(R.id.discard_save_editor);
        hide_discard_save_edicion();

        discard_save_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                estado_mapa_sin_edicion();
                hide_add_punto();
                hide_delete_geom();
                hide_undo_geom();
                drop_markers_intermediate();
                hide_edit_atributos();
                hide_discard_save_edicion();
                hide_save_edicion();
                hide_msg_area();
                hide_msg_distancia();
                drop_markers_edicion();

                if(tipo_edicion==1){
                    if(Point_shape!=null){
                        Point_shape.remove();
                        Point_shape=null;
                    }

                }

                if(tipo_edicion==2){
                    if(Polyline_shape!=null){
                        Polyline_shape.remove();
                        Polyline_shape=null;
                    }

                }
                if(tipo_edicion==3){
                    if(Polygon_shape!=null){
                        Polygon_shape.remove();
                        Polygon_shape=null;
                    }
                }

                if(tipo_edicion_guardar==3){
                    cod_poligonos_union.clear();
                    cod_poligonos_union=new ArrayList<>();

                    for(Polygon pol:polygon_union){
                        pol.remove();
                    }

                    polygon_union.clear();

                    borrar_poligono_seleccionado();
                    JoinPicker=true;
                }

                mitoast.generarToast("Edición terminada");
                estado_mapa_sin_edicion();
                medicion=false;
                control_dibujo=1;


            }
        });



        final com.getbase.floatingactionbutton.FloatingActionButton delete_geom = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.delete_geom);

        delete_geom.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
                dialogo.confirmacionBorrado();

            }
        });

        FloatingActionButton undo_edit = (FloatingActionButton) findViewById(R.id.undo_edit);

        undo_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tipo_edicion==2){

                    if((markers.size()-1)>1 && !medicion){
                       show_save_edicion();
                    }else{
                        hide_save_edicion();
                    }

                    if((markers.size()-1)>0){
                        markers.get(markers.size()-1).remove();
                        markers.remove(markers.size()-1);

                        List<LatLng> puntos=Polyline_shape.getPoints();
                        puntos.remove(puntos.size()-1);
                        Polyline_shape.setPoints(puntos);
                        distancia(Polyline_shape.getPoints());
                    }else{
                        markers.get(markers.size()-1).remove();
                        markers.remove(markers.size()-1);
                        Polyline_shape.remove();
                        Polyline_shape=null;
                        hide_undo_geom();
                        control_dibujo=1;
                    }



                }
                if(tipo_edicion==3){

                    if((markers.size()-1)>2 && !medicion){
                        show_save_edicion();
                    }else{
                        hide_save_edicion();
                    }


                    if((markers.size()-1)>0){
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
                    Polygon_shape.setFillColor(getResources().getColor(R.color.poligono_edicion));
                    area(Polygon_shape);
                    }else{
                        hide_undo_geom();
                        markers.get(markers.size()-1).remove();
                        markers.remove(markers.size()-1);

                        Polygon_shape.remove();
                        Polygon_shape=null;
                        control_dibujo=1;
                    }


                }

            }
        });




       com.getbase.floatingactionbutton.FloatingActionButton edit_atributos = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.edit_atributos);


        edit_atributos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (atributos.has("atributos")) {

                    try {
                        if(atributos.get("atributos").toString().equals("obra")){
                            tipo_edicion=4;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                DialogoEdicion dialog =new DialogoEdicion(MainActivity.this,MainActivity.this,tipo_edicion,true,atributos,codId);
                dialog.mostrarDialogoEdicion();
                hide_menu_grupo_edicion();

            }
        });

        final com.getbase.floatingactionbutton.FloatingActionButton cut_geom = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.cut_geom);

        cut_geom.setOnClickListener(new View.OnClickListener() {
            int num_clic=1;
            @Override
            public void onClick(View v) {

                show_save_edicion();
                show_discard_save_edicion();
                hide_menu_grupo_edicion();
                tipo_edicion_guardar=2;

                show_add_punto();
                dibujo_linea();

                mitoast.generarToastMapa("Dibuje una polilinea");

            }
        });


        final com.getbase.floatingactionbutton.FloatingActionButton quit_code_ena = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.quit_code_ena);

        quit_code_ena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dataBase db=new dataBase(MainActivity.this,MainActivity.this);
                String descripcion="";

                try {

                    String code=atributos.get("descripcion").toString();
                    int id= Integer.parseInt(atributos.get("id").toString());

                    descripcion= code.substring(0,9);
                    Log.d("descripcion",descripcion);


                    for(int i=0;i<polygon.size();i++){

                        if(polygon.get(i).getId().equals(codId)){

                            Novedades novedad=new Novedades(MainActivity.this,MainActivity.this,id,"ENA",descripcion);
                            novedad.updateNovedadAtributos();

                            String identificador=polygon.get(i).getId();
                            Util util=new Util(MainActivity.this,MainActivity.this);
                            util.borrarLabel(identificador);

                            int color =  getResources().getColor(R.color.poligono_abierto_ena);

                            polygon.get(i).setFillColor(color);


                            JSONObject atr=new JSONObject();
                            try{
                                atr.put("id", String.valueOf(id));
                                atr.put("tipo","ENA");
                                atr.put("descripcion",descripcion);

                            }catch (JSONException e) {

                            }
                            polygon.get(i).setTag(atr);

                            mitoast.generarToast("Geometria Habilitada");
                            break;

                        }
                    }
                    hide_menu_grupo_edicion();
                    borrar_poligono_seleccionado();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


        final com.getbase.floatingactionbutton.FloatingActionButton add_code_ena = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.add_code_ena);

        add_code_ena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataBase db=new dataBase(MainActivity.this,MainActivity.this);
                String descripcion="";

                try {

                    String code=atributos.get("descripcion").toString();
                    int id= Integer.parseInt(atributos.get("id").toString());

                    descripcion= db.getsiguienterecorte(code);


                    for(int i=0;i<polygon.size();i++){

                        if(polygon.get(i).getId().equals(codId)){

                            Novedades novedad=new Novedades(MainActivity.this,MainActivity.this,id,"ENA_F",descripcion);
                            novedad.updateNovedadAtributos();

                            String identificador=polygon.get(i).getId();
                            Util util=new Util(MainActivity.this,MainActivity.this);
                            util.borrarLabel(identificador);

                            //parte del label
                            String centroide=analisis.centroidePoligono(polygon.get(i));

                            util.generarLabel(centroide,descripcion,identificador);

                            int color =  getResources().getColor(R.color.poligono_cerrado_ena);

                            polygon.get(i).setFillColor(color);


                            JSONObject atr=new JSONObject();
                            try{
                                atr.put("id", String.valueOf(id));
                                atr.put("tipo","ENA_F");
                                atr.put("descripcion",descripcion);

                            }catch (JSONException e) {

                            }
                            polygon.get(i).setTag(atr);


                            mitoast.generarToast("Código Asignado");
                            break;

                        }
                    }
                    hide_menu_grupo_edicion();
                    borrar_poligono_seleccionado();

                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });

        final com.getbase.floatingactionbutton.FloatingActionButton edit_join = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.edit_join);
        edit_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                show_discard_save_edicion();
                hide_menu_grupo_edicion();
                tipo_edicion_guardar=3;
                JoinPicker=false;

                if(polygon_sel!=null){
                    polygon_sel.remove();
                    polygon_sel=null;
                }

                mitoast.generarToast("Seleccione dos poligonos contiguos");


            }
        });

        asignar_tarea = (FloatingActionButton) findViewById(R.id.asignar_tarea);

        asignar_tarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
                dialogo.MostrarDialogoAsignacion();

                asignar_tarea.setVisibility(View.INVISIBLE);
                JoinPicker=false;
                estado_mapa_asignacion();
                mitoast.generarToast("Comience a seleccionar los poligonos");


            }
        });

        hide_add_punto();
        hide_delete_geom();
        hide_undo_geom();
        hide_cut_geom();
        hide_msg_distancia();
        hide_msg_area();
        hide_edit_atributos();
        hide_edit_join();
        hide_menu_grupo_edicion();
        inhabilitar_code();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        controlToolsGoogle();

        mMapTypeSelector = (Spinner) findViewById(R.id.map_type_selector);
        mMapTypeSelector.setOnItemSelectedListener(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView1.getMenu();
        MenuItem nav_camara = menu.findItem(R.id.nav_usuario);

        session = new Session(MainActivity.this);
        String nombre=session.getnombre();
        nav_camara.setTitle(nombre);

        int rol =session.getrol();

        if(rol==1){
            nav_camara.setIcon(R.drawable.ic_menu_supervisor);
        }else if(rol==2){
            nav_camara.setIcon(R.drawable.ic_menu_encuestador);
            asignar_tarea.setVisibility(View.INVISIBLE);
        }
        asignar_tarea.setVisibility(View.INVISIBLE);


        Switch switch1= (Switch)menu.findItem(R.id.ver_labels).getActionView().findViewById(R.id.switchForActionBar);

        switch1.setChecked(true);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    for (Marker marker : label) {
                        marker.setVisible(true);
                    }
                }else{
                    for (Marker marker : label) {
                        marker.setVisible(false);
                    }
                }
            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

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
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_layers) {

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarMapasBase();

        }else if(id == R.id.action_search){

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoBusqueda();

        }
        if (id == R.id.action_novedad) {

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoNovedad();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.ver_labels){



        }

        else if(id==R.id.mapas_offline){

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarMapasOffline();

        }
        else if(id==R.id.buscar_ceed){

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoBusquedaCeed();

        }
        else if(id==R.id.zoom_obras){
            int i=0;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : puntos) {

                if(marker.getTag()!=null){
                    update_atributos(marker.getTag().toString());

                    try {

                        if(atributos.get("tipo").toString().equals("obra")){
                            builder.include(marker.getPosition());
                            i=1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



            }
            if(i==1){
                LatLngBounds bounds = builder.build();
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }else{
                mitoast.generarToast("No hay obras, intenta sincronizar");
            }


        }
        else if(id==R.id.zoom_ena){
            int i=0;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Polygon marker : polygon) {

                update_atributos(marker.getTag().toString());


                try {
                    if(atributos.get("tipo").toString().startsWith("ENA")){
                        builder.include(marker.getPoints().get(0));
                        i=1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if(i==1){
                LatLngBounds bounds = builder.build();
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }else{
                mitoast.generarToast("No hay Conglomerados, intenta sincronizar");
            }


        }
        else if (id == R.id.medir_distancia) {
            medicion=true;
            estado_mapa_edicion();

            dibujo_linea();
            show_add_punto();
            show_discard_save_edicion();

            drop_markers_edicion();
            borrar_poligono_seleccionado();
            hide_cut_geom();
            hide_delete_geom();
            drop_markers_intermediate();
            hide_msg_area();
            hide_edit_atributos();
            hide_edit_join();
            hide_menu_grupo_edicion();


        }else if (id == R.id.medir_area) {
            medicion=true;
            estado_mapa_edicion();
            dibujo_poligono();
            show_add_punto();
            show_discard_save_edicion();

            drop_markers_edicion();
            borrar_poligono_seleccionado();
            hide_cut_geom();
            hide_delete_geom();
            drop_markers_intermediate();
            hide_edit_atributos();
            hide_edit_join();
            hide_menu_grupo_edicion();


        }else if(id== R.id.sincronizar){

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoSincronizar();

        }
        else if (id == R.id.habilitar_giroscopio) {

            if(sensor_activado==0){

                mSensorManager.unregisterListener(this, mAccelerometer);
                sensor_activado=1;
            }
            if(sensor_activado==1){

                mAccelerometer =  mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
                int delay = 100000; //in microseconds equivalent to 0.1 sec
                mSensorManager.registerListener(this,
                        mAccelerometer,
                        delay
                );


                sensor_activado=0;
            }


        }else if (id == R.id.captura) {

            snapShot captura=new snapShot(MainActivity.this);
            captura.capturaPantalla();

        } else if (id == R.id.nav_buscar_coordenadas) {

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoCoordenadas();

        } else if (id == R.id.nav_acerca) {

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoAcerca();

        } else if (id == R.id.nav_ayuda) {

            /*
            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.MostrarDialogoAyuda();
*/
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }else if(id == R.id.nav_salir){

            DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
            dialogo.salirApp();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


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
        if(dir.exists()){
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
        }

        estado_mapa_sin_edicion();

        dataBase db=new dataBase(MainActivity.this,MainActivity.this);
        db.getGeomFromDatabase();

        db.getNovedades();
        db.getObrasCeed();

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);


        if ( !service.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {

        }else{
            Criteria criteria = new Criteria();
            String provider = service.getBestProvider(criteria, true);
            Location location = service.getLastKnownLocation(provider);
            if(location!= null){
                LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));
            }

        }


    }

    public void estado_mapa_asignacion(){
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                PolygonOptions opts=new PolygonOptions();
                opts.addAll(polygon.getPoints());

                if(!JoinPicker){
                    Polygon p=mMap.addPolygon(opts);
                    p.setStrokeColor(getResources().getColor(R.color.poligono_touch_boundary));
                    p.setZIndex(100);
                    cod_poligonos_union.add(polygon.getId());
                    polygon_union.add(p);


                }
            }
        });

    }

    public void estado_mapa_edicion(){

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                borrar_poligono_seleccionado();
                hide_cut_geom();
                hide_delete_geom();
                hide_edit_atributos();
                hide_edit_join();
                hide_menu_grupo_edicion();

            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {

            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

    }
    public void estado_mapa_sin_edicion(){

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                drop_markers_edicion();
                borrar_poligono_seleccionado();
                hide_cut_geom();
                hide_delete_geom();
                drop_markers_intermediate();
                hide_msg_distancia();
                hide_msg_area();
                hide_edit_atributos();
                hide_edit_join();
                hide_menu_grupo_edicion();

            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {



                drop_markers_edicion();
                drop_markers_intermediate();
                show_menu_grupo_edicion();

                borrar_poligono_seleccionado();

                PolygonOptions opts=new PolygonOptions();
                opts.addAll(polygon.getPoints());

                polygon_sel= mMap.addPolygon(opts);
                polygon_sel.setZIndex(50);
                polygon_sel.setStrokeColor(getResources().getColor(R.color.poligono_touch_boundary));




                tipo_edicion=3;

                codId=polygon.getId();

                area(polygon);

                show_edit_atributos();


                update_atributos(polygon.getTag().toString());

                try {
                    Log.d("Atributos:",atributos.get("tipo").toString());

                    if(!atributos.get("tipo").toString().equals("ENA_CONGLOMERADO") && !atributos.get("tipo").toString().equals("ENA")&& !atributos.get("tipo").toString().equals("ENA_F")){
                        //para edicion normal//
                        edicion(polygon.getPoints());
                        show_delete_geom();

                        //para edicion con corte de la ENA
                        show_cut_geom();
                        show_edit_join();

                    }else{

                        if(atributos.get("tipo").toString().equals("ENA_F")){
                            show_habilitar_code();

                        }else{
                            hide_habilitar_code();
                            show_cut_geom();
                            show_edit_join();
                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                drag_punto_edicion(3);

                //estamos en modo de toque multiple de poligonos
                if(!JoinPicker){
                    Polygon p=mMap.addPolygon(opts);
                    p.setZIndex(100);
                    p.setStrokeColor(getResources().getColor(R.color.poligono_touch_boundary));
                    cod_poligonos_union.add(polygon.getId());
                    polygon_union.add(p);
                    hide_menu_grupo_edicion();

                    if(polygon_union.size()==1){
                        mitoast.generarToast("Seleccione un poligono vecino");
                    }else{
                        show_save_edicion();
                    }

                }






            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener(){

            @Override
            public void onPolylineClick(Polyline polyline) {

                borrar_poligono_seleccionado();
                hide_cut_geom();
                hide_edit_join();
                show_menu_grupo_edicion();
                tipo_edicion=2;
                edicion(polyline.getPoints());
                codId=polyline.getId();
                show_delete_geom();
                distancia(polyline.getPoints());
                show_edit_atributos();
                hide_msg_area();
                update_atributos(polyline.getTag().toString());

                drag_punto_edicion(2);
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                if (marker.getTag() != null) {
                    if (marker.getTag().equals("edicion") || String.valueOf(marker.getTag()).endsWith("label")) { //if a marker has desired tag



                    }else{


                        update_atributos(marker.getTag().toString());


                        try {
                            if(atributos.get("tipo").toString().equals("obra")){

                                DialogoOtros dialogo=new DialogoOtros(MainActivity.this,MainActivity.this);
                                String serial=atributos.get("serial").toString();
                                String  nombreobra=atributos.get("nombreobra").toString();
                                String  direccion=atributos.get("direccion").toString();
                                String  barrio=atributos.get("barrio").toString();

                                dialogo.MostrarDialogoObras(serial,nombreobra,direccion,barrio);


                            }else{
                                borrar_poligono_seleccionado();
                                hide_cut_geom();
                                hide_edit_join();
                                show_menu_grupo_edicion();
                                hide_msg_area();
                                hide_msg_distancia();
                                tipo_edicion=1;
                                edicion_puntos(marker.getPosition());
                                codId=marker.getId();
                                show_delete_geom();
                                drop_markers_intermediate();
                                show_edit_atributos();

                                update_atributos(marker.getTag().toString());

                                drag_punto_edicion(1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }


                return false;
            }
        });



    }

    public void borrar_poligono_seleccionado(){

        if(polygon_sel!=null){
            polygon_sel.remove();
        }

    }

    public void update_atributos(String datos){

        try {

            atributos=new JSONObject(datos);
            Log.d("info:",datos);

        } catch (Throwable t) {

        }
    }

    public void drag_punto_edicion(final int tipo_edicion){



        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker arg0) {
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {

                dragMarker(arg0,tipo_edicion);
                estado_mapa_edicion();
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
            }
        });

    }



    public void dibujo_punto(){

        estado_mapa_edicion();
        show_discard_save_edicion();
        FloatingActionButton add_punto = (FloatingActionButton) findViewById(R.id.add_punto);

        add_punto.setOnClickListener(new View.OnClickListener() {
            int secuencia=1;
            @Override
            public void onClick(View view) {
                tipo_edicion=1;

                if(!medicion){
                    show_save_edicion();
                }


                LatLng center = mMap.getCameraPosition().target;
                Punto mipunto=new Punto(center);
                Marker marker = mMap.addMarker(new MarkerOptions().position(mipunto.getPunto()));
                Point_shape=marker;
                hide_add_punto();

            }
        });


    }

    public void dibujo_linea(){

        estado_mapa_edicion();
        show_discard_save_edicion();

        FloatingActionButton add_punto = (FloatingActionButton) findViewById(R.id.add_punto);

        add_punto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                tipo_edicion=2;

                if(control_dibujo>1){
                    show_undo_geom();
                    show_delete_geom();
                    if(!medicion){
                        show_save_edicion();
                    }
                }

                control_dibujo=control_dibujo+1;


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


                Polyline_shape=mMap.addPolyline(opts.width(5));
                Polyline_shape.setTag("edit_polygono");
                Polyline_shape.setClickable(true);



                distancia(Polyline_shape.getPoints());


                marker.setTag("edicion");

                markers.add(marker);

            }
        });






    }



    public void dibujo_poligono(){

        estado_mapa_edicion();
        show_discard_save_edicion();

        FloatingActionButton add_punto = (FloatingActionButton) findViewById(R.id.add_punto);

        add_punto.setOnClickListener(new View.OnClickListener() {

            int secuencia=1;
            @Override
            public void onClick(View view) {

                tipo_edicion=3;

                if(control_dibujo==1){
                    secuencia=1;
                }

                if(secuencia>1){
                    show_undo_geom();
                    show_delete_geom();
                }

                if(secuencia>2){
                    if(!medicion){
                        show_save_edicion();
                    }
                }

                secuencia=secuencia+1;

                LatLng center = mMap.getCameraPosition().target;

                Punto mipunto=new Punto(center);

                    Marker marker=createMarker(mipunto.getLat(), mipunto.getLng(), "pto", "");

                    List<LatLng> points = new ArrayList<>();

                    PolygonOptions opts=new PolygonOptions();

                    if (Polygon_shape != null) {
                        points=Polygon_shape.getPoints();
                        if(control_dibujo>2){
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

                    Polygon_shape.setFillColor(getResources().getColor(R.color.poligono_edicion));
                    area(Polygon_shape);




                marker.setTag("edicion");

                markers.add(marker);

                control_dibujo=control_dibujo+1;

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

        //Zoom del mapa
        float zoom=mMap.getCameraPosition().zoom;


        if(zoom>15){
            for (Marker marker : label) {
                marker.setVisible(true);
            }
        }else{
            for (Marker marker : label) {
                marker.setVisible(false);
            }
        }

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


        drop_markers_edicion();

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
    public void drop_markers_edicion(){
        for (Marker marker : markers) {
            if (marker.getTag().equals("edicion")) { //if a marker has desired tag
                marker.remove();
            }
        }
        markers.clear();
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
        tipo_edicion_guardar=1;
        show_save_edicion();
        show_discard_save_edicion();
        hide_menu_grupo_edicion();
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

        distancia_campo.setText("Longitud: "+String.format("%.2f",distancia)+" m");
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

    public void borrar_novedad(int id,int tipo_geometria){
        Novedades novedad=new Novedades(MainActivity.this,MainActivity.this,id,null,tipo_geometria,null,null,null);
        Boolean paso=novedad.eliminarNovedad();
        mitoast.generarToast("Elemento borrado");
    }

    public void borrar_geometria(){

        hide_delete_geom();
        hide_undo_geom();
        hide_save_edicion();
        hide_discard_save_edicion();
        drop_markers_intermediate();
        drop_markers_edicion();

        if(tipo_edicion==1){
            for(int i=0;i<puntos.size();i++){

                if(puntos.get(i).getId().equals(codId)){
                    try {
                        int id= Integer.parseInt(atributos.get("id").toString());

                        borrar_novedad(id,1);

                        puntos.get(i).remove();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        if(tipo_edicion==2){
            for(int i=0;i<line.size();i++){

                if(line.get(i).getId().equals(codId)){

                    try {
                        int id= Integer.parseInt(atributos.get("id").toString());

                        borrar_novedad(id,2);

                        line.get(i).remove();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


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

                    try {
                        int id= Integer.parseInt(atributos.get("id").toString());

                        borrar_novedad(id,3);

                        polygon.get(i).remove();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            borrar_poligono_seleccionado();

            if(Polygon_shape!=null){
                Polygon_shape.remove();
            }

            Polygon_shape=null;
        }
    }

public void cortar_geometria(){


        String poligono=analisis.PoligonoWKT(polygon_sel);

        String linea=analisis.LineaWKT(Polyline_shape);

        Boolean lineaSimple=analisis.PolylineItsSimple(linea);

        if(lineaSimple){

            String resultado=analisis.CutPolygonByLine(poligono,linea);
            if(!resultado.equals("fallo")){

                int id= 0;
                try {

                    id = Integer.parseInt(atributos.get("id").toString());
                    borrar_poligono(id);



                } catch (JSONException e) {
                    e.printStackTrace();
                }


                ArrayList<Integer> values = new ArrayList<>();


                try {
                    String code=atributos.get("descripcion").toString();

                    for(int i=0;i<polygon.size();i++){

                        JSONObject atr=new JSONObject();

                        atr=new JSONObject(polygon.get(i).getTag().toString());

                        String des=atr.get("descripcion").toString();

                        if(des.startsWith(code)){

                            values.add(i);

                        }


                    }

                    Log.d("id_ena:",code);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("valores", String.valueOf(values));

                String adyacencia=analisis.CutPointPolygon(linea,values);


                Boolean recorte=analisis.MultipolygonToPolygon(resultado);



            }else{
                mitoast.generarToast("No se puede realizar el corte");
            }

        }else{
            mitoast.generarToast("Polilinea no valida");
        }




        drop_markers_edicion();
        borrar_poligono_seleccionado();
        markers.clear();
        Polyline_shape.remove();
        Polyline_shape=null;
        hide_cut_geom();
        hide_undo_geom();
        hide_add_punto();



}

public void unir_geometria(){


        List<String> geometrias=new ArrayList<>();

        for (Polygon pol : polygon_union) {

            String poligono=analisis.PoligonoWKT(pol);
            geometrias.add(poligono);

        }

        String descripcion="";

        for(String id: cod_poligonos_union){

            for(int i=0;i<polygon.size();i++){



                if((polygon.get(i).getId()).equals(id)){


                    update_atributos(polygon.get(i).getTag().toString());

                    int id_p= 0;
                    try {
                        codId=polygon.get(i).getId();
                        id_p = Integer.parseInt(atributos.get("id").toString());
                        descripcion = atributos.get("descripcion").toString();
                        borrar_poligono(id_p);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            }

        }

        cod_poligonos_union.clear();
        cod_poligonos_union=new ArrayList<>();

        for(Polygon pol:polygon_union){
            pol.remove();
        }

        polygon_union.clear();

        borrar_poligono_seleccionado();

        analisis.unionPolygons(geometrias,descripcion);

        JoinPicker=true;



}


public void borrar_poligono(int id){

    for(int i=0;i<polygon.size();i++){

        if(polygon.get(i).getId().equals(codId)){

                Novedades novedad=new Novedades(MainActivity.this,MainActivity.this,id,null,3,null,null,null);

                Boolean paso=novedad.eliminarNovedad();
                Boolean cambio_estado=novedad.cambioEstadoGeometria();

                String identificador=polygon.get(i).getId();
                Util util=new Util(MainActivity.this,MainActivity.this);
                util.borrarLabel(identificador);

                polygon.get(i).remove();
                polygon.remove(i);

                mitoast.generarToast("Elemento borrado");
                break;

        }
    }

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
    final com.getbase.floatingactionbutton.FloatingActionButton delete_geom = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.delete_geom);
    delete_geom.setVisibility(View.GONE);
    hide_menu_grupo_edicion();
}

public void show_delete_geom(){
    final com.getbase.floatingactionbutton.FloatingActionButton delete_geom = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.delete_geom);
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
    final com.getbase.floatingactionbutton.FloatingActionButton cut_geom = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.cut_geom);
    cut_geom.setVisibility(View.GONE);
    hide_menu_grupo_edicion();
}

public void show_cut_geom(){
    final com.getbase.floatingactionbutton.FloatingActionButton cut_geom = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.cut_geom);
    cut_geom.setVisibility(View.VISIBLE);
}


public void show_habilitar_code(){
    final com.getbase.floatingactionbutton.FloatingActionButton quit_code_ena = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.quit_code_ena);
    quit_code_ena.setVisibility(View.VISIBLE);

    final com.getbase.floatingactionbutton.FloatingActionButton add_code_ena = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.add_code_ena);
    add_code_ena.setVisibility(View.GONE);
}

public void hide_habilitar_code(){
    final com.getbase.floatingactionbutton.FloatingActionButton quit_code_ena = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.quit_code_ena);
    quit_code_ena.setVisibility(View.GONE);

    final com.getbase.floatingactionbutton.FloatingActionButton add_code_ena = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.add_code_ena);
    add_code_ena.setVisibility(View.VISIBLE);
}

public void inhabilitar_code(){
    final com.getbase.floatingactionbutton.FloatingActionButton quit_code_ena = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.quit_code_ena);
    quit_code_ena.setVisibility(View.GONE);

    final com.getbase.floatingactionbutton.FloatingActionButton add_code_ena = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.add_code_ena);
    add_code_ena.setVisibility(View.GONE);
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

public void show_edit_atributos(){
    com.getbase.floatingactionbutton.FloatingActionButton edit_atributos = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.edit_atributos);
    edit_atributos.setVisibility(View.VISIBLE);
}

public void hide_edit_atributos(){
    com.getbase.floatingactionbutton.FloatingActionButton edit_atributos = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.edit_atributos);
    edit_atributos.setVisibility(View.GONE);
}

public void show_edit_join(){
    final com.getbase.floatingactionbutton.FloatingActionButton edit_join = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.edit_join);
    edit_join.setVisibility(View.VISIBLE);

}
public void hide_edit_join(){
    final com.getbase.floatingactionbutton.FloatingActionButton edit_join = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.edit_join);
    edit_join.setVisibility(View.GONE);
    hide_menu_grupo_edicion();

}

public void show_menu_grupo_edicion(){
    final com.getbase.floatingactionbutton.FloatingActionsMenu menu_editor = (com.getbase.floatingactionbutton.FloatingActionsMenu) findViewById(R.id.menu_editor);
    menu_editor.setVisibility(View.VISIBLE);
    menu_editor.expand();
}

public void hide_menu_grupo_edicion(){
    final com.getbase.floatingactionbutton.FloatingActionsMenu menu_editor = (com.getbase.floatingactionbutton.FloatingActionsMenu) findViewById(R.id.menu_editor);
    menu_editor.collapse();
    menu_editor.setVisibility(View.GONE);
}

public void controlToolsGoogle(){


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


}





}