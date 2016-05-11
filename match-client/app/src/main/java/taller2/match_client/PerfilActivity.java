package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/* Perfil Activity has user perfil fields. User can change its and save changes (the fields with new values are
   send to Server) */
public class PerfilActivity extends AppCompatActivity {

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
    FileManager fm;
    JSONObject profile;

    /* On Create */
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
                updateProfileOnClick(v);
            }
        });

        // TextViews
        userNameView = (EditText)findViewById(R.id.userNamePerfil);
        userRealNameView = (EditText)findViewById(R.id.userRealNamePerfil);

        /* Load Profile into Activity */
        try {
            Base64Converter b64conv = new Base64Converter();
            fm = new FileManager(this);
            JSONObject actualProfile = new JSONObject(fm.readFile(getResources().getString(R.string.profile_filename)));
            userRealNameView.setText(actualProfile.getString(getResources().getString(R.string.userName)));
            userNameView.setText(actualProfile.getString(getResources().getString(R.string.alias)));
            userPhoto.setImageBitmap(b64conv.Base64ToBitmap(actualProfile.getString(getResources().getString(R.string.photoProfile))));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
    }

    /*  */
    private void changeProfilePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture_en)), SELECT_PICTURE);
    }

    /*  */
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
        Bitmap bitmap_scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);       // Scale image to show large images

        userPhoto.setImageBitmap(bitmap_scaled);
        //userPhoto.setImageURI(imageUri);
    }

    /* */
    private String getPath(Uri imageUri) {
        if (imageUri == null) {
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return imageUri.getPath();
    }   // TODO: LO USAMOS AL FINAL?

    /*  */
    private void updateProfileOnClick(View v) {
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
            profile = new JSONObject(fm.readFile(getResources().getString(R.string.profile_filename)));
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
        if ( checkConection() ){
            loadingWindow.show();
            String url = getResources().getString(R.string.server_ip);
            String uri = getResources().getString(R.string.update_profile_uri);
            SendProfileTask checkLogin = new SendProfileTask();
            checkLogin.execute("POST",url, uri, String.valueOf(profile));
        } else {
            internetDisconnectWindow.show();
        }
        checkProfileResponse("200:ok"); //TODO: FOR NOW...
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

    /* Return true if format of fields is correct */
    private boolean checkFormatFields() {
        if (userName.isEmpty() || userRealName.isEmpty()) {
            emptyFieldsWindow.show();
            return false;
        }
        return true;
    }

    /* Check profile response from Server */
    private void checkProfileResponse(String response) {
        loadingWindow.dismiss();
        String responseCode = response.split(":")[0];
        String responseMessage = response.split(":")[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_login))) {   //TODO: DEFINIR MEJOR NOMBRE
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.profile_uploaded_en),
                    Toast.LENGTH_LONG).show();
            // Update Profile
            try {
                fm.writeFile(getResources().getString(R.string.profile_filename), String.valueOf(profile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // ERROR
        }
    }

    /* Send Login to Server */
    private class SendProfileTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkProfileResponse(dataGetFromServer);
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
