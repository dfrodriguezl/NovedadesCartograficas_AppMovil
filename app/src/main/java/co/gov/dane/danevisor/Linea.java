package co.gov.dane.danevisor;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Linea {

    private List<LatLng> linea;

    Linea(List<LatLng> linea) {
        this.linea=linea;
    }

    public List<LatLng> getLinea(){
        return linea;
    }


}