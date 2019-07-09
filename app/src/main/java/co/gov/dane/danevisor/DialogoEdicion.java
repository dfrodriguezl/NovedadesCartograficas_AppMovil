package co.gov.dane.danevisor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Polygon;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class DialogoEdicion {

    public Activity activity;
    private int opcion;
    MainActivity main;
    private Boolean edicion=false;
    private JSONObject json;
    String Color;
    String id_google;


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
        }
        if(opcion==2){
            opt=R.array.novedad_linea;
        }
        if(opcion==3){
            opt=R.array.novedad_poligono;
        }



        LinearLayout linear_estado_obra  =(LinearLayout)  mView.findViewById(R.id.linear_estado_obra);
        linear_estado_obra.setVisibility(View.GONE);

        LinearLayout linear_descripcion_obra  =(LinearLayout)  mView.findViewById(R.id.linear_descripcion_obra);
        linear_descripcion_obra.setVisibility(View.GONE);

        LinearLayout linear_tipo_novedad  =(LinearLayout)  mView.findViewById(R.id.linear_tipo_novedad);
        linear_tipo_novedad.setVisibility(View.VISIBLE);




        if(opcion==4){

            linear_estado_obra.setVisibility(View.VISIBLE);
            linear_descripcion_obra.setVisibility(View.VISIBLE);

            final Spinner spinner_estado_obra = (Spinner) mView.findViewById(R.id.estado_obra);
            final ArrayAdapter<CharSequence> adapter_obra = ArrayAdapter.createFromResource(mView.getContext(),
                    R.array.estado_obra, android.R.layout.simple_spinner_item);
            adapter_obra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner_estado_obra.setAdapter(adapter_obra);

            linear_tipo_novedad.setVisibility(View.GONE);

        }

        if(opcion==5){

            main.dibujo_linea();
            main.show_add_punto();
            main.show_discard_save_edicion();
            main.show_save_edicion();

            try {

                main.atributos =new JSONObject();
                main.atributos.put("tipo", "Ruta CEED");
                main.atributos.put("descripcion", "CONTROL CEED");
                main.atributos.put("color","#7D3C98");

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
            mitoast.generarToast("Ingrese los atributos de la nueva geometria");
            btn_dialog_cerrar_atributos.setVisibility(View.GONE);
            btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {

                    Spinner tipo_novedad= (Spinner) mView.findViewById(R.id.tipo_novedad);
                    Spinner estado_obra= (Spinner) mView.findViewById(R.id.estado_obra);

                    EditText descripcion_novedad= (EditText) mView.findViewById(R.id.descripcion_novedad);

                    String tipo=tipo_novedad.getSelectedItem().toString();
                    String descripcion=descripcion_novedad.getText().toString();


                    int position_spiner=0;
                    String[] array_color = new String[0];


                    if(opcion==1){
                        position_spiner=tipo_novedad.getSelectedItemPosition();
                        array_color = main.getResources().getStringArray(R.array.color_punto);
                    }
                    if(opcion==2){
                        position_spiner=tipo_novedad.getSelectedItemPosition();
                        array_color = main.getResources().getStringArray(R.array.color_linea);
                    }
                    if(opcion==3){
                        position_spiner=tipo_novedad.getSelectedItemPosition();
                        array_color = main.getResources().getStringArray(R.array.color_poligono);
                    }
                    if(opcion==4){
                        position_spiner=estado_obra.getSelectedItemPosition();
                        array_color = main.getResources().getStringArray(R.array.color_estado_obra_punto);
                    }



                    try {
                        main.atributos =new JSONObject();
                        main.atributos.put("tipo", tipo);
                        main.atributos.put("descripcion", descripcion);
                        main.atributos.put("color",array_color[position_spiner]);

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


                    main.show_discard_save_edicion();
                    main.show_save_edicion();


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
                        Spinner estado_obra= (Spinner) mView.findViewById(R.id.estado_obra);

                        EditText descripcion_novedad= (EditText) mView.findViewById(R.id.descripcion_novedad);

                        String tipo=tipo_novedad.getSelectedItem().toString();
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
                            position_spiner=tipo_novedad.getSelectedItemPosition();
                            array_color = main.getResources().getStringArray(R.array.color_punto);
                            obj.put("color",array_color[position_spiner]);
                            for(int i=0;i<main.puntos.size();i++){
                                if(main.puntos.get(i).getId().equals(id_google)){
                                    main.puntos.get(i).setTag(obj);
                                    main.puntos.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(Float.parseFloat(array_color[position_spiner])));
                                }

                            }

                        }
                            if(opcion==4){
                                position_spiner=tipo_novedad.getSelectedItemPosition();
                                array_color = main.getResources().getStringArray(R.array.color_estado_obra_punto);
                                obj.put("atributos","obra");
                                obj.put("color",array_color[position_spiner]);
                                for(int i=0;i<main.puntos.size();i++){
                                    if(main.puntos.get(i).getId().equals(id_google)){
                                        main.puntos.get(i).setTag(obj);
                                        main.puntos.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(Float.parseFloat(array_color[position_spiner])));
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


}
