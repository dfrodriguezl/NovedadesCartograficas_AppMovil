package co.gov.dane.novedades;

import android.os.Environment;

import java.io.File;

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


        final String archivo = Environment.getExternalStorageDirectory()+ File.separator + "Editor Dane" + File.separator +"db"+ File.separator +getItemName();

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