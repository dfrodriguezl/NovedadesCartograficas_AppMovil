package co.gov.dane.novedades;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DescargaInsumos extends AppCompatActivity {



    private RecyclerView recyclerView;
    private CustomListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descarga_insumos);








        Controlador con=new Controlador(DescargaInsumos.this);

        con.getInfo(new VolleyCallBackJSON() {
            @Override
            public void onSuccess(JSONObject result) {


                ArrayList<Item> itemsArrayList =new ArrayList<Item>();

                try {
                JSONArray key = result.names ();
                for (int i = 0; i < key.length (); ++i) {
                    String keys =  key.getString (i);

                    String value = result.getString (keys);

                    Item row = new Item(value,"");

                    itemsArrayList.add(row);

                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);

                mAdapter = new CustomListAdapter(itemsArrayList,DescargaInsumos.this);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(DescargaInsumos.this, LinearLayoutManager.VERTICAL));

                recyclerView.setAdapter(mAdapter);


            }

        });


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DescargaInsumos.this, MainActivity.class);
        startActivity(i);
        finish();
    }


}