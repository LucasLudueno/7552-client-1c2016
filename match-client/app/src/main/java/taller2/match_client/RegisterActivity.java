package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
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

/* RegisterActivity manage the Register. When the user register, check with the server if the email already exist and
 * if don't, the user is register. */
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
    private LocationListenerGetter locationListener;
    private int minTimeToRefresh = 5000;

    private String userName;
    private String userPassword;
    private String userRealName;
    private String userEmail;
    private String userBirthday;
    private String latitude = "";
    private String longitude = "";
    private JSONObject registerData;

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

        // Help Windows
        createHelpWindows();

        // Views
        instantiateViews();

        // Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListenerGetter();
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeToRefresh, 0, locationListener);
        } catch (SecurityException e) {
            // LOG - ERROR
        }
    }

    /* Create windows that are showed to users to comunicate something (error, information) */
    private void createHelpWindows() {
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

        // loadingWindow
        connectingToServerWindow = new ProgressDialog(this);
        connectingToServerWindow.setTitle(getResources().getString(R.string.please_wait_en));
        connectingToServerWindow.setMessage(getResources().getString(R.string.reg_processing_en));
        connectingToServerWindow.setMax(100);
    }

    /* Instantiate views inside Activity and keep it in attibutes */
    private void instantiateViews() {
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
    }

    /* This function check fields format and if its ok, send the register information to Server to check it.
        If its ok again and the user not exists, PrincipalAppActivity is created. */
    public void continueRegisterOnClick(View v) {
        userName = userNameView.getText().toString();
        userPassword = userPasswordView.getText().toString();
        userRealName = userRealNameView.getText().toString();
        userEmail = userMailView.getText().toString();
        userBirthday = userBirthdayView.getText().toString();

        // check format fields
        if (!checkFormatFields()) {
            return;
        }

        // check latitude and longitude
        latitude = Double.toString(locationListener.getLatitude());
        longitude = Double.toString(locationListener.getLongitude());

        if ((latitude.compareTo("") == 0) ||((longitude.compareTo("") == 0)))  {    // TODO: IMPLEMENTAR UNA ESPECIE DE WHILE HASTA TENER VALORES
            internetDisconnectWindow.show();
            return;
        }

        // check sex
        String userSex = "";
        if (userFemaleView.isChecked()) {
            userSex = getResources().getString(R.string.female_en);
        } else {
            userSex = getResources().getString(R.string.male_en);
        }

        // construct registerData
        try {
            // register fields
            registerData = new JSONObject();
            registerData.put(getResources().getString(R.string.alias), userName);
            registerData.put(getResources().getString(R.string.password), userPassword);
            registerData.put(getResources().getString(R.string.userName), userRealName);
            registerData.put(getResources().getString(R.string.email), userEmail);
            registerData.put(getResources().getString(R.string.birthday), userBirthday);
            registerData.put(getResources().getString(R.string.sex), userSex);

            // location
            JSONObject location = new JSONObject();
            location.put(getResources().getString(R.string.latitude),latitude);
            location.put(getResources().getString(R.string.longitude), longitude);
            registerData.put(getResources().getString(R.string.location),location);

            // interests
            JSONArray interestEmptyList = new JSONArray();
            registerData.put(getResources().getString(R.string.interests),interestEmptyList);

            // profile photo
            Bitmap photodefault = BitmapFactory.decodeResource(getResources(), R.drawable.no_match);
            Base64Converter bs64 = new Base64Converter();
            String base64 = bs64.bitmapToBase64(photodefault);
            registerData.put(getResources().getString(R.string.profilePhoto), base64);
        } catch (JSONException e) {
            // ERROR -LOG
        }

        // send registerData
        if ( ActivityHelper.checkConection(getApplicationContext()) ){
            connectingToServerWindow.show();
            String url =  getResources().getString(R.string.server_ip);;
            String uri = getResources().getString(R.string.register_uri);
            SendRegisterTask checkLogin = new SendRegisterTask();
            checkLogin.execute("POST",url, uri, String.valueOf(registerData));
        } else {
            internetDisconnectWindow.show();
        }
        //checkRegisterResponse("201:ok");
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

    /* Check register response from Server. Response includes responseCode and responseMessage*/
    private void checkRegisterResponseFromServer(String response) {
        connectingToServerWindow.dismiss();
        String responseCode = response.split(":", 2)[0];
        String responseMessage = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_register))) {
            // save registerdata in file
            try {
                FileManager.writeFile(getResources().getString(R.string.profile_filename), String.valueOf(registerData), getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // start principal activity
            Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
            startAppActivity.setAction(Intent.ACTION_MAIN);
            startAppActivity.addCategory(Intent.CATEGORY_HOME);
            startAppActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startAppActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startAppActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startAppActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startAppActivity.putExtra(getResources().getString(R.string.email), String.valueOf(userEmail)); // Send user email to principal Activity
            startActivity(startAppActivity);

            // Finish actual activity
            this.finish();
        } else {
            userMailExistWindow.show();
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
