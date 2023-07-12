package co.gov.dane.novedades;

import android.os.Build;
import android.os.Environment;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class Item {
    private String itemName;
    private String itemDescription;
    private boolean checked;

    public Item(String name, String description) {
        this.itemName = name;
        this.itemDescription = description;
        this.checked=false;
    }

    public String getItemName() {
        return this.itemName;
    }

    public int getItemDescription() {


        String archivo = null;

        if(Build.VERSION_CODES.KITKAT > Build.VERSION.SDK_INT){
            archivo= Environment.getExternalStorageDirectory()+ File.separator + "Editor Nc" + File.separator +"db"+ File.separator +getItemName();
        }else{
            archivo= Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + File.separator + "Editor Nc" + File.separator +"db"+ File.separator +getItemName();
        }

        File fichero = new File(archivo);

        if(fichero.exists()){
            return 1;
        }else{
            return 0;
        }



    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }


}