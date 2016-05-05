package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/* SettingsActivity has user interest. User can change its from differents categories and save changes
   (the new interests are send to Server) */
public class SettingsActivity extends AppCompatActivity {

    /* Attributes */
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
    private ProgressDialog loading;

    ProfileManager pf;
    JSONObject profile;

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

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle(getResources().getString(R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(R.string.internet_disconnect_error_en));

        // loadingWindow
        loading = new ProgressDialog(this);
        loading.setTitle(getResources().getString(R.string.please_wait_en));
        loading.setMessage(getResources().getString(R.string.log_processing_en));

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
                updateInterestOnClick();
            }
        });

        // CheckBoxs
        menSelected = (CheckBox)findViewById(R.id.checkMen);
        womenSelected = (CheckBox)findViewById(R.id.checkWomen);

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
    }

    /* Add interest in actual spinner */
    void addInterest() {
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
        String category = categoryList.getSelectedItem().toString();
        ArrayAdapter<String> adapter= getCategoryAdapter(category);
        adapter.remove((String) interestList.getSelectedItem());
        adapter.notifyDataSetChanged();
    }

    /* This function return the correspondent category ArrayAdapter */
    ArrayAdapter<String> getCategoryAdapter(String category) {
        ArrayAdapter<String> adapter = null;

        switch (category) {     // TODO: HACE FALTA AGREGAR LOS NOMBRES EN STRING.XML ?
            case "Music":
                adapter = listMusicAdapter;
                break;
            case "MusicBand":
                adapter = listMusicBandAdapter;
                break;
            case "Sport":
                adapter = listSportAdapter;
                break;
            case "Travel":
                adapter = listTravelAdapter;
                break;
            case "Food":
                adapter = listFoodAdapter;
                break;
            case "Outdoors":
                adapter = listOutdoorsAdapter;
                break;
        }
        return adapter;
    }


    /*  */
    private void updateInterestOnClick() {
        // Json Data
        String url = getResources().getString(R.string.server_ip);
        String uri = getResources().getString(R.string.interest_uri);
        pf = new ProfileManager(this);
        profile = pf.getProfile();

        try {
            JSONArray interestArray = new JSONArray();
            addInterestInJsonArray("MusicBand", listMusicBandAdapter, interestArray);
            addInterestInJsonArray("Outdoors", listOutdoorsAdapter, interestArray);
            addInterestInJsonArray("Music", listMusicAdapter, interestArray);
            addInterestInJsonArray("Sport", listSportAdapter, interestArray);
            addInterestInJsonArray("Food", listFoodAdapter, interestArray);
            addInterestInJsonArray("Travel", listTravelAdapter, interestArray);

            profile.remove(getResources().getString(R.string.interests));
            profile.put("interests",interestArray);
        } catch (JSONException e) {
            // ERROR
            // LOG
        }

        // Sending json data to Server
        loading.show();
        if ( checkConection() ){
            SendInterestTask checkLogin = new SendInterestTask();
            checkLogin.execute("POST", url, uri, profile.toString());
        } else {
            internetDisconnectWindow.show();
        }
        checkSettingResponse("200:ok");
    }

    private void addInterestInJsonArray(String category, ArrayAdapter<String> interests, JSONArray data) {
        for(int i=0 ; i < listMusicBandAdapter.getCount() ; i++){
            String interest = listMusicBandAdapter.getItem(i);
            JSONObject jsonInterest = new JSONObject();
            try {
                jsonInterest.put("category", category);
                jsonInterest.put("value", interest);
                data.put(jsonInterest);
            } catch (JSONException e) {
                //e.printStackTrace();
            }
        }
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

    /* Check profile response from Server */
    private void checkSettingResponse(String response) {
        loading.dismiss();
        String responseCode = response.split(":")[0];
        String responseMessage = response.split(":")[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_login))) { //TODO: DEFINIR MEJOR NOMBRE
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.interests_uploaded_en),
                    Toast.LENGTH_LONG).show();
            // Update Profile
            pf.updateProfile(profile);
        } else {
            // ERROR
        }
    }

    /* Send Login to Server */
    private class SendInterestTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkSettingResponse(dataGetFromServer);
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
