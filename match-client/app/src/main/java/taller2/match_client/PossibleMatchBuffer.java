package taller2.match_client;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/* This class is a Monitor of a Possible Match Buffer */
public class PossibleMatchBuffer {
    /* Attributes */
    private ReentrantLock mutex_pos_matches;
    private ArrayList<JSONObject> possibleMatchesBuffer;

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
        mutex_pos_matches.lock();
            possibleMatchesBuffer.add(possibleMatch);
        mutex_pos_matches.unlock();
    }

    /* Get element in position "pos" */
    public JSONObject get(int pos) {
        JSONObject possibleMatch;
        mutex_pos_matches.lock();
            possibleMatch = possibleMatchesBuffer.get(pos);
        mutex_pos_matches.unlock();
        return possibleMatch;
    }

    /* Remove element in position "pos" */
    public void remove(int pos) {
        mutex_pos_matches.lock();
            possibleMatchesBuffer.remove(pos);
        mutex_pos_matches.unlock();
    }
}
