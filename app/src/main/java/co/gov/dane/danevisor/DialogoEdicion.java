package co.gov.dane.danevisor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Spinner;

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


    public DialogoEdicion(MainActivity main, Activity _activity,int opcion){

        this.activity = _activity;
        this.opcion=opcion;
        this.main=main;

    }

    public DialogoEdicion(MainActivity main, Activity _activity,int opcion,Boolean edicion,JSONObject json){

        this.activity = _activity;
        this.opcion=opcion;
        this.main=main;
        this.edicion=edicion;
        this.json=json;

    }


    public void mostrarDialogoEdicion(){


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );


        AlertDialog.Builder mBuilder =new AlertDialog.Builder(activity);
        final View mView =inflater.inflate(R.layout.dialog_atributos,null);
        mBuilder.setView(mView);
        final AlertDialog dialog =mBuilder.create();

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        wmlp.y = 200;   //y position

        wmlp.width=mView.getWidth();
        dialog.getWindow().setDimAmount(0);
        dialog.show();

        final Spinner spinner = (Spinner) mView.findViewById(R.id.tipo_novedad);

        int opt=0;

        if(opcion==1){
            opt=R.array.novedad_punto;
        }
        if(opcion==2){
            opt=R.array.novedad_linea;
        }
        if(opcion==3){
            opt=R.array.novedad_poligono;
        }

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mView.getContext(),
                opt, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        Button btn_guardar_atributos= (Button) mView.findViewById(R.id.btn_dialog_guardar_atributos);

        if(!edicion){
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


                    if(opcion==1){
                        position_spiner=tipo_novedad.getSelectedItemPosition();
                        array_color = main.getResources().getStringArray(R.array.color_linea);
                    }
                    if(opcion==2){
                        position_spiner=tipo_novedad.getSelectedItemPosition();
                        array_color = main.getResources().getStringArray(R.array.color_linea);
                    }
                    if(opcion==3){
                        position_spiner=tipo_novedad.getSelectedItemPosition();
                        array_color = main.getResources().getStringArray(R.array.color_poligono);
                    }

                    try {
                        main.atributos =new JSONObject();
                        main.atributos.put("tipo", tipo);
                        main.atributos.put("descripcion", descripcion);
                        main.atributos.put("color",array_color[position_spiner]);
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

                    FloatingActionButton  save_edicion = (FloatingActionButton ) activity.findViewById(R.id.save_edicion);
                    save_edicion.setVisibility(View.VISIBLE);
                    FloatingActionButton discard_save_editor = (FloatingActionButton) activity.findViewById(R.id.discard_save_editor);
                    discard_save_editor.setVisibility(View.VISIBLE);


                }
            });
        }
        else if(edicion){
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

            btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });


        }








    }


}
