package co.gov.dane.novedades;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class DialogoOtros {

    public Activity activity;


    MainActivity main;

    private int mMapTypes[] = {
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE
    };



    public DialogoOtros(MainActivity main, Activity _activity){

        this.activity = _activity;

        this.main=main;

    }

    public void MostrarDialogoCoordenadas(){

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        AlertDialog.Builder mBuilder =new AlertDialog.Builder(activity);
        final View mView =inflater.inflate(R.layout.dialog_coordenadas,null);
        mBuilder.setView(mView);
        final AlertDialog dialog =mBuilder.create();


        Button btn_coordenadas= (Button) mView.findViewById(R.id.btn_dialog_coordenadas);
        final TextView latitud= (TextView) mView.findViewById(R.id.et_dialog_coordenadas_latitud);
        final TextView longitud= (TextView) mView.findViewById(R.id.et_dialog_coordenadas_longitud);

        latitud.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED );
        longitud.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED );

        btn_coordenadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = latitud.getText().toString();
                String lon = longitud.getText().toString();

                if(lat.equals("") || lon.equals("") ){
                    main.mitoast.generarToast("Faltan datos...");
                }else{
                    LatLng marker = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

                    main.mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

                    dialog.dismiss();
                }



            }
        });

        Button btn_cerrar_coordenadas= (Button) mView.findViewById(R.id.btn_dialog_coordenadas_close);

        btn_cerrar_coordenadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }



    public void scanQR(){

        IntentIntegrator integrator = new IntentIntegrator(main);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Escanear Código QR");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();

    }




    public void MostrarDialogoAcerca(){

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        AlertDialog.Builder mBuilder =new AlertDialog.Builder(activity);
        final View mView =inflater.inflate(R.layout.dialog_acerca,null);
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
                ((Activity) main).startActivity(browserIntent);
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
                ((Activity) main).startActivity(intent);
            }
        });

        dialog.setCanceledOnTouchOutside(false);

    }


    public void MostrarDialogoAyuda(){

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );


        AlertDialog.Builder mBuilder =new AlertDialog.Builder(activity);
        final View mView =inflater.inflate(R.layout.dialog_ayuda,null);
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

        dialog.setCanceledOnTouchOutside(false);

    }

    public void MostrarDialogoObras(String noformular,String nombreobra,String direccion, String barrio){

        final LinearLayout dialogo= (LinearLayout) main.findViewById(R.id.dialogo_atributos_obra);
        dialogo.setVisibility(View.VISIBLE);

        TextView tetxo_noformular=main.findViewById(R.id.dialogo_atributos_obra_noformular);
        tetxo_noformular.setText(noformular);

        TextView tetxo_nombreobra=main.findViewById(R.id.dialogo_atributos_obra_nombreobra);
        tetxo_nombreobra.setText(nombreobra);

        TextView tetxo_direccion=main.findViewById(R.id.dialogo_atributos_obra_direccion);
        tetxo_direccion.setText(direccion);

        TextView tetxo_barrio=main.findViewById(R.id.dialogo_atributos_obra_barrio);
        tetxo_barrio.setText(barrio);

        Button btn_dialog_atributos_obra_close= (Button) main.findViewById(R.id.btn_dialog_atributos_obra_close);

        btn_dialog_atributos_obra_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.setVisibility(View.INVISIBLE);
            }
        });

    }


    public void MostrarDialogoSincronizar(){

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );


        AlertDialog.Builder mBuilder =new AlertDialog.Builder(activity);
        final View mView =inflater.inflate(R.layout.dialog_sincronizar,null);
        mBuilder.setView(mView);

        Button btn_dialog_sincronizar_close= (Button) mView.findViewById(R.id.btn_dialog_sincronizar_close);
        final AlertDialog dialog =mBuilder.create();
        dialog.show();

        /*
        Button descargar_datos_nube= (Button) mView.findViewById(R.id.descargar_datos_nube);

        final TextView mensaje=(TextView) mView.findViewById(R.id.mensaje_sincronizar);

        descargar_datos_nube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Controlador con=new Controlador(mView.getContext());
                con.getData(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        main.finish();
                        main.startActivity(main.getIntent());
                    }
                });




            }
        });
        */

        Button subir_cambios_sincronizacion= (Button) mView.findViewById(R.id.subir_cambios_sincronizacion);

        subir_cambios_sincronizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controlador con=new Controlador(mView.getContext());
                con.uploadData();
            }
        });


        btn_dialog_sincronizar_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        Button descargar_insumos= (Button) mView.findViewById(R.id.descargar_insumos);

        descargar_insumos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(main, DescargaInsumos.class);
                main.startActivity(i);
                main.finish();

            }
        });


        dialog.setCanceledOnTouchOutside(false);

    }


    public void MostrarMapasOffline(){

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        AlertDialog.Builder mBuilder =new AlertDialog.Builder(activity);
        final View mView =inflater.inflate(R.layout.dialog_mapa_offline,null);
        mBuilder.setView(mView);
        final AlertDialog dialog =mBuilder.create();

        ArrayList<MapaOffline> mapas=new ArrayList<>();

        for(int i = 0; i < main.listado_mapas_offline.size(); i++){
            mapas.add(main.listado_mapas_offline.get(i));
        }

        MapaOfflineAdapter listAdapter = new MapaOfflineAdapter(activity, mapas);

        ListView listView=(ListView)mView.findViewById(R.id.listview_mapa_offline);

        listView.setAdapter(listAdapter);

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        Button btn_cerrar= (Button) mView.findViewById(R.id.cerrar_dialog_mapa_offline);
        btn_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



    }

    public void confirmacionBorrado(){
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle("Confirmación");
        builder.setMessage("Desea Borrar la Geometria?");
        builder.setIcon(R.drawable.ic_main_warning);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                main.borrar_geometria();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    public void salirApp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle("Confirmación");
        builder.setMessage("Desea salir del aplicativo ?");
        builder.setIcon(R.drawable.ic_menu_salir);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();


                main.session.borrarSession();

                Intent mainIntent = new Intent(main,login.class);
                main.startActivity(mainIntent);
                main.finish();


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    public void MostrarMapasBase(){


        AlertDialog.Builder mBuilder =new AlertDialog.Builder(main);
        final View mView =main.getLayoutInflater().inflate(R.layout.dialog_mapa_base,null);
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
                main.mMap.setMapType(mMapTypes[0]);
                dialog.dismiss();
            }
        });
        LinearLayout mapa_base_satelite = (LinearLayout) mView.findViewById(R.id.mapa_base_satelite);

        mapa_base_satelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.mMap.setMapType(mMapTypes[1]);
                dialog.dismiss();
            }
        });
        LinearLayout mapa_base_hibrido = (LinearLayout) mView.findViewById(R.id.mapa_base_hibrido);

        mapa_base_hibrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.mMap.setMapType(mMapTypes[2]);
                dialog.dismiss();
            }
        });
        LinearLayout mapa_base_tierra = (LinearLayout) mView.findViewById(R.id.mapa_base_tierra);

        mapa_base_tierra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.mMap.setMapType(mMapTypes[3]);
                dialog.dismiss();
            }
        });
        LinearLayout mapa_base_none = (LinearLayout) mView.findViewById(R.id.mapa_base_none);

        mapa_base_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.mMap.setMapType(mMapTypes[4]);
                dialog.dismiss();
            }
        });



    }


    public void busqueda(EditText busqueda){

        String text=busqueda.getText().toString().trim();
        int encontro=0;
        for(int i=0;i<main.polygon.size();i++){

            try {

                main.atributos=new JSONObject(main.polygon.get(i).getTag().toString());
                if(main.atributos.get("id").toString().equals(text)){
                    encontro=1;
                    String centroide=main.analisis.centroidePoligono(main.polygon.get(i));
                    WKTReader wkt=new WKTReader();
                    Coordinate[] coord1= new Coordinate[0];
                    try {
                        coord1 = wkt.read(centroide).getCoordinates();
                        LatLng punto = null;
                        for(int j=0;j<coord1.length;j++){
                            Double lat=coord1[j].y;
                            Double lon=coord1[j].x;
                            punto=new LatLng(lat,lon);

                        }

                        main.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 18));

                        InputMethodManager in = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(busqueda.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }





                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(encontro==0){
            main.mitoast.generarToast("Sin resultados");
        }

    }

    public void MostrarDialogoConteo (String manzana){

        DialogoEdicion dialogEditor =new DialogoEdicion(main,main,1);
        dialogEditor.DialogoEdicionConteo(false, manzana);




    }

    public void MostrarDialogoNovedad(){

        AlertDialog.Builder mBuilder =new AlertDialog.Builder(main);
        final View mView =main.getLayoutInflater().inflate(R.layout.dialog_add_novedad,null);
        mBuilder.setView(mView);
        final AlertDialog dialog =mBuilder.create();

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        wmlp.y = 200;   //y position

        wmlp.width=mView.getWidth();
        dialog.getWindow().setDimAmount(0);
        dialog.show();

        LinearLayout novedad_punto = (LinearLayout) mView.findViewById(R.id.novedad_punto);

        novedad_punto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoEdicion dialogEditor =new DialogoEdicion(main,main,1);
                dialogEditor.mostrarDialogoEdicionPL(false);
                dialog.hide();
            }
        });
        LinearLayout novedad_linea = (LinearLayout) mView.findViewById(R.id.novedad_linea);

        novedad_linea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoEdicion dialogEditor =new DialogoEdicion(main,main,2);
                dialogEditor.mostrarDialogoEdicionPL(false);
                dialog.hide();
            }
        });
        LinearLayout novedad_poligono = (LinearLayout) mView.findViewById(R.id.novedad_poligono);

        novedad_poligono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoEdicion dialogEditor =new DialogoEdicion(main,main,3);
                dialogEditor.mostrarDialogoEdicion();
                dialog.hide();
            }
        });

        /*
        LinearLayout adicionar_obra = (LinearLayout) mView.findViewById(R.id.adicionar_obra);

        adicionar_obra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoEdicion dialogEditor =new DialogoEdicion(main,main,4);
                dialogEditor.mostrarDialogoEdicion();
                dialog.hide();
            }
        });

        LinearLayout adicionar_ruta_ceed = (LinearLayout) mView.findViewById(R.id.adicionar_ruta_ceed);

        adicionar_ruta_ceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoEdicion dialogEditor =new DialogoEdicion(main,main,5);
                dialogEditor.mostrarDialogoEdicion();
                dialog.hide();
            }
        });

        */

    }


    public void MostrarDialogoBusquedaCeed(){

        AlertDialog.Builder mBuilder =new AlertDialog.Builder(main);
        final View mView =main.getLayoutInflater().inflate(R.layout.dialog_busqueda_ceed,null);
        mBuilder.setView(mView);
        final AlertDialog dialog =mBuilder.create();

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        wmlp.y = 200;   //y position

        wmlp.width=mView.getWidth();
        dialog.getWindow().setDimAmount(0);
        dialog.show();

        Button btn_dialog_cerrar_dialog_ceed =mView.findViewById(R.id.btn_dialog_cerrar_dialog_ceed);


        btn_dialog_cerrar_dialog_ceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        String ruta_db;
        if(Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT){
            ruta_db= Environment.getExternalStorageDirectory() + File.separator + "Editor Dane"+ File.separator+"db"+File.separator+"ceed.db";
        }else{
            ruta_db= Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Dane"+ File.separator+"db"+File.separator+"ceed.db";
        }

        final CeedDB db =new CeedDB(main,ruta_db);


        Spinner spinner_ceed_dpto = (Spinner) mView.findViewById(R.id.spinner_ceed_dpto);

        List<String> listado_depto = db.getDpto();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
        adapter.addAll(listado_depto);
        spinner_ceed_dpto.setAdapter(adapter);


        spinner_ceed_dpto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                final String id_depto=parentView.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                Spinner spinner_ceed_mpio = (Spinner) mView.findViewById(R.id.spinner_ceed_mpio);
                List<String> listado_mpio = db.getMpio(id_depto);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                adapter.addAll(listado_mpio);
                spinner_ceed_mpio.setAdapter(adapter);

                spinner_ceed_mpio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        final String id_mpio=parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                        Spinner spinner_ceed_clase = (Spinner) mView.findViewById(R.id.spinner_ceed_clase);
                        List<String> listado_clase = db.getClase(id_depto,id_mpio);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                        adapter.addAll(listado_clase);
                        spinner_ceed_clase.setAdapter(adapter);

                        spinner_ceed_clase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                                final String id_clase=parent.getItemAtPosition(position).toString();

                                Spinner spinner_ceed_sectorr = (Spinner) mView.findViewById(R.id.spinner_ceed_sectorr);
                                List<String> listado_sectorr = db.get_SectorR(id_depto,id_mpio,id_clase);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                                adapter.addAll(listado_sectorr);
                                spinner_ceed_sectorr.setAdapter(adapter);

                                spinner_ceed_sectorr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                                        final String id_sectorr=parent.getItemAtPosition(position).toString();

                                        Spinner spinner_ceed_seccionr = (Spinner) mView.findViewById(R.id.spinner_ceed_seccionr);
                                        List<String> listado_seccionr = db.get_SeccionR(id_depto,id_mpio,id_clase,id_sectorr);
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                                        adapter.addAll(listado_seccionr);
                                        spinner_ceed_seccionr.setAdapter(adapter);


                                        spinner_ceed_seccionr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                                                final String id_seccionr=parent.getItemAtPosition(position).toString();

                                                Spinner spinner_ceed_cpob = (Spinner) mView.findViewById(R.id.spinner_ceed_cpob);
                                                List<String> listado_cpob = db.get_CPob(id_depto,id_mpio,id_clase,id_sectorr,id_seccionr);
                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                                                adapter.addAll(listado_cpob);
                                                spinner_ceed_cpob.setAdapter(adapter);

                                                spinner_ceed_cpob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                                                        final String id_cpob=parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                                                        Spinner spinner_ceed_setu = (Spinner) mView.findViewById(R.id.spinner_ceed_setu);
                                                        List<String> listado_setu = db.get_SetU(id_depto,id_mpio,id_clase,id_sectorr,id_seccionr,id_cpob);
                                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                                                        adapter.addAll(listado_setu);
                                                        spinner_ceed_setu.setAdapter(adapter);

                                                        spinner_ceed_setu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                            @Override
                                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                                final String id_setu=parent.getItemAtPosition(position).toString();

                                                                Button btn_dialog_ceed_ver_latlon =mView.findViewById(R.id.btn_dialog_ceed_ver_latlon);

                                                                btn_dialog_ceed_ver_latlon.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        LatLng coord=db.get_Coordenadas(id_depto,id_mpio,id_clase,id_sectorr,id_seccionr,id_cpob,id_setu);

                                                                        main.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 17));

                                                                        dialog.hide();
                                                                    }
                                                                });

                                                            }

                                                            @Override
                                                            public void onNothingSelected(AdapterView<?> parent) {

                                                            }
                                                        });


                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                    }
                                                });


                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {


            }


        });




    }






}
