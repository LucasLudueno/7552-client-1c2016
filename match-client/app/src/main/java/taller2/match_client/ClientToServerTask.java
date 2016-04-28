package taller2.match_client;

import android.os.AsyncTask;
import android.widget.TextView;

/* This class represent a background task where the Client send to Server GET and POST request. */
public class ClientToServerTask extends AsyncTask<String, Integer, String> {

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
        HttpConectionClient httpConection = new HttpConectionClient();
        if (requestType.equals("GET") ) {
            receiveString = httpConection.GETRequest(url, uri);
        } else if (requestType.equals("POST") ) {
            receiveString = httpConection.POSTRequest(url, uri, data);
        } else {
            // ERROR - LOG
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
