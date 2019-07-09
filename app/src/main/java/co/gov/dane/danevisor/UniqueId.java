package co.gov.dane.danevisor;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UniqueId {

    private static String sID = null;
    private static final String SHARED_PREF_KEY = "id_app";
    private static final String ID_KEY = "id";


    public  String id(Context context) {
        if (sID == null) {
            SharedPreferences pref = context.getSharedPreferences(
                    SHARED_PREF_KEY, 0);

            sID = pref.getString(ID_KEY, "");

            if (sID == "") {
                sID = generateAndStoreUserId(pref);
            }

        }
        return sID;
    }

    private String generateAndStoreUserId(SharedPreferences pref) {
        String id = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ID_KEY, id);
        editor.commit();
        return id;
    }

}