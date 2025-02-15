package co.gov.dane.novedades;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class CeedDB  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ceed.db";

    public static String  ruta_db=Environment.getExternalStorageDirectory() + File.separator + "Editor Nc"+ File.separator+"db"+File.separator+DATABASE_NAME;



    public CeedDB(Context context, String ruta) {
        super(context, ruta , null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public List<String> getDpto() {
        List<String> labels = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select distinct DPTO,TEXTO from sector  INNER JOIN txt_dpto on  substr('00'||DPTO,-2)=txt_dpto.id order by DPTO ASC", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0)+"-"+res.getString(1));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> getMpio(String dpto) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select distinct MPIO,TEXTO from sector  INNER JOIN txt_mpio on substr('00'||DPTO,-2) || substr('000'||MPIO,-3)=txt_mpio.id where DPTO="+dpto+" order by MPIO ASC;", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0)+"-"+res.getString(1));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> getClase(String dpto,String mpio) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select distinct CLASE from sector  where DPTO="+dpto+" AND MPIO="+mpio+" order by CLASE ASC;", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> get_SectorR(String dpto, String mpio, String clase) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select distinct SET_R from sector  where DPTO="+dpto+" AND MPIO="+mpio+" and CLASE="+clase+" order by SET_R ASC", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> get_SeccionR(String dpto,String mpio, String clase, String sectorr) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select distinct SEC_R from sector  where DPTO="+dpto+" AND MPIO="+mpio+" and CLASE="+clase+" and SET_R ="+sectorr+" order by SEC_R ASC", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> get_CPob(String dpto,String mpio, String clase, String sectorr,String seccionr) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select distinct C_POB,TEXTO from sector INNER JOIN txt_cpob on  substr('00'||DPTO,-2) || substr('000'||MPIO,-3) || substr('000'||C_POB,-3)=txt_cpob.id where DPTO="+dpto+" AND mpio="+mpio+" and CLASE="+clase+" and SET_R ="+sectorr+" and SEC_R="+seccionr+" order by C_POB ASC", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0)+"-"+res.getString(1));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> get_SetU(String dpto, String mpio, String clase, String sectorr,String seccionr,String cpob) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select distinct SET_U from sector where DPTO="+dpto+" AND mpio="+mpio+" and CLASE="+clase+" and SET_R ="+sectorr+" and SEC_R="+seccionr+"  and  C_POB="+cpob+" order by SET_U ASC", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public LatLng get_Coordenadas(String dpto, String mpio, String clase, String sectorr, String seccionr, String cpob, String setu) {

        LatLng coord = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select  LAT,LON from sector where DPTO="+dpto+" AND mpio="+mpio+" and CLASE="+clase+" and SET_R ="+sectorr+" and SEC_R="+seccionr+"  and  C_POB="+cpob+" and  SET_U="+setu+" LIMIT 1", null );

        while (res.moveToNext()) {
            coord=new LatLng(Double.parseDouble(res.getString(0)),Double.parseDouble(res.getString(1)));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return coord;
    }



    public List<String> get_NovedadGrupo() {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select nombre from novedad_grupo", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> get_NovedadGrupoLinea() {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select grupo||' - '||nombre  from novedad_linea_grupo", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> get_NovedadSubGrupo(String id_grupo) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select nombre from novedad_subgrupo where grupo='"+id_grupo+"'", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<String> get_NovedadGrupoPoligono() {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select codigo ||' - '||nombre  from novedad_poligono", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }


    public List<String> get_NovedadSubGrupoLinea(String id_grupo) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select codigo||' - '||nombre from novedad_linea where grupo like'%"+id_grupo+"%' order by codigo" , null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }




    public List<String> get_NovedadItem(String id_grupo,String id_subgrupo) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select nombre from novedad_item where grupo='"+id_grupo+"' and subgrupo='"+id_subgrupo+"'", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }

    public String get_NovedadImagen(String id_grupo,String id_subgrupo,String id_item) {

        String imagen="";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select imagen from novedad_item where grupo='"+id_grupo+"' and subgrupo='"+id_subgrupo+"' and codigo='"+id_item+"'", null );

        while (res.moveToNext()) {
            imagen=res.getString(0);

        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return imagen;
    }

    public String get_LineaColor(String codigo) {

        String color="#5E5E5E";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select LINE_COLOR from novedad_linea where grupo||codigo='"+codigo+"' " , null );

        while (res.moveToNext()) {
            color=res.getString(0);

        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return color;
    }

    public String get_PoligonoColor(String codigo) {

        String color="#5E5E5E";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select FILL_COLOR from novedad_poligono where codigo='"+codigo+"' " , null );

        while (res.moveToNext()) {
            color=res.getString(0);

        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return color;
    }



    public String get_LineaStyle(String codigo) {

        String tipo="CONT";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select LINE_TYPE from novedad_linea where grupo||codigo='"+codigo+"' " , null );

        while (res.moveToNext()) {
            tipo=res.getString(0);

        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return tipo;
    }

    public String get_grupoPoligono(String codigo) {

        String tipo="1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select grupo from novedad_linea where codigo='"+codigo+"' " , null );

        while (res.moveToNext()) {
            tipo=res.getString(0);

        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return tipo;
    }

    public String getNombreDpto(String dpto) {
        String label = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select distinct TEXTO from txt_dpto where txt_dpto.id = '" +  dpto + "'", null );

        while (res.moveToNext()) {
            label = res.getString(0);
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return label;
    }

    public String getNombreMpio(String mpio) {
        String label = "";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select distinct TEXTO from txt_mpio where txt_mpio.id = '" +  mpio + "'", null );

        while (res.moveToNext()) {
            label = res.getString(0);
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return label;
    }

    public List<String> getNombreCPob(String dpto,String mpio, String clase, String sectorr,String seccionr) {

        List<String> labels = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select distinct C_POB,TEXTO from sector INNER JOIN txt_cpob on  substr('00'||DPTO,-2) || substr('000'||MPIO,-3) || substr('000'||C_POB,-3)=txt_cpob.id where DPTO="+dpto+" AND mpio="+mpio+" and CLASE="+clase+" and SET_R ="+sectorr+" and SEC_R="+seccionr+" order by C_POB ASC", null );

        while (res.moveToNext()) {
            labels.add(res.getString(0)+"-"+res.getString(1));
        }
        // closing connection
        res.close();
        db.close();

        // returning lables
        return labels;
    }


}
