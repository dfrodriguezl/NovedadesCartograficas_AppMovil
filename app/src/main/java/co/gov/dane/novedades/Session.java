package co.gov.dane.novedades;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setusename(String usename,String nombre,String rol, String investigacion) {
        prefs.edit().putString("username", usename).commit();
        prefs.edit().putString("nombre", nombre).commit();
        prefs.edit().putString("rol", rol).commit();
        prefs.edit().putString("investigacion", investigacion).commit();
        prefs.edit().putBoolean("capa_manzanas", true).commit();
        prefs.edit().putBoolean("capa_secciones_rurales", true).commit();
    }

    public void setusename(String geom){
        prefs.edit().putString("geom", geom).commit();
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
    public String getInvestigacion() {
        String investigacion = prefs.getString("investigacion","");
        return investigacion;
    }
    public String getGeom(){
        String geom = prefs.getString("geom","");
        return geom;
    }

    public void borrarSession(){
        prefs.edit().clear().commit();
    }

    public Boolean[] getVisibilidadCapas(){
        Boolean manzanas = prefs.getBoolean("capa_manzanas",true);
        Boolean secciones_rurales = prefs.getBoolean("capa_secciones_rurales",true);
        return new Boolean[]{manzanas, secciones_rurales};
    }

    public void setCapaManzanas(Boolean manzanas){
        prefs.edit().putBoolean("capa_manzanas", manzanas).commit();
    }

    public void setCapaSecciones(Boolean secciones_rurales){
        prefs.edit().putBoolean("capa_secciones_rurales", secciones_rurales).commit();
    }
}
