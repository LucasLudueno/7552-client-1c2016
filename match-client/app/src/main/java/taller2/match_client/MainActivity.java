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
    private AlertDialog badLoginWindow;
    private AlertDialog emptyFieldsWindow;
    private AlertDialog internetDisconnectWindow;
    private ProgressDialog loading;
    private EditText userMailView;
    private EditText userPasswordView;
    private Button login;
    private Button register;

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        // badLoginWindow
        badLoginWindow = new AlertDialog.Builder(this).create();
        badLoginWindow.setTitle("Error while login");
        badLoginWindow.setMessage("Mail or Password are incorrect");

        // emptyFieldsWindow
        emptyFieldsWindow = new AlertDialog.Builder(this).create();
        emptyFieldsWindow.setTitle("Fields Empty");
        emptyFieldsWindow.setMessage("Some fields are empty. Please complete the fields before continue");

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle("Internet disconnect");
        internetDisconnectWindow.setMessage("Please connect internet to continue");

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
        userMailView = (EditText)findViewById(R.id.userMailLogin);
        userPasswordView = (EditText)findViewById(R.id.userPasswordLogin);
    }

    /* When an user login, if the userName and the password are correct, PrincipalAppActivity is created. */
    private void loginOnClick(View v) {
        String userMail = userMailView.getText().toString();
        String userPassword = userPasswordView.getText().toString();

        /*if (userName.isEmpty() || userPassword.isEmpty()) {
            emptyFieldsWindow.show();
            return;
        }*/
        String url = "http://192.168.0.5:8000";
        String uri = getResources().getString(R.string.login_uri);
        JSONObject data = new JSONObject();

        try {
            data.put("userMail" , userMail);
            data.put("userPassword", userPassword);
        } catch (JSONException e) {
            // ERROR
            // LOG
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
    private void registerOnClick(View v) {
        Intent startRegisterActivity = new Intent(this, RegisterActivity.class);
        startActivity(startRegisterActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /* Handle menu item click */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /* Check internet connection */
    private boolean checkConection() {
        ConnectivityManager connectManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();
        if ((networkInfo != null && networkInfo.isConnected()) ) {
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

// REFACTOR
// TODO: EVALUAR QUÉ MENUES VAN DENTRO DE LAS ACTIVITIES. SI NO VAN: BORRAR DESDE MENU Y DESDE ACTIVITY
// TODO: ELEGIR UN DISEÑO DE CHAT, EL OTRO BORRARLO.
// TODO: VAUALIZAR LOS MARGENES DE SETTINGS SI CONSERVAMOS EL LAYOUT
// TODO: VALUALIZAR MARGENES EN GENERAL DE LOS CONTENT LAYOUT SI CONSERVAMOS LOS MISMOS
// TODO: ELIMINAR IMPORTS NO USADOS
// TODO: ELIMINAR STRINGS HARDCODEADOS EN XML
// TODO: ORGANIZAR CLASES

// DUDAS
// TODO: IMAGEN DE PERFIL CIRCULAR Y NOMBRE DE USUARIO ALINEADO
// TODO: AGRUPAR RIGHT Y LEFT MSG ??
// TODO: FONDO DE PANTALLA A CHAT ??
// TODO: AGRUPAR PERFIL Y REGISTER ??
// TODO: MAX HEIGHT EN CUADRO PARA ESCRIBIR CHAT ??


// NECESARIO
// TODO: CERRAR ACTIVITIES QUE NO SE USAN MAS
// TODO: MANEJAR TIPOS DE RESPONSE CODE
