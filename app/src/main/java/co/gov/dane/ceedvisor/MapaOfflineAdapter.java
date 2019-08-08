package co.gov.dane.ceedvisor;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import java.util.ArrayList;

public class MapaOfflineAdapter extends ArrayAdapter<String> {

    private Activity activity;
    ArrayList<MapaOffline> mapas;

    public MapaOfflineAdapter(Activity activity, ArrayList<MapaOffline> mensajes) {
        super(activity, R.layout.adapter_mapa_offline);
        this.activity = activity;
        this.mapas = mensajes;
    }

    static class ViewHolder {
    }

    public int getCount() {
        return mapas.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView,
                        final ViewGroup parent) {

        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = activity.getLayoutInflater();
            view = inflator.inflate(R.layout.adapter_mapa_offline, null);
            final ViewHolder viewHolder = new ViewHolder();

            final Switch description = (Switch) view.findViewById(R.id.swicth_mapas_offline);
            final SeekBar transparency= (SeekBar) view.findViewById(R.id.seekBar);

            description.setText(mapas.get(position).getnombre());

            Boolean activo=mapas.get(position).isEnabled();

            description.setChecked(activo);

            int transparecia =mapas.get(position).getTransparency();

            transparency.setProgress(transparecia);

            description.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {


                    MainActivity.listado_mapas_offline.get(position).setEnabled(isChecked);

                }
            });


            transparency.setMax(10);
            transparency.setOnSeekBarChangeListener(
                            new SeekBar
                                    .OnSeekBarChangeListener() {

                                // When the progress value has changed
                                @Override
                                public void onProgressChanged(
                                        SeekBar seekBar,
                                        int progress,
                                        boolean fromUser)
                                {

                                    MainActivity.listado_mapas_offline.get(position).setTransparency(getConvertedValue(progress));



                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar)
                                {

                                    // This method will automatically
                                    // called when the user touches the SeekBar
                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar)
                                {

                                    // This method will automatically
                                    // called when the user
                                    // stops touching the SeekBar
                                }
                            });







        }else{
            view = (View) convertView;
        }



        return view;
    }

    public float getConvertedValue(int intVal){
        float floatVal = (float) 0.0;
        floatVal = .1f * intVal;
        return floatVal;
    }

}