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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* MainActivity manage the Login. When the user login, check with the server login information */
public class MainActivity extends AppCompatActivity {

    /* Attributes */
    private AlertDialog badLoginWindow;
    private AlertDialog emptyFieldsWindow;
    private AlertDialog internetDisconnectWindow;
    private AlertDialog wrongMailWindow;
    private ProgressDialog loading;
    private EditText userMailView;
    private EditText userPasswordView;
    private Button login;
    private Button register;
    private String userMail;
    private String userPassword;

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        // emailWrongFormatWindow
        wrongMailWindow = new AlertDialog.Builder(this).create();
        wrongMailWindow.setTitle(getResources().getString(R.string.mail_wrong_format_error_title_en));
        wrongMailWindow.setMessage(getResources().getString(R.string.mail_wrong_format_error_en));

        // badLoginWindow
        badLoginWindow = new AlertDialog.Builder(this).create();
        badLoginWindow.setTitle(getResources().getString(R.string.fields_incorrect_error_title_en));
        badLoginWindow.setMessage(getResources().getString(R.string.fields_incorrect_error_en));

        // emptyFieldsWindow
        emptyFieldsWindow = new AlertDialog.Builder(this).create();
        emptyFieldsWindow.setTitle(getResources().getString(R.string.fields_empty_error_title_en));
        emptyFieldsWindow.setMessage(getResources().getString(R.string.fields_empty_error_en));

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle(getResources().getString(R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(R.string.internet_disconnect_error_en));

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

    /* When an user login, if the userName and the password are correct (that is checked with Server) PrincipalAppActivity is created. */
    private void loginOnClick(View v) {
        userMail = userMailView.getText().toString();
        userPassword = userPasswordView.getText().toString();

        /*if (!checkFormatFields()) {
            return;
        }*/

        String url = getResources().getString(R.string.server_ip);
        String uri = getResources().getString(R.string.login_uri);
        JSONObject data = new JSONObject();                         //Creamos el Json que mandaremos al Server

        try {
            data.put("email" , userMail);
            data.put("password", userPassword);
        } catch (JSONException e) {
            // ERROR
            // LOG
        }
        loading = ProgressDialog.show(MainActivity.this,
                                        getResources().getString(R.string.please_wait_en),
                                        getResources().getString(R.string.log_processing_en), true);
        /*if ( checkConection() ){
            SendLoginTask checkLogin = new SendLoginTask();
            checkLogin.execute("POST",url, uri, data.toString());   //CON ESTAS LINEAS MANDAS AL SERVER EL LOGIN
        } else {
            internetDisconnectWindow.show();
        }*/
        checkLoginResponse("201:ok");
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

    private boolean checkFormatFields() {
        if (userMail.isEmpty() || userPassword.isEmpty()) {
            emptyFieldsWindow.show();
            return false;
        }
        if (!checkEmailFormat(userMail)) {
            wrongMailWindow.show();
            return false;
        }
        return true;
    }

    /* Return true if the mail format is correct */
    public static boolean checkEmailFormat (String email) {
        String format = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /* Check login response from Server */
    private void checkLoginResponse(String response) {
        loading.dismiss();
        String responseCode = response.split(":")[0];
        String responseMessage = response.split(":")[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code))) {
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
// TODO: VAUALIZAR LOS MARGENES DE SETTINGS SI CONSERVAMOS EL LAYOUT
// TODO: VALUALIZAR MARGENES EN GENERAL DE LOS CONTENT LAYOUT SI CONSERVAMOS LOS MISMOS
// TODO: ELIMINAR IMPORTS NO USADOS
// TODO: ELIMINAR STRINGS HARDCODEADOS EN XML
// TODO: ORGANIZAR CLASES
// TODO: SEND LOGIS TASK COMO ATRIBUTO

// DUDAS
// TODO: IMAGEN DE PERFIL CIRCULAR Y NOMBRE DE USUARIO ALINEADO
// TODO: AGRUPAR RIGHT Y LEFT MSG ??
// TODO: FONDO DE PANTALLA A CHAT ??
// TODO: AGRUPAR PERFIL Y REGISTER ??
// TODO: MAX HEIGHT EN CUADRO PARA ESCRIBIR CHAT ??


// NECESARIO
// TODO: CERRAR ACTIVITIES QUE NO SE USAN MAS
// TODO: MANEJAR TIPOS DE RESPONSE CODE
// TODO: CORREGIR FORMATO FECHA: "^((?:19|20)\\d\\d)/(0?[1-9]|1[012])/([12][0-9]|3[01]|0?[1-9])$";
// TODO: ENCAPSULAMIENTO DE METODOS