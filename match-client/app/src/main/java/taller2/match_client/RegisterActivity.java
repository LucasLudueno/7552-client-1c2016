package taller2.match_client;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    /* Attributes */
    private AlertDialog wrongBirthdayWindow;
    private AlertDialog wrongMailWindow;
    private AlertDialog emptyFieldsWindow;
    private AlertDialog userMailExistWindow;
    private AlertDialog internetDisconnectWindow;
    private ProgressDialog connectingToServerWindow;
    private EditText userNameView;
    private EditText userMailView;
    private EditText userRealNameView;
    private EditText userPasswordView;
    private EditText userBirthdayView;
    private CheckBox userFemaleView;
    private CheckBox userMaleView;
    private Button continueRegButton;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private int minTimeToRefresh = 5000;

    private String userName;
    private String userPassword;
    private String userRealName;
    private String userMail;
    private String userBirthday;
    private String latitude = "";
    private String longitude = "";

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);

        // Add the back activity button in the toolbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // wrongFieldsWindow
        wrongBirthdayWindow = new AlertDialog.Builder(this).create();
        wrongBirthdayWindow.setTitle(getResources().getString(R.string.birthdate_wrong_format_error_title_en));
        wrongBirthdayWindow.setMessage(getResources().getString(R.string.birthdate_wrong_format_error_en));

        // emailWrongFormatWindow
        wrongMailWindow = new AlertDialog.Builder(this).create();
        wrongMailWindow.setTitle(getResources().getString(R.string.mail_wrong_format_error_title_en));
        wrongMailWindow.setMessage(getResources().getString(R.string.mail_wrong_format_error_en));

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle(getResources().getString(R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(R.string.internet_disconnect_error_en));

        // userNameExistWindow
        userMailExistWindow= new AlertDialog.Builder(this).create();
        userMailExistWindow.setTitle(getResources().getString(R.string.mail_exist_error_title_en));
        userMailExistWindow.setMessage(getResources().getString(R.string.mail_exist_error_en));

        // emptyFieldsWindow
        emptyFieldsWindow = new AlertDialog.Builder(this).create();
        emptyFieldsWindow.setTitle(getResources().getString(R.string.fields_empty_error_title_en));
        emptyFieldsWindow.setMessage(getResources().getString(R.string.fields_empty_error_en));

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
                };
            }
        });
        userMaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFemaleView.isChecked()) {
                    userFemaleView.setChecked(false);
                };
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeToRefresh, 0, locationListener);
        } catch (SecurityException e) {
            // LOG - ERROR
        }
    }

    /* This function check fields format and if its ok, send the register information to Server to check it.
        If its ok again and the user not exists, PrincipalAppActivity is created. */
    public void continueRegisterOnClick(View v) {
        if (!checkFormatFields()) {
            return;
        }

        if ((latitude.compareTo("") == 0) ||((longitude.compareTo("") == 0)))  {    // TODO: IMPLEMENTAR UNA ESPECIE DE WHILE HASTA TENER VALORES
            //internetDisconnectWindow.show();
            return;
        }

        String userSex = "";
        if (userFemaleView.isChecked()) {
            userSex = getResources().getString(R.string.female_en);
        } else {
            userSex = getResources().getString(R.string.male_en);
        }

        String url =  getResources().getString(R.string.server_ip);;
        String uri = getResources().getString(R.string.register_uri);
        JSONObject data = new JSONObject();

        try {
            data.put("alias", userName);
            data.put("password", userPassword);
            data.put("name", userRealName);
            data.put("email", userMail);
            data.put("birthday", userBirthday);
            data.put("sex", userSex);
            data.put("latitude",latitude);
            data.put("longitude",longitude);
        } catch (JSONException e) {
            // ERROR -LOG
        }

        if ( checkConection() ){
            connectingToServerWindow = ProgressDialog.show(RegisterActivity.this,
                                                            getResources().getString(R.string.please_wait_en),
                                                            getResources().getString(R.string.reg_processing_en), true);
            SendRegisterTask checkLogin = new SendRegisterTask();
            checkLogin.execute("POST",url, uri, String.valueOf(data));
        } else {
            internetDisconnectWindow.show();
        }
        //checkRegisterResponse("201:ok");
    }

    /* Check if the format fields is correct to continue */
    boolean checkFormatFields() {
        userName = userNameView.getText().toString();
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

            emptyFieldsWindow.show();
            return false;
        }
        if (!checkEmailFormat(userMail)) {
            wrongMailWindow.show();
            return false;
        }
        if (!checkDateFormat(userBirthday)) {
            wrongBirthdayWindow.show();
            return false;
        }
        return true;
    }

    @Override
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

    /* Check register response from Server. Response includes responseCode and responseMessage*/
    private void checkRegisterResponse(String response) {
        connectingToServerWindow.dismiss();
        String responseCode = response.split(":")[0];
        String responseMessage = response.split(":")[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code))) {
            Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
            startActivity(startAppActivity);
        } else {
            userMailExistWindow.setTitle(response); // TODO: Only for now...
            userMailExistWindow.show();
        }
    }

    /* Return true if the date format is correct (dd/mm/yyyy) */
    public static boolean checkDateFormat(String date) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            formatoFecha.setLenient(false);
            formatoFecha.parse(date);
        } catch (ParseException e) {
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

    /* This class send the register to Server */
    private class SendRegisterTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkRegisterResponse(dataGetFromServer);
        }
    }
}
