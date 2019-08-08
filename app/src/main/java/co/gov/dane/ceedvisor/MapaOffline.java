package co.gov.dane.ceedvisor;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.File;

public class MapaOffline{

    private String nombre;
    private boolean isEnable;
    private TileOverlay tileOverlay;
    int transparency;

    MapaOffline(String nombre,boolean activo,String url,int transparency){
        this.nombre=nombre;
        this.isEnable=activo;
        this.transparency=transparency;

        TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(url), 256, 256);

        GoogleMap mapa=MainActivity.mMap;

        tileOverlay = mapa.addTileOverlay(
                new TileOverlayOptions()
                        .tileProvider(tileProvider).zIndex(-1));
        tileOverlay.setVisible(false);

    }
    public String getnombre(){
        return this.nombre;
    }
    public void setnombre(String name){
        this.nombre = name;
    }
    public boolean isEnabled(){
        return this.isEnable;
    }
    public void setTransparency(Float transparency){
        int tra=Math.round(transparency*10);
        this.transparency=tra;
        tileOverlay.setTransparency(1-transparency);
    }
    public int getTransparency(){
        return this.transparency;
    }
    public void setEnabled(boolean value){
        this.isEnable = value;
        this.tileOverlay.setVisible(value);
    }
}