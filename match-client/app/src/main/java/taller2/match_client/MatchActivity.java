package taller2.match_client;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONObject;

/* Match Activity show matches and open chat Activity when some match is clecked */
public class MatchActivity extends AppCompatActivity {
    private MatchListAdapter matchListAdapter;
    private ListView matchListView;
    private MatchManager matchManager;
    private String userEmail = "";

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.matchToolbar);
        setSupportActionBar(toolbar);

        // Add back activity button in the toolbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // UserMail
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userEmail = bundle.getString(getResources().getString(R.string.email));
        }

        /***  Match Manager ***/
        matchManager = MatchManager.getInstance();

        // MatchList
        matchListAdapter = new MatchListAdapter(getApplicationContext());
        matchListView = (ListView) findViewById(R.id.matchList);
        matchListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        matchListView.setAdapter(matchListAdapter);

        // When some match is clicked, its chat activity is created
        matchListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                createChat(matchListAdapter.getEmail(position));
            }
        });

        matchListAdapter = matchManager.getMatchListAdapter();
        matchListView.setAdapter(matchListAdapter);


        // come back to principal activity
        Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
        startAppActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startAppActivity);
    }

    /* When some match are taken, its chat is created */
    private void createChat(String email) {
        Intent startChatActivity = new Intent(this, ChatActivity.class);
        startChatActivity.putExtra(getResources().getString(R.string.email), String.valueOf(email));
        startActivity(startChatActivity);
    }

    /* Handle menu item click */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {    // Back to previus Activity
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
