package taller2.match_client;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    /* Attributes */
    private Spinner musicBandList;
    private Spinner musicList;
    private Spinner outdoorsList;
    private Spinner sportList;
    private Spinner foodList;
    private Spinner travelList;

    ArrayAdapter<String> listMusicBandAdapter;
    ArrayAdapter<String> listOutdoorsAdapter;
    ArrayAdapter<String> listMusicAdapter;
    ArrayAdapter<String> listSportAdapter;
    ArrayAdapter<String> listFoodAdapter;
    ArrayAdapter<String> listTravelAdapter;

    private Button addMusicBand;
    private Button addOutdoor;
    private Button addMusic;
    private Button addTravel;
    private Button addFood;
    private Button addSport;
    private Button removeMusicBand;
    private Button removeOutdoor;
    private Button removeMusic;
    private Button removeSport;
    private Button removeTravel;
    private Button removeFood;

    private EditText music_band_edit;
    private EditText outdoor_edit;
    private EditText music_edit;
    private EditText food_edit;
    private EditText travel_edit;
    private EditText sport_edit;

    private CheckBox menSelected;
    private CheckBox womenSelected;

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

        /* Category List */

        // Music Band List
        List<String> musicBandListItems = new ArrayList<String>();
        musicBandList = (Spinner)findViewById(R.id.music_band_list);
        listMusicBandAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicBandListItems);
        listMusicBandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        musicBandList.setAdapter(listMusicBandAdapter);

        // Music List
        List<String> musicListItems = new ArrayList<String>();
        musicList = (Spinner)findViewById(R.id.music_list);
        listMusicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicListItems);
        listMusicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        musicList.setAdapter(listMusicAdapter);

        // Sport List
        List<String> sportListItems = new ArrayList<String>();
        sportList = (Spinner)findViewById(R.id.sports_list);
        listSportAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicListItems);
        listSportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportList.setAdapter(listSportAdapter);

        // Travel List
        List<String> travelListItems = new ArrayList<String>();
        travelList = (Spinner)findViewById(R.id.travel_list);
        listTravelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicListItems);
        listTravelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelList.setAdapter(listTravelAdapter);

        // Food List
        List<String> foodListItems = new ArrayList<String>();
        foodList = (Spinner)findViewById(R.id.food_list);
        listFoodAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicListItems);
        listFoodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodList.setAdapter(listFoodAdapter);

        // Outdoors List
        List<String> outdoorsListItems = new ArrayList<String>();
        outdoorsListItems.add("Running");
        outdoorsListItems.add("Swimming");
        outdoorsListItems.add("Nothing");
        outdoorsList = (Spinner)findViewById(R.id.outdoors_list);
        listOutdoorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, outdoorsListItems);
        listOutdoorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outdoorsList.setAdapter(listOutdoorsAdapter);

        // Interest edit text
        music_band_edit = (EditText)findViewById(R.id.music_band_edit);
        outdoor_edit = (EditText)findViewById(R.id.outdoors_edit);
        music_edit = (EditText)findViewById(R.id.music_edit);
        food_edit = (EditText)findViewById(R.id.food_edit);
        sport_edit = (EditText)findViewById(R.id.sports_edit);
        travel_edit = (EditText)findViewById(R.id.travel_edit);

        // Add Buttons
        addMusicBand = (Button)findViewById(R.id.AddMusicBandButton);
        addMusicBand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addItem(music_band_edit, listMusicBandAdapter);
            }
        });

        addOutdoor = (Button)findViewById(R.id.AddOutdoorsButton);
        addOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addItem(outdoor_edit, listOutdoorsAdapter);
            }
        });

        addMusic = (Button)findViewById(R.id.AddMusicButton);
        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addItem(music_edit, listMusicAdapter);
            }
        });

        addFood = (Button)findViewById(R.id.AddFoodButton);
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addItem(food_edit, listFoodAdapter);
            }
        });

        addTravel = (Button)findViewById(R.id.AddTravelButton);
        addTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addItem(travel_edit, listTravelAdapter);
            }
        });

        addSport = (Button)findViewById(R.id.AddSportsButton);
        addSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addItem(sport_edit, listSportAdapter);
            }
        });

        // Remove Buttons
        removeMusicBand = (Button)findViewById(R.id.RemoveMusicBandButton);
        removeMusicBand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { removeItem(musicBandList, listMusicBandAdapter); }
        });

        removeOutdoor = (Button)findViewById(R.id.RemoveOutdoorsButton);
        removeOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { removeItem(outdoorsList, listOutdoorsAdapter);
            }
        });

        removeMusic = (Button)findViewById(R.id.RemoveMusicButton);
        removeMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { removeItem(musicList, listMusicAdapter);
            }
        });

        removeTravel = (Button)findViewById(R.id.RemoveTravelButton);
        removeTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { removeItem(travelList, listTravelAdapter);
            }
        });

        removeFood = (Button)findViewById(R.id.RemoveFoodButton);
        removeFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { removeItem(foodList, listFoodAdapter);
            }
        });

        removeSport = (Button)findViewById(R.id.RemoveSportsButton);
        removeSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { removeItem(sportList, listSportAdapter);
            }
        });

        // CheckBoxs
        menSelected = (CheckBox)findViewById(R.id.checkMen);
        womenSelected = (CheckBox)findViewById(R.id.checkWomen);
    }

    /* Add item in spinner */
    void addItem(EditText edit, ArrayAdapter<String> adapter) {
        String item = edit.getText().toString();
        if (!item.isEmpty()) {
            adapter.add(item);
            edit.setText("");
        }
    }

    /* Remove item from spinner */
    void removeItem(Spinner list, ArrayAdapter<String> adapter) {
          adapter.remove((String) list.getSelectedItem());
          adapter.notifyDataSetChanged();
    }

    /* Handle menu item click */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
