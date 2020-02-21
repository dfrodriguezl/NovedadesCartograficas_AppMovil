package co.gov.dane.novedades;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.maps.android.ui.IconGenerator;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.Arrays;
import java.util.List;

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

                iconFactory.setBackground(main.getResources().getDrawable(R.drawable.borde_transparente));


                for(int j=0;j<coord1.length;j++){
                    Double lat=coord1[j].y;
                    Double lon=coord1[j].x;
                    punto=new LatLng(lat,lon);
                    opts1.position(punto);
                    opts1.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(String.valueOf(label))));
                     float anchor = (float) (iconFactory.getAnchorU()-0.55);
                    float anchor1= (float) (iconFactory.getAnchorV()+0.5);

                    opts1.anchor(anchor, anchor1);
                }

                main.label.add(main.mMap.addMarker(opts1));
                main.label.get(main.label.size()-1).setTag(id+"label");


            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
    public void generarLabelLinea(String centroide,String label,String id){

        try {


            WKTReader reader = new WKTReader();
            Coordinate[] coord1=reader.read(centroide).getCoordinates();

            MarkerOptions opts1=new MarkerOptions();
            LatLng punto;
            IconGenerator iconFactory = new IconGenerator(main);

            iconFactory.setTextAppearance(R.style.iconGenTextLinea);

            iconFactory.setBackground(main.getResources().getDrawable(R.drawable.borde_transparente));


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

    public Bitmap bitmapSizeByScall(Bitmap bitmapIn, float scall_zero_to_one_f) {

        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scall_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scall_zero_to_one_f), false);

        return bitmapOut;
    }

    public List<PatternItem> LineStyle(String style){
        List<PatternItem> PATTERN_POLYGON_ALPHA = null;

        if(style.equals("CONT")){


        }else if(style.equals("DOT")){
            int PATTERN_DASH_LENGTH_PX = 30;
            int PATTERN_GAP_LENGTH_PX = 30;
            PatternItem DOT = new Dot();
            PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
            PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
            PATTERN_POLYGON_ALPHA = Arrays.asList(DOT,GAP, DASH,GAP);

        }else if(style.equals("DASH")){
            int PATTERN_DASH_LENGTH_PX = 20;
            int PATTERN_GAP_LENGTH_PX = 20;
            PatternItem DOT = new Dot();
            PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
            PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
            PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

        }

        return PATTERN_POLYGON_ALPHA;

    }



}
