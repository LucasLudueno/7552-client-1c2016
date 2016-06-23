package taller2.match_client.Request;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import taller2.match_client.Request.ConnectionException;

/* This class send GET and POST request to Server by an URL */
public class RequestSender {

    /* Attributes */
    private URL url;
    private HttpURLConnection httpConnection;
    private static final String TAG = "RequestSender";

    /* Send GET request to Server. Throws ConnectionExeption in case error */
    public String sendGETRequest(String urlString, String uriString) throws ConnectionException {
        try {
            Log.d(TAG, "GET Request");
            url = new URL(urlString + uriString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            String responseCode = String.valueOf(httpConnection.getResponseCode()); // Get response code
            InputStream inputStream = httpConnection.getInputStream();              // Get response
            return responseCode + ":" + InputStreamToString(inputStream);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send GET Request", e);
            throw new ConnectionException("Failed to connect");
        } finally {
            httpConnection.disconnect();
        }
    }

    /* Send POST request to Server. Throws ConnectionExeption in case error */
    public String sendPOSTRequest(String urlString, String uriString, String data) throws ConnectionException {
        String responseCode = "";
        try {
            Log.d(TAG, "POST Request");
            url = new URL(urlString + uriString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            //httpConnection.setRequestProperty("Accept-Language", "UTF-8");
            httpConnection.setDoInput(true);

            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");

            httpConnection.setDoOutput(true);
            httpConnection.connect();

            OutputStreamWriter httpWritter = new OutputStreamWriter(httpConnection.getOutputStream());
            httpWritter.write(data);
            httpWritter.flush();
            httpWritter.close();

            responseCode = String.valueOf(httpConnection.getResponseCode()); // Get response code
            InputStream inputStream = httpConnection.getInputStream();              // Get response
            return responseCode + ":" + InputStreamToString(inputStream);
        } catch (IOException e) {
            if (responseCode.compareTo("") == 0) {
                Log.e(TAG, "Failed to send POST Request", e);
                throw new ConnectionException("Failed to connect");
            } else {
                return responseCode + ":" + "Error";
            }
        } finally {
            httpConnection.disconnect();
        }
    }

    /* Covert the content of InputStream to String */
    public static String InputStreamToString(InputStream stream) {
        InputStreamReader reader = new InputStreamReader(stream);
        StringBuilder builder = new StringBuilder();
        BufferedReader buffer = new BufferedReader(reader);
        try {
            String read = buffer.readLine();

            while(read !=null) {
                builder.append(read);
                read = buffer.readLine();
            }
        } catch (IOException e) {
            Log.w(TAG, "Can't convert InputStream to String");
            return "";
        }
        return builder.toString();
    }
}
