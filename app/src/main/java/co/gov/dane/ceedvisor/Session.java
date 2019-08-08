package co.gov.dane.ceedvisor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setusename(String usename,String nombre,String rol) {
        prefs.edit().putString("username", usename).commit();
        prefs.edit().putString("nombre", nombre).commit();
        prefs.edit().putString("rol", rol).commit();
    }

    public String getusename() {
        String usename = prefs.getString("username","");
        return usename;
    }
    public String getnombre() {
        String nombre = prefs.getString("nombre","");
        return nombre;
    }
    public int getrol() {
        String rol = prefs.getString("rol","0");
        return Integer.parseInt(rol);
    }


    public void borrarSession(){
        prefs.edit().clear().commit();
    }
}
