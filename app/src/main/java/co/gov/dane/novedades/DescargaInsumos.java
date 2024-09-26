package co.gov.dane.novedades;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class DescargaInsumos extends AppCompatActivity {


    private RecyclerView recyclerView;
    private CustomListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descarga_insumos);

        String ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db";

        if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
            ruta_db = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db";
        } else {
//            ruta_db = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator + "db";
            ruta_db = getExternalFilesDir("db").getAbsolutePath();
        }

        final File directory = new File(ruta_db);


//        ArrayList<Item> itemsArrayList = new ArrayList<Item>();
//        File directory = new File(ruta_db);
//        File[] files = directory.listFiles();
//
//        for(int i = 0; i < files.length; i++){
//            Item row = new Item(files[i].getName(),"");
//            itemsArrayList.add(row);
//        }
//
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);
//
//        mAdapter = new CustomListAdapter(itemsArrayList,DescargaInsumos.this);
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(DescargaInsumos.this, LinearLayoutManager.VERTICAL));
//
//        recyclerView.setAdapter(mAdapter);


        Controlador con = new Controlador(DescargaInsumos.this);

        con.getInfo(new VolleyCallBackJSON() {
            @Override
            public void onSuccess(JSONObject result) {


                ArrayList<Item> itemsArrayList = new ArrayList<Item>();

                try {
                    JSONArray key = result.names();
                    for (int i = 0; i < key.length(); ++i) {
                        String keys = key.getString(i);

                        String value = result.getString(keys);

                        Item row = new Item(value, "");

                        itemsArrayList.add(row);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                File[] files = directory.listFiles();

                for (int i = 0; i < files.length; i++) {
                    if(files[i].isFile() && files[i].getPath().endsWith(".db")){
                        Item row = new Item(files[i].getName(), "");
                        String archivo = null;

                        if(Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT){
                            archivo= Environment.getExternalStorageDirectory()+ File.separator + "Editor Nc" + File.separator +"db"+ File.separator + row.getItemName();
                        }else{
                            archivo = getExternalFilesDir("db") + File.separator + row.getItemName();
                        }

                        File fichero = new File(archivo);

                        if(fichero.exists()){
                            row.setItemDescription("1");
                        } else {
                            row.setItemDescription("0");
                        }

                        itemsArrayList.add(row);
                    }
                }

                recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);

                mAdapter = new CustomListAdapter(itemsArrayList, DescargaInsumos.this);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(DescargaInsumos.this, LinearLayoutManager.VERTICAL));

                recyclerView.setAdapter(mAdapter);


            }

            @Override
            public void onError() {
                ArrayList<Item> itemsArrayList = new ArrayList<Item>();
                File[] files = directory.listFiles();

                for (int i = 0; i < files.length; i++) {
                    Item row = new Item(files[i].getName(), "");
                    itemsArrayList.add(row);
                }


                recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);

                mAdapter = new CustomListAdapter(itemsArrayList, DescargaInsumos.this);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(DescargaInsumos.this, LinearLayoutManager.VERTICAL));

                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onSuccess(String result, String duration, String distance) {

            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(DescargaInsumos.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void refresh() {
        finish();
        startActivity(getIntent());
    }


}