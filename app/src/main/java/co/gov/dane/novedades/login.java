package co.gov.dane.novedades;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import co.gov.dane.novedades.EstructuraDataBase.Estructura;

public class login extends AppCompatActivity {

    private Session session;
    private EditText username,password;
    private Spinner spinner_investigacion;

    Mensajes mitoast;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.login);

        mitoast =new Mensajes(login.this);


        username=(EditText) findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        spinner_investigacion=(Spinner) findViewById(R.id.spinner_investigacion);




        ArrayAdapter<String> array_investigaciones =  new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.investigacion));

        spinner_investigacion.setAdapter(array_investigaciones);

        Button btn_login= (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final String investigacion = spinner_investigacion.getSelectedItem().toString();
                final String usuario = username.getText().toString();
                final String clave = password.getText().toString();

                Controlador con=new Controlador(login.this);

                con.getUsers(usuario,clave,new VolleyCallBack() {
                    @Override
                    public void onSuccess(String response) {

                        try {

                            JSONObject obj = new JSONObject(response);
                            String estado=obj.getString("estado");

                            if(estado.equals("true")){

                                session = new Session(login.this);
                                session.setusename(username.getText().toString(),"Usuario: "+usuario,"1",investigacion);
                                Intent mainIntent = new Intent(login.this,MainActivity.class);
                                login.this.startActivity(mainIntent);
                                login.this.finish();

                            }else{
                                Mensajes mitoast =new Mensajes(login.this);
                                mitoast.generarToast("Datos incorrectos");

                            }

                        } catch (Throwable t) {

                        }



                    }
                });



                /*

        */



            }
        });


    }


}