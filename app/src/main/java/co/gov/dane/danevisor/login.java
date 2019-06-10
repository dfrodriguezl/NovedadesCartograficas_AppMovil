package co.gov.dane.danevisor;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import co.gov.dane.danevisor.EstructuraDataBase.Estructura;

public class login extends AppCompatActivity {


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.login);

        Button btn_login= (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                IntentIntegrator integrator = new IntentIntegrator(login.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Escanear Código QR");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Operación cancelada", Toast.LENGTH_LONG).show();
            } else {


                try {
                    JSONObject json=new JSONObject(result.getContents());

                    SpatiaLite db=new SpatiaLite(login.this);

                    org.spatialite.database.SQLiteDatabase sp=db.getWritableDatabase();


                    String columns[] = new String[]{Estructura.UsuarioEntry.CLAVE};
                    String selection = Estructura.UsuarioEntry.USUARIO + " LIKE ?"; // WHERE id LIKE ?
                    String selectionArgs[] = new String[]{json.get("usuario").toString()};

                    Cursor c = sp.query(
                            Estructura.UsuarioEntry.TABLE_NAME,
                            columns,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );

                    while(c.moveToNext()){
                        String clave = c.getString(c.getColumnIndex(Estructura.UsuarioEntry.CLAVE));
                        if(clave.equals(json.get("clave").toString())){
                            Intent mainIntent = new Intent(login.this,MainActivity.class);
                            login.this.startActivity(mainIntent);
                            login.this.finish();
                        }else{
                            Toast.makeText(this, "QR no Válido", Toast.LENGTH_LONG).show();

                        }
                    }


                    sp.close();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "QR no Válido", Toast.LENGTH_LONG).show();
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}