package co.gov.dane.danevisor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DialogoOtros {

    public Activity activity;

    MainActivity main;

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

        btn_coordenadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = latitud.getText().toString();
                String lon = longitud.getText().toString();

                LatLng marker = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                main.mMap.addMarker(new MarkerOptions().position(marker)
                        .title("Ubicación"));
                main.mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
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

    public void MostrarDialogoSincronizar(){

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );


        AlertDialog.Builder mBuilder =new AlertDialog.Builder(activity);
        final View mView =inflater.inflate(R.layout.dialog_sincronizar,null);
        mBuilder.setView(mView);

        Button btn_dialog_sincronizar_close= (Button) mView.findViewById(R.id.btn_dialog_sincronizar_close);
        final AlertDialog dialog =mBuilder.create();
        dialog.show();

        Button descargar_datos_nube= (Button) mView.findViewById(R.id.descargar_datos_nube);

        final TextView mensaje=(TextView) mView.findViewById(R.id.mensaje_sincronizar);

        descargar_datos_nube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Controlador con=new Controlador(mView.getContext());
                con.getData();

            }
        });



        btn_dialog_sincronizar_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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

        // creamos el listado
        MapaOfflineAdapter listAdapter = new MapaOfflineAdapter(activity, mapas);

        ListView listView=(ListView)mView.findViewById(R.id.listview_mapa_offline);
        // establecemos el adaptador en la lista
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



}
