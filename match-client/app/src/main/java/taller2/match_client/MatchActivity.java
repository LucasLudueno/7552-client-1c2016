package taller2.match_client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MatchActivity extends AppCompatActivity {
    private String[] listview_names =  {"Milito","Licha", "Saja","Aued" };
    private static int[] listview_images =  { R.drawable.no_match,R.drawable.no_match,R.drawable.no_match,R.drawable.no_match};

    private MatchListAdapter matchListAdapter;
    private ListView matchListView;
    private static ArrayList<String> match_names_sort;
    //private static ArrayList<Integer> image_sort;
    static Context mcontext;

    private MatchManager matchManager;

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

        // Generate the match list
        match_names_sort = new ArrayList<String> (Arrays.asList(listview_names));
        /*image_sort = new ArrayList<Integer>();
        for (int index = 0; index < listview_images.length; index++) {
            image_sort.add(listview_images[index]);
        }*/

        // MatchList
        matchListAdapter = new MatchListAdapter(this);
        matchListView = (ListView) findViewById(R.id.matchList);
        matchListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        matchListView.setAdapter(matchListAdapter);

        // When some match is clicked, its chat activity is created
        matchListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                createChat();
            }
        });

        /***  Match Manager ***/
        /*matchManager = MatchManager.getInstance();
        List<JSONObject> matches = matchManager.getMatches();

        for (int i = 0; i < matches.size(); i++) {
            JSONObject match = matches.get(i);
            try {
                String alias = match.getString("alias");
                String photo = match.getString("photo_profile");
                Base64Converter bs64 = new Base64Converter();
                Bitmap photoBitmap = bs64.Base64ToBitmap(photo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/

    }

    /* When some match are taken, its chat is created */
    private void createChat() {
        Intent startChatActivity = new Intent(this, ChatActivity.class);
        //startChatActivity.putExtra("email", String.valueOf("seba@gmail.com"));  // TODO: DEBERIAMOS RECONOCER EL EMAIL Y MANDARLO AL CHAT ASÍ
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
        //startAppActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startAppActivity);
    }

    public static class MatchListAdapter extends BaseAdapter {
        Activity cntx;
        public MatchListAdapter(Activity context) {
            this.cntx=context;
        }

        public int getCount() {
            return match_names_sort.size();
        }

        public Object getItem(int position) {
            return match_names_sort.get(position);
        }

        public long getItemId(int position) {
            return match_names_sort.size();
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View row=null;
            LayoutInflater inflater = cntx.getLayoutInflater();
            row = inflater.inflate(R.layout.list_item, null);
            TextView matchName = (TextView) row.findViewById(R.id.matchName);
            ImageView matchPhoto = (ImageView) row.findViewById(R.id.matchPhoto);
            matchName.setText(match_names_sort.get(position));                                          // Match Name
            matchPhoto.setImageBitmap(getRoundedShape(decodeFile(cntx, listview_images[position]),200));    //Match Photo
            return row;
        }

        public static Bitmap decodeFile(Context context,int resId) {
            try {
// decode image size
                mcontext=context;
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(mcontext.getResources(), resId, o);
// Find the correct scale value. It should be the power of 2.
                final int REQUIRED_SIZE = 200;
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                int scale = 1;
                while (true)
                {
                    if (width_tmp / 2 < REQUIRED_SIZE
                            || height_tmp / 2 < REQUIRED_SIZE)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale++;
                }
// decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                return BitmapFactory.decodeResource(mcontext.getResources(), resId, o2);
            } catch (Exception e) {
            }
            return null;
        }
    }
    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage,int width) {
        // TODO Auto-generated method stub
        int targetWidth = width;
        int targetHeight = width;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);
        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }
}
