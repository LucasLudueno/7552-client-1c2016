package taller2.match_client;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    AlertDialog userMailExistWindow;
    AlertDialog internetDisconnectWindow;
    AlertDialog gpsDisconnectedWindow;
    ProgressDialog connectingToServerWindow;
    EditText userNameView;
    EditText userMailView;
    EditText userRealNameView;
    EditText userPasswordView;
    EditText userBirthdayView;
    CheckBox userFemaleView;
    CheckBox userMaleView;
    Button continueRegButton;
    ProgressBar loading;

    private LocationManager locationManager;
    private LocationListener locationListener;
    int minTimeToRefresh = 5000;

    String userName;
    String userPassword;
    String userRealName;
    String userMail;
    String userBirthday;
    String latitude = "";
    String longitude = "";

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
        loading = (ProgressBar) findViewById(R.id.progressBarRegister);

        // wrongFieldsWindow
        wrongFieldsWindow = new AlertDialog.Builder(this).create();
        wrongFieldsWindow.setTitle("Wrong Fields");
        wrongFieldsWindow.setMessage("Fields have not right format or are empty. Please complete right the fields before continue");

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle("Internet disconnect");
        internetDisconnectWindow.setMessage("Please connect to internet to continue");

        // userNameExistWindow
        userMailExistWindow= new AlertDialog.Builder(this).create();
        userMailExistWindow.setTitle("Mail already exist");
        userMailExistWindow.setMessage("The Mail you choose already exists. Please choose other");

        // gpsDisconnected
        gpsDisconnectedWindow = new AlertDialog.Builder(this).create();
        gpsDisconnectedWindow.setTitle("GPS is disconnected");
        gpsDisconnectedWindow.setMessage("Please connect the GPS");

        // Continue Button
        continueRegButton = (Button) findViewById(R.id.ContinueRegisterButton);
        continueRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueRegisterOnClick(v);
            }
        });

        // Male and Female CheckBox
        userFemaleView = (CheckBox) findViewById(R.id.userIsFemale);
        userMaleView = (CheckBox) findViewById(R.id.userIsMale);

        userFemaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMaleView.isChecked()) {
                    userMaleView.setChecked(false);
                }
                ;
            }
        });
        userMaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFemaleView.isChecked()) {
                    userFemaleView.setChecked(false);
                }
                ;
            }
        });

        // TextViews
        userNameView = (EditText) findViewById(R.id.userName);
        userPasswordView = (EditText) findViewById(R.id.userPassword);
        userRealNameView = (EditText) findViewById(R.id.userRealName);
        userMailView = (EditText) findViewById(R.id.userMail);
        userBirthdayView = (EditText) findViewById(R.id.userBirthdate);

        // Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = Double.toString(location.getLatitude());
                longitude = Double.toString(location.getLongitude());
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Latitude", "disable");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Latitude","enable");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Latitude","status");
            }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeToRefresh, 0, locationListener);
        } catch (SecurityException e) {
            gpsDisconnectedWindow.show();
            // LOG - ERROR
        }
    }

    /* When user register, PrincipalAppActivity is created */
    public void continueRegisterOnClick(View v) {
        if (!checkFormatFields()) {                     // TODO: BLOQUEAR BOTON AL PRESIONAR
            wrongFieldsWindow.show();
            return;
        }

        if ((latitude.compareTo("") == 0) ||((longitude.compareTo("") == 0)))  {
            gpsDisconnectedWindow.show();               // TODO: Refactor
            return;
        }

        String userSex = "";
        if (userFemaleView.isChecked()) {
            userSex = "Female";
        } else {
            userSex = "Male";
        }

        String url = "http://192.168.0.5:8000";
        //String url = "http://181.26.16.245:8000";
        String uri = getResources().getString(R.string.register_uri);
        JSONObject data = new JSONObject();

        try {
            data.put("userName", userName);
            data.put("userPassword", userPassword);
            data.put("userRealName", userRealName);
            data.put("userMail", userMail);
            data.put("userBirthday", userBirthday);
            data.put("userSex", userSex);
            data.put("latitude",latitude);
            data.put("longitude",longitude);
        } catch (JSONException e) {
            // ERROR
        }

        if ( checkConection() ){
            connectingToServerWindow = ProgressDialog.show(RegisterActivity.this, "Please wait...", "Registration processing", true);
            SendRegisterTask checkLogin = new SendRegisterTask();   //CON ESTAS LINEAS MANDAS AL SERVER EL REGISTER
            checkLogin.execute("POST",url, uri, String.valueOf(data));
        } else {
            internetDisconnectWindow.show();
        }

        //checkRegisterResponse("ok");
    }

    boolean checkFormatFields() {
        userName = userNameView.getText().toString();            // TODO: BLOQUEAR BOTON AL PRESIONAR
        userPassword = userPasswordView.getText().toString();
        userRealName = userRealNameView.getText().toString();
        userMail = userMailView.getText().toString();
        userBirthday = userBirthdayView.getText().toString();

        if (userName.isEmpty() ||
                userPassword.isEmpty() ||
                userRealName.isEmpty() ||
                userMail.isEmpty() ||
                userBirthday.isEmpty() ||
                (!userFemaleView.isChecked() && !userMaleView.isChecked()) ) {

            //TODO: CHECKEAR LOS FORMATOS
            return false;
        }
        return true;
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
        connectingToServerWindow.dismiss();
        if (response.equals("ok")) {
            Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
            startActivity(startAppActivity);
        } else {
            userMailExistWindow.setTitle(response);
            userMailExistWindow.show();
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
