package irrationalstudio.com.shareit;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import constants.ApplicationConstants;
import util.UtilClass;

/**
 * Created by prasadsawant on 4/22/16.
 */
public class NameActivity extends AppCompatActivity {

    private static final String TAG = NameActivity.class.getName();
    private Button btnGo;
    private EditText etFirstName, etLastName;
    private String firstName, lastName;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_name);

        etFirstName = (EditText) findViewById(R.id.et_first_name);
        etLastName = (EditText) findViewById(R.id.et_last_name);

        btnGo = (Button) findViewById(R.id.btn_go);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();

                if (firstName != null || !firstName.equals("") ||
                        lastName != null || !lastName.equals("")) {

                    Intent emailContactActivityIntent = new Intent(NameActivity.this, EmailContactActivity.class);
                    emailContactActivityIntent.putExtras(emailContactActivityIntent.getExtras());
                    emailContactActivityIntent.putExtra(ApplicationConstants.EXTRA_FIRST_NAME, firstName);
                    emailContactActivityIntent.putExtra(ApplicationConstants.EXTRA_LAST_NAME, lastName);
                    startActivity(emailContactActivityIntent);



                } else {

                    UtilClass.showToast(getApplicationContext(), getString(R.string.no_name));

                }

            }
        });
    }
}
