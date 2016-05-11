package taller2.match_client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrincipalAppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* Attributes */
    private AlertDialog internetDisconnectWindow;

    private ImageView possibleMatchPhoto;
    private CardView possibleMatchCard;
    private MatchManager matchManager;
    private JSONObject actualMatch = null;

    private String userMail = "";
    private int GET_MATCH_TIME = 100000;
    protected static final int GET_MATCH_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_app);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.principalAppToolbar);
        setSupportActionBar(toolbar);

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(this).create();
        internetDisconnectWindow.setTitle(getResources().getString(R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(R.string.internet_disconnect_error_en));

        // Like Icon
        FloatingActionButton likeIcon = (FloatingActionButton) findViewById(R.id.likeIcon);
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendInterestOfPosMatch(getResources().getString(R.string.like_uri));
            }
        });

        // Dont Like Icon
        FloatingActionButton dontlikeIcon = (FloatingActionButton) findViewById(R.id.dontLikeIcon);
        dontlikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendInterestOfPosMatch(getResources().getString(R.string.dont_like_uri));
            }
        });

        // Match Icon
        FloatingActionButton matchIcon = (FloatingActionButton) findViewById(R.id.matchIcon);
        dontlikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (actualMatch == null) {
                    String url = getResources().getString(R.string.server_ip);
                    String uri = getResources().getString(R.string.get_pos_matches_uri);;
                    String userEmail = matchManager.getUserEmail();
                    SendGetPossibleMatchsTask getPossibleMatches = new SendGetPossibleMatchsTask();
                    getPossibleMatches.execute("POST", url, uri, userEmail);
                } else {
                    updatePosMatch();
                }*/
            }
        });

        // Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation View
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        // UserMail
        // TODO: SACAR USER MAIL DE MATCHMANAGER O DE ARCHIVO??

        // GetMatch Timer
        //new Thread(new GetMatches()).start();

        // TODO: TEST: SOLO POR AHORA CREAMOS LOS ARCHIVOS INICIALES DE CONVERSACIONES Y MATCHES

        /*try {
            // matches
            JSONObject seba = new JSONObject("{\"name\":\"seba\",\"alias\":\"tristelme\",\"email\":\"seba@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");
            JSONObject fede = new JSONObject("{\"name\":\"fede\",\"alias\":\"algoritmo\",\"email\":\"fede@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");
            JSONObject eze = new JSONObject("{\"name\":\"eze\",\"alias\":\"el matero loco\",\"email\":\"eze@gmail.com\",\"birthday\":\"13/08/93\",\"sex\":\"Male\",\"location\":{ \"longitude\":\"-58.37\",\"latitude\":\"-34.69\"},\"password\":\"contraseña\"}");

            Bitmap photodefault = BitmapFactory.decodeResource(getResources(), R.drawable.no_match);
            Base64Converter bs64 = new Base64Converter();
            String base64 = bs64.bitmapToBase64(photodefault);
            seba.put(getResources().getString(R.string.profilePhoto), base64);
            fede.put(getResources().getString(R.string.profilePhoto), base64);
            eze.put(getResources().getString(R.string.profilePhoto), base64);

            JSONArray match_array = new JSONArray();
            match_array.put(seba);
            match_array.put(fede);
            match_array.put(eze);

            JSONObject matches = new JSONObject();
            matches.put("matches",match_array); // array de matches

            FileManager fm = new FileManager(getApplicationContext());
            try {
                fm.writeFile("matches_lucas@gmail.com",String.valueOf(matches));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // conversaciones
            JSONObject seba_conv = new JSONObject();
            JSONObject fede_conv = new JSONObject();
            JSONObject eze_conv = new JSONObject();

            JSONObject seba_msg1 = new JSONObject("{\"sendFrom\":\"seba@gmail.com\",\"msg\":\"hola soy seba\"}");
            JSONObject seba_msg2 = new JSONObject("{\"sendFrom\":\"lucas@gmail.com\",\"msg\":\"hola soy lucas\"}");
            JSONObject fede_msg1 = new JSONObject("{\"sendFrom\":\"fede@gmail.com\",\"msg\":\"hola soy fede\"}");
            JSONObject fede_msg2 = new JSONObject("{\"sendFrom\":\"lucas@gmail.com\",\"msg\":\"hola soy lucas\"}");
            JSONObject eze_msg1 = new JSONObject("{\"sendFrom\":\"eze@gmail.com\",\"msg\":\"hola soy eze\"}");
            JSONObject eze_msg2 = new JSONObject("{\"sendFrom\":\"lucas@gmail.com\",\"msg\":\"hola soy lucas\"}");

            JSONArray seba_msg = new JSONArray();
            seba_msg.put(seba_msg1);
            seba_msg.put(seba_msg2);
            JSONArray fede_msg = new JSONArray();
            fede_msg.put(fede_msg1);
            fede_msg.put(fede_msg2);
            JSONArray eze_msg = new JSONArray();
            eze_msg.put(eze_msg1);
            eze_msg.put(eze_msg2);

            seba_conv.put("email","seba@gmail.com");
            seba_conv.put("messages",seba_msg);
            fede_conv.put("email","fede@gmail.com");
            fede_conv.put("messages",fede_msg);
            eze_conv.put("email", "eze@gmail.com");
            eze_conv.put("messages",eze_msg);

            JSONArray conversations = new JSONArray();
            conversations.put(seba_conv);
            conversations.put(fede_conv);
            conversations.put(eze_conv);

            JSONObject conversation_total = new JSONObject();
            conversation_total.put("conversations",conversations);

            try {
                fm.writeFile("conversations_lucas@gmail.com",String.valueOf(conversation_total));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //TextView descrip = (TextView)findViewById(R.id.possibleMatchDescription);
            //descrip.setText(String.valueOf(conversation_total));

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        /*** Match Manager: load saved matches and conversations ***/
        /*matchManager = MatchManager.getInstance();
        matchManager.setData(getApplicationContext(), "matches_lucas@gmail.com", "conversations_lucas@gmail.com", "lucas@gmail.com");*/
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

    @Override /* Close drawer if its open */
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

    /*  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal_app, menu);
        return true;
    }

    /* Handle menu item click */
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

    /* Handle navigation view item clicks and create Activities */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent startSettingActivity = new Intent(this, SettingsActivity.class);
            startSettingActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(startSettingActivity);
        } else if (id == R.id.nav_information) {

        } else if (id == R.id.nav_perfil) {
            Intent startProfileActivity = new Intent(this, PerfilActivity.class);
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


    /*  */
    private void sendInterestOfPosMatch(String sendUri) {
        if (actualMatch == null) {
            // TODO: INFORMAR QUE NO HAY MATCH
            return;
        }
        if (checkConection()) {
            String pos_match_email = "";
            try {
                pos_match_email = actualMatch.getString(getResources().getString(R.string.email));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = getResources().getString(R.string.server_ip);
            String uri = sendUri;
            SendInterestOfPosMatchTask sendPosMatchInterest = new SendInterestOfPosMatchTask();
            sendPosMatchInterest.execute("POST", url, uri, pos_match_email);   //CON ESTAS LINEAS MANDAS AL SERVER EL LOGIN
        } else {
            internetDisconnectWindow.show();
        }
    }

    /*  */
    private void checkPosMatchResponse(String response) {   // TODO: PENSAR QUE PASA SI AL MISMO TIEMPO APRETAMOS UPDATE...
        String responseCode = response.split(":")[0];       // TODO: SOLUCIÓN: CAMBIO AUTOMÁTICO...
        String responseMessage = response.split(":")[1];

        if (responseCode.compareTo("201") == 0) {   // TODO: CHECKEAR CODIGO CON EZE
            String pos_match_email = null;
            try {
                pos_match_email = actualMatch.getString(getResources().getString(R.string.email));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            matchManager.removePossibleMatch(pos_match_email);
            updatePosMatch();
        }
    }

    /*  */
    private void checkGetPosMatchResponse(String response) {
        String responseCode = response.split(":")[0];
        String responseMessage = response.split(":")[1];

        if (responseCode.compareTo("201") == 0) {
            try {
                JSONArray posMatchArray = new JSONArray(responseMessage);
                matchManager.addPossibleMatches(posMatchArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updatePosMatch();
        }
    }

    /*  */
    private void checkGetMatchResponse(String response) {
        internetDisconnectWindow.setMessage(response);
        internetDisconnectWindow.show();
    };


    /*  */
    private void updatePosMatch() {
        actualMatch = matchManager.getPossibleMatch();
        if (actualMatch == null) {
            // TODO: INFORMAR QUE NO HAY MÁS MATCHS O PEDIR MÁS MATCHS
        } else {
            // TODO: CARGAR NUEVO MATCH EN PATANLLA PRINCIPAL
        }
    }


    // TODO: AGRUPAR ESTAS CLASES DE ALGUNA MANERA: DEVOLVIENDO CÓDIGO DE OPERACION Y ASIGNANDOLO AL CREAR LA CLASE O INVOCARLA.
    // TODO: CHECKEAR SI SE PUEDE TENER SOLO UNA INSTACIA...
    /*  */
    private class SendInterestOfPosMatchTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkPosMatchResponse(dataGetFromServer);
        }
    }

    /*  */
    private class SendGetPossibleMatchsTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer) {
            checkGetPosMatchResponse(dataGetFromServer);
        }
    }

    /*  */
    private class SendGetMatchsTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer) {
            checkGetMatchResponse(dataGetFromServer);
        }
    }

    /*  */
    Handler matchManagerHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_MATCH_CODE:
                    //SendGetMatchsTask getMatchs = new SendGetMatchsTask();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /*  */
    class GetMatches implements Runnable {
        public void run() {
            while (! Thread.currentThread().isInterrupted()) {
                Message message = new Message();
                message.what = GET_MATCH_CODE;
                PrincipalAppActivity.this.matchManagerHandler.sendMessage(message);

                try {
                    Thread.sleep(GET_MATCH_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
