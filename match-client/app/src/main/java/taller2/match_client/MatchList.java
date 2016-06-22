package taller2.match_client;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/* This class represent an List Adapter that have match items and show match alias and photo
 * when is used in a ListView */
public class MatchList extends BaseAdapter {
    /* Attributes */
    private Context cntx;
    private ArrayList<JSONObject> matches;
    private Base64Converter bs64;
    private ReentrantLock mutex;

    private static final String TAG = "ChatConversation";
    private static final int MATCH_PHOTO_SIZE = 200;

    public MatchList(Context context) {
        this.cntx = context;
        matches = new ArrayList<JSONObject>();
        bs64 = new Base64Converter();
        mutex = new ReentrantLock();
    }

    /* Return size */
    public int getCount() {
        mutex.lock();
            int size = matches.size();
        mutex.unlock();
        return size;
    }

    /* Return item in position */
    public Object getItem(int position) {
        mutex.lock();
            JSONObject item = matches.get(position);
        mutex.unlock();
        return item;
    }

    /* Return match email in indicated position */
    public String getEmail(int position) {
        String email = "";
        try {
            mutex.lock();
                email = matches.get(position).getString(cntx.getResources().getString(R.string.email));
            mutex.unlock();
        } catch (JSONException e) {
            Log.w(TAG, "Can't get email from match(position)");
        }
        return email;
    }

    /* Add item (match) */
    public void addItem(JSONObject match) {
        Log.d(TAG, "Add Chat Message");
        mutex.lock();
            matches.add(match);
        mutex.unlock();
        notifyDataSetChanged();
    }

    /* return size */
    public long getItemId(int position) {
        mutex.lock();
        int size = matches.size();
        mutex.unlock();
        return size;
    }

    /* Return View with Match alias and photo */
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Get Match View");
        View row = null;
        LayoutInflater inflater = (LayoutInflater) this.cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //LayoutInflater inflater = cntx.getLayoutInflater();
        row = inflater.inflate(R.layout.list_item, null);
        TextView matchName = (TextView) row.findViewById(R.id.matchName);
        ImageView matchPhoto = (ImageView) row.findViewById(R.id.matchPhoto);

        String matchNameString = null;
        String matchPhotoString = null;
        try {
            matchNameString = matches.get(position).getString(cntx.getResources().getString(R.string.alias));
            matchPhotoString = matches.get(position).getString(cntx.getResources().getString(R.string.photoProfile));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bitmap photo = bs64.Base64ToBitmap(matchPhotoString);
        matchName.setText(matchNameString);                                            // Match Name
        matchPhoto.setImageBitmap(getRoundedShape(photo, MATCH_PHOTO_SIZE));           // Match Photo
        return row;
    }

    /* Return rounder image (Bitmap) with indicated width */
    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage,int width) {
        Log.d(TAG, "Get Rounded Shape");
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
};