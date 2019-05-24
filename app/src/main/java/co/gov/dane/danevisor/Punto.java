package co.gov.dane.danevisor;

import com.google.android.gms.maps.model.LatLng;

public class Punto {

    private LatLng punto;

    Punto(LatLng punto) {
        this.punto=punto;
    }

    public LatLng getPunto(){
        return punto;
    }

    public double getLat() {
        return punto.latitude;
    }

    public double getLng() {
        return punto.longitude;
    }

}
