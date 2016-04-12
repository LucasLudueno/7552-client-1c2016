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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    /* Attributes */
    Spinner musicList;
    ArrayAdapter<String> listMusicAdapter;
    Spinner outdoorsList;
    ArrayAdapter<String> listOutdoorsAdapter;
    Button addMusic;
    Button addOutdoor;
    EditText music_band_edit;
    EditText outdoor_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);

        // Add the back activity button in the toolbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Category List

        // Music List
        List<String> musicListItems = new ArrayList<String>();
        musicListItems.add("");
        musicListItems.add("Pink Floyd");
        musicListItems.add("Beatles");
        musicList = (Spinner)findViewById(R.id.music_band_list);
        listMusicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, musicListItems);
        listMusicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        musicList.setAdapter(listMusicAdapter);

        // Outdoors List
        List<String> outdoorsListItems = new ArrayList<String>();
        outdoorsListItems.add("");
        outdoorsListItems.add("Running");
        outdoorsListItems.add("Swimming");
        outdoorsListItems.add("Nothing");
        outdoorsList = (Spinner)findViewById(R.id.outdoors_list);
        listOutdoorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, outdoorsListItems);
        listOutdoorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outdoorsList.setAdapter(listOutdoorsAdapter);

        // Add Buttons
        addMusic = (Button)findViewById(R.id.AddMusicBandButton);
        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMusicItem();
            }
        });

        addOutdoor = (Button)findViewById(R.id.AddOutdoorsButton);
        addOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOutdoorItem();
            }
        });

        // Interest edit text
        music_band_edit = (EditText)findViewById(R.id.music_band_edit);
        outdoor_edit = (EditText)findViewById(R.id.music_band_edit);

    }

    void addMusicItem() {
        String music = music_band_edit.getText().toString();
        if (!music.isEmpty()) {
            listMusicAdapter.add(music);
            music_band_edit.setText("");
        }
    }

    void addOutdoorItem() {
        String outdoor = outdoor_edit.getText().toString();
        if (!outdoor.isEmpty()) {
            listOutdoorsAdapter.add(outdoor);
            outdoor_edit.setText("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
