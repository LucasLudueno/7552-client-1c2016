package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/* Profile Activity has user profile fields. UserProfile can change its and save changes (the fields with new values are
 * send to Server) */
public class ProfileActivity extends AppCompatActivity {
    /* Attributes */
    private TextView userNameView;
    private AlertDialog emptyFieldsWindow;
    private AlertDialog internetDisconnectWindow;
    private ProgressDialog loadingWindow;
    private AlertDialog unavailableServiceWindow;
    private Toast profileCreated;
    private ImageView userPhoto;
    private Button saveChangesButton;
    private String userName;
    private JSONObject profile;

    private LocationManager locationManager;
    private ActivityLocationListener locationListener;
    private int minTimeToRefresh = 5000;

    private static final int SELECT_PICTURE = 1;
    private static final int PROFILE_IMAGE_SIZE = 250;
    private static final String TAG = "ProfileActivity";

    /* On Create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.perfilToolbar);
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
        locationListener = new ActivityLocationListener();
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeToRefresh, 0, locationListener);
        } catch (SecurityException e) {
            Log.w(TAG, "Can't set LocationListener");
        }

        /* Load Profile into Activity */
        try {
            Base64Converter b64conv = new Base64Converter();
            JSONObject actualProfile = new JSONObject(FileManager.readFile(getResources().getString(R.string.profile_filename), getApplicationContext()));
            userNameView.setText(actualProfile.getString(getResources().getString(R.string.alias)));
            userPhoto.setImageBitmap(b64conv.Base64ToBitmap(actualProfile.getString(getResources().getString(R.string.photoProfile))));
        } catch (JSONException e) {
            Log.w(TAG, "Can't read Profile File");
        } catch (IOException e) {
            Log.w(TAG, "Can't get alias and Photo from Profile File");
        }
        Log.i(TAG, "Profile Activity is created");
    }

    /* Create windows that are showed to users to comunicate something (error, information) */
    private void createHelpWindows() {
        // emptyFieldsWindow
        emptyFieldsWindow = new AlertDialog.Builder(this).create();
        emptyFieldsWindow.setTitle(getResources().getString(R.string.fields_empty_error_title_en));
        emptyFieldsWindow.setMessage(getResources().getString(R.string.fields_empty_error_en));

        // internetDisconnectWindow
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle(getResources().getString(R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(R.string.internet_disconnect_error_en));

        // loadingWindow
        loadingWindow = new ProgressDialog(this);
        loadingWindow.setTitle(getResources().getString(R.string.please_wait_en));
        loadingWindow.setMessage(getResources().getString(R.string.updating_profile_en));

        // profileCreated Toast
        profileCreated = Toast.makeText(getApplicationContext(), getResources().getString(R.string.profile_uploaded_en), Toast.LENGTH_LONG);
    }

    /* Instantiate views inside Activity and keep it in attibutes */
    private void instantiateViews() {
        // When the image is clicked, the gallery is open and user can choose other profile photo
        userPhoto = (ImageView)findViewById(R.id.userPerfilPhoto);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePhoto();
            }
        });

        // Save changes button
        saveChangesButton = (Button) findViewById(R.id.savePerfilButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUpdateProfileToServer();
            }
        });

        // TextViews
        userNameView = (EditText)findViewById(R.id.userNamePerfil);

        // UnavailableServiceWindow
        unavailableServiceWindow = new AlertDialog.Builder(this).create();
        unavailableServiceWindow.setTitle(getResources().getString(R.string.unavailable_service_title_en));
        unavailableServiceWindow.setMessage(getResources().getString(R.string.unavailable_service_error_en));
    }

    /* When profile photo is pressed, gallery option to choose other is open. */
    private void changeProfilePhoto() {
        Log.d(TAG, "Change Profile Photo");
        Intent imageDownload = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageDownload.putExtra("crop", "true");
        imageDownload.putExtra("aspectX", 1);
        imageDownload.putExtra("aspectY", 1);
        imageDownload.putExtra("outputX", PROFILE_IMAGE_SIZE);
        imageDownload.putExtra("outputY", PROFILE_IMAGE_SIZE);
        imageDownload.putExtra("return-data", true);
        startActivityForResult(imageDownload, SELECT_PICTURE);
    }

    /* On activity result after press profile photo */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap image = extras.getParcelable("data");
            userPhoto.setImageBitmap(image);
            Log.d(TAG, "Profile Photo is changed");
        }
    }

    /* Updated profile is sending to Server. */
    private void sendUpdateProfileToServer() {
        userName = userNameView.getText().toString();

        // check format fields
        if (!checkFormatFields()) {
            return;
        }

        // Latitude and longitude
        Double latitude = locationListener.getLatitude();
        Double longitude = locationListener.getLongitude();

        // profile photo ---> base64
        BitmapDrawable drawable = (BitmapDrawable) userPhoto.getDrawable();
        Bitmap bitmapProfilePhoto = drawable.getBitmap();
        Base64Converter bs64 = new Base64Converter();
        String profilePhotoBase64 = bs64.bitmapToBase64(bitmapProfilePhoto);

        // construct Profile
        try {
            profile = new JSONObject(FileManager.readFile(getResources().getString(R.string.profile_filename),getApplicationContext()));
            profile.remove(getResources().getString(R.string.alias));
            profile.put(getResources().getString(R.string.alias), userName);
            profile.remove(getResources().getString(R.string.profilePhoto));
            profile.put(getResources().getString(R.string.profilePhoto), profilePhotoBase64);

            if (! ((latitude == 0.0) || (longitude == 0.0)) )  {
                JSONObject location = new JSONObject();
                location.put(getResources().getString(R.string.latitude), latitude);
                location.put(getResources().getString(R.string.longitude), longitude);
                profile.remove(getResources().getString(R.string.location));
                profile.put(getResources().getString(R.string.location),location);
            }

        } catch (JSONException e) {
            Log.w(TAG, "Can't create Json Profile Request");
        } catch (IOException e) {
            Log.e(TAG, "Can't read Profile File");
        }
        // Sending json data to Server
        if ( ActivityHelper.checkConection(getApplicationContext()) ) {
            Log.d(TAG, "Send Profile to Server: " + String.valueOf(profile));
            loadingWindow.show();
            String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
            String uri = getResources().getString(R.string.update_profile_uri);
            SendProfileTask checkProfileUpdate = new SendProfileTask();
            checkProfileUpdate.execute("POST",url, uri, String.valueOf(profile));
        } else {
            internetDisconnectWindow.show();
        }
       // checkProfileResponseFromServer("200:ok"); //TODO: TEST
    }

    /* Return true if format of fields is correct */
    private boolean checkFormatFields() {
        if (userName.isEmpty()) {
            emptyFieldsWindow.show();
            return false;
        }
        return true;
    }

    /* Check profile response from Server. If it is ok, new profile is saved in file. */
    private void checkProfileResponseFromServer(String response) {
        Log.d(TAG, "Response from Server is received: " + response);
        loadingWindow.dismiss();
        String responseCode = response.split(":", 2)[0];
        String responseMessage = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_upload_profile))) {
            profileCreated.show();
            // Update Profile
            try {
                FileManager.writeFile(getResources().getString(R.string.profile_filename), String.valueOf(profile), getApplicationContext());
            } catch (IOException e) {
                Log.w(TAG, "Can't write Profile File");
            }
        } else {
            unavailableServiceWindow.show();
        }
    }

    /* Send Updated Profile to Server */
    private class SendProfileTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkProfileResponseFromServer(dataGetFromServer);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    /* Handle menu item click */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* When back button is pressed, PrincipalAppActivity is bring to front */
    public void onBackPressed () {
        Log.i(TAG, "Back to previous Activity");
        Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
        startAppActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startAppActivity);
    }
}
