package taller2.match_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import taller2.match_client.Helpers.ActivityHelper;
import taller2.match_client.Helpers.Base64Converter;
import taller2.match_client.Helpers.FileManager;
import taller2.match_client.Match_Manage.MatchList;
import taller2.match_client.Match_Manage.MatchManagerProxy;
import taller2.match_client.Request.ClientToServerTask;
import taller2.match_client.Test.MockServer;

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
    private MatchManagerProxy matchManager;
    private JSONObject actualMatch = null;
    private PossibleMatchBuffer possibleMatchesBuffer;
    private Base64Converter bs64;
    private Menu menu = null;
    private Animation left_up_animation;
    private Animation right_up_animation;
    private TextView likeShape;
    private TextView dontLikeShape;
    private FloatingActionButton likeIcon;
    private FloatingActionButton dontlikeIcon;

    private Thread getMatchTimer;
    private Thread getPosMatchTimer;
    private Thread getConversationTimer;
    private Thread checkTokenTimer;
    private Thread updateConversationTimer;

    private String userEmail = "";
    private String token = "";
    private boolean areConversationLoad = false;
    protected static final int GET_MATCH_SLEEP_TIME = 20000;           // 20 seg
    protected static final int GET_POS_MATCH_SLEEP_TIME = 10000;       // 10 seg
    protected static final int GET_CONVERSATION_SLEEP_TIME = 20000;    // 30 seg
    protected static final int UPDATE_CONVERSATION_SLEEP_TIME = 5000;  // 5 seg
    protected static final int CHECK_TOKEN_SLEEP_TIME = 20000;         // 20 seg
    protected static final int GET_MATCH_CODE = 1;
    protected static final int GET_CONVERSATION_CODE = 2;
    protected static final int GET_POS_MATCH_CODE = 3;
    protected static final int UPDATE_CONVERSATIONS_CODE = 4;
    protected static final int CHECK_TOKEN_CODE = 5;
    protected static final int MIN_POS_MATCHES_COUNT = 1;
    protected static final int POS_MATCH_COUNT_TO_REQUEST = 2;
    private static final String TAG = "PrincipalActivity";

    ProfileActivity profileActivity;
    SettingsActivity settingsActivity;
    MatchActivity matchActivity;

    /*** MockServer ***/
    MockServer mockServer;

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(taller2.match_client.R.layout.activity_principal_app);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(taller2.match_client.R.id.principalAppToolbar);
        setSupportActionBar(toolbar);

        // Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(taller2.match_client.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, taller2.match_client.R.string.navigation_drawer_open, taller2.match_client.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Help Windows
        createHelpWindows();

        // Views
        instantiateViews();

        // UserMail
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userEmail = bundle.getString(getResources().getString(taller2.match_client.R.string.email));
            token = bundle.getString(getResources().getString(taller2.match_client.R.string.token));
        }

        // Possible Matches
        possibleMatchesBuffer = new PossibleMatchBuffer();

        // Base64 Converter
        bs64 = new Base64Converter();

        // Update Head Profile
        updateHeadProfile();

        /*** Match Manager: load saved matches and conversations ***/
        matchManager = MatchManagerProxy.getInstance();
        matchManager.initialize(userEmail, getApplicationContext());
        Log.d(TAG, "Initialize MatchManager");

        // Timers
        getMatchTimer = new Thread(new GetMatches());
        getMatchTimer.start();
        getPosMatchTimer = new Thread(new GetPossibleMatches());
        getPosMatchTimer.start();
        checkTokenTimer = new Thread(new CheckToken());
        checkTokenTimer.start();

        /*** Mock Server***/
        //mockServer = new MockServer(getApplicationContext());

        Log.i(TAG, "Principal Activity is created");

        // load match activity
        matchActivity = new MatchActivity();
        profileActivity = new ProfileActivity();
        settingsActivity = new SettingsActivity();
        Log.i(TAG, "Create Match Activity");
        Intent startMatchActivity = new Intent(this, matchActivity.getClass());
        startMatchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startMatchActivity);
    }

    /* Set up conversation in Match Manager */
    private void loadConversationsFromFile() {
        // load conversations
        String conversationsFileName =  getResources().getString(taller2.match_client.R.string.conversation_prefix_filename) + userEmail;
        String conversationsList = "";

        // if files don't exist, then create.
        if(!FileManager.fileExists(conversationsFileName, getApplicationContext())) {
            Log.d(TAG, "Conversation file does not exists");
            try {
                JSONObject conv = new JSONObject();
                JSONArray convArray = new JSONArray();
                conv.put("conversations", convArray);
                FileManager.writeFile(conversationsFileName, conv.toString(), getApplicationContext());
            } catch (JSONException e) {
                Log.d(TAG, "Can't construct Conversation file");
            } catch (IOException e) {
                Log.d(TAG, "Can't write Conversation file");
            }
        }

        // read file
        try {
            conversationsList = FileManager.readFile(conversationsFileName, this);
        } catch (IOException e) {
            Log.w(TAG, "Can't read Match File and Conversation File");
        }

        // add conversations
        try {
            JSONObject matchConversations = new JSONObject(conversationsList);
            JSONArray conversationsArray = matchConversations.getJSONArray("conversations");
            for (int i = 0; i < conversationsArray.length(); ++i) {
                JSONObject conversation = conversationsArray.getJSONObject(i);
                matchManager.addConversation(conversation, false);
            }
        } catch (JSONException e) {
            Log.w(TAG, "Error while construct conversation Json");
        }

        areConversationLoad = true;
        //Start timers
        getConversationTimer = new Thread(new GetConversations());
        getConversationTimer.start();
        updateConversationTimer = new Thread(new UpdateConversations());
        updateConversationTimer.start();
    }

    /* Create windows that are showed to users to comunicate something (error, information) */
    private void createHelpWindows() {
        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle(getResources().getString(taller2.match_client.R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(taller2.match_client.R.string.internet_disconnect_error_en));

        // loadingWindow
        connectingToServerWindow = new ProgressDialog(this);
        connectingToServerWindow.setTitle(getResources().getString(taller2.match_client.R.string.please_wait_en));
        connectingToServerWindow.setMessage(getResources().getString(taller2.match_client.R.string.sending_interest_en));

        // Toast
        noMatchesAvailableInFront = Toast.makeText(getApplicationContext(), getResources().getString(taller2.match_client.R.string.no_possible_matches_are_available_en), Toast.LENGTH_LONG);
    }

    /* Instantiate views inside Activity and keep it in attibutes */
    private void instantiateViews() {
        //Animations
        left_up_animation = AnimationUtils.loadAnimation(getApplicationContext(), taller2.match_client.R.anim.swing_up_left);
        right_up_animation = AnimationUtils.loadAnimation(getApplicationContext(), taller2.match_client.R.anim.swing_up_right);

        // Like and Dont Like Shapes
        likeShape = (TextView)findViewById(taller2.match_client.R.id.likeShape);
        dontLikeShape = (TextView)findViewById(taller2.match_client.R.id.dontLikeShape);

        // Like Icon
        likeIcon = (FloatingActionButton) findViewById(taller2.match_client.R.id.likeIcon);
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualMatch != null) {
                    likeShape.setVisibility(View.VISIBLE);
                    possibleMatchCard.startAnimation(right_up_animation);
                    likeIcon.setEnabled(false);
                    dontlikeIcon.setEnabled(false);
                }
                (new LikeDontTimer(getResources().getInteger(taller2.match_client.R.integer.card_refresh), 1,
                        getResources().getString(taller2.match_client.R.string.like_uri))).start();
            }
        });

        // Dont Like Icon
        dontlikeIcon = (FloatingActionButton) findViewById(taller2.match_client.R.id.dontLikeIcon);
        dontlikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualMatch != null) {
                    dontLikeShape.setVisibility(View.VISIBLE);
                    possibleMatchCard.startAnimation(left_up_animation);
                    likeIcon.setEnabled(false);
                    dontlikeIcon.setEnabled(false);
                }
                (new LikeDontTimer(getResources().getInteger(taller2.match_client.R.integer.card_refresh), 1,
                        getResources().getString(taller2.match_client.R.string.dont_like_uri))).start();
            }
        });

        // Match Icon
        FloatingActionButton matchIcon = (FloatingActionButton) findViewById(taller2.match_client.R.id.matchIcon);
        matchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (possibleMatchesBuffer.size() <= 0) {
                    if (ActivityHelper.checkConection(getApplicationContext())) {
                        sendGetPossibleMatchRequestToServer();
                    } else {
                        internetDisconnectWindow.show();
                    }
                } else {
                    updatePosMatch();
                }
            }
        });

        // Match photo. Set Size.
        /*Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;*/

        possibleMatchCard = (CardView)findViewById(taller2.match_client.R.id.card_view);
        ///possibleMatchCard.getLayoutParams().height = (55 * height) / 100;

        possibleMatchPhoto = (ImageView)findViewById(taller2.match_client.R.id.possibleMatchPhoto);
        //possibleMatchPhoto.getLayoutParams().height = (5 * height) / 10;
        //possibleMatchPhoto.getLayoutParams().width = (5 * height) / 10;

        possibleMatchAlias = (TextView)findViewById(taller2.match_client.R.id.possibleMatchAlias);
        possibleMatchCard.setVisibility(View.INVISIBLE);

        View v = findViewById(taller2.match_client.R.id.drawer_layout); // Change background
        v.setBackground(getResources().getDrawable(taller2.match_client.R.drawable.no_possible_match));
    }

    /* Update UserProfile profile photo and Alias in NavigationHead */
    private void updateHeadProfile() {
        Log.d(TAG, "Update head photo and alias in navigationHeadView");
        // UserProfile photo and alias in NavHead
        NavigationView navigationView = (NavigationView) findViewById(taller2.match_client.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        ImageView userPhoto = (ImageView) hView.findViewById(taller2.match_client.R.id.userPhotoInPrincipalApp);
        TextView userAlias = (TextView) hView.findViewById(taller2.match_client.R.id.userAliasInPrincipalApp);

        JSONObject profile = null;
        String alias = "";
        String userPhotoInB64 = "";
        try {
            profile = new JSONObject(FileManager.readFile(getResources().getString(taller2.match_client.R.string.profile_filename), getApplicationContext()));
            alias = profile.getString(getResources().getString(taller2.match_client.R.string.alias));
            userPhotoInB64 = profile.getString(getResources().getString(taller2.match_client.R.string.profilePhoto));
        } catch (JSONException e) {
            Log.w(TAG, "Can't open Json Profile");
        } catch (IOException e) {
            Log.w(TAG, "Can't open Profile File");
        }
        Base64Converter bs64 = new Base64Converter();
        Bitmap userPhotoBitmap = MatchList.getRoundedShape(bs64.Base64ToBitmap(userPhotoInB64), 150);
        userPhoto.setImageBitmap(userPhotoBitmap);  // Set Profile Photo
        userAlias.setText(alias);                   // Set Alias
    }

    /* Close drawer if its open */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(taller2.match_client.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAplication();
            //super.onBackPressed();
        }
    }

    /* This method finish all activities and aplication */
    private void finishAplication() {
        Log.i(TAG, "Finish application");
        try {
            getMatchTimer.interrupt();
            getConversationTimer.interrupt();
            getPosMatchTimer.interrupt();
            updateConversationTimer.interrupt();
            checkTokenTimer.interrupt();
            getMatchTimer.join();
            getConversationTimer.join();
            getPosMatchTimer.join();
            updateConversationTimer.join();
            checkTokenTimer.join();
        } catch (InterruptedException e) {
            Log.w(TAG, "Can't join threads");
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    /* Inflate Menu when menu button is pressed */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(taller2.match_client.R.menu.menu_principal_app, menu);
        this.menu = menu;
        return true;
    }

    /* Handle menu item clicks */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == taller2.match_client.R.id.action_settings) {
            return true;
        } else if (id == taller2.match_client.R.id.action_chat) {
            Intent startMatchActivity = new Intent(this, matchActivity.getClass());
            startMatchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(startMatchActivity);

            // Update match icon
            menu.getItem(1).setIcon(getResources().getDrawable(taller2.match_client.R.drawable.ic_person_white_36dp));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Handle navigation view item clicks and then create Activities */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == taller2.match_client.R.id.nav_settings) {
            Intent startSettingActivity = new Intent(this, settingsActivity.getClass());
            startSettingActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(startSettingActivity);
        } else if (id == taller2.match_client.R.id.nav_perfil) {
            Intent startProfileActivity = new Intent(this, profileActivity.getClass());
            startProfileActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(startProfileActivity);
        } else if (id == taller2.match_client.R.id.nav_chat) {
            Intent startMatchActivity = new Intent(this, matchActivity.getClass());
            startMatchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(startMatchActivity);

            // Update match icon
            menu.getItem(1).setIcon(getResources().getDrawable(taller2.match_client.R.drawable.ic_person_white_36dp));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(taller2.match_client.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* Update photo and alias of possible match. If possible match buffer is empty,
     * no_match user is set */
    private void updatePosMatch() {
        if (possibleMatchesBuffer.size() > 0) {
            Log.d(TAG, "Change possible match");
            possibleMatchCard.setVisibility(View.VISIBLE);
            Random r = new Random();
            int random = r.nextInt(possibleMatchesBuffer.size());

            if (actualMatch != null) {
                possibleMatchesBuffer.add(actualMatch);
            }
            actualMatch = possibleMatchesBuffer.get(random);
            possibleMatchesBuffer.remove(random);

            View v = findViewById(taller2.match_client.R.id.drawer_layout); // Change background
            v.setBackground(getResources().getDrawable(taller2.match_client.R.drawable.white_background));
        } else {
            if (actualMatch == null) {
                Log.d(TAG, "There are not possible matches in buffer");
                possibleMatchCard.setVisibility(View.INVISIBLE);
                View v = findViewById(taller2.match_client.R.id.drawer_layout); // Change background
                v.setBackground(getResources().getDrawable(taller2.match_client.R.drawable.no_possible_match));
            }
            return;
        }

        // set possible match on Principal Card
        Log.d(TAG, "Set possible match in Card");
        try {
            String alias = actualMatch.getString(getResources().getString(taller2.match_client.R.string.alias));
            String age = String.valueOf(actualMatch.getInt(getResources().getString(taller2.match_client.R.string.age)));
            String photoInB64 = actualMatch.getString(getResources().getString(taller2.match_client.R.string.photoProfile));

            if (photoInB64.length() > 50) {   // TODO: ESTO SE DEJA SOLO POR SI SE DIÓ DE ALTA UN USUARIO SIN FOTO DE PERFIL
                possibleMatchPhoto.setImageBitmap(bs64.Base64ToBitmap(actualMatch.getString(getResources().getString(taller2.match_client.R.string.photoProfile))));
            } else {
                possibleMatchPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), taller2.match_client.R.drawable.standard_photo_profile_small));
            }
            possibleMatchAlias.setText(alias + ", " + age);
        } catch (JSONException e) {
            Log.w(TAG, "Can't get age, alias or profile photo from actual match (JsonObject)");
        }
    }

    /* Send a request to Server asking if there are more possible match if we have more than
     * MIN_POS_MATCHES_COUNT. In other case,updatePosMatch function is called */
    private void sendGetPossibleMatchRequestToServer() {
        // construct possible match request
        JSONObject posMatchRequest = new JSONObject();
        try {
            posMatchRequest.put(getResources().getString(taller2.match_client.R.string.email), userEmail);
            posMatchRequest.put(getResources().getString(taller2.match_client.R.string.pos_match_count), POS_MATCH_COUNT_TO_REQUEST);
        } catch (JSONException e) {
            Log.w(TAG, "Can't create GetPossibleMatch Json Request");
        }

        // send request if there are not possible matches
        if (possibleMatchesBuffer.size() <= MIN_POS_MATCHES_COUNT) {
                    if (ActivityHelper.checkConection(getApplicationContext())) {
                        String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
                        String uri = getResources().getString(taller2.match_client.R.string.get_pos_matches_uri);;
                        SendGetPossibleMatchesTask getPossibleMatches = new SendGetPossibleMatchesTask();
                        getPossibleMatches.execute("POST", url, uri, posMatchRequest.toString());
                        Log.d(TAG, "Send Get possible matches Request to Server: " + posMatchRequest.toString());
                    }
            //checkGetPosMatchResponseFromServer(mockServer.getPossibleMatches(posMatchRequest.toString())); // MOCK TEST
        }
    }

    /* When user touch like or dont like icon, if there is a possible match on screen,
     * interest request is send to Server */
    private void sendInterestOfPosMatchToServer(String sendUri) {
        if (actualMatch == null) {
            noMatchesAvailableInFront.show();
            return;
        }
        if (ActivityHelper.checkConection(getApplicationContext())) {
            JSONObject interestMatches = new JSONObject();
            String pos_match_email = "";
            try {
                pos_match_email = actualMatch.getString(getResources().getString(taller2.match_client.R.string.email));
                interestMatches.put(getResources().getString(taller2.match_client.R.string.email_src),userEmail);
                interestMatches.put(getResources().getString(taller2.match_client.R.string.email_dst), pos_match_email);
            } catch (JSONException e) {
                Log.w(TAG, "Can't create InterestOfPossibleMatch Json Request");
            }
            connectingToServerWindow.show();
            Log.d(TAG, "Send interest of possible match Request to Server: " + interestMatches.toString());
            String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
            String uri = sendUri;
            SendInterestOfPosMatchTask sendPosMatchInterest = new SendInterestOfPosMatchTask();
            sendPosMatchInterest.execute("POST", url, uri, interestMatches.toString());

            //checkInterestPosMatchResponseFromServer(mockServer.like_dont(interestMatches.toString()));  //MOCK TEST
        } else {
            internetDisconnectWindow.show();
            possibleMatchCard.setVisibility(View.VISIBLE);
        }
    }

    /* Send a request to Server asking if there are new Matches */
    private void sendGetMatchesRequestToServer() {
        JSONObject userMailJson = new JSONObject();
        try {
            userMailJson.put(getResources().getString(taller2.match_client.R.string.email), userEmail);
        } catch (JSONException e) {
            Log.w(TAG, "Can't create GetMatches Json Request");
        }
        if (ActivityHelper.checkConection(getApplicationContext())) {
            Log.d(TAG, "Send Get match Request to Server: " + userMailJson.toString());
            String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
            String uri = getResources().getString(taller2.match_client.R.string.get_matches_uri);;
            SendGetMatchesTask getMatches = new SendGetMatchesTask();
            getMatches.execute("POST", url, uri, userMailJson.toString());
            //checkGetMatchResponseFromServer(mockServer.getMatches(userMailJson.toString()));  // MOCK TEST
        } else {
            // No hay internet
        }
    }

    /* Send a request asking if there are new conversations */
    private void sendGetConversationsRequestToServer() {
        List<JSONObject> matches = matchManager.getMatches();
        for (int i = 0; i < matches.size(); ++i) {
            JSONObject match = matches.get(i);
            String matchEmail = "";
            JSONObject convRequest = new JSONObject();
            try {
                matchEmail = match.getString(getResources().getString(taller2.match_client.R.string.email));
                convRequest.put(getResources().getString(taller2.match_client.R.string.email_src),
                        userEmail);
                convRequest.put(getResources().getString(taller2.match_client.R.string.email_dst),
                        matchEmail);
            } catch (JSONException e) {
                Log.w(TAG, "Can't create GetConversation Json Request");
            }
            if (ActivityHelper.checkConection(getApplicationContext())) {
                Log.d(TAG, "Send GetConversation Request to Server: " + convRequest.toString());
                String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
                String uri = getResources().getString(taller2.match_client.R.string.get_conversation_uri);;
                SendGetConversationTask getConversation = new SendGetConversationTask();
                getConversation.execute("POST", url, uri, convRequest.toString());
                //checkGetConversationResponseFromServer( mockServer.getConversation(convRequest.toString()));    //MOCK TEST
            } else {
                // No hay internet
            }
        }
    }

    /* Send Check Token Request To Server */
    private void sendCheckTokenRequestToServer() {
        JSONObject tokenRequest = new JSONObject();
        try {
            tokenRequest.put(getResources().getString(R.string.email), userEmail);
            tokenRequest.put(getResources().getString(R.string.token), token);
        } catch (JSONException e) {
            Log.w(TAG, "Can't create SendCheckToken Json Request");
        }
        if (ActivityHelper.checkConection(getApplicationContext())) {
            Log.d(TAG, "Send CheckToken Request to Server: " + tokenRequest.toString());
            String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
            String uri = getResources().getString(R.string.send_check_token_uri);;
            SendCheckTokenRequestToServerTask checkToken = new SendCheckTokenRequestToServerTask();
            checkToken.execute("POST", url, uri, tokenRequest.toString());
        } else {
            // No hay internet
        }
    }

    /* Check response from Server after sending possible match interest. If response is ok
     * actual possible match is remove and updatePosMatch is called */
    private void checkInterestPosMatchResponseFromServer(String response) {
        Log.d(TAG, "Interest Response from Server is received: " + response);
        String responseCode = response.split(":", 2)[0];
        String responseMessage = response.split(":", 2)[1];
        connectingToServerWindow.dismiss();

        if (responseCode.compareTo(getResources().getString(taller2.match_client.R.string.ok_response_code_send_pos_match_interest)) == 0) {
            actualMatch = null;
            if (possibleMatchesBuffer.size() <= 0) {
                sendGetPossibleMatchRequestToServer();
                View v = findViewById(taller2.match_client.R.id.drawer_layout); // Change background
                v.setBackground(getResources().getDrawable(taller2.match_client.R.drawable.no_possible_match));
            } else {
                updatePosMatch();
            }
        } else {
            possibleMatchCard.setVisibility(View.VISIBLE);
        }
    }

    /* Check response from Server after sending get possible matches request. If new pos matches are
     * received this ones are saving into buffer. */
    private void checkGetPosMatchResponseFromServer(String response) {
        Log.d(TAG, "Get Possible Matches Response from Server is received: " + response);
        String responseCode = response.split(":", 2)[0];
        String possibleMatches = response.split(":", 2)[1];

        if (responseCode.compareTo(getResources().getString(taller2.match_client.R.string.ok_response_code_get_pos_matches)) == 0) {
            try {
                JSONObject possibleMatchesjson = new JSONObject(possibleMatches);
                JSONArray posMatchArray = possibleMatchesjson.getJSONArray(getResources().getString(taller2.match_client.R.string.possible_matches));

                for (int i = 0; i < posMatchArray.length(); ++i) {
                    JSONObject posMatch = (posMatchArray.getJSONObject(i)).getJSONObject(getResources().getString(taller2.match_client.R.string.user));
                    this.possibleMatchesBuffer.add(posMatch);
                }

                if (actualMatch == null && possibleMatchesBuffer.size() > 0) {
                    updatePosMatch();
                }
            } catch (JSONException e) {
                Log.w(TAG, "Can't process Json Possible Matches received from Server");
            }
        }
    }

    /* Check response from Server after sending get matches request. If new matches are
    * received, MatchManager keep this ones. */
    private void checkGetMatchResponseFromServer(String response) {
        Log.d(TAG, "Get Matches Response from Server is received: " + response);
        String responseCode = response.split(":", 2)[0];
        String matches = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(taller2.match_client.R.string.ok_response_code_get_matches))) {
            int newMatchesCount = 0;
            // Get each match from Server response
            JSONObject matchData = null;
            JSONArray matchesArray = null;
            try {
                matchData = new JSONObject(matches);
                matchesArray = matchData.getJSONArray(getResources().getString(taller2.match_client.R.string.matches));
                for (int i = 0; i < matchesArray.length(); ++i) {
                    JSONObject match = (matchesArray.getJSONObject(i)).getJSONObject(getResources().getString(taller2.match_client.R.string.user));
                    if(matchManager.addMatch(match)) {   // Put in MatchManager
                        ++newMatchesCount;
                    }
                }

            } catch (JSONException e) {
                Log.w(TAG, "Can't process Matches Json received from Server");
            }
            if ( (newMatchesCount > 0) && areConversationLoad) {
                // Update match icon
                menu.getItem(1).setIcon(getResources().getDrawable(taller2.match_client.R.drawable.ic_person_add_white_36dp));

                Toast.makeText(getApplicationContext(), getResources().getString(taller2.match_client.R.string.new_matches_en),
                        Toast.LENGTH_LONG).show();
            }

            if (!areConversationLoad) {
                loadConversationsFromFile();
            }
        }
    }

    /* Check response from Server after sending get conversation request. If new conversations are
    * received, MatchManager keep this ones. */
    private void checkGetConversationResponseFromServer(String response) {
        Log.d(TAG, "Get Conversation Response from Server is received: " + response);
        String responseCode = response.split(":", 2)[0];
        String conversation = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(taller2.match_client.R.string.ok_response_code_get_conversation))) {
            JSONObject conversationJson = null;
            try {
                conversationJson = new JSONObject(conversation);
            } catch (JSONException e) {
                Log.w(TAG, "Can't process Matches Conversation Json received from Server");
            }
            matchManager.addConversation(conversationJson, true);
        }
    }

    /* Check response from Server after sending check Token request */
    private void checkSendCheckTokenResponseFromServer(String response) {
        Log.d(TAG, "Check Token Response from Server is received: " + response);
        String responseCode = response.split(":", 2)[0];
        String conversation = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(taller2.match_client.R.string.ok_response_code_check_token_interest))) {
            // OK
        } else if (responseCode.equals(getResources().getString(taller2.match_client.R.string.invalid_token_response_code))) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.session_expired_en),
                    Toast.LENGTH_LONG).show();
            finishAplication();
        }
    }

    /* Send Interest of possible match to Server */
    private class SendInterestOfPosMatchTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            //SystemClock.sleep(750);
            checkInterestPosMatchResponseFromServer(dataGetFromServer);
        }
    }

    /* Send get possible match request to Server */
    private class SendGetPossibleMatchesTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer) {
            checkGetPosMatchResponseFromServer(dataGetFromServer);
        }
    }

    /* Send get match request to Server */
    private class SendGetMatchesTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer) {
            checkGetMatchResponseFromServer(dataGetFromServer);
        }
    }

    /* Get Conversation to Server */
    private class SendGetConversationTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkGetConversationResponseFromServer(dataGetFromServer);
        }
    }

    /* Send check token to Server */
    private class SendCheckTokenRequestToServerTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkSendCheckTokenResponseFromServer(dataGetFromServer);
        }
    }

    /* Thread Handler, handle get match and get conversation events */
    Handler matchManagerHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_MATCH_CODE:    // Send Get match request to Server
                    sendGetMatchesRequestToServer();
                    break;
                case GET_CONVERSATION_CODE: // Send Get conversation request to Server
                    sendGetConversationsRequestToServer();
                    break;
                case GET_POS_MATCH_CODE: // Send Get Possible Match request to Server
                    sendGetPossibleMatchRequestToServer();
                    break;
                case UPDATE_CONVERSATIONS_CODE: // Send Get Possible Match request to Server
                    String conversationsFileName =  getResources().getString(taller2.match_client.R.string.conversation_prefix_filename) + userEmail;
                    matchManager.updateConversationInFile(conversationsFileName);
                    break;
                case CHECK_TOKEN_CODE: // Send check Token request to Server
                    sendCheckTokenRequestToServer();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /* Get Matches Thread. After a time send get matches request to Server */
    class GetMatches implements Runnable {
        public void run() {
            while (! Thread.currentThread().isInterrupted()) {
                Log.i(TAG, "Get Matches Thread wake up");
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
                Log.i(TAG, "Get Conversations Thread wake up");
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
                Log.i(TAG, "Get Possible matches Thread wake up");
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

    /* Update conversations Thread. After a time update conversations */
    class UpdateConversations implements Runnable {
        public void run() {
            while (! Thread.currentThread().isInterrupted()) {
                Log.i(TAG, "Update conversations in file");
                Message message = new Message();
                message.what = UPDATE_CONVERSATIONS_CODE;
                PrincipalAppActivity.this.matchManagerHandler.sendMessage(message);

                try {
                    Thread.sleep(UPDATE_CONVERSATION_SLEEP_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /* Check Token to Server */
    class CheckToken implements Runnable {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(TAG, "Check Token");
                Message message = new Message();
                message.what = CHECK_TOKEN_CODE;
                PrincipalAppActivity.this.matchManagerHandler.sendMessage(message);

                try {
                    Thread.sleep(CHECK_TOKEN_SLEEP_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /* Timer to wait animation card */
    class LikeDontTimer extends CountDownTimer {
        String uri = "";
        public LikeDontTimer(long millisInFuture, long countDownInterval, String uri) {
            super(millisInFuture, countDownInterval);
            this.uri = uri;
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            likeShape.setVisibility(View.INVISIBLE);
            dontLikeShape.setVisibility(View.INVISIBLE);
            likeIcon.setEnabled(true);
            dontlikeIcon.setEnabled(true);
            possibleMatchCard.setVisibility(View.INVISIBLE);
            sendInterestOfPosMatchToServer(uri);
        }
    }

    /* On resume load nav head profile */
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        updateHeadProfile();
    }
}
