package co.gov.dane.ceedvisor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DialogoEdicion {

    public Activity activity;
    private int opcion;
    MainActivity main;
    private Boolean edicion=false;
    private JSONObject json;
    String Color;
    String id_google;
    String imagen="";


    public DialogoEdicion(MainActivity main, Activity _activity,int opcion){

        this.activity = _activity;
        this.opcion=opcion;
        this.main=main;

    }

    public DialogoEdicion(MainActivity main, Activity _activity,int opcion,Boolean edicion,JSONObject json,String id_google){

        this.activity = _activity;
        this.opcion=opcion;
        this.main=main;
        this.edicion=edicion;
        this.json=json;
        this.id_google=id_google;

    }


    public void mostrarDialogoEdicion(){


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );


        AlertDialog.Builder mBuilder =new AlertDialog.Builder(activity);

        final View mView =inflater.inflate(R.layout.dialog_atributos,null);

        mBuilder.setView(mView);
        final AlertDialog dialog =mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        wmlp.y = 200;   //y position

        wmlp.width=mView.getWidth();
        dialog.getWindow().setDimAmount(0);




        final Spinner spinner = (Spinner) mView.findViewById(R.id.tipo_novedad);

        int opt=R.array.novedad_punto;

        if(opcion==1){
            opt=R.array.novedad_punto;


            SpinnerNovedadPunto(mView,0,0,0);

            LinearLayout linear_tipo_novedad  =(LinearLayout)  mView.findViewById(R.id.linear_tipo_novedad);
            linear_tipo_novedad.setVisibility(View.GONE);

            LinearLayout panel_atributos_punto  =(LinearLayout)  mView.findViewById(R.id.panel_atributos_punto);

            panel_atributos_punto.setVisibility(View.VISIBLE);

        }
        if(opcion==2){
            opt=R.array.novedad_linea;
            LinearLayout linear_tipo_novedad  =(LinearLayout)  mView.findViewById(R.id.linear_tipo_novedad);
            linear_tipo_novedad.setVisibility(View.VISIBLE);
        }
        if(opcion==3){
            opt=R.array.novedad_poligono;
            LinearLayout linear_tipo_novedad  =(LinearLayout)  mView.findViewById(R.id.linear_tipo_novedad);
            linear_tipo_novedad.setVisibility(View.VISIBLE);
        }



        LinearLayout linear_estado_obra  =(LinearLayout)  mView.findViewById(R.id.linear_estado_obra);
        linear_estado_obra.setVisibility(View.GONE);

        LinearLayout linear_descripcion_obra  =(LinearLayout)  mView.findViewById(R.id.linear_descripcion_obra);
        linear_descripcion_obra.setVisibility(View.GONE);




        if(opcion==4){

            /*
            linear_estado_obra.setVisibility(View.VISIBLE);
            linear_descripcion_obra.setVisibility(View.VISIBLE);

            final Spinner spinner_estado_obra = (Spinner) mView.findViewById(R.id.estado_obra);
            final ArrayAdapter<CharSequence> adapter_obra = ArrayAdapter.createFromResource(mView.getContext(),
                    R.array.estado_obra, android.R.layout.simple_spinner_item);
            adapter_obra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner_estado_obra.setAdapter(adapter_obra);
*/
            LinearLayout linear_tipo_novedad  =(LinearLayout)  mView.findViewById(R.id.linear_tipo_novedad);
            linear_tipo_novedad.setVisibility(View.GONE);

        }

        if(opcion==5){

            main.dibujo_linea();
            main.show_add_punto();


            try {

                main.atributos =new JSONObject();
                main.atributos.put("tipo", "Ruta CEED");
                main.atributos.put("descripcion", "CONTROL CEED");
                main.atributos.put("color","#56D5FF");

            } catch (JSONException e) {

            }

        }else{
            dialog.show();
        }

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mView.getContext(),
                opt, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);



        Button btn_guardar_atributos= (Button) mView.findViewById(R.id.btn_dialog_guardar_atributos);
        Button btn_dialog_cerrar_atributos= (Button) mView.findViewById(R.id.btn_dialog_cerrar_atributos);


        if(!edicion){
            Mensajes mitoast =new Mensajes(activity);

            if(opcion==5){
                mitoast.generarToastMapa("Dibuje el trayecto recorrido");
            }
            else{
                mitoast.generarToast("Ingrese los atributos de la nueva geometria");
            }

            btn_dialog_cerrar_atributos.setVisibility(View.GONE);
            btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {

                    Spinner tipo_novedad= (Spinner) mView.findViewById(R.id.tipo_novedad);

                    EditText descripcion_novedad= (EditText) mView.findViewById(R.id.descripcion_novedad);

                    String tipo=tipo_novedad.getSelectedItem().toString();

                    String descripcion=descripcion_novedad.getText().toString();


                    int position_spiner=0;
                    String[] array_color = new String[0];
                    String color="";



                    if(opcion==1){

                        tipo= imagen.replace(".png", "");

                        color=imagen;

                    }

                    if(opcion==2){
                        position_spiner=tipo_novedad.getSelectedItemPosition();
                        array_color = main.getResources().getStringArray(R.array.color_linea);
                        color=array_color[position_spiner];
                    }
                    if(opcion==3){
                        position_spiner=tipo_novedad.getSelectedItemPosition();
                        array_color = main.getResources().getStringArray(R.array.color_poligono);
                        color=array_color[position_spiner];
                    }
                    if(opcion==4){
                        tipo="ObraProyectada";
                        color="";
                    }



                    try {
                        main.atributos =new JSONObject();
                        main.atributos.put("tipo", tipo);
                        main.atributos.put("descripcion", descripcion);
                        main.atributos.put("color",color);

                        if(opcion==4){
                            main.atributos.put("atributos","obra");
                        }

                    } catch (JSONException e) {

                    }


                    dialog.dismiss();


                    if(opcion==1){
                        main.dibujo_punto();
                        main.show_add_punto();
                    }
                    if(opcion==2){
                        main.dibujo_linea();
                        main.show_add_punto();
                    }
                    if(opcion==3){
                        main.dibujo_poligono();
                        main.show_add_punto();
                    }
                    if(opcion==4){
                        main.dibujo_punto();
                        main.show_add_punto();
                    }



                }
            });
        }
        else if(edicion){
            btn_dialog_cerrar_atributos.setVisibility(View.VISIBLE);
            try {

                String tipo= json.get("tipo").toString();
                String descripcion=json.get("descripcion").toString();

                if(tipo!=null){
                    int spinnerPosition = adapter.getPosition(tipo);
                    spinner.setSelection(spinnerPosition);

                    if(opcion==1){
                        final int grupo= Integer.parseInt(tipo.substring(0, 2))-1;
                        final int subgrupo= Integer.parseInt(tipo.substring(2, 4))-1;
                        final int item= Integer.parseInt(tipo.substring(4, 6))-1;

                        SpinnerNovedadPunto(mView,grupo,subgrupo,item);


                    }
                }

                EditText descripcion_novedad= (EditText) mView.findViewById(R.id.descripcion_novedad);
                descripcion_novedad.setText(descripcion);


            } catch (Throwable t) {

            }

            btn_dialog_cerrar_atributos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        int id = Integer.parseInt(json.get("id").toString());

                        Spinner tipo_novedad= (Spinner) mView.findViewById(R.id.tipo_novedad);

                        EditText descripcion_novedad= (EditText) mView.findViewById(R.id.descripcion_novedad);

                        String tipo="";

                        if(opcion==1 ){

                            tipo= imagen.replace(".png", "");

                        }else if(opcion==4){
                            tipo="ObraProyectada";
                        }else{
                            tipo=tipo_novedad.getSelectedItem().toString();
                        }


                        String descripcion=descripcion_novedad.getText().toString();

                        Novedades novedad=new Novedades(activity,main,id,tipo,descripcion);
                        novedad.updateNovedadAtributos();

                        int position_spiner=0;
                        String[] array_color = new String[0];
                        try {
                            JSONObject obj =new JSONObject();
                            obj.put("id", String.valueOf(id));
                            obj.put("tipo", tipo);
                            obj.put("descripcion", descripcion);


                        if(opcion==3){

                            position_spiner=tipo_novedad.getSelectedItemPosition();
                            array_color = main.getResources().getStringArray(R.array.color_poligono);
                            obj.put("color",array_color[position_spiner]);

                            for(int i=0;i<main.polygon.size();i++){
                                if(main.polygon.get(i).getId().equals(id_google)){
                                        main.polygon.get(i).setTag(obj);
                                        main.polygon.get(i).setFillColor(android.graphics.Color.parseColor(array_color[position_spiner]));

                                }

                            }

                        }

                        if(opcion==2){
                            position_spiner=tipo_novedad.getSelectedItemPosition();
                            array_color = main.getResources().getStringArray(R.array.color_linea);
                            obj.put("color",array_color[position_spiner]);
                            for(int i=0;i<main.line.size();i++){
                                if(main.line.get(i).getId().equals(id_google)){
                                    main.line.get(i).setTag(obj);
                                    main.line.get(i).setColor(android.graphics.Color.parseColor(array_color[position_spiner]));
                                }

                            }
                        }
                        if(opcion==1){

                            for(int i=0;i<main.puntos.size();i++){
                                if(main.puntos.get(i).getId().equals(id_google)){
                                    main.puntos.get(i).setTag(obj);

                                    String imagen=tipo;

                                    AssetManager mg = main.getResources().getAssets();
                                    InputStream is = null;
                                    try {
                                        is = mg.open("img/"+imagen+".png");
                                        main.puntos.get(i).setIcon(BitmapDescriptorFactory.fromAsset("img/"+imagen+".png"));
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
                                }

                            }



                        }
                            if(opcion==4){
                                for(int i=0;i<main.puntos.size();i++){
                                    if(main.puntos.get(i).getId().equals(id_google)){
                                        main.puntos.get(i).setTag(obj);

                                    }

                                }

                            }



                        } catch (JSONException e) {

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    dialog.dismiss();
                }
            });


        }








    }


    public void SpinnerNovedadPunto(final View mView, final int posGrupo, final int posSubgrupo, final int posItem){


        final CeedDB db =new CeedDB(main);

        Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);


        List<String> listado = db.get_NovedadGrupo();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
        adapter.addAll(listado);
        spinner_novedad_grupo.setAdapter(adapter);
        spinner_novedad_grupo.setSelection(posGrupo);

        spinner_novedad_grupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                final String id_grupo=parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                Spinner spinner_novedad_subgrupo = (Spinner) mView.findViewById(R.id.spinner_novedad_subgrupo);
                List<String> listado = db.get_NovedadSubGrupo(id_grupo);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                adapter.addAll(listado);
                spinner_novedad_subgrupo.setAdapter(adapter);
                spinner_novedad_subgrupo.setSelection(posSubgrupo);
                spinner_novedad_subgrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        final String id_subgrupo=parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                        Spinner spinner_novedad_item = (Spinner) mView.findViewById(R.id.spinner_novedad_item);

                        List<String> listado = db.get_NovedadItem(id_grupo,id_subgrupo);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                        adapter.addAll(listado);
                        spinner_novedad_item.setAdapter(adapter);
                        spinner_novedad_item.setSelection(posItem);
                        spinner_novedad_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                final String id_item=parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                                imagen = db.get_NovedadImagen(id_grupo,id_subgrupo,id_item);

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



}
