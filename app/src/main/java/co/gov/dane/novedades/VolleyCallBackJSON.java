package co.gov.dane.novedades;

import org.json.JSONObject;

public interface VolleyCallBackJSON {
    void onSuccess(JSONObject result);
    void onError();

    void onSuccess(String result, String duration, String distance);
}
