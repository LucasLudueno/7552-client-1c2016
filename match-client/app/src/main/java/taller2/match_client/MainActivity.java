package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


/* MainActivity manage the Login. When the user login, check with the server login information */
public class MainActivity extends AppCompatActivity {
    /* Attributes */
    private AlertDialog badLoginWindow;
    private AlertDialog emptyFieldsWindow;
    private AlertDialog internetDisconnectWindow;
    private AlertDialog wrongMailWindow;
    private AlertDialog unavailableServiceWindow;
    private ProgressDialog loadingWindow;
    private EditText userMailView;
    private EditText userPasswordView;
    private Button login;
    private Button register;
    private String userEmail;
    private String userPassword;

    static String ipServer; //TODO: SACAR

    private static final String TAG = "MainActivity";

    /***MockServer***/
    private MockServer mockServer;

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Create MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create internal files
        File file = new File(this.getFilesDir(), getResources().getString(R.string.profile_filename));

        // Toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        // Help Windows
        createHelpWindows();

        // Views
        instantiateViews();

        Log.i(TAG, "MainActivity is created");

        //mockServer = new MockServer(getApplicationContext());     //MOCK TEST
    }

    /* Create windows that are showed to users to comunicate something (error, information) */
    private void createHelpWindows() {
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

        // loadingWindow
        loadingWindow = new ProgressDialog(this);
        loadingWindow.setTitle(getResources().getString(R.string.please_wait_en));
        loadingWindow.setMessage(getResources().getString(R.string.log_processing_en));

        // UnavailableServiceWindow
        unavailableServiceWindow = new AlertDialog.Builder(this).create();
        unavailableServiceWindow.setTitle(getResources().getString(R.string.unavailable_service_title_en));
        unavailableServiceWindow.setMessage(getResources().getString(R.string.unavailable_service_error_en));
    }

    /* Instantiate views inside Activity and keep it in attibutes */
    private void instantiateViews() {
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

        ((EditText)findViewById(R.id.ip)).setText(getResources().getString(R.string.server_ip));    //TODO: SACAR
    }

    /* When an user login, if the userName and the password are correct (that is checked with Server)
     * PrincipalAppActivity is created. */
    private void loginOnClick(View v) {
        ipServer = ((EditText)findViewById(R.id.ip)).getText().toString(); //TODO: SACAR

        userEmail = userMailView.getText().toString();
        userPassword = userPasswordView.getText().toString();

        // check formats
        if (!checkFormatFields()) {
            return;
        }

        // construct json login
        JSONObject data = new JSONObject();
        try {
            data.put(getResources().getString(R.string.password), userPassword);
            data.put(getResources().getString(R.string.email), userEmail);
        } catch (JSONException e) {
            Log.w(TAG, "Can't create Json Request with Email and Password");
        }
        if ( ActivityHelper.checkConection(getApplicationContext()) ){
            Log.d(TAG, "Send Login to Server: " + data.toString());
            loadingWindow.show();
            String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
            String uri = getResources().getString(R.string.login_uri);
            SendLoginTask checkLogin = new SendLoginTask();
            checkLogin.execute("POST",url, uri, data.toString());
        } else {
            internetDisconnectWindow.show();
        }

        /*** MockServer ***/
        //checkLoginResponseFromServer(mockServer.Login(data));     //MOCK TEST
    }

    /* When user registers, RegisterActivity is created */
    private void registerOnClick(View v) {
        ipServer = ((EditText)findViewById(R.id.ip)).getText().toString(); //TODO: SACAR

        Log.i(TAG, "Create RegisterActivity");
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

    /* Return true if the format of fields is correct */
    private boolean checkFormatFields() {
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            emptyFieldsWindow.show();
            return false;
        }
        if (!ActivityHelper.checkEmailFormat(userEmail)) {
            wrongMailWindow.show();
            return false;
        }
        return true;
    }

    /* Check login response from Server */
    private void checkLoginResponseFromServer(String response) {
        Log.d(TAG, "Response from Server is received: " + response);
        loadingWindow.dismiss();
        String responseCode = response.split(":", 2)[0];
        String profile = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_login))) {
            // update profile
            JSONObject profileJson = null;
            String profileComplete = "";
            try {
                profileJson = new JSONObject(profile);
                profileComplete = profileJson.getString(getResources().getString(R.string.user)).toString();
            } catch (JSONException e) {
                Log.e(TAG, "Can't get Profile from Server response");
            }

            try {
                FileManager.writeFile(getResources().getString(R.string.profile_filename), profileComplete, getApplicationContext());
            } catch (IOException e) {
                Log.e(TAG, "Can't write Profile File");
            }
            // start principal aplication
            Log.i(TAG, "Create Principal Activity");
            Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
            startAppActivity.putExtra(getResources().getString(R.string.email), String.valueOf(userEmail));
            startActivity(startAppActivity);
            this.finish();
        } else if (responseCode.equals(getResources().getString(R.string.existing_user_code))) {
            badLoginWindow.show();
        } else {
            unavailableServiceWindow.show();
        }
    }

    /* Send Login to Server */
    private class SendLoginTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkLoginResponseFromServer(dataGetFromServer);
        }
    }
}

// REFACTOR
// TODO: ELIMINAR IMPORTS NO USADOS

// DUDAS

// NECESARIO
// TODO: MANEJAR LOS RESPONSE CODES