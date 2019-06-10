package co.gov.dane.danevisor;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class Mensajes {

    Context context;

    Mensajes(Context context){
        this.context=context;

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
