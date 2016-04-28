package taller2.match_client;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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
    private EditText interest_edit;
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
