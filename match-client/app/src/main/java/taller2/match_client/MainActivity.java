package taller2.match_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int mailButtonPressedNumber = 0;
    private int passwordButtonPressedNumber = 0;
    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Magic Match");
    }

    // when an user login, if the userName and the password are correct, the app activity is created.
    public void loginOnClick(View v) {
        Button button = (Button) v;
        TextView userMail = (TextView)findViewById(R.id.userMail);
        TextView userPassword = (TextView)findViewById(R.id.userPassword);

        // TODO: Chequear contraseña y usuario con Server
        if(userMail.getEditableText().toString().compareTo("lucas") == 0 &
                userPassword.getEditableText().toString().compareTo("crack") == 0) {
            Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
            startActivity(startAppActivity);
        }
    }

    // when pressed the Register Free link, the register activity is created
    public void registerOnClick(View v) {
        Intent startRegisterActivity = new Intent(this, RegisterActivity.class);
        startActivity(startRegisterActivity);
    }

    // when clicked in mailIdButton, its content is deleted
    public void mailOnClick(View v) {
        EditText mail = (EditText) v;

        if (mailButtonPressedNumber == 0) {
            mail.setText("");
            mailButtonPressedNumber++;
        }
    }

    // when clicked in passwordIdButton, its content is deleted and change its mode to: password
    public void passwordOnClick(View v) {
        EditText password = (EditText) v;

        if (passwordButtonPressedNumber == 0) {
            password.setText("");
            password.setTransformationMethod(new PasswordTransformationMethod());
            passwordButtonPressedNumber++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
