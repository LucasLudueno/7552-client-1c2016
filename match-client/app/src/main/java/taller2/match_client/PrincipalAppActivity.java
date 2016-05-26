package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* PrincipalAppActivity manage other Activities, possible matches, matches and conversations */
public class PrincipalAppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* Attributes */
    private AlertDialog internetDisconnectWindow;
    private ProgressDialog connectingToServerWindow;

    private ImageView possibleMatchPhoto;
    private TextView possibleMatchAlias;
    private CardView possibleMatchCard;
    private Toast noMatchesAvailableInFront;
    private Toast noMatchesAvailableForDay;
    private MatchManager matchManager;
    private JSONObject actualMatch = null;
    private JSONObject noMatch = null;
    //private ArrayList<JSONObject> possibleMatchesBuffer;
    private PossibleMatchBuffer possibleMatchesBuffer;
    private Base64Converter bs64;

    private String userEmail = "";
    protected static final int GET_MATCH_SLEEP_TIME = 120000;         // 2 min
    protected static final int GET_POS_MATCH_SLEEP_TIME = 60000;      // 1 min
    protected static final int GET_CONVERSATION_SLEEP_TIME = 120000;  // 2 min
    protected static final int GET_MATCH_CODE = 1;
    protected static final int GET_CONVERSATION_CODE = 2;
    protected static final int GET_POS_MATCH_CODE = 3;
    protected static final int MIN_POS_MATCHES_COUNT = 1;
    protected static final int POS_MATCH_COUNT_TO_REQUEST = 2;


    /*** MockServer ***/
    MockServer mockServer;

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_app);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.principalAppToolbar);
        setSupportActionBar(toolbar);

        // Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Help Windows
        createHelpWindows();

        // Views
        instantiateViews();

        // Match photo. Set Size.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        possibleMatchPhoto = (ImageView)findViewById(R.id.possibleMatchPhoto);
        possibleMatchPhoto.getLayoutParams().height = (5 * height) / 10;

        possibleMatchCard = (CardView)findViewById(R.id.card_view);
        possibleMatchCard.getLayoutParams().height = (6 * height) / 10;

        possibleMatchAlias = (TextView)findViewById(R.id.possibleMatchAlias);

        // UserMail
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userEmail = bundle.getString(getResources().getString(R.string.email));
        }

        // Possible Matches
        possibleMatchesBuffer = new PossibleMatchBuffer();

        // Base64 Converter
        bs64 = new Base64Converter();

        // User photo and alias in NavHead
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        ImageView userPhoto = (ImageView) hView.findViewById(R.id.userPhotoInPrincipalApp);
        TextView userAlias = (TextView) hView.findViewById(R.id.userAliasInPrincipalApp);

        JSONObject profile = null;
        String alias = "";
        String userPhotoInB64 = "";
        try {
            profile = new JSONObject(FileManager.readFile(getResources().getString(R.string.profile_filename), getApplicationContext()));
            alias = profile.getString(getResources().getString(R.string.alias));
            userPhotoInB64 = profile.getString(getResources().getString(R.string.profilePhoto));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Base64Converter bs64 = new Base64Converter();
        Bitmap userPhotoBitmap = MatchListAdapter.getRoundedShape(bs64.Base64ToBitmap(userPhotoInB64), 200);
        userPhoto.setImageBitmap(userPhotoBitmap);  // Set Profile Photo
        userAlias.setText(alias);                   // Set Alias

        /*** Match Manager: load saved matches and conversations ***/
        matchManager = MatchManager.getInstance();
        String conversationFileName =  getResources().getString(R.string.conversation_prefix_filename) + userEmail;
        String matchFileName = getResources().getString(R.string.matches_prefix_filename) + userEmail;
        //matchManager.setData(getApplicationContext(), matchFileName, conversationFileName, userEmail);
        matchManager.setData(getApplicationContext(), "", "", userEmail);   // TODO: PARA QUE NO QUEDEN GUARDADOS LOS MATCH

        // Timers
        new Thread(new GetMatches()).start();
        new Thread(new GetConversations()).start();
        new Thread(new GetPossibleMatches()).start();

        // Update
        //updatePosMatch();

        /*** Mock Server***/
        mockServer = MockServer.getInstance();
    }

    /* Create windows that are showed to users to comunicate something (error, information) */
    private void createHelpWindows() {
        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle(getResources().getString(R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(R.string.internet_disconnect_error_en));

        // loadingWindow
        connectingToServerWindow = new ProgressDialog(this);
        connectingToServerWindow.setTitle(getResources().getString(R.string.please_wait_en));
        connectingToServerWindow.setMessage(getResources().getString(R.string.sending_interest_en));

        // Toast
        noMatchesAvailableInFront = Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_possible_matches_are_available_en), Toast.LENGTH_LONG);
        noMatchesAvailableForDay = Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_more_pos_matches_available_en), Toast.LENGTH_LONG);
    }

    /* Instantiate views inside Activity and keep it in attibutes */
    private void instantiateViews() {
        // Like Icon
        FloatingActionButton likeIcon = (FloatingActionButton) findViewById(R.id.likeIcon);
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInterestOfPosMatchToServer(getResources().getString(R.string.like_uri));
            }
        });

        // Dont Like Icon
        FloatingActionButton dontlikeIcon = (FloatingActionButton) findViewById(R.id.dontLikeIcon);
        dontlikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInterestOfPosMatchToServer(getResources().getString(R.string.dont_like_uri));
            }
        });

        // Match Icon
        FloatingActionButton matchIcon = (FloatingActionButton) findViewById(R.id.matchIcon);
        matchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGetPossibleMatchRequestToServer();
                updatePosMatch();
            }
        });
    }

    /* Close drawer if its open */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAplication();
            //super.onBackPressed();
        }
    }

    /* This method finish all activities and aplication */
    public void finishAplication() {
        this.finish();
        Intent finishAplication = new Intent(Intent.ACTION_MAIN);
        finishAplication.addCategory(Intent.CATEGORY_HOME);
        finishAplication.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(finishAplication);
    }

    /* Inflate Menu when menu button is pressed */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal_app, menu);
        return true;
    }

    /* Handle menu item clicks */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Handle navigation view item clicks and then create Activities */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent startSettingActivity = new Intent(this, SettingsActivity.class);
            startSettingActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(startSettingActivity);
        } else if (id == R.id.nav_information) {

        } else if (id == R.id.nav_perfil) {
            Intent startProfileActivity = new Intent(this, ProfileActivity.class);
            startProfileActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(startProfileActivity);
        } else if (id == R.id.nav_chat) {
            Intent startMatchActivity = new Intent(this, MatchActivity.class);
            startMatchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(startMatchActivity);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* Update photo and alias of possible match. If possible match buffer is empty,
     * no_match user is setted */
    private void updatePosMatch() {
        if (possibleMatchesBuffer.size() > 0) {
            possibleMatchCard.setVisibility(View.VISIBLE);
            Random r = new Random();
            int random = r.nextInt(possibleMatchesBuffer.size());

            if (actualMatch != null) {
                possibleMatchesBuffer.add(actualMatch);
            }
            actualMatch = possibleMatchesBuffer.get(random);
            possibleMatchesBuffer.remove(random);
        } else {
            if (actualMatch == null) {
                possibleMatchCard.setVisibility(View.INVISIBLE);
            }
            return;
        }

        // set possible match on Principal Card
        try {
            possibleMatchPhoto.setImageBitmap(bs64.Base64ToBitmap(actualMatch.getString(getResources().getString(R.string.photoProfile))));
            possibleMatchAlias.setText(actualMatch.getString(getResources().getString(R.string.alias)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Send a Get request to Server asking if there are more possible match if we have more than
     * MIN_POS_MATCHES_COUNT. In other case,updatePosMatch function is called */
    private void sendGetPossibleMatchRequestToServer() {
        // construct possible match request
        JSONObject posMatchRequest = new JSONObject();
        try {
            posMatchRequest.put(getResources().getString(R.string.email), userEmail);
            posMatchRequest.put(getResources().getString(R.string.pos_match_count), POS_MATCH_COUNT_TO_REQUEST);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // send request if there are not possible matches
        if (possibleMatchesBuffer.size() <= MIN_POS_MATCHES_COUNT) {
                    /*if (ActivityHelper.checkConection(getApplicationContext())) {
                        String url = getResources().getString(R.string.server_ip);
                        String uri = getResources().getString(R.string.get_pos_matches_uri);;
                        SendGetPossibleMatchsTask getPossibleMatches = new SendGetPossibleMatchsTask();
                        getPossibleMatches.execute("POST", url, uri, posMatchRequest.toString());
                    } else {
                        internetDisconnectWindow.show();
                    }*/
            checkGetPosMatchResponseFromServer(mockServer.getPossibleMatches(posMatchRequest.toString()));
        } /*else {
            updatePosMatch();
        }*/
    }

    /* When user touch like or dont like icon, if there is a possible match on screen,
     * interest request is send to Server */
    private void sendInterestOfPosMatchToServer(String sendUri) {
        if (actualMatch == null) {
            noMatchesAvailableInFront.show();
            return;
        }
        //if (ActivityHelper.checkConection(getApplicationContext())) {
            JSONObject interestMatches = new JSONObject();
            String pos_match_email = "";
            try {
                pos_match_email = actualMatch.getString(getResources().getString(R.string.email));
                interestMatches.put(getResources().getString(R.string.email_src),userEmail);
                interestMatches.put(getResources().getString(R.string.email_dst), pos_match_email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            connectingToServerWindow.show();
            /*String url = getResources().getString(R.string.server_ip);
            String uri = sendUri;
            SendInterestOfPosMatchTask sendPosMatchInterest = new SendInterestOfPosMatchTask();
            sendPosMatchInterest.execute("POST", url, uri, pos_match_email);*/
            checkInterestPosMatchResponseFromServer(mockServer.like_dont(interestMatches.toString()));
        //} else {
            //internetDisconnectWindow.show();
        //}
    }

    /* Check response from Server after sending possible match interest. If response is ok
     * actual possible match is remove and updatePosMatch is called */
    private void checkInterestPosMatchResponseFromServer(String response) {
        String responseCode = response.split(":", 2)[0];
        String responseMessage = response.split(":", 2)[1];
        connectingToServerWindow.dismiss();

        if (responseCode.compareTo(getResources().getString(R.string.ok_response_code_send_pos_match_interest)) == 0) {
            //possibleMatchesBuffer.remove(actualMatch);
            actualMatch = null;   //TODO: DESREFERENCIO, ALGUNA FORMA DE BORRARLO ?
            updatePosMatch();
        }
    }

    /* Check response from Server after sending get possible matches request. If new pos matches are
     * received this ones are saving into buffer. */
    private void checkGetPosMatchResponseFromServer(String response) {
        String responseCode = response.split(":", 2)[0];
        String possibleMatches = response.split(":", 2)[1];

        if (responseCode.compareTo(getResources().getString(R.string.ok_response_code_get_pos_matches)) == 0) {
            try {
                JSONObject possibleMatchesjson = new JSONObject(possibleMatches);
                JSONArray posMatchArray = possibleMatchesjson.getJSONArray(getResources().getString(R.string.possible_matches));

                for (int i = 0; i < posMatchArray.length(); ++i) {
                    JSONObject posMatch = posMatchArray.getJSONObject(i);
                    this.possibleMatchesBuffer.add(posMatch);
                }

                if (actualMatch == null && possibleMatchesBuffer.size() > 0) {
                    updatePosMatch();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /* Check response from Server after sending get matches request. If new matches are
    * received, MatchManager keep this ones. */
    private void checkGetMatchResponseFromServer(String response) {
        String responseCode = response.split(":", 2)[0];
        String matches = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_login))) {
            JSONObject matchData = null;
            JSONArray matchesArray = null;
            try {
                matchData = new JSONObject(matches);
                matchesArray = matchData.getJSONArray(getResources().getString(R.string.matches));
                for (int i = 0; i < matchesArray.length(); ++i) {
                    JSONObject match = matchesArray.getJSONObject(i);
                    matchManager.addMatch(match);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (matchesArray.length() > 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.new_matches_en),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /* Check response from Server after sending get conversation request. If new conversations are
    * received, MatchManager keep this ones. */
    private void checkGetConversationResponseFromServer(String response) {
        String responseCode = response.split(":", 2)[0];
        String conversation = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_login))) {
            JSONObject conversationJson = null;
            try {
                conversationJson = new JSONObject(conversation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            matchManager.addConversation(conversationJson);
        }
    }

    /* Send Interest of possible match to Server */
    private class SendInterestOfPosMatchTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkInterestPosMatchResponseFromServer(dataGetFromServer);
        }
    }

    /* Send get possible match request to Server */
    private class SendGetPossibleMatchsTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer) {
            checkGetPosMatchResponseFromServer(dataGetFromServer);
        }
    }

    /* Send get match request to Server */
    private class SendGetMatchsTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer) {
            checkGetMatchResponseFromServer(dataGetFromServer);
        }
    }

    /* Thread Handler, handle get match and get conversation events */
    Handler matchManagerHandler = new Handler() { // TODO: RECOMENDACION DE STATIC ?? MUTEX ?
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_MATCH_CODE:    // Send Get match request to Server
                    //SendGetMatchsTask getMatchs = new SendGetMatchsTask();
                    JSONObject userMailJson = new JSONObject();
                    try {
                        userMailJson.put(getResources().getString(R.string.email), userEmail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    checkGetMatchResponseFromServer(mockServer.getMatches(userMailJson.toString()));
                    break;
                case GET_CONVERSATION_CODE: // Send Get conversation request to Server
                    List<JSONObject> matches = matchManager.getMatches();
                    for (int i = 0; i < matches.size(); ++i){
                        JSONObject match = matches.get(i);
                        String matchEmail = "";
                        JSONObject convRequest = new JSONObject();
                        try {
                            matchEmail = match.getString(getResources().getString(R.string.email));
                            convRequest.put(getResources().getString(R.string.email_src),
                                    matchEmail);
                            convRequest.put(getResources().getString(R.string.email_dst),
                                    userEmail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //SendGetMatchsTask getMatchs = new SendGetMatchsTask();
                        checkGetConversationResponseFromServer(
                                mockServer.getConversation(convRequest.toString()));
                    }
                    break;
                case GET_POS_MATCH_CODE: // Send Get Possible Match request to Server
                    sendGetPossibleMatchRequestToServer();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /* Get Matches Thread. After a time send get matches request to Server */
    class GetMatches implements Runnable {
        public void run() {
            while (! Thread.currentThread().isInterrupted()) {
                Message message = new Message();
                message.what = GET_MATCH_CODE;
                PrincipalAppActivity.this.matchManagerHandler.sendMessage(message);

                try {
                    Thread.sleep(GET_MATCH_SLEEP_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /* Get Conversations Thread. After a time send get conversation request to Server */
    class GetConversations implements Runnable {
        public void run() {
            while (! Thread.currentThread().isInterrupted()) {
                Message message = new Message();
                message.what = GET_CONVERSATION_CODE;
                PrincipalAppActivity.this.matchManagerHandler.sendMessage(message);

                try {
                    Thread.sleep(GET_CONVERSATION_SLEEP_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /* Get Matches Thread. After a time send get matches request to Server */
    class GetPossibleMatches implements Runnable {
        public void run() {
            while (! Thread.currentThread().isInterrupted()) {
                Message message = new Message();
                message.what = GET_POS_MATCH_CODE;
                PrincipalAppActivity.this.matchManagerHandler.sendMessage(message);

                try {
                    Thread.sleep(GET_POS_MATCH_SLEEP_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
