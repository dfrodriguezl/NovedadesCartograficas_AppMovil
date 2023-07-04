package co.gov.dane.novedades;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;

import android.util.Log;
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
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.PatternItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class DialogoEdicion {

    public Activity activity;
    private int opcion;
    MainActivity main;
    private Boolean edicion = false;
    private JSONObject json;
    String Color;
    String id_google;
    String imagen = "";

    int seguir_spinner_linea = 0;
    int seguir_spinner_punto = 0;

    CeedDB db;
    Util utilidad;

    public DialogoEdicion(MainActivity main, Activity _activity, int opcion) {

        this.activity = _activity;
        this.opcion = opcion;
        this.main = main;

    }

    public DialogoEdicion(MainActivity main, Activity _activity, int opcion, Boolean edicion, JSONObject json, String id_google) {

        this.activity = _activity;
        this.opcion = opcion;
        this.main = main;
        this.edicion = edicion;
        this.json = json;
        this.id_google = id_google;

    }

    public void DialogoEdicionConteo (Boolean WindowVisible, final String manzana) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);

        final View mView = inflater.inflate(R.layout.dialog_conteo_unidades, null);

        TextView mznText = (TextView) mView.findViewById(R.id.cod_mzn);

        final Spinner spinner_tipo_nov_conteo = mView.findViewById(R.id.spinner_tipo_nov_conteo);
        final ArrayAdapter<String> array_tipo_novedad_conteo = new ArrayAdapter<String>(main,
                android.R.layout.simple_spinner_item, main.getResources().getStringArray(R.array.tipo_novedad_conteo));
        spinner_tipo_nov_conteo.setAdapter(array_tipo_novedad_conteo);

        if (manzana != null) {
            Log.d("text mzn", String.valueOf(mznText.getText()));
            Log.d("text mzn", manzana);
            mznText.setText(mznText.getText() + ": " + manzana);
            LinearLayout panel_mzn = (LinearLayout) mView.findViewById(R.id.panel_mzn);
            panel_mzn.setVisibility(View.GONE);
        } else {
            mznText.setText("No hay elemento de referencia del MGN");
            LinearLayout panel_mzn = (LinearLayout) mView.findViewById(R.id.panel_mzn);
            panel_mzn.setVisibility(View.GONE);
        }

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();


        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        wmlp.y = 200;   //y position

        wmlp.width = mView.getWidth();
        dialog.getWindow().setDimAmount(0);

        if (!WindowVisible) {
            main.dibujo_punto_conteo();
            main.show_add_punto();
        } else {
            dialog.show();
        }

        if (WindowVisible) {
            Button btn_guardar_conteo= (Button) mView.findViewById(R.id.btn_dialog_guardar_conteo);
            Button btn_dialog_cancelar_conteo = (Button) mView.findViewById(R.id.btn_dialog_cancelar_conteo);

            btn_guardar_conteo.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {

                    Log.d("yeiner mendivelso", "onClick: en 3");

                    EditText num_edificaciones = (EditText) mView.findViewById(R.id.num_edificaciones);

                    String nume_edificaciones = num_edificaciones.getText().toString();

                    EditText num_viviendas = (EditText) mView.findViewById(R.id.num_viviendas);

                    String nume_viviendas = num_viviendas.getText().toString();

                    EditText num_ueconomicas = (EditText) mView.findViewById(R.id.num_ueconomicas);

                    String nume_ueconomicas = num_ueconomicas.getText().toString();

                    String tipo_nov_conteo = spinner_tipo_nov_conteo.getSelectedItem().toString();

                    EditText descripcion_conteo = (EditText) mView.findViewById(R.id.observaciones_conteo);

                    String descripcion = descripcion_conteo.getText().toString();

                    try {
                        main.atributos = new JSONObject();
                        main.atributos.put("manzana", manzana);
                        main.atributos.put("edificaciones", nume_edificaciones);
                        main.atributos.put("viviendas", nume_viviendas);
                        main.atributos.put("ue", nume_ueconomicas);
                        main.atributos.put("tipo_nov", tipo_nov_conteo);
                        main.atributos.put("descripcion", descripcion);

                    } catch (JSONException e) {

                    }

                    dialog.dismiss();

                    main.drawPC();

                }
            });

            btn_dialog_cancelar_conteo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    main.cancelarConteo();
                }
            });
        }
    }

    public void mostrarDialogoEdicionPL(Boolean WindowVisible) {
        String ruta_db;
        if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
            ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Dane" + File.separator + "db" + File.separator + "ceed.db";
        } else {
            ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Dane" + File.separator + "db" + File.separator + "ceed.db";
        }

        db = new CeedDB(main, ruta_db);
        utilidad = new Util(main, main);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);

        final View mView = inflater.inflate(R.layout.dialog_atributos, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        wmlp.y = 200;   //y position

        wmlp.width = mView.getWidth();
        dialog.getWindow().setDimAmount(0);


        Button btn_guardar_atributos = (Button) mView.findViewById(R.id.btn_dialog_guardar_atributos);
        Button btn_dialog_cerrar_atributos = (Button) mView.findViewById(R.id.btn_dialog_cerrar_atributos);


        if (opcion == 1) {

            SpinnerNovedadPunto(mView, 0, 0, 0);

        }
        if (opcion == 2) {
            LinearLayout panel_item = (LinearLayout) mView.findViewById(R.id.panel_item);
            panel_item.setVisibility(View.GONE);
            SpinnerNovedadLinea(mView, 0, 0);

        }
        if (opcion == 4) {
            LinearLayout panel_item = (LinearLayout) mView.findViewById(R.id.panel_item);
            panel_item.setVisibility(View.GONE);
            SpinnerNovedadLinea(mView, 0, 0);
        }

        if (!WindowVisible) {
            if (opcion == 1) {
                main.dibujo_punto();
                main.show_add_punto();
            }
            if (opcion == 2) {
                main.dibujo_linea(2);
                main.show_add_punto();
            }
            if (opcion == 5) {
                main.show_add_punto();
            }

        } else {
            dialog.show();
        }

        if (WindowVisible) {

            btn_dialog_cerrar_atributos.setVisibility(View.GONE);
            btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {

                    Log.d("yeiner mendivelso", "onClick: en 2");

                    EditText descripcion_novedad = (EditText) mView.findViewById(R.id.descripcion_novedad);

                    String tipo = "";

                    String descripcion = descripcion_novedad.getText().toString();

                    String color = "";


                    if (opcion == 1) {
                        tipo = imagen.replace(".png", "");
                        color = imagen;
                    }

                    if (opcion == 2) {

                        Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);
                        String grupo = spinner_novedad_grupo.getSelectedItem().toString().replaceAll("[^0-9]", "");

                        Spinner spinner_novedad_subgrupo = (Spinner) mView.findViewById(R.id.spinner_novedad_subgrupo);
                        String subgrupo = spinner_novedad_subgrupo.getSelectedItem().toString().replaceAll("[^0-9]", "");
                        tipo = grupo + subgrupo;

                        color = db.get_LineaColor(tipo);

                    }
                    if (opcion == 3) {
                        Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);
                        tipo = spinner_novedad_grupo.getSelectedItem().toString().replaceAll("[^0-9]", "");
                        color = db.get_PoligonoColor(tipo);
                    }

                    if (opcion == 4) {

                        Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);
                        String grupo = spinner_novedad_grupo.getSelectedItem().toString().replaceAll("[^0-9]", "");

                        Spinner spinner_novedad_subgrupo = (Spinner) mView.findViewById(R.id.spinner_novedad_subgrupo);
                        String subgrupo = spinner_novedad_subgrupo.getSelectedItem().toString().replaceAll("[^0-9]", "");
                        tipo = grupo + subgrupo;

                        color = db.get_LineaColor(tipo);

                    }

                    try {
                        main.atributos = new JSONObject();
                        main.atributos.put("tipo", tipo);
                        main.atributos.put("descripcion", descripcion);
                        main.atributos.put("color", color);

                    } catch (JSONException e) {

                    }

                    dialog.dismiss();

                    if (opcion == 1) {
                        main.drawP();
                    }
                    if (opcion == 2) {
                        main.drawL(1);
                    }
                    if (opcion == 4) {
                        main.drawL(1);
                    }

                }
            });


        }


    }


    public void mostrarDialogoEdicion() {

        String ruta_db;
        if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
            ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Dane" + File.separator + "db" + File.separator + "ceed.db";
        } else {
            ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Dane" + File.separator + "db" + File.separator + "ceed.db";
        }

        db = new CeedDB(main, ruta_db);
        utilidad = new Util(main, main);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);

        final View mView = inflater.inflate(R.layout.dialog_atributos, null);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        wmlp.y = 200;   //y position

        wmlp.width = mView.getWidth();
        dialog.getWindow().setDimAmount(0);

        if (opcion == 1) {

            SpinnerNovedadPunto(mView, 0, 0, 0);

        }
        if (opcion == 2) {
            LinearLayout panel_item = (LinearLayout) mView.findViewById(R.id.panel_item);
            panel_item.setVisibility(View.GONE);
            SpinnerNovedadLinea(mView, 0, 0);

        }

        if (opcion == 3 || opcion == 5) {
            LinearLayout panel_subgrupo = (LinearLayout) mView.findViewById(R.id.panel_subgrupo);
            panel_subgrupo.setVisibility(View.GONE);
            LinearLayout panel_item = (LinearLayout) mView.findViewById(R.id.panel_item);
            panel_item.setVisibility(View.GONE);
            SpinnerNovedadPoligono(mView, 0);
        }

