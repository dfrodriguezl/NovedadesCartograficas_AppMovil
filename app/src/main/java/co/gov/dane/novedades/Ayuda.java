package co.gov.dane.novedades;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Ayuda extends Activity {
    private Session session;

    private WebView pagina;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);

        setContentView(R.layout.ayuda);

        pagina=findViewById(R.id.web);

        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) pagina.getLayoutParams();

        p.leftMargin = 5;
        p.rightMargin = 5;

        pagina.setLayoutParams(p);


        pagina.getSettings().setJavaScriptEnabled(true);
        pagina.getSettings().setLoadsImagesAutomatically(true);
        pagina.getSettings().setDomStorageEnabled(true);
        pagina.loadUrl("file:///android_asset/index.html");
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