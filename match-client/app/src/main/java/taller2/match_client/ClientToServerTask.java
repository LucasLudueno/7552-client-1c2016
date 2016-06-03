package taller2.match_client;

import android.os.AsyncTask;
import android.util.Log;

/* This class represent a background task where the Client send to Server GET and POST request. */
public class ClientToServerTask extends AsyncTask<String, Integer, String> {
    /* Attributes */
    private static final String TAG = "ClientToServerTask";
    private static final String CANT_CONNECT_TO_SERVER_ERROR = "400:Error";

    @Override
    protected void onPreExecute(){
        //Setup is done here
    }

    /* Send to Server GET and POST request.
     * param[0] = requestType (GET or POST)
     * param[1] = url
     * param[2] = uri
     * param[3] = data (for POST request) */
    @Override
    protected String doInBackground(String... params) {
        String requestType = params[0];
        String url = params[1];
        String uri = params[2];
        String data = params[3];

        String receiveString = "";
        HttpConnectionClient httpConnection = new HttpConnectionClient();
        try {
            if (requestType.equals("GET") ) {
                Log.d(TAG, "Send GET Request");
                receiveString = httpConnection.GETRequest(url, uri);
            } else if (requestType.equals("POST") ) {
                receiveString = httpConnection.POSTRequest(url, uri, data);
                Log.d(TAG, "Send POST Request");
            } else {
                Log.d(TAG, "No type of Request is specified");
            }
        } catch (ConnectionException e) {
            Log.w(TAG, "Can't send Resquest to Server");
            receiveString = CANT_CONNECT_TO_SERVER_ERROR;
        }
        return receiveString;
    }

    @Override
    protected void onProgressUpdate(Integer... params) {
        //Update a progress bar here, or ignore it...
    }

    @Override
    protected void onCancelled(){
        // Handle what you want to do if you cancel this task
    }
}
