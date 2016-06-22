package taller2.match_client;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/* This class contains a buffer of possibles matches. Can add and remove them */
public class PossibleMatchBuffer {
    /* Attributes */
    private ReentrantLock mutex_pos_matches;
    private ArrayList<JSONObject> possibleMatchesBuffer;
    private static final String TAG = "PossibleMatchBuffer";

    PossibleMatchBuffer() {
        mutex_pos_matches = new ReentrantLock();
        possibleMatchesBuffer = new ArrayList<JSONObject>();
    }

    /* Return size */
    public int size() {
        mutex_pos_matches.lock();
            int size = possibleMatchesBuffer.size();
        mutex_pos_matches.unlock();
        return size;
    }

    /* Add element */
    public void add(JSONObject possibleMatch) {
        Log.d(TAG, "Add possible match");
        mutex_pos_matches.lock();
            possibleMatchesBuffer.add(possibleMatch);
        mutex_pos_matches.unlock();
    }

    /* Get element in position "pos" */
    public JSONObject get(int pos) {
        Log.d(TAG, "Get possible match");
        JSONObject possibleMatch;
        mutex_pos_matches.lock();
            possibleMatch = possibleMatchesBuffer.get(pos);
        mutex_pos_matches.unlock();
        return possibleMatch;
    }

    /* Remove element in position "pos" */
    public boolean remove(int pos) {
        Log.d(TAG, "Remove possible match");
        boolean canRemove = false;
        mutex_pos_matches.lock();
            if (possibleMatchesBuffer.size() > pos) {
                possibleMatchesBuffer.remove(pos);
                canRemove = true;
            }
        mutex_pos_matches.unlock();
        return canRemove;
    }
}
