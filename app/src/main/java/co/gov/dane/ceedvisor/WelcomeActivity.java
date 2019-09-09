package co.gov.dane.ceedvisor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.honu.aloha.BaseWelcomeActivity;
import com.honu.aloha.PageDescriptor;



public class WelcomeActivity extends BaseWelcomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSkipButton = Button.class.cast(findViewById(R.id.skip));
        mSkipButton.setText("Saltar");
        mNextButton = Button.class.cast(findViewById(R.id.next));

        mNextButton.setText("Siguiente");
        mDoneButton = Button.class.cast(findViewById(R.id.done));
        mDoneButton.setText("Hecho");


        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }


    @Override
    public void createPages() {
        addPage(new PageDescriptor(R.string.titulo1, R.string.contenido1, R.drawable.about1, R.color.transicion1));
        addPage(new PageDescriptor(R.string.titulo2, R.string.contenido2, R.drawable.about2, R.color.transicion1));
        addPage(new PageDescriptor(R.string.titulo3, R.string.contenido3, R.drawable.about3, R.color.transicion1));
        addPage(new PageDescriptor(R.string.titulo4, R.string.contenido4, R.drawable.about4, R.color.transicion1));
        addPage(new PageDescriptor(R.string.titulo5, R.string.contenido5, R.drawable.about5, R.color.transicion1));
        addPage(new PageDescriptor(R.string.titulo6, R.string.contenido6, R.drawable.about6, R.color.transicion1));
    }
}
