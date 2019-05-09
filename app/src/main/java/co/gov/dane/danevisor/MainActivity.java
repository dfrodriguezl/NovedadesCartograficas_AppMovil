package co.gov.dane.danevisor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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


import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GoogleMap.OnMyLocationClickListener,AdapterView.OnItemSelectedListener,
        GoogleMap.OnCameraMoveListener, SensorEventListener {

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Spinner mMapTypeSelector;

    private int mMapTypes[] = {
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE
    };
    private Polyline line;
    private Polygon poli;
    int sensor_activado=1;

    List<Marker> markers = new ArrayList<>();
    List<Polygon> Polygon_shape = new ArrayList<>();


    private SensorManager mSensorManager;
    private float[] mRotationMatrix = new float[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},00);


        //creación del Folder para guardar los mbtiles.
        String ruta_mbtiles=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"mbtiles";
        File folder = new File(ruta_mbtiles);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            Log.d("Carpeta:","Ya creada");






        } else {

        }




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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





        mMapTypeSelector = (Spinner) findViewById(R.id.map_type_selector);
        mMapTypeSelector.setOnItemSelectedListener(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);




        GeoJsonLayer layer;
        String PLACES_URL = "http://geoportal.dane.gov.co/geoserver/dig/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=dig%3AENA_GP_PREDIAL_05107_BRICENO%7C&maxfeatures=50&outputformat=json";
        // Instantiate the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        //Prepare the Request
        StringRequest request = new StringRequest(
                Request.Method.GET, //GET or POST
                PLACES_URL, //URL
                new Response.Listener<String>() { //Listener OK

                    @Override
                    public void onResponse(String  responsePlaces) {
                        //Response OK!! :)
                        try {

                            JSONObject obj = new JSONObject(responsePlaces);

                            GeoJsonLayer layer = new GeoJsonLayer(mMap, obj);

                            Log.i("json:", String.valueOf(obj));


                            layer.addLayerToMap();

                            GeoJsonPolygonStyle poli = new GeoJsonPolygonStyle();

                            for (GeoJsonFeature feature : layer.getFeatures()) {

                            }


                            for (GeoJsonFeature feature : layer.getFeatures()) {
                                if (feature.hasProperty("OBJECTID")) {
                                    String id = feature.getProperty("OBJECTID");
                                    Log.i("Propiedad:",id);
                                }
                                ;
                            }


                            layer.setOnFeatureClickListener(new GeoJsonLayer.OnFeatureClickListener() {
                                @SuppressLint("RestrictedApi")
                                @Override
                                public void onFeatureClick(com.google.maps.android.data.Feature feature) {

                                    for (Marker marker : markers) {
                                        if (marker.getTag().equals("hola")) { //if a marker has desired tag

                                            marker.remove();


                                        }
                                    }
                                    markers.clear();

                                    final GeoJsonPolygon pol= (GeoJsonPolygon) feature.getGeometry();

                                    TextView area_campo = (TextView)findViewById(R.id.area);

                                    double area = SphericalUtil.computeArea(pol.getOuterBoundaryCoordinates());
                                    area=area*0.0001;

                                    area_campo.setText("Área: "+String.format("%.4f",area)+" Ha");
                                    area_campo.setTextColor(Color.parseColor("#ad2055"));


                                    ArrayList<LatLng> dat= (ArrayList<LatLng>) pol.getCoordinates().get(0);

                                    for (int i=0;i<dat.size();i++){
                                        Marker mimarker=createMarker( dat.get(i).latitude,dat.get(i).longitude,"ivan","hola");

                                        markers.add(mimarker);

                                    }

                                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                        @Override
                                        public void onMarkerDragStart(Marker arg0) {
                                        }

                                        @SuppressWarnings("unchecked")
                                        @Override
                                        public void onMarkerDragEnd(Marker arg0) {
                                            Log.d("System out", "onMarkerDragEnd...");
                                            mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));

                                            ArrayList<LatLng> edit= new ArrayList<LatLng>();
                                            PolygonOptions opts=new PolygonOptions();

                                            for (Polygon poli : Polygon_shape) {
                                                if (poli.getTag().equals("editado")) { //if a marker has desired tag

                                                    poli.remove();

                                                }
                                            }
                                            Polygon_shape.clear();

                                            for (Marker marker : markers) {
                                                if (marker.getTag().equals("hola")) { //if a marker has desired tag

                                                    edit.add(marker.getPosition());

                                                }
                                            }
                                            for (LatLng location : edit) {
                                                opts.add(location);
                                            }

                                            Polygon polygon = mMap.addPolygon(opts.strokeColor(Color.RED).fillColor(Color.parseColor("#8059A4FC")));
                                            polygon.setTag("editado");
                                            Polygon_shape.add(polygon);

                                            TextView area_campo = (TextView)findViewById(R.id.area);

                                            double area = SphericalUtil.computeArea(polygon.getPoints());
                                            area=area*0.0001;

                                            area_campo.setText("Área: "+String.format("%.4f",area)+" Ha");
                                            area_campo.setTextColor(Color.parseColor("#2F75C9"));

                                        }

                                        @Override
                                        public void onMarkerDrag(Marker arg0) {
                                        }
                                    });




                                    FloatingActionButton editarFeature = (FloatingActionButton) findViewById(R.id.editarFeature);

                                    editarFeature.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            for (Marker marker : markers) {
                                                if (marker.getTag().equals("hola")) { //if a marker has desired tag

                                                    marker.remove();


                                                }
                                            }
                                            markers.clear();
                                        }
                                    });
                                    editarFeature.setVisibility(View.VISIBLE);




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

                                    mPerth.setTag("hola");
                                    mPerth.setDraggable(true);

                                    return mPerth;
                                }


                            });




                            JSONArray names = obj.names();
                            JSONArray values = obj.getJSONArray("features");
                            for(int i=0; i<values.length(); i++){

                                JSONObject obj1  = values.getJSONObject(i);
                                String code = obj1.getString("geometry");
                                Log.i("MyClass", code );

                            }

                        } catch (Throwable t) {
                            Log.e("error", "Could not parse malformed JSON: \"" + responsePlaces + "\"");
                        }
                    }
                }, new Response.ErrorListener() { //Listener ERROR

            @Override
            public void onErrorResponse(VolleyError error) {
                //There was an error :(
                Log.i("bye",error.toString());
            }
        });





        //Send the request to the requestQueue
        requestQueue.add(request);


        //codigo atributos
        FloatingActionButton editarPunto = (FloatingActionButton) findViewById(R.id.editarPunto);

        editarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder mBuilder =new AlertDialog.Builder(MainActivity.this);
                final View mView =getLayoutInflater().inflate(R.layout.dialog_atributos,null);
                mBuilder.setView(mView);
                final AlertDialog dialog =mBuilder.create();

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

                wmlp.gravity = Gravity.TOP | Gravity.CENTER;
                wmlp.y = 200;   //y position

                wmlp.width=mView.getWidth();
                dialog.getWindow().setDimAmount(0);
                dialog.show();

                Spinner spinner = (Spinner) mView.findViewById(R.id.spinner_novedad);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mView.getContext(),
                R.array.novedad_punto, android.R.layout.simple_spinner_item);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(adapter);

                Button btn_guardar_atributos= (Button) mView.findViewById(R.id.btn_dialog_guardar_atributos);

                btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        dibujo_punto();


                    }
                });

            }
        });



        FloatingActionButton editarLinea = (FloatingActionButton) findViewById(R.id.editarLinea);

        editarLinea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder mBuilder =new AlertDialog.Builder(MainActivity.this);
                final View mView =getLayoutInflater().inflate(R.layout.dialog_atributos,null);
                mBuilder.setView(mView);
                final AlertDialog dialog =mBuilder.create();

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

                wmlp.gravity = Gravity.TOP | Gravity.CENTER;
                wmlp.y = 200;   //y position

                wmlp.width=mView.getWidth();
                dialog.getWindow().setDimAmount(0);
                dialog.show();

                Spinner spinner = (Spinner) mView.findViewById(R.id.spinner_novedad);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mView.getContext(),
                        R.array.novedad_linea, android.R.layout.simple_spinner_item);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(adapter);

                Button btn_guardar_atributos= (Button) mView.findViewById(R.id.btn_dialog_guardar_atributos);

                btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        dibujo_linea();


                    }
                });

            }
        });


        FloatingActionButton editarPolygono = (FloatingActionButton) findViewById(R.id.editarPolygono);

        editarPolygono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder mBuilder =new AlertDialog.Builder(MainActivity.this);
                final View mView =getLayoutInflater().inflate(R.layout.dialog_atributos,null);
                mBuilder.setView(mView);
                final AlertDialog dialog =mBuilder.create();

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

                wmlp.gravity = Gravity.TOP | Gravity.CENTER;
                wmlp.y = 200;   //y position

                wmlp.width=mView.getWidth();
                dialog.getWindow().setDimAmount(0);
                dialog.show();

                Spinner spinner = (Spinner) mView.findViewById(R.id.spinner_novedad);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mView.getContext(),
                        R.array.novedad_poligono, android.R.layout.simple_spinner_item);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(adapter);

                Button btn_guardar_atributos= (Button) mView.findViewById(R.id.btn_dialog_guardar_atributos);

                btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        dibujo_poligono();


                    }
                });

            }
        });










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


        if (id == R.id.dibujo_linea) {
            dibujo_linea();
        }else if (id == R.id.dibujo_poligono) {
            dibujo_poligono();
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
            snapShot();

        } else if (id == R.id.nav_buscar_coordenadas) {

            AlertDialog.Builder mBuilder =new AlertDialog.Builder(MainActivity.this);
            final View mView =getLayoutInflater().inflate(R.layout.dialog_coordenadas,null);
            mBuilder.setView(mView);
            final AlertDialog dialog =mBuilder.create();


            Button btn_coordenadas= (Button) mView.findViewById(R.id.btn_dialog_coordenadas);
            final TextView latitud= (TextView) mView.findViewById(R.id.et_dialog_coordenadas_latitud);
            final TextView longitud= (TextView) mView.findViewById(R.id.et_dialog_coordenadas_longitud);

            btn_coordenadas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat = latitud.getText().toString();
                    String lon = longitud.getText().toString();

                    LatLng marker = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    mMap.addMarker(new MarkerOptions().position(marker)
                            .title("Ubicación"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

                    dialog.dismiss();
                }
            });

            /*
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            dialog.show();
            */
            dialog.show();

        } else if (id == R.id.nav_acerca) {

            AlertDialog.Builder mBuilder =new AlertDialog.Builder(MainActivity.this);
            final View mView =getLayoutInflater().inflate(R.layout.dialog_acerca,null);
            mBuilder.setView(mView);

            Button btn_acerca= (Button) mView.findViewById(R.id.btn_dialog_acerca);
            final AlertDialog dialog =mBuilder.create();
            dialog.show();

            btn_acerca.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            LinearLayout ly_dialog_acerca_web= (LinearLayout) mView.findViewById(R.id.ly_dialog_acerca_web);

            ly_dialog_acerca_web.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dane.gov.co/"));
                    startActivity(browserIntent);
                }
            });

            LinearLayout ly_dialog_acerca_contacto= (LinearLayout) mView.findViewById(R.id.ly_dialog_acerca_contacto);

            ly_dialog_acerca_contacto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse("mailto:"
                            + "sige@dane.gov.co"
                            + "?subject=" + "Aplicativo Movil Edición DANE" + "&body=" + "");
                    intent.setData(data);
                    startActivity(intent);
                }
            });


        } else if (id == R.id.nav_ayuda) {

            AlertDialog.Builder mBuilder =new AlertDialog.Builder(MainActivity.this);
            final View mView =getLayoutInflater().inflate(R.layout.dialog_ayuda,null);
            mBuilder.setView(mView);

            Button btn_ayuda= (Button) mView.findViewById(R.id.btn_dialog_ayuda);
            final AlertDialog dialog =mBuilder.create();
            dialog.show();

            btn_ayuda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(4, -72);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


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





        String ruta_mbtiles=Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"mbtiles";

        TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(ruta_mbtiles+"/capa.mbtiles"), 256, 256);
        TileOverlay tileOverlay = mMap.addTileOverlay(
                new TileOverlayOptions()
                        .tileProvider(tileProvider).zIndex(-1));


    }

    public void dibujo_punto(){

       mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(point));
            }

        });
    }

    public void dibujo_linea(){

        line= mMap.addPolyline(new PolylineOptions()
                .width(5)
                .color(Color.RED));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(point));

                List<LatLng> points;
                points=line.getPoints();
                points.add(new LatLng(point.latitude, point.longitude));
                line.setPoints(points);

                double distance = SphericalUtil.computeLength(points);

                Toast.makeText(MainActivity.this, "Distancia:\n"+ Double.toString(distance), Toast.LENGTH_LONG).show();
            }

        });
    }

    public void dibujo_poligono(){

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            int control=1;

            @Override
            public void onMapClick(LatLng point) {

                Marker marker = mMap.addMarker(new MarkerOptions().position(point));
                if(control==1){
                    poli= mMap.addPolygon(new PolygonOptions()
                            .add(new LatLng(point.latitude, point.longitude))
                            .strokeColor(Color.RED)
                            .fillColor(Color.parseColor("#D6EAF8")));
                }else{
                    List<LatLng> points =new ArrayList<LatLng>();


                    points=poli.getPoints();

                    if(control>2){
                        points.remove( points.size() - 1 );
                    }
                    points.add(new LatLng(point.latitude, point.longitude));
                    Log.v("Puntos", String.valueOf(points));
                    poli.setPoints(points);

                    double area = SphericalUtil.computeArea(points);

                    Toast.makeText(MainActivity.this, "Area:\n"+ Double.toString(area)+"m2", Toast.LENGTH_LONG).show();
                }
                control=control+1;

            }

        });
    }

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

    //Método para captura de tomas de pantalla.
    public void snapShot(){
        GoogleMap.SnapshotReadyCallback callback=new GoogleMap.SnapshotReadyCallback () {
            Bitmap bitmap;
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                bitmap=snapshot;

                try{
                    Date now = new Date();
                    android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), now+".png");
                    FileOutputStream fout=new FileOutputStream (file);
                    bitmap.compress (Bitmap.CompressFormat.PNG,90,fout);

                    final MediaPlayer mp = MediaPlayer.create(MainActivity.this,R.raw.camera_shutter);
                    mp.start();

                    Toast.makeText(MainActivity.this, "Captura de pantalla: \n"+ now+".png", Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    e.printStackTrace ();

                }


            }
        };mMap.snapshot (callback);
    }



}