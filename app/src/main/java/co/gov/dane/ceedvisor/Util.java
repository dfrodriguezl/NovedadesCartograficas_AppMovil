package co.gov.dane.ceedvisor;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class Util {


    Context context;
    MainActivity main;


    Util(Context context,MainActivity main){
        this.context=context;
        this.main=main;
    }

        public void generarLabel(String centroide,String label,String id){

            try {


                WKTReader reader = new WKTReader();
                Coordinate[] coord1=reader.read(centroide).getCoordinates();

                MarkerOptions opts1=new MarkerOptions();
                LatLng punto;
                IconGenerator iconFactory = new IconGenerator(main);

                if(id.equals("ENA_LABEL")){
                    iconFactory.setTextAppearance(R.style.iconGenTextENA);
                }else{
                    iconFactory.setTextAppearance(R.style.iconGenText);
                }

                iconFactory.setBackground(main.getResources().getDrawable(R.drawable.borde_boton));


                for(int j=0;j<coord1.length;j++){
                    Double lat=coord1[j].y;
                    Double lon=coord1[j].x;
                    punto=new LatLng(lat,lon);
                    opts1.position(punto);
                    opts1.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(String.valueOf(label))));
                    opts1.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                }

                main.label.add(main.mMap.addMarker(opts1));
                main.label.get(main.label.size()-1).setTag(id+"label");


            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        public void borrarLabel(String id){


            for(int i=0;i<main.label.size();i++){

                if(main.label.get(i).getTag().equals(id+"label")){

                    main.label.get(i).remove();
                    main.label.remove(i);

                    break;
                }

            }



        }


}
