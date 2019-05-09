package co.gov.dane.danevisor;


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

public class login extends AppCompatActivity {


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.login);

        Button btn_login= (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText user_name= (EditText) findViewById(R.id.user_name);
                EditText user_password= (EditText) findViewById(R.id.user_password);
                TextView aviso_login=(TextView) findViewById(R.id.aviso_login);


                String usuario_nombre=user_name.getText().toString();
                String usuario_password=user_password.getText().toString();

                if(usuario_nombre.isEmpty() || usuario_password.isEmpty()){
                    aviso_login.setVisibility(View.VISIBLE);
                    aviso_login.setText("Datos incompletos");

                }else{
                    //Abrimos la base de datos 'DBUsuarios' en modo escritura
                    dataBase usdbh =new dataBase(login.this, "GeoEditor", null, 1);

                    SQLiteDatabase db = usdbh.getWritableDatabase();

                    //Si hemos abierto correctamente la base de datos
                    if(db != null)
                    {
                        Cursor c = db.rawQuery(" SELECT * FROM Usuarios WHERE usuario='"+usuario_nombre+"' and  clave='"+usuario_password+"'", null);

                        if(c.getCount() == 0){
                            aviso_login.setVisibility(View.VISIBLE);
                            aviso_login.setText("Usuario ó contraseña incorrectos");
                        }else{
                            if (c.moveToFirst()) {
                                //Recorremos el cursor hasta que no haya más registros
                                do {
                                    String codigo= c.getString(0);
                                    String usuario = c.getString(1);
                                    String clave = c.getString(2);
                                    String nombre = c.getString(3);
                                    String vigencia = c.getString(4);

                                    Intent intent = new Intent(login.this, MainActivity.class);
                                    startActivity(intent);

                                } while(c.moveToNext());
                            }
                        }





                        //Cerramos la base de datos
                        db.close();
                    }
                }


            }
        });


    }
}