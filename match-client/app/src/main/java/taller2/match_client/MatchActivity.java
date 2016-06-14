package taller2.match_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;

/* Match Activity show matches and open chat Activity when some match is clecked */
public class MatchActivity extends AppCompatActivity {
    private MatchList matchList;
    private ListView matchListView;
    private MatchManagerProxy matchManager;

    private static final String TAG = "MatchActivity";

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

        /***  Match Manager ***/
        matchManager = MatchManagerProxy.getInstance();

        // MatchList
        matchList = matchManager.getMatchList();
        matchListView = (ListView) findViewById(R.id.matchList);
        matchListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        matchListView.setAdapter(matchList);

        // When some match is clicked, its chat activity is created
        matchListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                createChat(matchList.getEmail(position));
            }
        });
        Log.i(TAG, "MatchActivity is created");

        // come back to principal activity
        Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
        startAppActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startAppActivity);
    }

    /* When some match are taken, its chat is created */
    private void createChat(String email) {
        Log.i(TAG, "Create ChatActivity");
        String alias = "";
        try {
            alias = matchManager.getMatch(email).getString(getResources().getString(R.string.alias));
        } catch (JSONException e) {
            Log.w(TAG, "Can't get match from matchManager");
        }
        Intent startChatActivity = new Intent(this, ChatActivity.class);
        startChatActivity.putExtra(getResources().getString(R.string.email), String.valueOf(email));
        startChatActivity.putExtra(getResources().getString(R.string.alias), String.valueOf(alias));
        startActivity(startChatActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match, menu);
        return true;
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
        Log.i(TAG, "Back to previous Activity");
        Intent startAppActivity = new Intent(this, PrincipalAppActivity.class);
        startAppActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startAppActivity);
    }
}
