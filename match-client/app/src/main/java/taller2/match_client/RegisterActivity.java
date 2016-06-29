package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import taller2.match_client.Helpers.ActivityHelper;
import taller2.match_client.Helpers.ActivityLocationListener;
import taller2.match_client.Helpers.Base64Converter;
import taller2.match_client.Helpers.FileManager;
import taller2.match_client.Request.ClientToServerTask;

/* RegisterActivity manage the Register. When the user register, check with the server if the email already exist and
 * if don't, the user is register. */
public class RegisterActivity extends AppCompatActivity {
    /* Attributes */
    private AlertDialog wrongBirthdayWindow;
    private AlertDialog wrongMailWindow;
    private AlertDialog emptyFieldsWindow;
    private AlertDialog userMailExistWindow;
    private AlertDialog unavailableServiceWindow;
    private AlertDialog internetDisconnectWindow;
    private AlertDialog gpsDisconnectWindow;
    private AlertDialog waitForLocation;
    private ProgressDialog connectingToServerWindow;
    private EditText userNameView;
    private EditText userMailView;
    private EditText userRealNameView;
    private EditText userPasswordView;
    private EditText userBirthdayView;
    private CheckBox userFemaleView;
    private CheckBox userMaleView;
    private Button continueRegButton;

    private LocationManager locationManagerInternet;
    private LocationManager locationManagerGps;
    private ActivityLocationListener locationListenerGps;
    private ActivityLocationListener locationListenerInternet;
    private int minTimeToRefresh = 5000;

    private String userName;
    private String userPassword;
    private String userRealName;
    private String userEmail;
    private String userBirthday;
    private String latitude = "";
    private String longitude = "";
    private JSONObject registerData;

    private static final String TAG = "RegisterActivity";

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(taller2.match_client.R.layout.activity_register);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(taller2.match_client.R.id.registerToolbar);
        setSupportActionBar(toolbar);

        // Add the back activity button in the toolbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Help Windows
        createHelpWindows();

        // Views
        instantiateViews();

