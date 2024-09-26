package co.gov.dane.novedades;


import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.MyViewHolder> {

    private ArrayList<Item> formulario;
    private Context context;
    private Session session;
    private ProgressBar progressBar;

    private AlertDialog dialogProgress;
    private TextView textoProgress;

    public CustomListAdapter(ArrayList<Item> items, Context context) {
        this.formulario = items;
        this.context = context;
        this.session = new Session(context);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        final Item item = formulario.get(i);


        holder.nombre.setText(item.getItemName());

        if (item.getItemDescription().equals("1")) {
            holder.descripcion.setText("Descargado en el equipo");
            holder.checkBox.setEnabled(true);
            holder.descripcion.setTextColor(ContextCompat.getColor(context, R.color.verde));
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.descripcion.setTextColor(ContextCompat.getColor(context, R.color.raton));
            holder.descripcion.setText("Sin descargar");
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.item_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String archivo = formulario.get(i).getItemName();
                String ruta = null;

                if (Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT) {
                    ruta = Environment.getExternalStorageDirectory() + File.separator + "Editor Nc" + File.separator + "db";
                } else {
                    ruta = context.getExternalFilesDir("db").getAbsolutePath();
                }

                FileDownloader fileDownloader = new FileDownloader(context);
                showDialogProgress();

                fileDownloader.downloadFile("https://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/file_get.php?name=" + archivo,
                        ruta,
                        archivo,
                        new FileDownloader.FileDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                dialogProgress.dismiss();
                                item.setChecked(true);
                                session.setusename(formulario.get(i).getItemName());
//                                ((DescargaInsumos) context).finish();
                            }

                            @Override
                            public void onDownloadFailed(String errorMessage) {
                                dialogProgress.dismiss();
                                Toast.makeText(context, "Error en la descarga: " + errorMessage, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onProgressUpdate(int progress) {
                                progressBar.setProgress(progress);
                                textoProgress.setText(String.format("%d %%", progress));
                            }
                        });


//                DownloadFileFromURL mTask = new DownloadFileFromURL(context, null);
//
//                try {
//                    mTask.execute("https://geoportal.dane.gov.co/laboratorio/serviciosjson/edicion_mobile/file_get.php?name=" + archivo).get();
//                } catch (ExecutionException e) {
//                    throw new RuntimeException(e);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }


            }
        });


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    session = new Session(context);
                    session.setusename(formulario.get(i).getItemName());
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return formulario.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_view_row_items, parent, false);

        return new MyViewHolder(itemView);
    }

    private void showDialogProgress(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        final View mView = inflater.inflate(R.layout.dialog_progress_bar, null);
        progressBar = mView.findViewById(R.id.progress_bar_descarga);
        textoProgress = mView.findViewById(R.id.texto_porcentaje);

        mBuilder.setView(mView);

        dialogProgress = mBuilder.create();
        dialogProgress.setTitle("Descargando datos...");
        dialogProgress.show();
        dialogProgress.setCanceledOnTouchOutside(false);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, descripcion;
        public ImageView item_download;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.item_nombre);
            descripcion = (TextView) view.findViewById(R.id.item_descripcion);
            item_download = (ImageView) view.findViewById(R.id.item_download);
            checkBox = (CheckBox) view.findViewById(R.id.activar);
        }

    }

}


