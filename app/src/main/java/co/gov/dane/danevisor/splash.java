package co.gov.dane.danevisor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class splash extends Activity {
    private Session session;
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);




        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                session = new Session(splash.this);

                String usuario=session.getusename();


                if(usuario.equals("")){

                    Intent mainIntent = new Intent(splash.this,login.class);
                    splash.this.startActivity(mainIntent);
                    splash.this.finish();

                }else{

                    Intent mainIntent = new Intent(splash.this,MainActivity.class);
                    splash.this.startActivity(mainIntent);
                    splash.this.finish();


                }


            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}