package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

/* Perfil Activity has user perfil fields. User can change its and save changes (the fields with new values are
 * send to Server) */
public class ProfileActivity extends AppCompatActivity {

    /* Attributes */
    private TextView userNameView;
    private TextView userRealNameView;
    private AlertDialog emptyFieldsWindow;
    private AlertDialog internetDisconnectWindow;
    private ProgressDialog loadingWindow;
    private ImageView userPhoto;
    private Button saveChangesButton;
    private static final int SELECT_PICTURE = 1;
    private String userName;
    private String userRealName;
    JSONObject profile;

    /* On Create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

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

        /* Load Profile into Activity */
        try {
            Base64Converter b64conv = new Base64Converter();
            JSONObject actualProfile = new JSONObject(FileManager.readFile(getResources().getString(R.string.profile_filename), getApplicationContext()));
            userRealNameView.setText(actualProfile.getString(getResources().getString(R.string.userName)));
            userNameView.setText(actualProfile.getString(getResources().getString(R.string.alias)));
            userPhoto.setImageBitmap(b64conv.Base64ToBitmap(actualProfile.getString(getResources().getString(R.string.photoProfile))));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        loadingWindow.setMessage(getResources().getString(R.string.log_processing_en));
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
        userRealNameView = (EditText)findViewById(R.id.userRealNamePerfil);
    }

    /* When profile photo is pressed, gallery option to choose other is open. */
    private void changeProfilePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture_en)), SELECT_PICTURE);
    }

    /* On activity result after press profile photo */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
            // LOG
        }
        Uri imageUri = data.getData();
        //String imagePath = getPath(imageUri);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
        Bitmap bitmap_scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);       // TODO: CHECK Scale image to show large images

        userPhoto.setImageBitmap(bitmap_scaled);
    }

    /* Updated profile is sending to Server. */
    private void sendUpdateProfileToServer() {
        userName = userNameView.getText().toString();
        userRealName = userRealNameView.getText().toString();

        // check format fields
        if (!checkFormatFields()) {
            return;
        }
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
            profile.remove(getResources().getString(R.string.userName));
            profile.put(getResources().getString(R.string.userName), userRealName);
            profile.remove(getResources().getString(R.string.profilePhoto));
            profile.put(getResources().getString(R.string.profilePhoto), profilePhotoBase64);
        } catch (JSONException e) {
            // ERROR
            // LOG
        } catch (IOException e) {

        }
        // Sending json data to Server
        /*if ( ActivityHelper.checkConection(getApplicationContext()) ){
            loadingWindow.show();
            String url = getResources().getString(R.string.server_ip);
            String uri = getResources().getString(R.string.update_profile_uri);
            SendProfileTask checkLogin = new SendProfileTask();
            checkLogin.execute("POST",url, uri, String.valueOf(profile));
        } else {
            internetDisconnectWindow.show();
        }*/
        checkProfileResponseFromServer("200:ok"); //TODO: FOR NOW...
    }

    /* Return true if format of fields is correct */
    private boolean checkFormatFields() {
        if (userName.isEmpty() || userRealName.isEmpty()) {
            emptyFieldsWindow.show();
            return false;
        }
        return true;
    }

    /* Check profile response from Server. If it is ok, new profile is saved in file. */
    private void checkProfileResponseFromServer(String response) {
        loadingWindow.dismiss();
        String responseCode = response.split(":", 2)[0];
        String responseMessage = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_upload_profile))) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.profile_uploaded_en),
                    Toast.LENGTH_LONG).show();
            // Update Profile
            try {
                FileManager.writeFile(getResources().getString(R.string.profile_filename), String.valueOf(profile), getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // ERROR
        }
    }

    /* Send Updated Profile to Server */
    private class SendProfileTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkProfileResponseFromServer(dataGetFromServer);
        }
    }

    /* Handle menu item click */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* When back button is pressed, PrincipalAppActivity is bring to front */
    public void onBackPressed () {
        Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
        startAppActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startAppActivity);
    }
}
