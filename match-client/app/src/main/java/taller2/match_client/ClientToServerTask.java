package taller2.match_client;

import android.os.AsyncTask;
import android.widget.TextView;

/* This class represent a task where the Client send to Server something */
public class ClientToServerTask extends AsyncTask<String, Integer, String> {

    @Override
    protected void onPreExecute(){
        //Setup is done here
    }

    @Override
    /* Send to Server GET and POST request */
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
            // ERROR
            // Log
        }
        return receiveString;
    }

    @Override
    protected void onProgressUpdate(Integer... params) {
        //Update a progress bar here, or ignore it, it's up to you
    }

    @Override
    protected void onCancelled(){
        // Handle what you want to do if you cancel this task
    }
}
