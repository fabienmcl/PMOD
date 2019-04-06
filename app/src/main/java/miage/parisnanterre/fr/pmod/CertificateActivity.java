package miage.parisnanterre.fr.pmod;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CertificateActivity extends AppCompatActivity {

    EditText urlTXT;
    EditText editTextBLABLA;
    Button check;
    Button loadsave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        getSupportActionBar().hide();

        urlTXT = (EditText) findViewById(R.id.editTextURL);
        editTextBLABLA = (EditText) findViewById(R.id.editTextBLABLA);
        check = (Button) findViewById(R.id.buttonCheck);
        loadsave = (Button) findViewById(R.id.buttonLoadAdd);

        editTextBLABLA.setText(" Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus pharetra sollicitudin mollis. Nam eu ultrices felis. Quisque ultricies diam in quam ornare viverra. Donec quis nisl tincidunt, aliquam diam eu, molestie odio. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam auctor nec turpis eget ornare. Nam porta risus massa, sit amet feugiat dui egestas vitae. Vivamus tempor hendrerit porttitor. Cras sagittis molestie eros. Vivamus auctor semper tortor non feugiat. Proin nulla sem, tincidunt eu erat id, mollis suscipit lorem. Nam elit dolor, tristique vel lacus quis, posuere posuere enim. Aenean ut hendrerit neque, sed egestas dolor.\n" +
                "\n" +
                "Proin nec bibendum ex. Curabitur mollis ultrices rhoncus. Praesent ante quam, sodales quis dignissim sed, venenatis quis ante. Etiam suscipit nibh augue, eget efficitur leo bibendum nec. Duis quis varius lectus, sit amet dignissim ipsum. In sit amet felis volutpat, porttitor nisi et, tempus erat. Proin ac aliquet velit. Duis vel ante scelerisque, lobortis metus vitae, sagittis neque. Nullam sed magna auctor, condimentum leo a, consequat urna. Sed quis iaculis dui. Suspendisse semper turpis a orci dictum, nec rutrum velit cursus. Nam non blandit augue. ");
    }

    public void loadadd(View v){


    }
}
