package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* SettingsActivity has user interest. UserProfile can change its from differents categories and save changes
   (the new interests are send to Server) */
public class SettingsActivity extends AppCompatActivity {
    /* Attributes */
    JSONObject profile;
    private Spinner categoryList;
    private Spinner interestList;
    private ArrayAdapter<String> listMusicBandAdapter;
    private ArrayAdapter<String> listOutdoorsAdapter;
    private ArrayAdapter<String> listMusicAdapter;
    private ArrayAdapter<String> listSportAdapter;
    private ArrayAdapter<String> listFoodAdapter;
    private ArrayAdapter<String> listTravelAdapter;
    private ArrayAdapter listCategoryAdapter;

    private Button addInterest;
    private Button removeInterest;
    private Button saveChangesButton;
    private EditText interest_edit;
    private CheckBox menSelected;
    private CheckBox womenSelected;

    private AlertDialog internetDisconnectWindow;
    private AlertDialog unavailableServiceWindow;
    private Toast profileCreated;
    private ProgressDialog loading;

    private LocationManager locationManager;
    private ActivityLocationListener locationListener;
    private int minTimeToRefresh = 5000;

    private static final String TAG = "SettingsActivity";

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);

        // Add the back activity button in the toolbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Help Windows
        createHelpWindows();

        // Views
        instantiateViews();

        // Category listener
        AdapterView.OnItemSelectedListener interestSelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,
                                       int position, long id) {
                String category = categoryList.getSelectedItem().toString();
                ArrayAdapter<String> adapter= getCategoryAdapter(category);
                interestList.setAdapter(adapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing for now...
            }
        };

        // Setting ItemClick Handler for Spinner Widget
        categoryList.setOnItemSelectedListener(interestSelectedListener);

        // Include Profile interests in Spinners
        includeProfileInterest();

        // Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new ActivityLocationListener();
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeToRefresh, 0, locationListener);
        } catch (SecurityException e) {
            Log.w(TAG, "Can't set LocationListener");
        }

        Log.i(TAG, "Register Activity is created");
    }

    /* Create windows that are showed to users to comunicate something (error, information) */
    private void createHelpWindows() {
        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle(getResources().getString(R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(R.string.internet_disconnect_error_en));

        // loadingWindow
        loading = new ProgressDialog(this);
        loading.setTitle(getResources().getString(R.string.please_wait_en));
        loading.setMessage(getResources().getString(R.string.updating_profile_en));

        // UnavailableServiceWindow
        unavailableServiceWindow = new AlertDialog.Builder(this).create();
        unavailableServiceWindow.setTitle(getResources().getString(R.string.unavailable_service_title_en));
        unavailableServiceWindow.setMessage(getResources().getString(R.string.unavailable_service_error_en));
    }

    /* Instantiate views inside Activity and keep it in attibutes */
    private void instantiateViews() {
        // Category List
        categoryList = (Spinner)findViewById(R.id.categoriesList);
        listCategoryAdapter = ArrayAdapter.createFromResource(this, R.array.category_list_items, android.R.layout.simple_spinner_item);
        listCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryList.setAdapter(listCategoryAdapter);

        // Interest List
        interestList = (Spinner)findViewById(R.id.interestList);

        // Music Band List
        List<String> musicBandListInterest = new ArrayList<String>();
        listMusicBandAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicBandListInterest);
        listMusicBandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Music List
        List<String> musicListInterest = new ArrayList<String>();
        listMusicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicListInterest);
        listMusicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Sport List
        List<String> sportListInterest = new ArrayList<String>();
        listSportAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sportListInterest);
        listSportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Travel List
        List<String> travelListInterest = new ArrayList<String>();
        listTravelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, travelListInterest);
        listTravelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Food List
        List<String> foodListInterest = new ArrayList<String>();
        listFoodAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, foodListInterest);
        listFoodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Outdoors List
        List<String> outdoorsListInterest = new ArrayList<String>();
        listOutdoorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, outdoorsListInterest);
        listOutdoorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Interest edit text
        interest_edit = (EditText)findViewById(R.id.interestEdit);

        // Add Button
        addInterest = (Button)findViewById(R.id.AddInterestButton);
        addInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addInterest();  }
        });

        // Remove Button
        removeInterest = (Button)findViewById(R.id.RemoveInterestButton);
        removeInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { removeInterest(); }
        });

        // Save changes button
        saveChangesButton = (Button) findViewById(R.id.saveSettingButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUpdateProfileToServer();
            }
        });

        // CheckBoxs
        menSelected = (CheckBox)findViewById(R.id.checkMen);
        womenSelected = (CheckBox)findViewById(R.id.checkWomen);

        // profileCreated Toast
        profileCreated = Toast.makeText(getApplicationContext(), getResources().getString(R.string.profile_uploaded_en), Toast.LENGTH_LONG);
    }

    /* Add interest in actual spinner */
    void addInterest() {
        Log.d(TAG, "Add Interest");
        String item = interest_edit.getText().toString();
        if (item.isEmpty()) {
            return;
        }
        String category = categoryList.getSelectedItem().toString();
        ArrayAdapter<String> adapter= getCategoryAdapter(category);

        if (adapter != null) {
            adapter.add(item);
        }
        interest_edit.setText("");
    }

    /* Remove interest selected from actual spinner */
    void removeInterest() {
        Log.d(TAG, "Remove Interest");
        String category = categoryList.getSelectedItem().toString();
        ArrayAdapter<String> adapter= getCategoryAdapter(category);
        adapter.remove((String) interestList.getSelectedItem());
        adapter.notifyDataSetChanged();
    }

    /* This function return the correspondent category ArrayAdapter */
    ArrayAdapter<String> getCategoryAdapter(String category) {
        ArrayAdapter<String> adapter = null;

        switch (category) {
            case "music":
                adapter = listMusicAdapter;
                break;
            case "musicband":
                adapter = listMusicBandAdapter;
                break;
            case "sport":
                adapter = listSportAdapter;
                break;
            case "travel":
                adapter = listTravelAdapter;
                break;
            case "food":
                adapter = listFoodAdapter;
                break;
            case "outdoors":
                adapter = listOutdoorsAdapter;
                break;
        }
        return adapter;
    }

    /* Updated profile is sending to Server. */
    private void sendUpdateProfileToServer() {
        // Json Data
        String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
        String uri = getResources().getString(R.string.update_profile_uri);

        // Latitude and longitude
        Double latitude = locationListener.getLatitude();
        Double longitude = locationListener.getLongitude();

        try {
            profile = new JSONObject(FileManager.readFile(getResources().getString(R.string.profile_filename), getApplicationContext()));
            JSONArray interestArray = new JSONArray();
            addInterestInJsonArray(getResources().getString(R.string.music_band_category), listMusicBandAdapter, interestArray);
            addInterestInJsonArray(getResources().getString(R.string.outdooors_category), listOutdoorsAdapter, interestArray);
            addInterestInJsonArray(getResources().getString(R.string.music_category), listMusicAdapter, interestArray);
            addInterestInJsonArray(getResources().getString(R.string.sport_category), listSportAdapter, interestArray);
            addInterestInJsonArray(getResources().getString(R.string.food_category), listFoodAdapter, interestArray);
            addInterestInJsonArray(getResources().getString(R.string.travel_category), listTravelAdapter, interestArray);

            if ( (menSelected.isChecked() && womenSelected.isChecked()) ||
                    (!menSelected.isChecked() && !womenSelected.isChecked())) {
                JSONObject interest = new JSONObject();
                interest.put(getResources().getString(R.string.category), getResources().getString(R.string.sex_category));
                interest.put(getResources().getString(R.string.value), getResources().getString(R.string.any));
                interestArray.put(interest);
            } else if (womenSelected.isChecked()) {
                JSONObject interest = new JSONObject();
                interest.put(getResources().getString(R.string.category), getResources().getString(R.string.sex_category));
                interest.put(getResources().getString(R.string.value), getResources().getString(R.string.women));
                interestArray.put(interest);
            } else if (menSelected.isChecked()) {
                JSONObject interest = new JSONObject();
                interest.put(getResources().getString(R.string.category), getResources().getString(R.string.sex_category));
                interest.put(getResources().getString(R.string.value), getResources().getString(R.string.men));
                interestArray.put(interest);
            }

            profile.remove(getResources().getString(R.string.interests));
            profile.put(getResources().getString(R.string.interests), interestArray);

            if (! ((latitude == 0.0) || (longitude == 0.0)) )  {
                JSONObject location = new JSONObject();
                location.put(getResources().getString(R.string.latitude), latitude);
                location.put(getResources().getString(R.string.longitude), longitude);
                profile.remove(getResources().getString(R.string.location));
                profile.put(getResources().getString(R.string.location), location);
            }

        } catch (JSONException e) {
            Log.w(TAG, "Can't create Json Profile Request");
        } catch (IOException e) {
            Log.e(TAG, "Can't read Profile File");
        }

        // Sending json data to Server
        if ( ActivityHelper.checkConection(getApplicationContext()) ){
            Log.d(TAG, "Send Profile to Server: " + String.valueOf(profile));
            loading.show();
            SendInterestTask checkLogin = new SendInterestTask();
            checkLogin.execute("POST", url, uri, profile.toString());
        } else {
            internetDisconnectWindow.show();
        }
        //checkSettingResponseFromServer("200:ok");
    }

    /* Add each interest of interest Array and save they in Json Array */
    private void addInterestInJsonArray(String category, ArrayAdapter<String> interestAdapter, JSONArray data) {
        for(int i=0 ; i < interestAdapter.getCount() ; i++){
            String interest = interestAdapter.getItem(i);
            JSONObject jsonInterest = new JSONObject();
            try {
                jsonInterest.put(getResources().getString(R.string.category), category);
                jsonInterest.put(getResources().getString(R.string.value), interest);
                data.put(jsonInterest);
            } catch (JSONException e) {
                Log.w(TAG, "Can't add interest in JsonProfile");
            }
        }
    }

    /* When Activity is created, profile interest are included in spinner adapters */
    private void includeProfileInterest() {
        try {
            JSONObject actualProfile = new JSONObject(FileManager.readFile(getResources().getString(R.string.profile_filename), getApplicationContext()));
            JSONArray interests = actualProfile.getJSONArray(getResources().getString(R.string.interests));

            for (int i = 0; i < interests.length(); ++i) {
                JSONObject interest = interests.getJSONObject(i);
                String category = interest.getString(getResources().getString(R.string.category));
                String value = interest.getString(getResources().getString(R.string.value));

                if (category.compareTo(getResources().getString(R.string.sex)) == 0) {
                    if (value.compareTo(getResources().getString(R.string.men)) == 0) {
                        menSelected.setChecked(true);
                    } else if (value.compareTo(getResources().getString(R.string.women)) == 0) {
                        womenSelected.setChecked(true);
                    } else if (value.compareTo(getResources().getString(R.string.any)) == 0) {
                        womenSelected.setChecked(true);
                        menSelected.setChecked(true);
                    }
                }
                ArrayAdapter<String> adapter= getCategoryAdapter(category);
                if (adapter != null) {
                    adapter.add(value);
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "Can't create Json Profile Request");
        } catch (IOException e) {
            Log.e(TAG, "Can't read Profile File");
        }
    }

    /* Check profile response from Server */
    private void checkSettingResponseFromServer(String response) {
        Log.d(TAG, "Response from Server is received: " + response);
        loading.dismiss();
        String responseCode = response.split(":", 2)[0];
        String responseMessage = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_upload_profile))) {
            profileCreated.show();
            // Update Profile
            try {
                FileManager.writeFile(getResources().getString(R.string.profile_filename), String.valueOf(profile), getApplicationContext());
            } catch (IOException e) {
                Log.e(TAG, "Can't write Profile File");
            }
        } else {
            unavailableServiceWindow.show();
        }
    }

    /* Send Login to Server */
    private class SendInterestTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkSettingResponseFromServer(dataGetFromServer);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
        Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
        startAppActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startAppActivity);
    }
}
