package taller2.match_client;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    /* Attributes */
    AlertDialog badLoginWindow;
    AlertDialog emptyFieldsWindow;
    AlertDialog internetDisconnectWindow;
    EditText userNameView;
    EditText userPasswordView;
    Button login;
    Button register;
    ProgressDialog loading;

    /* On create Activity */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        // badLoginWindow
        badLoginWindow = new AlertDialog.Builder(this).create();
        badLoginWindow.setTitle("Error while login");
        badLoginWindow.setMessage("User Name or Password are incorrect");

        // emptyFieldsWindow
        emptyFieldsWindow = new AlertDialog.Builder(this).create();
        emptyFieldsWindow.setTitle("Fields Empty");
        emptyFieldsWindow.setMessage("Some fields are empty. Please complete the fields first before continue");

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle("Internet disconnect");
        internetDisconnectWindow.setMessage("Please connect to internet to continue");

        // Login Button
        login = (Button)findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOnClick(v);
            }
        });

        // Register Button
        register = (Button)findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerOnClick(v);
            }
        });

        // TextViews
        userNameView = (EditText)findViewById(R.id.userNameLogin);
        userPasswordView = (EditText)findViewById(R.id.userPasswordLogin);
    }


    /* When an user login, if the userName and the password are correct, PrincipalAppActivity is created. */
    public void loginOnClick(View v) {
        String userName = userNameView.getText().toString();            // TODO: BLOQUEAR BOTON AL PRESIONAR
        String userPassword = userPasswordView.getText().toString();

        /*if (userName.isEmpty() || userPassword.isEmpty()) {
            emptyFieldsWindow.show();
            return;
        }*/
        String url = "http://192.168.0.5:8000";
        String uri = getResources().getString(R.string.login_uri);
        JSONObject data = new JSONObject();

        try {
            data.put("userName" , userName);
            data.put("userPassword", userPassword);
        } catch (JSONException e) {
            // ERROR
        }
        loading = ProgressDialog.show(MainActivity.this, "Please wait...", "Login processing", true);
        /*if ( checkConection() ){
            SendLoginTask checkLogin = new SendLoginTask();   //TODO: Checkear si no se puede tener como atribb
            checkLogin.execute("POST",url, uri, data.toString());   //CON ESTAS LINEAS MANDAS AL SERVER EL LOGIN
        } else {
            internetDisconnectWindow.show();
        }*/
        checkLoginResponse("ok");
    }

    /* When user registers, RegisterActivity is created */
    public void registerOnClick(View v) {
        Intent startRegisterActivity = new Intent(this, RegisterActivity.class);
        startActivity(startRegisterActivity);
    }

    // TODO: DUDO QUE USE EL MENU EN LA PANTALLA PRINCIPAL
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


    /* Check if the conection to internet is stable */
    private boolean checkConection() {
        ConnectivityManager connectManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();
        if ((networkInfo != null && networkInfo.isConnected()) ) {        // Check if i have internet.
            return true;
        }
        return false;
    }

    /* Check login response from Server */
    private void checkLoginResponse(String response) {
        loading.dismiss();
        if (response.equals("ok")) {
            Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
            startActivity(startAppActivity);
        } else {
            badLoginWindow.show();
        }
    }

    /* Send Login to Server */
    private class SendLoginTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkLoginResponse(dataGetFromServer);
        }
    }
}
