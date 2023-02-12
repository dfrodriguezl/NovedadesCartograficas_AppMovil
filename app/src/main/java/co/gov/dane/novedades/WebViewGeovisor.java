package co.gov.dane.novedades;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;

public class WebViewGeovisor extends Activity {
    private WebView pagina;
    private String url;
    private Session session;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.web_view_geovisor);
        session = new Session(this);
        pagina = findViewById(R.id.webview);
        url = "https://geoportal.dane.gov.co/geovisores/territorio/cartografia-colaborativa-mgn/?email=" + session.getusename();


        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) pagina.getLayoutParams();

        p.leftMargin = 5;
        p.rightMargin = 5;

        pagina.setLayoutParams(p);


        pagina.getSettings().setJavaScriptEnabled(true);
        pagina.getSettings().setLoadsImagesAutomatically(true);
        pagina.getSettings().setDomStorageEnabled(true);
        pagina.loadUrl(this.url);
        pagina.getSettings().setLoadWithOverviewMode(true);
        pagina.getSettings().setUseWideViewPort(true);
        pagina.getSettings().setBuiltInZoomControls(true);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }
}