//        if (opcion == 4) {
//            LinearLayout panel_item = (LinearLayout) mView.findViewById(R.id.panel_item);
//            panel_item.setVisibility(View.GONE);
//            SpinnerNovedadLinea(mView, 0, 0);
//        }

        dialog.show();


        Button btn_guardar_atributos = (Button) mView.findViewById(R.id.btn_dialog_guardar_atributos);
        Button btn_dialog_cerrar_atributos = (Button) mView.findViewById(R.id.btn_dialog_cerrar_atributos);


        if (!edicion) {

            Mensajes mitoast = new Mensajes(activity);


            mitoast.generarToast("Ingrese los atributos de la nueva geometria");

            btn_dialog_cerrar_atributos.setVisibility(View.GONE);
            btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {

                    Log.d("yeiner mendivelso", "onClick: en 1");

                    EditText descripcion_novedad = (EditText) mView.findViewById(R.id.descripcion_novedad);

                    String tipo = "";

                    String descripcion = descripcion_novedad.getText().toString();

                    String color = "";


                    if (opcion == 3 || opcion == 5) {
                        Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);
                        tipo = spinner_novedad_grupo.getSelectedItem().toString().replaceAll("[^0-9]", "");
                        color = db.get_PoligonoColor(tipo);
                    }

                    try {
                        main.atributos = new JSONObject();
                        main.atributos.put("tipo", tipo);
                        main.atributos.put("descripcion", descripcion);
                        main.atributos.put("color", color);

                    } catch (JSONException e) {

                    }


                    dialog.dismiss();

                    if (opcion == 3 || opcion == 5) {
                        if (tipo.equals("3")) {// se implementa la unión de manzanas
                            main.mitoast.generarToast("Seleccione más de una Manzana");
                            main.JoinPicker = false;
                        } else if (tipo.equals("2") || tipo.equals("4") || tipo.equals("5") || tipo.equals("6")) {// se implementa para AG NO EXISTE
                            main.mitoast.generarToast("Seleccione una Manzana");
                            main.JoinPicker = false;
                        } else {// se implementa para las novedades tipo poligono que se deben dibujar
                            if(opcion == 5){
                                main.crearPoligonoDesdeLinea();
                            } else {
                                main.dibujo_poligono();
                                main.show_add_punto();
                            }

                        }


                    }


                }
            });


        } else if (edicion) {
            btn_dialog_cerrar_atributos.setVisibility(View.VISIBLE);
            try {

                String tipo = json.get("tipo").toString();
                String descripcion = json.get("descripcion").toString();

                if (tipo != null) {

                    Log.d("opcion_escogida:", String.valueOf(opcion));

                    if (opcion == 1) {

                        final int grupo = Integer.parseInt(tipo.substring(0, 2)) - 1;

                        int subgrupo = 0;
                        if (grupo == 0) {
                            subgrupo = Integer.parseInt(tipo.substring(2, 4)) - 1;
                        } else {
                            subgrupo = Integer.parseInt(tipo.substring(2, 4)) - 1;
                        }


                        int item = 0;
                        if (grupo == 0 && subgrupo == 0) {
                            item = Integer.parseInt(tipo.substring(4, 6)) - 8;
                        } else {
                            item = Integer.parseInt(tipo.substring(4, 6)) - 1;
                        }

                        Log.d("grupo:", String.valueOf(grupo));
                        Log.d("subgrupo:", String.valueOf(subgrupo));
                        Log.d("item:", String.valueOf(item));

                        SpinnerNovedadPunto(mView, grupo, subgrupo, item);

                    }
                    if (opcion == 2) {

                        final int grupo = Integer.parseInt(tipo.substring(0, 2));

                        int subgrupo = 0;
                        subgrupo = Integer.parseInt(tipo.substring(2, 4));

                        SpinnerNovedadLinea(mView, grupo - 1, subgrupo - 1);

                    }
                    if (opcion == 3) {

                        final int grupo = Integer.parseInt(tipo);

                        SpinnerNovedadPoligono(mView, grupo - 1);

                    }
//                    if (opcion == 4) {
//
//                        final int grupo = Integer.parseInt(tipo.substring(0, 2));
//
//                        int subgrupo = 0;
//                        subgrupo = Integer.parseInt(tipo.substring(2, 4));
//
//                        SpinnerNovedadLinea(mView, grupo - 1, subgrupo - 1);
//
//                    }


                }

                EditText descripcion_novedad = (EditText) mView.findViewById(R.id.descripcion_novedad);
                descripcion_novedad.setText(descripcion);


            } catch (Throwable t) {

            }

            btn_dialog_cerrar_atributos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main.drop_markers_edicion();
                    main.borrar_poligono_seleccionado();
                    main.hide_cut_geom();
                    main.hide_delete_geom();
                    main.drop_markers_intermediate();
                    main.hide_msg_distancia();
                    main.hide_msg_area();
                    main.hide_edit_atributos();
                    main.hide_edit_join();
                    main.hide_menu_grupo_edicion();
                    dialog.dismiss();
                }
            });

            btn_guardar_atributos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        int id = Integer.parseInt(json.get("id").toString());
                        EditText descripcion_novedad = (EditText) mView.findViewById(R.id.descripcion_novedad);

                        String tipo = "";

                        if (opcion == 1) {

                            tipo = imagen.replace(".png", "");

                        } else if (opcion == 2) {

                            Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);
                            String grupo = spinner_novedad_grupo.getSelectedItem().toString().replaceAll("[^0-9]", "");

                            Spinner spinner_novedad_subgrupo = (Spinner) mView.findViewById(R.id.spinner_novedad_subgrupo);
                            String subgrupo = spinner_novedad_subgrupo.getSelectedItem().toString().replaceAll("[^0-9]", "");
                            tipo = grupo + subgrupo;

                        } else if (opcion == 3) {
                            Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);
                            tipo = spinner_novedad_grupo.getSelectedItem().toString().replaceAll("[^0-9]", "");
                        }


                        String descripcion = descripcion_novedad.getText().toString();

                        Novedades novedad = new Novedades(activity, main, id, tipo, descripcion);
                        novedad.updateNovedadAtributos();

                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("id", String.valueOf(id));
                            obj.put("tipo", tipo);
                            obj.put("descripcion", descripcion);


                            if (opcion == 3) {

                                Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);
                                tipo = spinner_novedad_grupo.getSelectedItem().toString().replaceAll("[^0-9]", "");
                                String color = db.get_PoligonoColor(tipo);
                                obj.put("color", color);

                                for (int i = 0; i < main.polygon.size(); i++) {
                                    if (main.polygon.get(i).getId().equals(id_google)) {
                                        main.polygon.get(i).setTag(obj);
                                        main.polygon.get(i).setFillColor(android.graphics.Color.parseColor(color));

                                    }

                                }

                            }

                            if (opcion == 2) {

                                Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);
                                String grupo = spinner_novedad_grupo.getSelectedItem().toString().replaceAll("[^0-9]", "");

                                Spinner spinner_novedad_subgrupo = (Spinner) mView.findViewById(R.id.spinner_novedad_subgrupo);
                                String subgrupo = spinner_novedad_subgrupo.getSelectedItem().toString().replaceAll("[^0-9]", "");
                                tipo = grupo + subgrupo;

                                String color = db.get_LineaColor(tipo);
                                obj.put("color", color);
                                String style = db.get_LineaStyle(tipo);

                                List<PatternItem> PATTERN_POLYGON_ALPHA = utilidad.LineStyle(style);

                                for (int i = 0; i < main.line.size(); i++) {
                                    if (main.line.get(i).getId().equals(id_google)) {
                                        main.line.get(i).setTag(obj);
                                        main.line.get(i).setColor(android.graphics.Color.parseColor(color));
                                        main.line.get(i).setPattern(PATTERN_POLYGON_ALPHA);
                                    }

                                }
                            }
                            if (opcion == 1) {

                                for (int i = 0; i < main.puntos.size(); i++) {
                                    if (main.puntos.get(i).getId().equals(id_google)) {
                                        main.puntos.get(i).setTag(obj);

                                        String imagen = tipo;

                                        AssetManager mg = main.getResources().getAssets();
                                        InputStream is = null;
                                        try {
                                            is = mg.open("img/" + imagen + ".png");
                                            main.puntos.get(i).setIcon(BitmapDescriptorFactory.fromAsset("img/" + imagen + ".png"));
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


                        } catch (JSONException e) {
                            Log.e("error:", e.toString());

                        }


                    } catch (JSONException e) {
                        ;
                        Log.e("error:", e.toString());
                    }

                    main.drop_markers_edicion();
                    main.borrar_poligono_seleccionado();
                    main.hide_cut_geom();
                    main.hide_delete_geom();
                    main.drop_markers_intermediate();
                    main.hide_msg_distancia();
                    main.hide_msg_area();
                    main.hide_edit_atributos();
                    main.hide_edit_join();
                    main.hide_menu_grupo_edicion();

                    dialog.dismiss();
                }
            });


        }


    }


    public void SpinnerNovedadPunto(final View mView, final int posGrupo, final int posSubgrupo, final int posItem) {

        Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);


        List<String> listado = db.get_NovedadGrupo();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
        adapter.addAll(listado);
        spinner_novedad_grupo.setAdapter(adapter);
        spinner_novedad_grupo.setSelection(posGrupo);

        seguir_spinner_punto = 0;

        spinner_novedad_grupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                final String id_grupo = parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                Spinner spinner_novedad_subgrupo = (Spinner) mView.findViewById(R.id.spinner_novedad_subgrupo);
                List<String> listado = db.get_NovedadSubGrupo(id_grupo);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                adapter.addAll(listado);
                spinner_novedad_subgrupo.setAdapter(adapter);


                if (seguir_spinner_punto == 0) {
                    spinner_novedad_subgrupo.setSelection(posSubgrupo);
                } else {
                    spinner_novedad_subgrupo.setSelection(0);
                }


                spinner_novedad_subgrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        final String id_subgrupo = parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                        Spinner spinner_novedad_item = (Spinner) mView.findViewById(R.id.spinner_novedad_item);


                        List<String> listado = db.get_NovedadItem(id_grupo, id_subgrupo);

                        Log.d("listado:", String.valueOf(listado));

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                        adapter.addAll(listado);
                        spinner_novedad_item.setAdapter(adapter);
                        spinner_novedad_item.setSelection(posItem);

                        if (seguir_spinner_punto == 0) {
                            spinner_novedad_item.setSelection(posItem);
                        } else {
                            spinner_novedad_item.setSelection(0);
                        }
                        seguir_spinner_punto = seguir_spinner_punto + 1;

                        spinner_novedad_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                final String id_item = parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                                imagen = db.get_NovedadImagen(id_grupo, id_subgrupo, id_item);

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

    public void SpinnerNovedadLinea(final View mView, final int posGrupo, final int posSubgrupo) {

        Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);

        List<String> listado = db.get_NovedadGrupoLinea();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
        adapter.addAll(listado);
        spinner_novedad_grupo.setAdapter(adapter);
        spinner_novedad_grupo.setSelection(posGrupo);

        seguir_spinner_linea = 0;

        spinner_novedad_grupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final String id_grupo = parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");

                Spinner spinner_novedad_subgrupo = (Spinner) mView.findViewById(R.id.spinner_novedad_subgrupo);
                List<String> listado = db.get_NovedadSubGrupoLinea(id_grupo);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
                adapter.addAll(listado);
                spinner_novedad_subgrupo.setAdapter(adapter);

                if (seguir_spinner_linea == 0) {
                    spinner_novedad_subgrupo.setSelection(posSubgrupo);
                } else {
                    spinner_novedad_subgrupo.setSelection(0);
                }

                seguir_spinner_linea = seguir_spinner_linea + 1;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public void SpinnerNovedadPoligono(final View mView, final int posGrupo) {

        Spinner spinner_novedad_grupo = (Spinner) mView.findViewById(R.id.spinner_novedad_grupo);

        List<String> listado = db.get_NovedadGrupoPoligono();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(main, android.R.layout.simple_spinner_item);
        adapter.addAll(listado);
        spinner_novedad_grupo.setAdapter(adapter);
        spinner_novedad_grupo.setSelection(posGrupo);


    }

    // Mostrar dialogo para la selección de geometria cuando se hace un trazo GPS
    public void mostrarDialogoSeleccionGeometria() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        View mView = inflater.inflate(R.layout.dialog_asignacion, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        wmlp.y = 200;   //y position
        wmlp.width = mView.getWidth();
        dialog.getWindow().setDimAmount(0);

        final Spinner spinner_tipo_geometria = mView.findViewById(R.id.spinner_tipo_geometria);
        ArrayAdapter<String> array_tipo_geometria = new ArrayAdapter<String>(main,
                android.R.layout.simple_spinner_item, main.getResources().getStringArray(R.array.tipo_geometria_gps));

        spinner_tipo_geometria.setAdapter(array_tipo_geometria);

        dialog.show();


        Button btn_confirmar = (Button) mView.findViewById(R.id.btn_dialog_confirmar);
        Button btn_cancelar = (Button) mView.findViewById(R.id.btn_dialog_cancelar);

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                terminarEdicionMapa();
            }
        });

        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                String tipo_geometria = spinner_tipo_geometria.getSelectedItem().toString();

                if (tipo_geometria.equals("Polígono")) {
                    setOpcion(5);
                    mostrarDialogoEdicion();
                } else if (tipo_geometria.equals("Línea")) {
                    setOpcion(2);
                    mostrarDialogoEdicionPL(true);
                }

                dialog.dismiss();

            }
        });
    }

    private void terminarEdicionMapa() {
        Mensajes mitoast = new Mensajes(activity);
        main.remove_tracking_gps();
        main.hide_captura_gps();
        main.hide_discard_save_edicion();
        main.hide_discard_save_edicion_track();
        mitoast.generarToast("Edición terminada");
        main.estado_mapa_sin_edicion();
        main.hide_save_edicion();
        main.hide_save_edicion_track();
        main.setSwitchGPS(false);
    }

    public void setOpcion(int opcion) {
        this.opcion = opcion;
    }

}
