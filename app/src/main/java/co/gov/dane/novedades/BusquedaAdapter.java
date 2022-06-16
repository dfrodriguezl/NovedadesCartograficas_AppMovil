package co.gov.dane.novedades;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.util.ArrayList;

public class BusquedaAdapter extends ArrayAdapter implements Filterable {

    private Activity activity;
    ArrayList<Busqueda> datos;
    ArrayList<Busqueda> datos_filtrados;

    public BusquedaAdapter(Activity activity, ArrayList<Busqueda> datos) {

        super(activity, R.layout.adapter_busqueda);
        this.activity = activity;
        this.datos = datos;
        this.datos_filtrados = new ArrayList<Busqueda>(datos);


    }
    static class ViewHolder {
    }
    public int getCount() {
        return datos_filtrados.size();
    }

    public Busqueda getItem(int position) {
        return datos_filtrados.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView,
                        final ViewGroup parent) {

        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = activity.getLayoutInflater();
            view = inflator.inflate(R.layout.adapter_busqueda, null);
            final ViewHolder viewHolder = new ViewHolder();


        }else{
            view = (View) convertView;
        }
        TextView description = (TextView) view.findViewById(R.id.resultado_buscar);

        description.setText(datos_filtrados.get(position).get_nombre());

        LinearLayout linear_item_busqueda=view.findViewById(R.id.linear_item_busqueda);

        linear_item_busqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String centro=datos_filtrados.get(position).get_centroide();

                WKTReader reader = new WKTReader();
                Coordinate[] coord1= new Coordinate[0];
                try {
                    coord1 = reader.read(centro).getCoordinates();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                LatLng punto = null;

                for(int j=0;j<coord1.length;j++){
                    Double lat=coord1[j].y;
                    Double lon=coord1[j].x;
                    punto=new LatLng(lat,lon);
                }

                LinearLayout linear_resultados_busqueda =activity.findViewById(R.id.linear_resultados_busqueda);

                linear_resultados_busqueda.setVisibility(View.GONE);


                MainActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(punto, 18));
            }
        });
        return view;

    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filtrobusqueda;
    }

    private Filter filtrobusqueda = new Filter() {
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results=new FilterResults();

        ArrayList<Busqueda> sugeridos= new ArrayList<>();

        if(constraint==null || constraint.length()==0){
            sugeridos.addAll(datos);
        }else{
            String pattern=constraint.toString().toLowerCase().trim();

            for(Busqueda item: datos){
                if(item.get_nombre().toLowerCase().contains(pattern)){
                    sugeridos.add(item);
                }
            }

        }

        results.values=sugeridos;
        results.count=sugeridos.size();

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {


        datos_filtrados= ((ArrayList<Busqueda>) results.values);

        notifyDataSetChanged();
        clear();
        addAll((ArrayList)datos_filtrados);
        notifyDataSetInvalidated();




    }
};




}
