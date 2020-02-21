package co.gov.dane.novedades;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Mensajes {

    Context context;

    Mensajes(Context context){
        this.context=context;

    }

    public void generarToast(String mensaje,String tipo){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view = inflater.inflate( R.layout.toast, null );

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(mensaje);

        if(tipo.equals("error")){
            ImageView imageView6=(ImageView) view.findViewById(R.id.imageView6);
            imageView6.setImageResource(R.drawable.ic_close);
            text.setTextColor(context.getResources().getColor(R.color.rojo));
        }


        android.widget.Toast toast = new android.widget.Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(android.widget.Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();

    }




    public void generarToast(String mensaje){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view = inflater.inflate( R.layout.toast, null );

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(mensaje);

        android.widget.Toast toast = new android.widget.Toast(context);
        toast.setDuration(android.widget.Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();

    }

    public void generarToastMapa(String mensaje){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view = inflater.inflate( R.layout.toast, null );

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(mensaje);

        android.widget.Toast toast = new android.widget.Toast(context);
        toast.setGravity(Gravity.CENTER|Gravity.LEFT, 0, -50);
        toast.setDuration(android.widget.Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();

    }


}