        // Location Manager
        locationManagerInternet = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListenerInternet = new ActivityLocationListener();
        locationListenerGps = new ActivityLocationListener();
        try {
            locationManagerInternet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeToRefresh, 0, locationListenerInternet);
            locationManagerGps.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeToRefresh, 0, locationListenerGps);
        } catch (SecurityException e) {
            Log.w(TAG, "Can't set LocationListener");
        }
        Log.i(TAG, "Register Activity is created");
    }

    /* Create windows that are showed to users to comunicate something (error, information) */
    private void createHelpWindows() {
        // wrongFieldsWindow
        wrongBirthdayWindow = new AlertDialog.Builder(this).create();
        wrongBirthdayWindow.setTitle(getResources().getString(taller2.match_client.R.string.birthdate_wrong_format_error_title_en));
        wrongBirthdayWindow.setMessage(getResources().getString(taller2.match_client.R.string.birthdate_wrong_format_error_en));

        // emailWrongFormatWindow
        wrongMailWindow = new AlertDialog.Builder(this).create();
        wrongMailWindow.setTitle(getResources().getString(taller2.match_client.R.string.mail_wrong_format_error_title_en));
        wrongMailWindow.setMessage(getResources().getString(taller2.match_client.R.string.mail_wrong_format_error_en));

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle(getResources().getString(taller2.match_client.R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(taller2.match_client.R.string.internet_disconnect_error_en));

        // userNameExistWindow
        userMailExistWindow= new AlertDialog.Builder(this).create();
        userMailExistWindow.setTitle(getResources().getString(taller2.match_client.R.string.mail_exist_error_title_en));
        userMailExistWindow.setMessage(getResources().getString(taller2.match_client.R.string.mail_exist_error_en));

        // emptyFieldsWindow
        emptyFieldsWindow = new AlertDialog.Builder(this).create();
        emptyFieldsWindow.setTitle(getResources().getString(taller2.match_client.R.string.fields_empty_error_title_en));
        emptyFieldsWindow.setMessage(getResources().getString(taller2.match_client.R.string.fields_empty_error_en));

        // UnavailableServiceWindow
        unavailableServiceWindow = new AlertDialog.Builder(this).create();
        unavailableServiceWindow.setTitle(getResources().getString(taller2.match_client.R.string.unavailable_service_title_en));
        unavailableServiceWindow.setMessage(getResources().getString(taller2.match_client.R.string.unavailable_service_error_en));

        // loadingWindow
        connectingToServerWindow = new ProgressDialog(this);
        connectingToServerWindow.setTitle(getResources().getString(taller2.match_client.R.string.please_wait_en));
        connectingToServerWindow.setMessage(getResources().getString(taller2.match_client.R.string.reg_processing_en));
        connectingToServerWindow.setMax(100);

        // waitForLocationWindow
        waitForLocation = new AlertDialog.Builder(this).create();
        waitForLocation.setTitle(getResources().getString(taller2.match_client.R.string.wait_for_location_error_title_en));
        waitForLocation.setMessage(getResources().getString(taller2.match_client.R.string.wait_for_location_error_en));

        // gpsDisconnectWindow
        gpsDisconnectWindow = new AlertDialog.Builder(this).create();
        gpsDisconnectWindow.setTitle(getResources().getString(taller2.match_client.R.string.gps_disconnect_error_title_en));
        gpsDisconnectWindow.setMessage(getResources().getString(taller2.match_client.R.string.gps_disconnect_error_en));
    }

    /* Instantiate views inside Activity and keep it in attibutes */
    private void instantiateViews() {
        // Continue Button
        continueRegButton = (Button) findViewById(taller2.match_client.R.id.ContinueRegisterButton);
        continueRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocation();
            }
        });

        // Male and Female CheckBox
        userFemaleView = (CheckBox) findViewById(taller2.match_client.R.id.userIsFemale);
        userMaleView = (CheckBox) findViewById(taller2.match_client.R.id.userIsMale);

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
        userNameView = (EditText) findViewById(taller2.match_client.R.id.userName);
        userPasswordView = (EditText) findViewById(taller2.match_client.R.id.userPassword);
        userRealNameView = (EditText) findViewById(taller2.match_client.R.id.userRealName);
        userMailView = (EditText) findViewById(taller2.match_client.R.id.userMail);
        userBirthdayView = (EditText) findViewById(taller2.match_client.R.id.userBirthdate);
    }

    /*  Check Location */
    private void checkLocation() {
        if (!ActivityHelper.checkConection(getApplicationContext()) ) {
            internetDisconnectWindow.show();
            return;
        }

        if(!locationManagerGps.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            gpsDisconnectWindow.show();
            return;
        }

        // check latitude and longitude from gps and internet
        Double latitudeInternet = locationListenerInternet.getLatitude();
        Double longitudeInternet = locationListenerInternet.getLongitude();
        Double latitudeGps = locationListenerGps.getLatitude();
        Double longitudeGps = locationListenerGps.getLongitude();

        if ((latitudeInternet.compareTo(0.0) == 0) ||((longitudeInternet.compareTo(0.0) == 0)))  {
            if ((latitudeGps.compareTo(0.0) == 0) ||((longitudeGps.compareTo(0.0) == 0)))  {
                waitForLocation.show();
                return;
            } else {
                latitude = Double.toString(locationListenerGps.getLatitude());
                longitude = Double.toString(locationListenerGps.getLongitude());
                sendRegisterToServer();
            }
        } else {
            latitude = Double.toString(locationListenerInternet.getLatitude());
            longitude = Double.toString(locationListenerInternet.getLongitude());
            sendRegisterToServer();
        }
    }

    /* This function check fields format and if its ok, send the register information to Server to check it.
        If its ok again and the user not exists, PrincipalAppActivity is created. */
    public void sendRegisterToServer() {
        userName = userNameView.getText().toString();
        userPassword = userPasswordView.getText().toString();
        userRealName = userRealNameView.getText().toString();
        userEmail = userMailView.getText().toString();
        userBirthday = userBirthdayView.getText().toString();

        // check format fields
        if (!checkFormatFields()) {
            return;
        }

        // check sex
        String userSex = "";
        if (userFemaleView.isChecked()) {
            userSex = getResources().getString(taller2.match_client.R.string.female_en);
        } else {
            userSex = getResources().getString(taller2.match_client.R.string.male_en);
        }

        // calculate Age
        int userAge = ActivityHelper.calculateAge(userBirthday);

        // construct registerData
        try {
            // register fields
            registerData = new JSONObject();
            registerData.put(getResources().getString(taller2.match_client.R.string.alias), userName);
            registerData.put(getResources().getString(taller2.match_client.R.string.password), userPassword);
            registerData.put(getResources().getString(taller2.match_client.R.string.userName), userRealName);
            registerData.put(getResources().getString(taller2.match_client.R.string.email), userEmail);
            registerData.put(getResources().getString(taller2.match_client.R.string.age),userAge);
            registerData.put(getResources().getString(taller2.match_client.R.string.sex), userSex);

            // location
            JSONObject location = new JSONObject();
            location.put(getResources().getString(taller2.match_client.R.string.latitude), latitude);
            location.put(getResources().getString(taller2.match_client.R.string.longitude), longitude);
            registerData.put(getResources().getString(taller2.match_client.R.string.location),location);

            // interests
            JSONArray interests = new JSONArray();
            JSONObject sexInterest = new JSONObject();
            sexInterest.put(getResources().getString(taller2.match_client.R.string.category), getResources().getString(taller2.match_client.R.string.sex));
            sexInterest.put(getResources().getString(taller2.match_client.R.string.value), getResources().getString(taller2.match_client.R.string.any));
            interests.put(sexInterest);
            registerData.put(getResources().getString(taller2.match_client.R.string.interests),interests);

            // profile photo
            Bitmap photodefault = BitmapFactory.decodeResource(getResources(), taller2.match_client.R.drawable.standard_photo_profile_small);
            Base64Converter bs64 = new Base64Converter();
            String base64 = bs64.bitmapToBase64(photodefault);
            registerData.put(getResources().getString(taller2.match_client.R.string.profilePhoto), base64);
        } catch (JSONException e) {
            Log.w(TAG, "Can't create Json Register Request");
        }

        // send registerData
        if ( ActivityHelper.checkConection(getApplicationContext()) ){
            Log.d(TAG, "Send Register Request to Server: " + registerData.toString());
            connectingToServerWindow.show();
            String url =  MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
            String uri = getResources().getString(taller2.match_client.R.string.register_uri);
            SendRegisterTask checkRegister = new SendRegisterTask();
            checkRegister.execute("POST",url, uri, String.valueOf(registerData));
        } else {
            internetDisconnectWindow.show();
        }
    }

    /* Check if the format fields is correct to continue */
    boolean checkFormatFields() {
        if (userName.isEmpty() ||
                userPassword.isEmpty() ||
                userRealName.isEmpty() ||
                userEmail.isEmpty() ||
                userBirthday.isEmpty() ||
                (!userFemaleView.isChecked() && !userMaleView.isChecked()) ) {

            emptyFieldsWindow.show();
            return false;
        }
        if (!ActivityHelper.checkEmailFormat(userEmail)) {
            wrongMailWindow.show();
            return false;
        }
        if (!ActivityHelper.checkDateFormat(userBirthday)) {
            wrongBirthdayWindow.show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(taller2.match_client.R.menu.menu_register, menu);
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

    /* Check register response from Server. Response includes responseCode and responseMessage*/
    private void checkRegisterResponseFromServer(String response) {
        Log.d(TAG, "Response from Server is received: " + response);
        connectingToServerWindow.dismiss();
        String responseCode = response.split(":", 2)[0];
        String responseMessage = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(taller2.match_client.R.string.ok_response_code_register))) {
            // save registerdata in file
            try {
                FileManager.writeFile(getResources().getString(taller2.match_client.R.string.profile_filename), String.valueOf(registerData), getApplicationContext());
            } catch (IOException e) {
                Log.e(TAG, "Can't write profile");
            }
            // get token
            String token = "";
            try {
                token = (new JSONObject(responseMessage)).getString(getResources().getString(R.string.token));
            } catch (JSONException e) {
                Log.w(TAG, "Can't get token from Server Response");
            }
            // start principal activity
            Log.d(TAG, "Create PrincipalAppActivity");
            Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
            startAppActivity.putExtra(getResources().getString(taller2.match_client.R.string.email), String.valueOf(userEmail)); // Send user email to principal Activity
            startAppActivity.putExtra(getResources().getString(taller2.match_client.R.string.token), token); // Send token to principal Activity
            startActivity(startAppActivity);

            // Finish actual activity
            this.finish();
        } else if (responseCode.equals(getResources().getString(taller2.match_client.R.string.existing_user_code))){
            userMailExistWindow.show();
        } else {
            unavailableServiceWindow.show();
        }
    }

    /* This class send the register to Server */
    private class SendRegisterTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkRegisterResponseFromServer(dataGetFromServer);
        }
    }
}