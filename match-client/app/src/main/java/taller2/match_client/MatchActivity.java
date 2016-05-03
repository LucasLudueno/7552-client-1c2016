package taller2.match_client;

import java.util.ArrayList;
import java.util.Arrays;

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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MatchActivity extends ListActivity {
    private String[] listview_names =  {"Milito","Licha", "Saja","Aued" };
    private static int[] listview_images = { R.drawable.no_match,R.drawable.no_match,R.drawable.no_match,R.drawable.no_match};

    static Context mcontext;
    private ListView matchListView;
    private static ArrayList<String> match_names_sort;
    private static ArrayList<Integer> image_sort;

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // Toolbar
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);*/

        matchListView = (ListView) findViewById(android.R.id.list);
        match_names_sort = new ArrayList<String> (Arrays.asList(listview_names));
        image_sort = new ArrayList<Integer>();
        for (int index = 0; index < listview_images.length; index++) {
            image_sort.add(listview_images[index]);
        }
        setListAdapter(new matchListAdapter(this));

        matchListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                createChat();
            }
        });
    }

    /* When some match are taken, its chat is created */
    private void createChat() {
        Intent startChatActivity = new Intent(this, ChatActivity.class);
        startActivity(startChatActivity);
    }

    public static class matchListAdapter extends BaseAdapter {
        Activity cntx;
        public matchListAdapter(Activity context) {
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
