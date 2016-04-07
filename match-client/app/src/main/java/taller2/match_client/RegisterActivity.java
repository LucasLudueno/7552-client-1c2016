package taller2.match_client;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    /* Attributes */
    AlertDialog wrongFieldsWindow;
    AlertDialog emptyFieldsWindow;
    AlertDialog userNameExistWindow;
    AlertDialog internetDisconnectWindow;
    EditText userNameView;
    EditText userMailView;
    EditText userRealNameView;
    EditText userPasswordView;
    EditText userBirthdayView;
    CheckBox userFemaleView;
    CheckBox userMaleView;
    Button continueRegButton;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);

        // Add the back activity button in the toolbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ProgressBar
        loading = (ProgressBar)findViewById(R.id.progressBarRegister);

        // wrongFieldsWindow
        wrongFieldsWindow = new AlertDialog.Builder(this).create();
        wrongFieldsWindow.setTitle("Wrong Fields");
        wrongFieldsWindow.setMessage("Fields have not right format. Please complete right the fields first before continue");

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle("Internet disconnect");
        internetDisconnectWindow.setMessage("Please connect to internet to continue");

        // emptyFieldsWindow
        emptyFieldsWindow = new AlertDialog.Builder(this).create();
        emptyFieldsWindow.setTitle("Fields Empty");
        emptyFieldsWindow.setMessage("Some fields are empty. Please complete the fields first before continue");

        // userNameExistWindow
        userNameExistWindow = new AlertDialog.Builder(this).create();
        userNameExistWindow.setTitle("User Name already exist");
        userNameExistWindow.setMessage("The User Name you choose already exists. Please choose other");

        // Continue Button
        continueRegButton = (Button)findViewById(R.id.ContinueRegisterButton);
        continueRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueRegisterOnClick(v);
            }
        });

        // Male and Female CheckBox
        userFemaleView = (CheckBox)findViewById(R.id.userIsFemale);
        userMaleView = (CheckBox)findViewById(R.id.userIsMale);

        userFemaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMaleView.isChecked()){
                    userMaleView.setChecked(false);
                };
            }
        });
        userMaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFemaleView.isChecked()){
                    userFemaleView.setChecked(false);
                };
            }
        });

        // TextViews
        userNameView = (EditText)findViewById(R.id.userName);
        userPasswordView = (EditText)findViewById(R.id.userPassword);
        userRealNameView = (EditText)findViewById(R.id.userRealName);
        userMailView = (EditText)findViewById(R.id.userMail);
        userBirthdayView = (EditText)findViewById(R.id.userBirthdate);
    }

    /* When user continue register, PrincipalAppActivity is created */
    public void continueRegisterOnClick(View v) {
        String userName = userNameView.getText().toString();            // TODO: BLOQUEAR BOTON AL PRESIONAR
        String userPassword = userPasswordView.getText().toString();
        String userRealName = userRealNameView.getText().toString();
        String userMail = userMailView.getText().toString();
        String userBirthday = userBirthdayView.getText().toString();

        if (userName.isEmpty() ||
                userPassword.isEmpty() ||
                userRealName.isEmpty() ||
                userMail.isEmpty() ||
                userBirthday.isEmpty() ||
                (!userFemaleView.isChecked() && !userMaleView.isChecked()) ) {
            emptyFieldsWindow.show();
            //TODO: CHECKEAR LOS FORMATOS
            return;
        }
        String userSex = "";
        if (userFemaleView.isChecked()) {
            userSex = "Female";
        } else {
            userSex = "Male";
        }
        String url = "http://192.168.0.5:8000";
        String uri = getResources().getString(R.string.register_uri);
        JSONObject data = new JSONObject();

        try {
            data.put("userName", userName);
            data.put("userPassword", userPassword);
            data.put("userRealName", userRealName);
            data.put("userMail", userMail);
            data.put("userBirthday", userBirthday);
            data.put("userSex", userSex);
        } catch (JSONException e) {
            // ERROR
        }
        loading.setVisibility(View.VISIBLE);
        /*if ( checkConection() ){
            SendRegisterTask checkLogin = new SendRegisterTask();   //CON ESTAS LINEAS MANDAS AL SERVER EL REGISTER
            checkLogin.execute("POST",url, uri, data.toString());
        } else {
            internetDisconnectWindow.show();
        }*/
        checkRegisterResponse("ok");
    }

    // TODO: DUDO QUE USE EL MENU EN LA PANTALLA
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {    // Back to previus Activity
            this.finish();
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

    /* Check register response from Server */
    private void checkRegisterResponse(String response) {
        loading.setVisibility(View.GONE);
        if (response.equals("ok")) {
            Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
            startActivity(startAppActivity);
        } else {
            userNameExistWindow.show();
        }
    }

    /* Send Register to Server */
    private class SendRegisterTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkRegisterResponse(dataGetFromServer);
        }
    }
}
