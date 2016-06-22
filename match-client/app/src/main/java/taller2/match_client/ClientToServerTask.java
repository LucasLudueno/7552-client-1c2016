package taller2.match_client;

import android.os.AsyncTask;
import android.util.Log;

/* This class represent a background task where the Client send to Server GET and POST request. */
public class ClientToServerTask extends AsyncTask<String, Integer, String> {
    /* Attributes */
    private static final String TAG = "ClientToServerTask";
    private static final String CANT_CONNECT_TO_SERVER_ERROR = "401:Cant connect with Server";
    private static final String GET_TYPE = "GET";
    private static final String POST_TYPE = "POST";

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
        RequestSender httpConnection = new RequestSender();
        try {
            if (requestType.equals(GET_TYPE) ) {
                Log.d(TAG, "Send GET Request");
                receiveString = httpConnection.sendGETRequest(url, uri);
            } else if (requestType.equals(POST_TYPE) ) {
                receiveString = httpConnection.sendPOSTRequest(url, uri, data);
                Log.d(TAG, "Send POST Request");
            } else {
                Log.d(TAG, "Invalid type of Request is specified");
            }
        } catch (ConnectionException e) {
            Log.w(TAG, "Can't send Request to Server");
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
