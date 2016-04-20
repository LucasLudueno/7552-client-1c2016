package taller2.match_client;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
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

public class PrincipalAppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* Attributes */
    private ImageView possibleMatchPhoto;
    private CardView possibleMatchCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_app);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.principalAppToolbar);
        setSupportActionBar(toolbar);

        // Chat Icon
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.chatIcon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { createChatActivity();}
        });

        <android.support.design.widget.FloatingActionButton
        android:id="@+id/chatIcon"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@android:drawable/sym_action_chat"
        android:layout_alignParentTop="true"
        android:layout_alignParentRight="true" />
        */

        // Like Icon
        FloatingActionButton likeIcon = (FloatingActionButton) findViewById(R.id.likeIcon);
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODAVIA NO HACE NADA
            }
        });

        // Dont Like Icon
        FloatingActionButton dontlikeIcon = (FloatingActionButton) findViewById(R.id.dontLikeIcon);
        dontlikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODAVIA NO HACE NADA
            }
        });

        // Match Icon
        FloatingActionButton matchIcon = (FloatingActionButton) findViewById(R.id.matchIcon);
        dontlikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODAVIA NO HACE NADA
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
    }

    /* When chatIcon is pressed create the Chat Activity */
    public void createChatActivity() {
        Intent startAppActivity = new Intent(this, ChatActivity.class);
        startActivity(startAppActivity);
    }

    @Override /* Close drawer if its open */
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

    /* Handle navigation view item clicks */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent startAppActivity = new Intent(this, SettingsActivity.class);
            startActivity(startAppActivity);

        } else if (id == R.id.nav_information) {

        } else if (id == R.id.nav_perfil) {
            Intent startAppActivity = new Intent(this, PerfilActivity.class);
            startActivity(startAppActivity);

        } else if (id == R.id.nav_chat) {
            Intent startAppActivity = new Intent(this, ChatActivity.class);
            startActivity(startAppActivity);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
